package com.x7ff.discord.slave

import com.x7ff.discord.slave.bot.DiscordBot
import com.x7ff.discord.slave.model.PersistedAssignableRoles
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.IOException
import java.sql.Connection

class Slave(
    private val environment: String,
    private val databaseFile: String,
    private val botToken: String
) {
    private lateinit var bot: DiscordBot

    fun connectDatabase(): Database {
        with(File(databaseFile)) {
            if (!exists()) {
                val created = createNewFile()
                if (!created) {
                    throw IOException("Failed to create database file $databaseFile")
                }
            }
        }

        return Database.connect(url = "jdbc:sqlite:$databaseFile", driver = "org.sqlite.JDBC").apply {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

            transaction {
                SchemaUtils.createMissingTablesAndColumns(PersistedAssignableRoles)
            }
        }
    }

    fun connectBot() {
        val builder = JDABuilder(AccountType.BOT)
        builder.setToken(botToken)
        builder.addEventListener()
        val jdaBot = builder.buildBlocking()
        bot = DiscordBot(commandPrefix, jdaBot).apply {
            jdaBot.addEventListener(this)
        }
    }

    companion object {
        const val commandPrefix = "!"
    }

}

fun main(args: Array<String>) {
    val environment = when(System.getenv("ENV")) {
        "prod" -> "prod"
        else -> "dev"
    }
    println("Starting in environment '$environment'")

    val slave = Slave(
        environment = environment,
        databaseFile = getEnvVar("DATABASE_FILE"),
        botToken = getEnvVar("BOT_TOKEN")
    )
    slave.connectDatabase()
    slave.connectBot()
}

private fun getEnvVar(key: String): String {
    return System.getenv(key) ?: throw NullPointerException("Environment variable '$key' not set")
}