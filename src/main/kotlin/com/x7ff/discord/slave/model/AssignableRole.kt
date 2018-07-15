package com.x7ff.discord.slave.model

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

data class AssignableRole (
    private val guildId: Long,
    val reactionEmote: String,
    val role: String
) {
    fun insert(): PersistedAssignableRole? {
        return PersistedAssignableRole.new {
            guildId = this@AssignableRole.guildId
            reactionEmote = this@AssignableRole.reactionEmote
            role = this@AssignableRole.role
        }
    }

    companion object {
        fun getRoles(guildId: Long): List<AssignableRole> {
            return transaction {
                PersistedAssignableRole
                    .find { PersistedAssignableRoles.guildId eq guildId }
                    .map { role ->
                        AssignableRole(role.guildId, role.reactionEmote, role.role)
                    }
            }
        }

        fun getRole(guildId: Long, emote: String): AssignableRole? {
            return transaction {
                PersistedAssignableRole
                    .find { PersistedAssignableRoles.guildId.eq(guildId)
                        .and(PersistedAssignableRoles.reactionEmote.eq(emote)) }
                    .map { role ->
                        AssignableRole(role.guildId, role.reactionEmote, role.role)
                    }
                    .firstOrNull()
            }
        }

        fun removeRole(guildId: Long, searchCriteria: RoleSearchCriteria): Pair<Boolean, AssignableRole?> {
            return transaction {
                val role = PersistedAssignableRole
                    .find { PersistedAssignableRoles.guildId.eq(guildId)
                        .and(searchCriteria.column.eq(searchCriteria.value)) }
                    .firstOrNull()
                role?.delete()

                if (role == null) {
                    Pair(false, null)
                } else {
                    Pair(true, AssignableRole(role.guildId, role.reactionEmote, role.role))
                }
            }
        }

    }

}