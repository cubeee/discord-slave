package com.x7ff.discord.slave.command

enum class Command(
    val action: CommandAction,
    val trigger: String,
    vararg val triggers: String
) {
    ADD_ROLE(AddRoleCommandAction(), "addrole"),
    REMOVE_ROLE(RemoveRoleCommandAction(), "removerole"),
    ROLE_LIST(RoleListCommandAction(), "roles");

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