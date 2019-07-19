package io.github.emputi.mc.miniaturengine.command.parameter

import io.github.emputi.mc.miniaturengine.thread.PluginCallback
import io.github.emputi.mc.miniaturengine.event.EventArguments
import io.github.emputi.mc.miniaturengine.event.command.ParameterClickEvent
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.*

typealias ParameterFunction = (EventArguments?) -> Any?
open class ParameterElementAction
{
    constructor() {}

    constructor(clickAction: (EventArguments?) -> Any?) {
        this.actionableFunction = clickAction
    }

    /**
     *
     */
    private var actionableFunction : ParameterFunction? = null

    /**
     *
     */
    private var parameterElement : ParameterElement? = null
    fun getParameterElement() : ParameterElement? = this.parameterElement

    /**
     * Sets
     */
    fun setActionableFunction(function : ParameterFunction)
    {
        this.actionableFunction = function
    }

    /**
     * The function id which is implemented function of parameter that can be clickable.
     * That is command of executing this method.
     */
    private val functionId : String = UUID.randomUUID().toString().replace("-", "")

    /**
     * Returns id of function.
     * @return the function id
     */
    fun getFunctionId() : String = this.functionId

    /**
     * Executes assert method before the callback function is call.
     * @param sender
     * @param args
     */
    open fun proxyExecute(args : EventArguments): PluginCallback<EventArguments, Any?>
    {
        if(args.getClicker() is ConsoleCommandSender) {
            throw ParameterActionException("The click event of chat is not supported in console.")
        }
        val callback = this.execute0(args)
        this.debug("the event function of parameter was called")
        return callback
    }

    /**
     *
     */
    protected open fun debug(message : String)
    {
        Bukkit.getConsoleSender().sendMessage(message)
    }

    /**
     * Executes method that can be callback value.
     * @param args
     */
    private fun execute0(args : EventArguments) : PluginCallback<EventArguments, Any?>
    {
        val parameterEvent = ParameterClickEvent(args.getClicker() as Player, this)
        if(! parameterEvent.isCancelled) {
            parameterEvent.run()

            val function0 = parameterEvent.parameterFunction.actionableFunction
                ?: throw ParameterActionException("No construct actionable-function")
            return PluginCallback(function0, args)
        }
        else {
            throw ParameterActionException("The event of function is cancelled")
        }
    }
}