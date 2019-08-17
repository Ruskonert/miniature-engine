package io.github.emputi.mc.miniaturengine.command.parameter

interface ICommandParameter<V> {
    fun getParameterName() : String
    fun getValue() : V
}