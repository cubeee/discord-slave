package com.x7ff.discord.slave.model

import org.jetbrains.exposed.sql.Column

data class RoleSearchCriteria(val value: String, val column: Column<String>)
