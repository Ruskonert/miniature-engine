package io.github.emputi.mc.miniaturengine

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.impl.CommandProcessorProxy
import io.github.emputi.mc.miniaturengine.command.impl.ParameterMethodProxy
import io.github.emputi.mc.miniaturengine.std.command.ClearConsoleCommand
import io.github.emputi.mc.miniaturengine.example.CommandExample
import io.github.emputi.mc.miniaturengine.example.CommandExample2
import io.github.emputi.mc.miniaturengine.std.command.MiniatureEngineCommand

class Application : Bootstrapper()
{
    fun test() {
        val c1  = CommandExample()
        val c2  = CommandExample2()
        val c3  = ClearConsoleCommand()
        c1.medicateCommand()
        c2.medicateCommand()
        c3.medicateCommand()
    }

    override fun startApplication(handleInstance: Any?) {
        // this.test()
        MiniatureEngineCommand().medicateCommand()

        // Running engine for sustain service constantly
        val a = ParameterMethodProxy()
        a.activePlugin = this
        a.parameterMethodProxy0()

        val b = CommandProcessorProxy()
        b.activePlugin = this
        b.commandProcessorProxy0()
    }
}