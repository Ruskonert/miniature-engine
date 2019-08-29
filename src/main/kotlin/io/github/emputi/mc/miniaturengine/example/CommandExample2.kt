package io.github.emputi.mc.miniaturengine.example

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement
import org.bukkit.command.CommandSender

class CommandExample2 : CommandProcessor("example2")
{
    init {
        val parameterElement = ParameterElement("FirstArgument")
        parameterElement.isOptional = false
        this.addParameterOfArgument(parameterElement)

        val parameterElement2 = ParameterElement("SecondArgument")
        this.addParameterOfArgument(parameterElement2)
    }

    override fun invoke(
        sender: CommandSender,
        args: List<CommandArgument>,
        optionalArgs: List<CommandOptionalArgument>,
        defaultArgs: CommandDefaultArgument
    ): Boolean {
        sender.sendMessage("§bAnother command, But it is not use named argument.")
        sender.sendMessage("§aFirst argument is required")
        sender.sendMessage("§aBut second argument is optional")
        sender.sendMessage("§6each argument matches of in order the parameter argument element.")

        val string : String = args[1].getCommand()
        sender.sendMessage("§fYour first argument value is ${args[0].getCommand()}")
        sender.sendMessage("§fYour second argument value is $string")
        return true
    }
}