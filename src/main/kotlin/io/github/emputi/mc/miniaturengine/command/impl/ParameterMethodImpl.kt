package io.github.emputi.mc.miniaturengine.command.impl

import io.github.emputi.mc.miniaturengine.apis.ParameterMethod
import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.parameter.ParameterActionException
import io.github.emputi.mc.miniaturengine.command.parameter.ParameterElement
import io.github.emputi.mc.miniaturengine.event.EventArguments
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentLinkedQueue

open class ParameterMethodImpl : Command, PluginIdentifiableCommand, ParameterMethod
{
    private var _handlerLastConnected : Bootstrapper? = null
    private var handler : Bootstrapper? = null
    fun getHandler() : Bootstrapper? = this.handler

    override fun setEnable(active: Boolean)
    {
        if(active)
        {
            if(this.isEnabled()) {
            }
            else {
                if(_handlerLastConnected == null) {
                    if(this.handler == null)
                        throw ParameterActionException("Please set bootstrapper if want to enable it.")
                }
                else {
                    this.handler = this._handlerLastConnected
                    this._handlerLastConnected = null
                }
            }
        }
        else
        {
            if(this.handler != null) {
                this._handlerLastConnected = this.handler!!
                this.handler = null
            }
            else {
                Bukkit.getConsoleSender().sendMessage("Already disabled")
            }
        }
    }

    override fun setEnable(plugin: Bootstrapper?) {
        this.handler = plugin
        this.setEnable(this.handler != null)
    }

    override fun isEnabled(): Boolean {
        return this.handler != null
    }

    override fun getPlugin(): Plugin {
        if(this.getHandler() == null) {
            throw ParameterActionException("The plugin of parameter element is null, it guess the element is not enabled yet.")
        }
        return this.getHandler() as Plugin
    }

    companion object {
        private val methods0 : ConcurrentLinkedQueue<ParameterMethodImpl> = ConcurrentLinkedQueue()
        private val unloadedMethods0 : ConcurrentLinkedQueue<ParameterMethodImpl> = ConcurrentLinkedQueue()
        fun getUnloadedMethods() : ConcurrentLinkedQueue<ParameterMethodImpl> = unloadedMethods0
        fun getRegistryMethods() : ConcurrentLinkedQueue<ParameterMethodImpl> = methods0
        fun queueActivateImpl(pmi : ParameterMethodImpl) { methods0.add(pmi) }
        fun isMedicated(target : ParameterElement) : ParameterMethodImpl? {
            for(v in methods0) {
                if(v.parameterElement == target) return v
            }
            return null
        }
    }

    private val parameterElement : ParameterElement
    override fun getParameterElement() : ParameterElement {
        return this.parameterElement
    }

    private var isAsync : Boolean = true
    override fun isAsync(): Boolean {
        return this.isAsync
    }

    override fun getPermission(): String?
    {
        return this.parameterElement.getPermission().getPermission()
    }

    @Suppress("LeakingThis")
    constructor(pea : ParameterElement, commandName : String, async : Boolean) : super(commandName) {
        this.parameterElement = pea
        this.isAsync = async
        this.description = "The parameter method for command: ParameterMethod[${pea.getParameterName()}@${this}]"
        this.permission = this.parameterElement.getPermission().getPermission()
    }

    @Volatile private var executeResult : Any? = null
    override fun getResult() : Any? = this.executeResult

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean
    {
        val method0 = fun() : Boolean {
            if (!this.isEnabled()) {
                throw ParameterActionException("This ParameterMethodImpl is not activated, Please enable for use.")
            }
            return try {
                this.executeResult = this.parameterMethodExecute(sender, EventArguments(sender, args.asList()))
                this.executeResult == null
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        if(this.isAsync) return method0()
        else synchronized(this) { return method0() }
    }

    protected fun parameterMethodExecute(requestsClient : CommandSender, handleInstance : Any?) : Any?
    {
        val permission = this.parameterElement.getPermission()
        if(requestsClient is ConsoleCommandSender) {
            Bukkit.getConsoleSender().sendMessage("You tried to execute parameter method, " +
                    "But It maybe not supported on Console. Only debug.")
        }

        // If the client has no permission of this command
        if(!requestsClient.hasPermission(permission.getPermission())) {
            requestsClient.sendMessage("You have not the permission of this command: ${permission.getPermission()}")
            return false
        }

        val handleInstanceArgument : EventArguments? = handleInstance as? EventArguments
        val function = this.parameterElement.getAction()
            ?: throw NullPointerException("The parameter function must be non-null!")

        val callbackResult = function.proxyExecute(handleInstanceArgument!!)
        if(isAsync) return callbackResult
        return callbackResult.getReturnAwait()
    }

    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(other !is ParameterMethodImpl) return false
        return (this.parameterElement == other.parameterElement) && (this.name == other.name)
    }

    override fun hashCode(): Int {
        var result = parameterElement.hashCode()
        result = 31 * result + isAsync.hashCode()
        result = 31 * result + (executeResult?.hashCode() ?: 0)
        return result
    }
}