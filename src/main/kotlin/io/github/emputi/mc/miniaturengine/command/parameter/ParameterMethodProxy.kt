package io.github.emputi.mc.miniaturengine.command.parameter

import io.github.emputi.mc.miniaturengine.command.Handle
import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap

class ParameterMethodProxy : Handle
{
    private var plugin : Bootstrapper? = null
    fun getPlugin() : Bootstrapper? { return this.plugin }

    private var isRunnable : Boolean = true

    override fun executeTask(handleInstance : Any?) : Any?
    {
        if(handleInstance !is Bootstrapper) {
            throw ParameterActionException("The handleInstance must be Bootstrapper type")
        }
        this.plugin = handleInstance
        val func : () -> Unit = fun() {
            while(true) { this.parameterMethodProxy0(); if(! this.isRunnable) break }
        }
        try {
            handleInstance.server.scheduler.runTaskAsynchronously(handleInstance, func)
        }
        catch(e : Exception) {
            e.printStackTrace()
            return false
        }
        return true
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
        if(knownCommand == null) {
            this.isRunnable = false
            throw RuntimeException("Internal error, The known command not found!")
        }

        for(parameterImpl in ParameterMethodImpl.getRegistryMethods()) {
            val element = parameterImpl.getParameterElement()
            val action = element.getAction()
            if(action == null) {
                println("Internal error, But we made not implementing tis routine yet.")
                parameterImpl.setActivate(false)
                continue
            }

            if(!parameterImpl.isRegistered) {
                parameterImpl.register(commandMap)
                knownCommand[parameterImpl.name] = parameterImpl
                parameterImpl.setActivate(true)
            }
            else {
                if(parameterImpl.isEnabled()) {

                }
            }
        }
    }
}