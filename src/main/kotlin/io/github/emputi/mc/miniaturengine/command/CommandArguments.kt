package io.github.emputi.mc.miniaturengine.command

abstract class CommandArguments {
    private val baseCommandProcessor : CommandProcessor
    constructor(command : CommandProcessor) {
        this.baseCommandProcessor = command
    }
    fun getBasePlugin() : CommandProcessor {
        return this.baseCommandProcessor
    }
}