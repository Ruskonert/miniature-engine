package io.github.emputi.mc.miniaturengine.command.impl

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin

class CommandProcessorImpl(private val commandName: String, private val commandBase: CommandProcessor) : Command(
    commandName,
    "",
    "",
    commandBase.getAliases()
), PluginIdentifiableCommand {

    override fun getPlugin(): Plugin = commandBase.getDelegate()

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        return commandBase.execute(sender, args.toList())
    }
}