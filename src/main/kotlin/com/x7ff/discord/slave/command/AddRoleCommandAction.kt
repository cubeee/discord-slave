package com.x7ff.discord.slave.command

import com.x7ff.discord.slave.*
import com.x7ff.discord.slave.model.AssignableRole
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import org.jetbrains.exposed.sql.transactions.transaction

class AddRoleCommandAction: CommandAction() {
    companion object {
        const val successfulOperationEmoji = "✅"
    }

    override fun handleCommand(jda: JDA, rawMessage: String, message: Message, member: Member) {
        val parts = rawMessage.split(" ")
        if (parts.size != 2) {
            message.reply("syntax: addrole :reaction: @role") // TODO: i18n
            return
        }

        val emoji = parts[0]
        val role = parts[1].toMentionableRole(jda)
        if (role == null) {
            message.reply("Invalid role entered, roles have to be @mentioned") // TODO: i18n
            return
        }

        try {
            if (emoji.isNotEmpty() || (emoji.isCustomEmote() && emoji.toCustomEmote(message.jda) != null)) {
                val added = transaction {
                    AssignableRole(message.guild.idLong, emoji, role.asMention).insert()
                }
                added.let {
                    message.addReaction(successfulOperationEmoji).submit()
                    jda.updateRoleListMessages(jda, message.guild)
                }
            } else {
                message.reply("Invalid emoji entered as reaction") // TODO: i18n
            }
        } catch (e: Exception) {
            e.printStackTrace()
            message.reply("Failed to add role") // TODO: i18n
        }
    }

}
