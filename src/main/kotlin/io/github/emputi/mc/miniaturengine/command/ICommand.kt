package io.github.emputi.mc.miniaturengine.command

interface ICommand
{
    fun execute(command : CommandID, arguments: CommandArguments, attributes : Any?) : StatusError
}