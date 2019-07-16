package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.communication.SerializbleEntity

class CommandID : SerializbleEntity
{
    private val commandId : String
    constructor(commandId : String) : super() {
        this.commandId = commandId
    }

    constructor(commandId : String, action : IAction) {
        this.commandId = commandId

    }

}