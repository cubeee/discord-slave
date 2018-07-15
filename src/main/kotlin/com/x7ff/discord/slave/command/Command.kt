package com.x7ff.discord.slave.command

import net.dv8tion.jda.core.Permission

enum class Command(
    val action: CommandAction,
    val requiredPermissions: List<Permission>,
    val trigger: String,
    vararg val triggers: String
) {
    ADD_ROLE(AddRoleCommandAction(), listOf(Permission.ADMINISTRATOR), "addrole"),
    REMOVE_ROLE(RemoveRoleCommandAction(), listOf(Permission.ADMINISTRATOR),"removerole"),
    ROLE_LIST(RoleListCommandAction(), listOf(Permission.ADMINISTRATOR),"roles");

    private lateinit var allTriggers: List<String>

    companion object {
        fun getCommand(trigger: String): Command?
                = Command.values().firstOrNull { it.allTriggers.contains(trigger.toLowerCase()) }

        init {
            for (command in Command.values()) {
                command.allTriggers = listOf(command.trigger.toLowerCase()) + command.triggers.map { it.toLowerCase() }
            }
        }
    }
}