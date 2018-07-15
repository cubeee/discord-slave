package com.x7ff.discord.slave.command

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message

abstract class CommandAction {

    abstract fun handleCommand(jda: JDA, rawMessage: String, message: Message, member: Member)

}