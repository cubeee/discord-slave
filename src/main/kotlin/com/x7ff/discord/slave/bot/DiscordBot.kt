package com.x7ff.discord.slave.bot

import com.x7ff.discord.slave.command.Command
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class DiscordBot(
    private val commandPrefix: String,
    private val jda: JDA
): ListenerAdapter() {

    private val roleReactionHandler = RoleReactionHandler(jda)

    override fun onMessageReceived(messageEvent: MessageReceivedEvent?) {
        messageEvent?.let { event ->
            if (!event.isFromType(ChannelType.TEXT)) {
                return
            }

            val message = event.message.contentRaw

            if (message.startsWith(commandPrefix) && message.length > commandPrefix.length) {
                val commandEnd = when {
                    message.contains(" ") -> message.indexOf(" ")
                    else -> message.length
                }
                val trigger = message.substring(commandPrefix.length, commandEnd)
                val argumentPart = message.substring(commandEnd).trimStart()
                handleCommand(trigger, event.member, argumentPart, event.message)
            }
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        super.onMessageReactionAdd(event)
        roleReactionHandler.onMessageReactionAdd(event)
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        super.onMessageReactionRemove(event)
        roleReactionHandler.onMessageReactionRemove(event)
    }

    private fun handleCommand(trigger: String, member: Member, argumentPart: String, message: Message) {
        val command = Command.getCommand(trigger)
        command?.let {
            if (!memberHasPermissions(member, command.requiredPermissions)) {
                return
            }
            command.action.handleCommand(jda, argumentPart, message, member)
        }
    }

    private fun memberHasPermissions(member: Member, permissions: List<Permission>): Boolean {
        return member.roles
            .flatMap { role -> role.permissions }
            .containsAll(permissions)
    }

}