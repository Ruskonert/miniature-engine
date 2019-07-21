package io.github.emputi.mc.miniaturengine.command.impl

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

open class CommandProcessorImpl : Command("Unknown")
{
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        return true
    }

}