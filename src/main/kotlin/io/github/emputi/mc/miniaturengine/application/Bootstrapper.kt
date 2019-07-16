package io.github.emputi.mc.miniaturengine.application

import io.github.emputi.mc.miniaturengine.thread.PluginCallback
import io.github.emputi.mc.miniaturengine.thread.AwaitFunction
import io.github.emputi.mc.miniaturengine.thread.Task
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

abstract class Bootstrapper : JavaPlugin() {
    companion object {
        var BootstrapperBase : Bootstrapper? = null
    }

    private val hwConfiguration : (handleInstance : Any?) -> Any? = fun(configurationObject : Any?) : Any? {
        return true
    }

    abstract fun startApplication(handleInstance : Any?)

    @AwaitFunction
    open fun onPreLoad(handleInstance : Any?) : Task<Any?, Any?> {
        return PluginCallback(this, server.scheduler, hwConfiguration, null)
    }

    open fun unloadApplication(handleInstance : Any?) {

    }

    final override fun onEnable() {
        val callbackResult = this.onPreLoad(null)
        if(callbackResult.isTerminated()) {
            Bukkit.getConsoleSender().sendMessage("Something is wrong, Try again.")
        }
        this.startApplication(this)
        BootstrapperBase = this
        Bukkit.getConsoleSender().sendMessage("${this.name} is loaded successfully!")
    }

    final override fun onDisable() {

    }
}