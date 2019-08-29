package io.github.emputi.mc.miniaturengine.apis

import io.github.emputi.mc.miniaturengine.command.PluginHandler
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement

interface ParameterMethod : PluginHandler
{
    fun getResult(): Any?

    fun getParameterElement(): ParameterElement

fun isAsync() : Boolean
}
