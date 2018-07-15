package com.x7ff.discord.slave.command

import com.x7ff.discord.slave.model.AssignableRole
import com.x7ff.discord.slave.model.PersistedAssignableRoles
import com.x7ff.discord.slave.model.RoleSearchCriteria
import com.x7ff.discord.slave.removeReactionFromRoleListMessages
import com.x7ff.discord.slave.reply
import com.x7ff.discord.slave.toMentionableRole
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message

class RemoveRoleCommandAction: CommandAction() {
    companion object {
        const val successfulOperationEmoji = "✅"
    }

    override fun handleCommand(jda: JDA, rawMessage: String, message: Message, member: Member) {
        val parts = rawMessage.split(" ")
        if (parts.size != 1) {
            message.reply("syntax: removerole :reaction:/@role") // TODO: i18n
            return
        }

        val identifier = parts[0]

        val role = identifier.toMentionableRole(jda)
        val roleSearchCriteria = when(role) {
            null -> RoleSearchCriteria(identifier, PersistedAssignableRoles.reactionEmote)
            else -> RoleSearchCriteria(role.asMention, PersistedAssignableRoles.role)
        }

        val (success, assignableRole) = AssignableRole.removeRole(message.guild.idLong, roleSearchCriteria)
        if (success) {
            message.addReaction(successfulOperationEmoji).submit()
            jda.removeReactionFromRoleListMessages(jda, message.guild, assignableRole!!)
        } else {
            message.reply("Failed to remove role reaction") // TODO: i18n
        }
    }

}
