package io.github.emputi.mc.miniaturengine.event.command

import io.github.emputi.mc.miniaturengine.command.parameter.ParameterElementAction
import org.bukkit.entity.Player

class ParameterHoverEvent : ParameterEvent
{
    var player : Player
    constructor(player : Player, parameterFunction : ParameterElementAction) : super(parameterFunction) {
        this.player = player
    }
}