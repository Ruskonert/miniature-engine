package io.github.emputi.mc.miniaturengine.apis

import io.github.emputi.mc.miniaturengine.command.parameter.ParameterElement

interface ParameterMethod
{
    fun setActivate(active: Boolean)
    fun getResult(): Any?
    fun getParameterElement(): ParameterElement
}
