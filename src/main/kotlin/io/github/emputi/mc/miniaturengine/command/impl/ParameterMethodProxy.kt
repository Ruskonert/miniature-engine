package io.github.emputi.mc.miniaturengine.command.impl

import io.github.emputi.mc.miniaturengine.thread.BukkitThreadSynchronise
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import java.util.concurrent.ConcurrentLinkedQueue

class ParameterMethodProxy : BukkitThreadSynchronise()
{
    private var isRunnable : Boolean = true

    override fun executeTask(handleInstance : Any?) : Any?
    {
        while(true) {
            if (!this.isRunnable) {
                Bukkit.getConsoleSender().sendMessage("ParameterMethodProxy[${this}] is stopped, Maybe it cannot update the command info at real-time.")
                this.task!!.cancel()
                break
            }
            this.parameterMethodProxy0()
        }
        return true
    }

    companion object {
        private val reserveDeactivateMethod0 : ConcurrentLinkedQueue<ParameterMethodImpl> = ConcurrentLinkedQueue()
        fun reserveDeactivate(impl : ParameterMethodImpl){
            reserveDeactivateMethod0.add(impl)
        }
    }

    private fun parameterMethodProxy0()
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

        val parameterMethods =  ParameterMethodImpl.getRegistryMethods()
        for(parameterImpl in parameterMethods) {
            val element = parameterImpl.getParameterElement()
            val action = element.getAction()
            if(action == null) {
                Bukkit.getConsoleSender().sendMessage("ParameterMethodImpl[${parameterImpl.name}] is disabled, Because action is null.")
                this.deactivateMethod0(parameterImpl)
                this.removeKnownCommand0(parameterImpl, knownCommand)
                continue
            }

            if(! parameterImpl.isRegistered)
            {
                parameterImpl.register(commandMap)
                knownCommand[parameterImpl.name] = parameterImpl
                parameterImpl.setEnable(this.activePlugin)
            }

            if(reserveDeactivateMethod0.isNotEmpty()) {
                val deactivates = reserveDeactivateMethod0
                for(impl in deactivates) {
                    this.deactivateMethod0(impl)
                    this.removeKnownCommand0(impl, knownCommand)
                    reserveDeactivateMethod0.remove(impl)
                }
            }
        }
    }

    private fun deactivateMethod0(impl : ParameterMethodImpl) {
        val target = ParameterMethodImpl.getRegistryMethods()
        Bukkit.getConsoleSender().sendMessage("ParameterMethodImpl[${impl.name}] is disabled now.")
        target.remove(impl)
        impl.setEnable(false)
        ParameterMethodImpl.getUnloadedMethods().add(impl)
    }

    private fun removeKnownCommand0(impl : ParameterMethodImpl, commandMap : HashMap<String, Command>) {
        if(commandMap.containsKey(impl.name)) {
            commandMap.remove(impl.name)
        }
    }
}