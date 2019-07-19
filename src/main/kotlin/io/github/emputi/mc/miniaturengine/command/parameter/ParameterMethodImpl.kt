package io.github.emputi.mc.miniaturengine.command.parameter

import io.github.emputi.mc.miniaturengine.apis.ParameterMethod
import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.PluginHandler
import io.github.emputi.mc.miniaturengine.event.EventArguments
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin
import java.lang.Exception
import java.lang.NullPointerException
import java.util.concurrent.ConcurrentLinkedQueue

open class ParameterMethodImpl : Command, PluginIdentifiableCommand, ParameterMethod, PluginHandler
{
    private var enabled : Boolean = true
    override fun setEnable(active: Boolean) {
        this.enabled = active
    }

    override fun setEnable(plugin: Bootstrapper?) {
        throw NotImplementedError("Please call this#setEnable(Boolean) to enable or disable.")
    }

    override fun isEnabled(): Boolean {
        return this.enabled
    }

    override fun getPlugin(): Plugin {
        if(this.parameterElement.getHandler() == null) {
            throw ParameterActionException("The plugin of parameter element is null, it guess the element is not enabled yet.")
        }
        return this.parameterElement.getHandler() as Plugin
    }

    companion object {
        private val methods0 : ConcurrentLinkedQueue<ParameterMethodImpl> = ConcurrentLinkedQueue()
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

    private var isActivated : Boolean = false
    fun isMethodActivated() : Boolean = this.isActivated

    override fun setActivate(active : Boolean) {
        this.isActivated = active
    }

    override fun getPermission(): String?
    {
        return this.parameterElement.getPermission().getPermission()
    }

    ove


    constructor(pea : ParameterElement, commandName : String, async : Boolean) : super(commandName) {
        this.parameterElement = pea
        this.isAsync = async
    }

    @Volatile private var executeResult : Any? = null
    override fun getResult() : Any? = this.executeResult

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean
    {
        if(!this.isActivated) {
            throw ParameterActionException("This ParameterMethodImpl is not activated, Please enable for use.")
        }
        return try {
            this.executeResult = this.parameterMethodExecute(sender, EventArguments(sender, args.asList()))
            this.executeResult == null
        }
        catch(e : Exception) {
            e.printStackTrace()
            false
        }
    }

    protected open fun parameterMethodExecute(requestsClient : CommandSender, handleInstance : Any?) : Any?
    {
        val permission = this.parameterElement.getPermission()
        if(requestsClient is ConsoleCommandSender) {
            Bukkit.getConsoleSender().sendMessage("You tried to execute parameter method, " +
                    "But It maybe not supported on Console. Only debug.")
        }

        if(!requestsClient.hasPermission(permission.getPermission())) {
            // TODO("Need to implement for doing about player has no permission.")
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
        result = 31 * result + isActivated.hashCode()
        result = 31 * result + (executeResult?.hashCode() ?: 0)
        return result
    }
}