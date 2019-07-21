package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.command.parameter.ParameterElement

class CommandArgument {
    constructor(parameterElement: ParameterElement, value : String) {
        this.argumentsPair = parameterElement to value
    }
    private val argumentsPair : Pair<ParameterElement, String?>
    fun getArgument() : Pair<ParameterElement, String?> = this.argumentsPair
}