package io.github.emputi.mc.miniaturengine

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.parameter.ParameterMethodProxy
import io.github.emputi.mc.miniaturengine.example.CommandExample

class Application : Bootstrapper()
{
    override fun startApplication(handleInstance: Any?) {
        val commandExample = CommandExample()
        val parameterMethodProxy = ParameterMethodProxy()
        parameterMethodProxy.executeTask(this)
    }
}