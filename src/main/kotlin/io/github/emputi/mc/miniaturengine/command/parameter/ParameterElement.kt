package io.github.emputi.mc.miniaturengine.command.parameter

import io.github.emputi.mc.miniaturengine.command.PluginHandler
import io.github.emputi.mc.miniaturengine.apis.ParameterMethod
import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.configuration.ConfigurationException
import io.github.emputi.mc.miniaturengine.configuration.command.ParameterConfiguration
import io.github.emputi.mc.miniaturengine.policy.Permission

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent

open class ParameterElement : PluginHandler
{
    private var handler : Bootstrapper? = null
    fun getHandler() : Bootstrapper? = this.handler

    override fun setEnable(active: Boolean) {
        if(active) {

        }
        else {

        }

    }

    override fun setEnable(plugin: Bootstrapper?) {
        this.handler = plugin
        this.setEnable(this.handler != null)
    }

    override fun isEnabled(): Boolean {
        return this.handler != null
    }

    private val parameterElementFunc0 : (ParameterElementAction) -> Unit = fun(element : ParameterElementAction) {
        val field = element::class.java.getDeclaredField("parameterElement")
        field.isAccessible = true
        field.set(element, this)
    }

    private var onClickFunction : ParameterElementAction? = null
    fun getAction() : ParameterElementAction? = this.onClickFunction
    fun setAction(pea : ParameterElementAction) {
        this.onClickFunction = pea
        this.parameterElementFunc0(this.onClickFunction!!)
    }

    private val description : List<String> = ArrayList()
    fun getDescription() : List<String> = this.description

    private var permission : Permission
    fun getPermission() : Permission = this.permission

    private val parameterName : String
    private var isOptional : Boolean = true
    fun setIsOptional(isOptional : Boolean) {
        this.isOptional = isOptional
    }

    constructor(name : String)
    {
        this.parameterName = name
        this.permission = Permission(this.parameterName)
    }

    constructor(name : String, permission : Permission)
    {
        this.parameterName = name
        this.permission = permission
    }

    fun mediateParameterFunction() : ParameterMethod {
        val medicatedMethodImpl = ParameterMethodImpl.isMedicated(this)
        return if(medicatedMethodImpl == null) {
            val pmi = ParameterMethodImpl(this, this.onClickFunction!!.getFunctionId(), true)
            ParameterMethodImpl.queueActivateImpl(pmi)
            pmi
        } else {
            medicatedMethodImpl
        }
    }


    @Suppress("LeakingThis")
    constructor(name : String, permission : Permission, function : ParameterElementAction) {
        this.parameterName = name
        this.permission = permission
        this.onClickFunction = function
        this.parameterElementFunc0(this.onClickFunction!!)
    }

    fun getTextComponent() : TextComponent {
        val function0 = this.onClickFunction
        val configuration = ParameterConfiguration.configurationInst()

        val paramDisplayFormat = if(this.isOptional) {
            configuration.getAttribute("Parameter.Format.Optional")
        }
        else {
            configuration.getAttribute("Parameter.Format.Requirement")
        } ?: throw ConfigurationException("Check the attributes: Parameter.Format.*")

        val stringComponent = TextComponent(paramDisplayFormat.format(this.parameterName))
        if(function0 != null) {
            // Add the function of click-function, which can be callback.
            stringComponent.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, function0.getFunctionId())
        }

        // Add the description of parameter.
        // stringComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, <Description>)
        /*
         * TODO("This method not made completely yet.")
         */
        return stringComponent
    }

    override fun equals(other: Any?): Boolean {
        if(other !is ParameterElement) return false
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        if (onClickFunction != other.onClickFunction) return false
        if (description != other.description) return false
        if (permission != other.permission) return false
        if (parameterName != other.parameterName) return false
        if (isOptional != other.isOptional) return false
        return true
    }

    override fun hashCode(): Int {
        var result = onClickFunction?.hashCode() ?: 0
        result = 31 * result + description.hashCode()
        result = 31 * result + permission.hashCode()
        result = 31 * result + parameterName.hashCode()
        result = 31 * result + isOptional.hashCode()
        return result
    }
}