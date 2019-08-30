package io.github.emputi.mc.miniaturengine.example

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement.Companion.createDelicateParameterElement
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElementAction
import org.bukkit.command.CommandSender

class CommandExample : CommandProcessor("example")
{
    init {
        val parameterClickAction = ParameterElementAction { args ->
            if (args == null) throw NullPointerException("Nop! arguments is null")
            args.getClicker().sendMessage("Hello ${args.getClicker().name}! You clicked parameter string of argument 'one'.")
            true
        }

        val parameterElement = createDelicateParameterElement("message", action = parameterClickAction)
        this.addParameterOfArgument(parameterElement)
        this.usingNamedArgument = true
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