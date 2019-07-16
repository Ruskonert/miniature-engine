package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.policy.Permission

abstract class CommandProcessor
{
    private var plugin : Bootstrapper? = null
    fun getPlugin() : Bootstrapper? {
        return this.plugin
    }
    fun isEnabled() : Boolean {
        return this.plugin != null
    }

    fun setEnable(plugin : Bootstrapper) : Boolean {
        this.plugin = plugin
        return this.isEnabled()
    }

    private var commandArguments : CommandArguments? = null
    fun setCommandArguments(ca : CommandArguments) {
        this.commandArguments = ca
    }

    constructor() {

    }
    constructor(processorId : String, command : String) {

    }

    private val processorId : String = "Application.Processor"
    private lateinit var permission : Permission
}