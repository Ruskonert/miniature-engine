package io.github.emputi.mc.miniaturengine.command.impl

import io.github.emputi.mc.miniaturengine.thread.BukkitThreadSynchronise
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import java.util.concurrent.ConcurrentLinkedQueue

open class CommandProcessorProxy : BukkitThreadSynchronise()
{
    init {
        this.isSync = false
    }
    private var isRunnable : Boolean = true
    override fun executeTask(handleInstance : Any?) : Any?
    {
        while(true) {
            if (!this.isRunnable) {
                Bukkit.getConsoleSender().sendMessage("CommandProcessorProxy[${this}] is stopped, Maybe it cannot update the command info at real-time.")
                this.task!!.cancel()
                break
            }
            this.commandProcessorProxy0()
        }
        return true
    }

    companion object {
        private val reserveDeactivateMethod0 : ConcurrentLinkedQueue<CommandProcessorImpl> = ConcurrentLinkedQueue()
            fun reserveDeactivate(impl : CommandProcessorImpl){
            reserveDeactivateMethod0.add(impl)
        }
    }

    fun commandProcessorProxy0()
    {
        val server = Bukkit.getServer()
        val serverClazz = server::class.java

        val commandMapField = serverClazz.getDeclaredField("commandMap")
        commandMapField.isAccessible = true

        val knownCommandsField = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
        knownCommandsField.isAccessible = true

        val commandMap  = commandMapField.get(server) as? SimpleCommandMap
        if(commandMap == null) {
            this.isRunnable = false
            throw RuntimeException("Internal error, The command map not found!")
        }

        @Suppress("UNCHECKED_CAST")
        val knownCommand = knownCommandsField.get(commandMap) as? HashMap<String, Command>
        // if the known command is null (internal error)
        if(knownCommand == null) {
            this.isRunnable = false
            throw RuntimeException("Internal error, The known command not found, Task is stopped")
        }

        val commandMethods =  CommandProcessorImpl.getRegistryMethods()
        for(commandImpl in commandMethods) {

            if(! commandImpl.isRegistered)
            {
                commandImpl.register(commandMap)
                knownCommand[commandImpl.getCommandName()] = commandImpl
                //commandImpl.setEnable(this.activePlugin)
            }

            if(reserveDeactivateMethod0.isNotEmpty()) {
                val deactivates = reserveDeactivateMethod0
                for(impl in deactivates) {
                    this.deactivateMethod0(impl)
                    removeKnownCommand0(impl, knownCommand)
                    reserveDeactivateMethod0.remove(impl)
                }
            }
        }
    }

    fun removeKnownCommand0(impl: CommandProcessorImpl, commandMap: HashMap<String, Command>) {
        if (commandMap.containsKey(impl.name)) {
            commandMap.remove(impl.name)
        }
    }

    private fun deactivateMethod0(impl : CommandProcessorImpl) {
        val target = CommandProcessorImpl.getRegistryMethods()
        Bukkit.getConsoleSender().sendMessage("CommandProcessorImpl[${impl.name}] is disabled now.")
        target.remove(impl)
        //impl.setEnable(false)
        CommandProcessorImpl.getUnloadedMethods().add(impl)
    }
}