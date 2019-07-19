package io.github.emputi.mc.miniaturengine.command

interface ICommand
{
    @ExperimentalUnsignedTypes
    fun execute(command : CommandID, arguments: CommandArguments, attributes : Any?) : StatusError
}