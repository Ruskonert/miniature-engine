package io.github.emputi.mc.miniaturengine.command.impl

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.concurrent.ConcurrentLinkedQueue

open class CommandProcessorImpl(private val commandName: String, private val commandBase: CommandProcessor) : Command(
    commandName,
    "This command was registered by miniature-engine",
    "",
    commandBase.getAliases()
) {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        return commandBase.execute(sender, args.toList())
    }

    fun getCommandName() : String = this.commandName

    companion object {
        private val methods0 : ConcurrentLinkedQueue<CommandProcessorImpl> = ConcurrentLinkedQueue()
        private val unloadedMethods0 : ConcurrentLinkedQueue<CommandProcessorImpl> = ConcurrentLinkedQueue()
        fun getUnloadedMethods() : ConcurrentLinkedQueue<CommandProcessorImpl> = unloadedMethods0
        fun getRegistryMethods() : ConcurrentLinkedQueue<CommandProcessorImpl> = methods0
        fun queueActivateImpl(pmi : CommandProcessorImpl) { methods0.add(pmi) }
        fun isMedicated(target : CommandProcessor) : CommandProcessorImpl? {
            for(v in methods0) if(v.commandBase == target) return v
            return null
        }
    }
}