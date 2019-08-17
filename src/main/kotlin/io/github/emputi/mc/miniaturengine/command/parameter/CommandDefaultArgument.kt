package io.github.emputi.mc.miniaturengine.command.parameter

class CommandDefaultArgument(vararg argument: String) : ICommandParameter<MutableList<String>> {
    private val _mismatched : MutableList<String> = argument.toMutableList()
    fun addValue(string : String) { this._mismatched.add(string) }
    override fun getValue(): MutableList<String> = this._mismatched
    override fun getParameterName(): String = "MISMATCHED_ARGUMENTS"
}