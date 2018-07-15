package com.x7ff.discord.slave.command

import com.x7ff.discord.slave.addRoleReactions
import com.x7ff.discord.slave.model.AssignableRole
import com.x7ff.discord.slave.model.RoleListMessageContent
import com.x7ff.discord.slave.toMentionableRole
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.Role
import java.awt.Color

class RoleListCommandAction: CommandAction() {

    override fun handleCommand(jda: JDA, rawMessage: String, message: Message, member: Member) {
        val (embed, roles) = message.jda.createRoleListMessage(message.guild)

        message.textChannel.sendMessage(embed).queue { msg ->
            msg.addRoleReactions(roles)
        }
    }

    companion object {
        fun JDA.createRoleListMessage(guild: Guild): RoleListMessageContent {
            val embedBuilder = EmbedBuilder()
            embedBuilder.setColor(Color.DARK_GRAY)
            embedBuilder.setTitle("__**Roolit**__") // todo: i18n
            embedBuilder.setFooter(RoleListMessageContent.messageFooter, null)

            // TODO: i18n
            embedBuilder.addField(
                "Liittyminen",
                "Rooliin liittyäksesi sinun tarvitsee vain lisätä reaktio painamalla roolia kuvaavan emojin reaktiota.",
                false)
            embedBuilder.addField(
                "Lähteminen", """
                    Roolista voi lähteä ottamalla reaktion pois.
                    Annetun reaktion puuttuessa tai manuaalisesti annetusta roolista voi poistua ensin lisäämällä reaktion ja sen jälkeen ottamalla sen pois.
                    """.trimIndent(),
                false)

            val roles = AssignableRole.getRoles(guild.idLong)

            val discordRoleList = mutableListOf<Role>()
            val roleList = StringBuffer()
            roles.onEach { role ->
                val discordRole = role.role.toMentionableRole(this)
                discordRole?.let {
                    roleList.append("${role.reactionEmote} - ${discordRole.name}").append("\n")

                    discordRoleList.add(discordRole)
                }
            }
            embedBuilder.addField("Saatavilla olevat roolit:", roleList.toString(), false) // TODO: i18n

            return RoleListMessageContent(embedBuilder.build(), roles)
        }
    }

}