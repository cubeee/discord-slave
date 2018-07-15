package com.x7ff.discord.slave.bot

import com.x7ff.discord.slave.isRoleList
import com.x7ff.discord.slave.model.AssignableRole
import com.x7ff.discord.slave.toMentionableRole
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.MessageReaction
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent

class RoleReactionHandler(private val jda: JDA) {
    enum class ReactionAction {
        ADD, REMOVE
    }

    fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.user.isBot) {
            return
        }

        event.channel.getMessageById(event.messageId).queue { message ->
            if (message.member.user.idLong != jda.selfUser.idLong) {
                return@queue
            }

            if (message.isRoleList()) {
                handleRoleListReactionEvent(ReactionAction.ADD, message.guild, event.reaction, event.member)
            }
        }
    }

    fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        if (event.user.isBot) {
            return
        }

        event.channel.getMessageById(event.messageId).queue { message ->
            if (message.member.user.idLong != jda.selfUser.idLong) {
                return@queue
            }

            if (message.isRoleList()) {
                handleRoleListReactionEvent(ReactionAction.REMOVE, message.guild, event.reaction, event.member)
            }
        }
    }

    private fun handleRoleListReactionEvent(reactionAction: ReactionAction, guild: Guild, reaction: MessageReaction, member: Member) {
        val emote = if (reaction.reactionEmote.emote != null) {
            reaction.reactionEmote.emote.asMention
        } else {
            reaction.reactionEmote.name
        }

        AssignableRole.getRole(guild.idLong, emote)?.let {
            val role = it.role.toMentionableRole(jda)

            when(reactionAction) {
                ReactionAction.ADD -> guild.controller.addSingleRoleToMember(member, role).submit()
                ReactionAction.REMOVE -> guild.controller.removeSingleRoleFromMember(member, role).submit()
            }
        }

    }

}