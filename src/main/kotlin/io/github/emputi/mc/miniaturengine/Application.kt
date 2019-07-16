package io.github.emputi.mc.miniaturengine

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.parameter.ParameterMethodProxy
import io.github.emputi.mc.miniaturengine.event.command.ParameterClickEvent
import io.github.emputi.mc.miniaturengine.example.CommandExample
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class Application : Bootstrapper()
{
    class EventListener : Listener {

        @EventHandler
        fun onClickEvent(e : ParameterClickEvent) {
            e.isCancelled = true
        }
    }

    override fun startApplication(handleInstance: Any?) {
        val commandExample = CommandExample()
        val parameterMethodProxy = ParameterMethodProxy()
        parameterMethodProxy.executeTask(this)
        Bukkit.getPluginManager().registerEvents(EventListener(), this)
    }
}