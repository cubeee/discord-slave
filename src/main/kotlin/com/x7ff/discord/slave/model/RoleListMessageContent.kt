package com.x7ff.discord.slave.model

import net.dv8tion.jda.core.entities.MessageEmbed

data class RoleListMessageContent (
    val embed: MessageEmbed,
    val roles: List<AssignableRole>
) {
    companion object {
        const val messageFooter = "Roolilista" // TODO: i18n
    }
}