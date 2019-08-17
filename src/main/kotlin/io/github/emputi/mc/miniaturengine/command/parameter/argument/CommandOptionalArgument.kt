package io.github.emputi.mc.miniaturengine.command.parameter.argument

import io.github.emputi.mc.miniaturengine.command.parameter.ICommandParameter

class CommandOptionalArgument(private val parameterName : String) :
    ICommandParameter<Boolean>
{
    override fun getValue() : Boolean = true
    override fun getParameterName(): String = this.parameterName
}
