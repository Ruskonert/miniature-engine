package io.github.emputi.mc.miniaturengine.command.parameter.argument

import io.github.emputi.mc.miniaturengine.command.parameter.ICommandParameter

class CommandDefaultArgument(vararg argument: String) :
    ICommandParameter<MutableList<String>> {
    private val _mismatched : MutableList<String> = argument.toMutableList()
    fun addValue(string : String) { this._mismatched.add(string) }
    override fun getValue(): MutableList<String> = this._mismatched
    override fun getParameterName(): String = "MISMATCHED_ARGUMENTS"
    fun hasArguments() : Boolean = this._mismatched.isNotEmpty()
}