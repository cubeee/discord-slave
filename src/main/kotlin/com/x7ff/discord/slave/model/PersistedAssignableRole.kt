package com.x7ff.discord.slave.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object PersistedAssignableRoles : IntIdTable() {
    val guildId = long("guild_id").index()
    val reactionEmote = varchar("reaction_emote", 128)
    val role = varchar("role", 128)

    init {
        index(true, guildId, reactionEmote, role)
    }
}

class PersistedAssignableRole(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PersistedAssignableRole>(PersistedAssignableRoles)

    var guildId by PersistedAssignableRoles.guildId
    var reactionEmote by PersistedAssignableRoles.reactionEmote
    var role by PersistedAssignableRoles.role
}