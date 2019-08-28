package io.github.emputi.mc.miniaturengine

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.impl.CommandProcessorProxy
import io.github.emputi.mc.miniaturengine.command.impl.ParameterMethodProxy
import io.github.emputi.mc.miniaturengine.example.CommandExample
import io.github.emputi.mc.miniaturengine.example.CommandExample2

class Application : Bootstrapper()
{
    override fun startApplication(handleInstance: Any?) {
        CommandExample()
        CommandExample2()
        var a = ParameterMethodProxy()
        a.activePlugin = this
        a.parameterMethodProxy0()

        var b = CommandProcessorProxy()
        b.activePlugin = this
        b.commandProcessorProxy0()
    }
}