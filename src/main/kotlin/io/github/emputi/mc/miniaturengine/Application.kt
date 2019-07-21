package io.github.emputi.mc.miniaturengine

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.impl.ParameterMethodProxy
import io.github.emputi.mc.miniaturengine.example.CommandExample

class Application : Bootstrapper()
{
    override fun startApplication(handleInstance: Any?) {
        val ce = CommandExample()
        ParameterMethodProxy().setEnable(this)
    }
}