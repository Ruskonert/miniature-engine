package io.github.emputi.mc.miniaturengine.event.command

import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElementAction
import org.bukkit.entity.Player

class ParameterClickEvent : ParameterEvent
{
    var player : Player
    constructor(player : Player, parameterFunction : ParameterElementAction) : super(parameterFunction) {
        this.player = player
    }
}