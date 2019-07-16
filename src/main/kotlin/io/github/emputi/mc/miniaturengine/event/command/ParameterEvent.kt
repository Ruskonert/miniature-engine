package io.github.emputi.mc.miniaturengine.event.command

import io.github.emputi.mc.miniaturengine.command.parameter.ParameterElementAction
import io.github.emputi.mc.miniaturengine.event.AbstractEvent
import org.bukkit.event.HandlerList

open class ParameterEvent(var parameterFunction: ParameterElementAction) : AbstractEvent()
{
    override fun getHandlers(): HandlerList {
        return handlerList
    }
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() : HandlerList {
            return handlerList
        }
    }

}
