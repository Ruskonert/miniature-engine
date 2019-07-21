package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.application.Bootstrapper

open class CommandProcessor : Handle, PluginHandler
{
    override fun executeTask(handleInstance: Any?): Any? {
        return null
    }

    override fun setEnable(active: Boolean) {

    }

    override fun setEnable(plugin: Bootstrapper?) {

    }

    override fun isEnabled(): Boolean {
        return true
    }

}