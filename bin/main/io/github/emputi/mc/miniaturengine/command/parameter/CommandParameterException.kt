package io.github.emputi.mc.miniaturengine.command.parameter

import io.github.emputi.mc.miniaturengine.command.CommandException

class CommandParameterException : CommandException
{
    constructor(msg : String) : super(msg)
    constructor(msg : String, cause : Throwable) : super(msg, cause)
}