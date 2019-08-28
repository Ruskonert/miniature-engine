package io.github.emputi.mc.miniaturengine.application

import io.github.emputi.mc.miniaturengine.configuration.Attribute
import io.github.emputi.mc.miniaturengine.thread.PluginCallback
import io.github.emputi.mc.miniaturengine.thread.AwaitFunction
import io.github.emputi.mc.miniaturengine.thread.Task
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

abstract class Bootstrapper : JavaPlugin() {
    companion object {
        var BootstrapperBase : Bootstrapper? = null
    }
    private val attribute : Attribute? = null
    private val _hwConfiguration : (handleInstance : Any?) -> Any? = fun(configurationObject : Any?) : Any? {
        return null
    }

    abstract fun startApplication(handleInstance : Any?)

    @AwaitFunction
    open fun onPreLoad(handleInstance : Any?) : Task<Any?, Any?> {
        return PluginCallback(this, server.scheduler, _hwConfiguration, null)
    }

    open fun unloadApplication(handleInstance : Any?) {

    }

    final override fun onEnable() {
        val callbackResult = this.onPreLoad(null)
        if(callbackResult.isTerminated()) {
            Bukkit.getConsoleSender().sendMessage("Something is wrong, Try again.")
        }
        BootstrapperBase = this
        this.startApplication(this)
        Bukkit.getConsoleSender().sendMessage("${this.name} is loaded successfully!")
    }

    final override fun onDisable() {

    }
}