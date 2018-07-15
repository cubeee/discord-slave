package com.x7ff.discord.slave

import com.x7ff.discord.slave.command.RoleListCommandAction.Companion.createRoleListMessage
import com.x7ff.discord.slave.model.AssignableRole
import com.x7ff.discord.slave.model.RoleListMessageContent
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.requests.restaction.MessageAction
import net.dv8tion.jda.core.utils.MiscUtil

fun String.isCustomEmote(): Boolean = startsWith("<") && endsWith(">")

fun String.toCustomEmote(jda: JDA): Emote? {
    val matcher = Message.MentionType.EMOTE.pattern.matcher(this)
    while (matcher.find()) {
        val emoteId = MiscUtil.parseSnowflake(matcher.group(2))
        return jda.getEmoteById(emoteId)
    }
    return null
}

fun String.toMentionableRole(jda: JDA): Role? {
    val matcher = Message.MentionType.ROLE.pattern.matcher(this)
    while (matcher.find()) {
        val roleId = MiscUtil.parseSnowflake(matcher.group(1))
        return jda.getRoleById(roleId)
    }
    return null
}

fun Message.isRoleList(): Boolean {
    if (embeds.isEmpty()) {
        return false
    }

    embeds.onEach { embed ->
        embed.footer.let {
            if (it.text == RoleListMessageContent.messageFooter) {
                return true
            }
        }
    }
    return false
}

fun Message.addRoleReactions(roles: List<AssignableRole>) {
    roles.onEach { role ->
        val restAction = if (role.reactionEmote.isCustomEmote()) {
            addReaction(role.reactionEmote.toCustomEmote(jda))
        } else {
            addReaction(role.reactionEmote)
        }
        restAction.submit()
    }
}

fun Message.removeRoleReaction(role: AssignableRole) {
    val restAction = if (role.reactionEmote.isCustomEmote()) {
        channel.removeReactionById(idLong, role.reactionEmote.toCustomEmote(jda))
    } else {
        channel.removeReactionById(idLong, role.reactionEmote)
    }
    restAction.submit()
}

fun Message.reply(out: Any) {
    if (isFromType(ChannelType.PRIVATE)) {
        author.openPrivateChannel().queue { pc -> sendMessage(pc, out).submit() }
    } else {
        sendMessage(textChannel, out).submit()
    }
}

private fun sendMessage(channel: MessageChannel, out: Any): MessageAction {
    return when(out) {
        is String -> channel.sendMessage(out)
        is MessageEmbed -> channel.sendMessage(out)
        is Message -> channel.sendMessage(out)
        else -> throw IllegalArgumentException("Invalid type passed to reply as 'out'")
    }
}

fun JDA.updateRoleListMessages(jda: JDA, guild: Guild) {
    findRoleListMessages(jda, guild)
        .onEach { message ->
            val (embed, roles) = jda.createRoleListMessage(guild)

            message.editMessage(embed).queue { editedMessage ->
                editedMessage.addRoleReactions(roles)
            }
        }
}

fun JDA.removeReactionFromRoleListMessages(jda: JDA, guild: Guild, role: AssignableRole) {
    findRoleListMessages(jda, guild)
        .onEach { message ->
            val (embed, _) = jda.createRoleListMessage(guild)

            message.editMessage(embed).queue { editedMessage ->
                editedMessage.removeRoleReaction(role)
            }
        }
}

private fun findRoleListMessages(jda: JDA, guild: Guild): List<Message> {
    return guild.textChannels.flatMap { channel ->
        channel.iterableHistory.limit(channel.iterableHistory.maxLimit)
            .filter { msg -> msg.author.idLong == jda.selfUser.idLong && msg.isRoleList() }
            .toList()
    }
}