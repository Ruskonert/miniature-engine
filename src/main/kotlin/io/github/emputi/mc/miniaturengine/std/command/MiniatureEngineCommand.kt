package io.github.emputi.mc.miniaturengine.std.command

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.policy.Permission

class MiniatureEngineCommand : CommandProcessor("miniaturengine", permissionBased = true) {

    private val clearConsoleCommand = ClearConsoleCommand()
    private val reloadCommand = ReloadCommand()
    private val navigateCommand = NavigateCommand()
    private val statusCommand = StatusCommand()
    init {
        this.alias += "mengine"
        this.alias += "miniature"
        this.permission = Permission("miniaturengine")
        this.executeConsole = true

        this.addChild(clearConsoleCommand)
        this.addChild(reloadCommand)
        this.addChild(navigateCommand)
        this.addChild(statusCommand)
    }
}
