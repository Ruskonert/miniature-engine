package io.github.emputi.mc.miniaturengine.example

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElementAction
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterFunction
import io.github.emputi.mc.miniaturengine.event.EventArguments
import org.bukkit.command.CommandSender

class CommandExample : CommandProcessor("example")
{
    private val clickAction : ParameterFunction = fun(args : EventArguments?) : Any? {
        if (args == null) throw NullPointerException("Nop! arguments is null")
        args.getClicker().sendMessage("Hello player! You clicked arg1 parameter string.")
        return true
    }

    init {

        val parameterElement = ParameterElement("message")
        parameterElement.isOptional = false
        val parameterClickAction = ParameterElementAction(clickAction)
        parameterElement.setAction(parameterClickAction)
        this.addParameterOfArgument(parameterElement)
        this.usingNamedArgument = true
        this.medicateCommand()

        parameterElement.mediateParameterFunction()
    }

    override fun invoke(
        sender: CommandSender,
        args: List<CommandArgument>,
        optionalArgs: List<CommandOptionalArgument>,
        defaultArgs: CommandDefaultArgument
    ): Boolean {
        sender.sendMessage("§fYou spend the message:§e ${args[0].getCommand()}")
        return true
    }
}