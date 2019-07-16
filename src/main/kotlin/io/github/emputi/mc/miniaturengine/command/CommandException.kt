package io.github.emputi.mc.miniaturengine.command


open class CommandException : org.bukkit.command.CommandException
{
    constructor(msg : String) : super(msg)
    constructor(msg : String, cause : Throwable) : super(msg, cause)
}
