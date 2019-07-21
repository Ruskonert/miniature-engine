package io.github.emputi.mc.miniaturengine.command.parameter

import io.github.emputi.mc.miniaturengine.IString
import io.github.emputi.mc.miniaturengine.apis.ParameterMethod
import io.github.emputi.mc.miniaturengine.command.TextComponentUtil
import io.github.emputi.mc.miniaturengine.command.impl.ParameterMethodImpl
import io.github.emputi.mc.miniaturengine.configuration.ConfigurationException
import io.github.emputi.mc.miniaturengine.configuration.command.ParameterConfiguration
import io.github.emputi.mc.miniaturengine.policy.Permission
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent

open class ParameterElement : IString
{
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

    private var description : MutableList<String> = ArrayList()
    fun getDescription() : MutableList<String> = this.description
    fun setDescription(list : MutableList<String>) {
        this.description = list
    }
    fun setDescription(index : Int, string : String) {
        this.description[index] = string
    }

    private var permission : Permission
    fun getPermission() : Permission = this.permission

    private val parameterName : String
    fun getParameterName() : String = this.parameterName

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

    constructor(name : String, permission : Permission, function : ParameterElementAction) {
        this.parameterName = name
        this.permission = permission
        this.onClickFunction = function
        this.parameterElementFunc0(this.onClickFunction!!)
    }

    override fun getTextComponent() : TextComponent
    {
        val configuration = ParameterConfiguration.configurationInst()
        val paramDisplayFormat = if(this.isOptional) {
            configuration.getAttribute("Parameter.Format.Optional")
        }
        else {
            configuration.getAttribute("Parameter.Format.Requirement")
        } ?: throw ConfigurationException("Check the attributes: Parameter.Format.*")

        val stringComponent = TextComponent(paramDisplayFormat.format(this.parameterName))
        this.applyClickFunction(stringComponent)
        this.applyHoverFunction(stringComponent)
        return stringComponent
    }

    open fun applyClickFunction(target : TextComponent) {
        val function0 = this.onClickFunction
        if(function0 != null) {
            // Add the function of click-function, which can be callback.
            target.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, function0.getFunctionId())
        }
    }

    open fun applyHoverFunction(target : TextComponent) {
        if(this.description.isNotEmpty()) {
            target.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentUtil.completeString(this.description))
        }
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