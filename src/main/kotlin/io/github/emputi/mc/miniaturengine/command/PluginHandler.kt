package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.application.Bootstrapper

interface PluginHandler
{
    fun setEnable(active : Boolean)

    fun setEnable(plugin : Bootstrapper?)

    fun isEnabled() : Boolean
}
