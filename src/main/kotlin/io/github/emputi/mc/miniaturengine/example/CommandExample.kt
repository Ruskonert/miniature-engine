package io.github.emputi.mc.miniaturengine.example

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.ParameterElement
import io.github.emputi.mc.miniaturengine.command.parameter.ParameterElementAction
import io.github.emputi.mc.miniaturengine.command.parameter.ParameterFunction
import io.github.emputi.mc.miniaturengine.event.EventArguments
import org.bukkit.Bukkit

class CommandExample : CommandProcessor() {
    private val clickAction : ParameterFunction = fun(args : EventArguments?) : Any? {
        if (args == null) {
            throw NullPointerException("Nop! arguments is null")
        }
        args.getClicker().sendMessage("Hello player! You clicked arg1 parameter string.")
        return true
    }

    init {
        val parameterElement = ParameterElement("arg1")
        parameterElement.setIsOptional(false)
        val parameterClickAction = ParameterElementAction(this.clickAction)
        parameterElement.setAction(parameterClickAction)
        parameterElement.mediateParameterFunction()
        Bukkit.getConsoleSender().sendMessage(parameterClickAction.getFunctionId())
    }
}