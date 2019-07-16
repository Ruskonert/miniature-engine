package io.github.emputi.mc.miniaturengine.event

import org.bukkit.command.CommandSender

class EventArguments {
    private var clickedBy : CommandSender
    private var stringArguments : ArrayList<String> = ArrayList()

    constructor(causedBy : CommandSender) {
        this.clickedBy = causedBy
    }

    constructor(causedBy: CommandSender, stringArgument: List<String>) {
        this.clickedBy = causedBy
        this.stringArguments.addAll(stringArgument)
    }

    fun getClicker() : CommandSender {
        return this.clickedBy
    }

    fun getStringArguments() : List<String> = this.stringArguments
    fun hasStringArguments() : Boolean = this.stringArguments.isNotEmpty()
}