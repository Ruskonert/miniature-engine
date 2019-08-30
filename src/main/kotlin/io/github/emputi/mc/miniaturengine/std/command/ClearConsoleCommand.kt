package io.github.emputi.mc.miniaturengine.std.command

import io.github.emputi.mc.miniaturengine.application.FrameApplicationManager
import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.communication.MfeDataDeliver
import org.bukkit.command.CommandSender

class ClearConsoleCommand : CommandProcessor("clearscreen")
{
    override fun invoke(
        sender: CommandSender,
        args: List<CommandArgument>,
        optionalArgs: List<CommandOptionalArgument>,
        defaultArgs: CommandDefaultArgument
    ): Boolean {
        //FrameApplicationManager.Util.clearConsoleWindow()
        val testIsSuccess = MfeDataDeliver.Util.isValidMfeChecksum("Hello world")
        return testIsSuccess
    }
}
