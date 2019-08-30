package io.github.emputi.mc.miniaturengine.std.command

import io.github.emputi.mc.miniaturengine.command.CommandProcessor
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.util.ArgumentDisplayElement
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement
import io.github.emputi.mc.miniaturengine.command.util.CommandNotification
import io.github.emputi.mc.miniaturengine.configuration.ParameterConfiguration
import io.github.emputi.mc.miniaturengine.policy.Permission
import org.bukkit.command.CommandSender

class NavigateCommand : CommandProcessor("help")
{
    init {
        this.alias += "?"
        val parameterElement = ParameterElement.createDelicateParameterElement("page(s)", isOption = true)
        parameterElement.setDescription(0, "&aThe navigate page of input page(s) number")
        this.addParameterOfArgument(parameterElement)
    }

    override fun invoke(sender: CommandSender, args: List<CommandArgument>, optionalArgs: List<CommandOptionalArgument>, defaultArgs: CommandDefaultArgument): Boolean {
        val command = this.getDelegateCommand()
        if(command == null) {
            // No provided any information about child command.
            return true
        }
        else {
            val notification = CommandNotification()
            val inst = ParameterConfiguration.configurationInst()

            // There's no arguments
            if(command.getChild().isEmpty())
            {
                val commandArgumentElement = investigateCommandArguments(command, sender)
                if(commandArgumentElement.isEmpty()) {
                    val temp = ArrayList<ArgumentDisplayElement>()
                    temp.add(ArgumentDisplayElement.create(inst, ParameterElement("", Permission("", command.getCommandPermission())), sender))
                    notification[command] = temp
                }
                else {
                    notification[command] = commandArgumentElement
                }
            }
            else {
                // add self (the navigate command of target)
                notification[this] = investigateCommandArguments(this, sender)

                // add child command
                for(c in this.getChild()) {
                    val temp = ArrayList<ArgumentDisplayElement>()
                    temp.add(ArgumentDisplayElement.create(inst, ParameterElement(c.getCommandName(), Permission("", c.getCommandPermission())), sender))
                    notification[command] = temp
                }
            }
        }
        // TODO will be use notification
        return true
    }
}
