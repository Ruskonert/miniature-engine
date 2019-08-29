package io.github.emputi.mc.miniaturengine

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.impl.CommandProcessorProxy
import io.github.emputi.mc.miniaturengine.command.impl.ParameterMethodProxy
import io.github.emputi.mc.miniaturengine.configuration.command.ClearConsoleCommand
import io.github.emputi.mc.miniaturengine.example.CommandExample
import io.github.emputi.mc.miniaturengine.example.CommandExample2

class Application : Bootstrapper()
{
    override fun startApplication(handleInstance: Any?) {
        val c1  = CommandExample()
        val c2  = CommandExample2()
        val c3  = ClearConsoleCommand()
        c1.medicateCommand()
        c2.medicateCommand()
        c3.medicateCommand()

        // Running engine for constantly
        val a = ParameterMethodProxy()
        a.activePlugin = this
        a.parameterMethodProxy0()

        val b = CommandProcessorProxy()
        b.activePlugin = this
        b.commandProcessorProxy0()
    }
}