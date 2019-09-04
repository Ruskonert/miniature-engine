package io.github.emputi.mc.miniaturengine.std.command

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.communication.MfeDataDeliver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class StatusCommand : CommandProcessor("status")
{
    override fun invoke(
        sender: CommandSender,
        args: List<CommandArgument>,
        optionalArgs: List<CommandOptionalArgument>,
        defaultArgs: CommandDefaultArgument
    ): Boolean {
        val threadId = Thread.currentThread().id
        Bukkit.getConsoleSender().sendMessage("Thread id: 0x$threadId")
        val publicKey : String
        synchronized(this) {
            publicKey = MfeDataDeliver.Util.getPublicKey()
        }
        Bukkit.getConsoleSender().sendMessage("Server MFE_UniqueId: $publicKey")
        return true
    }
}
