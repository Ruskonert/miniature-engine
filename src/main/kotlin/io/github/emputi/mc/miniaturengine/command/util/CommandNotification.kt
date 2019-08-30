package io.github.emputi.mc.miniaturengine.command.util

import io.github.emputi.mc.miniaturengine.command.CommandProcessorDelegate
import io.github.emputi.mc.miniaturengine.command.parameter.argument.util.ArgumentDisplayElement

typealias ArgumentDisplaySplit = List<ArgumentDisplayElement>
class CommandNotification
{
    private val argumentNotificationList : HashMap<CommandProcessorDelegate, ArgumentDisplaySplit> = HashMap()
    operator fun set(key : CommandProcessorDelegate, value : ArgumentDisplaySplit) {
        this.argumentNotificationList[key] = value
    }
}

