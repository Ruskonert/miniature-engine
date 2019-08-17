package io.github.emputi.mc.miniaturengine.command.parameter.argument

import io.github.emputi.mc.miniaturengine.command.Handle
import io.github.emputi.mc.miniaturengine.command.parameter.ICommandParameter
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement

open class CommandArgument : Handle, ICommandParameter<String>
{
    override fun getValue(): String = this.argumentsPair.second

    override fun getParameterName(): String {
        return this.argumentsPair.first.getParameterName()
    }

    override fun executeTask(handleInstance: Any?): Any? {
        throw RuntimeException("stub!")
    }

    constructor(parameterElement: ParameterElement, value : String) {
        this.argumentsPair = parameterElement to value
    }

    private var argumentsPair : Pair<ParameterElement, String>

    fun getArgument() : Pair<ParameterElement, String> = this.argumentsPair
    fun getCommand() : String = this.argumentsPair.second
    fun isNull() : Boolean = this.argumentsPair.second.isEmpty()

    fun setArgument(element : ParameterElement, label : String) {
        this.argumentsPair = element to label
    }
}