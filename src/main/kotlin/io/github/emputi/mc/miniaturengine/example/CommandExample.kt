package io.github.emputi.mc.miniaturengine.example

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElementAction
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterFunction
import io.github.emputi.mc.miniaturengine.event.EventArguments
import org.bukkit.command.CommandSender

class CommandExample : CommandProcessor("example", Bootstrapper.BootstrapperBase!!)
{
    private val clickAction : ParameterFunction = fun(args : EventArguments?) : Any? {
        if (args == null) throw NullPointerException("Nop! arguments is null")
        args.getClicker().sendMessage("Hello player! You clicked arg1 parameter string.")
        return true
    }

    init {
        val parameterElement = ParameterElement("arg1")
        parameterElement.setIsOptional(false)

        val parameterClickAction = ParameterElementAction(clickAction)
        parameterElement.setAction(parameterClickAction)
        parameterElement.mediateParameterFunction()

        this.addParameterOfArgument(parameterElement)
        this.usingNamedArgument = true
    }

    override fun invoke(
        sender: CommandSender,
        args: List<CommandArgument>,
        optionalArgs: List<CommandOptionalArgument>,
        defaultArgs: List<CommandDefaultArgument>
    ): Boolean {
        sender.sendMessage("Hello world! ${defaultArgs[0]}")
        return true
    }
}