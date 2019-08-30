package io.github.emputi.mc.miniaturengine.command.parameter.argument.util

import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement
import io.github.emputi.mc.miniaturengine.configuration.Attribute
import io.github.emputi.mc.miniaturengine.configuration.ParameterConfiguration
import org.bukkit.command.CommandSender

class ArgumentDisplayElement private constructor(var option : String, var permission : String, var description : String) {
    companion object {
        private const val PARAM_FORMAT_OPTIONAL = "Parameter.Format.Optional"
        private const val PARAM_FORMAT_REQUIREMENT = "Parameter.Format.Requirement"
        private const val PARAM_NO_PROVIDED_INFO = "Parameter.Format.NoProvidedDescription"

        fun create(
            inst : ParameterConfiguration,
            argument: ParameterElement,
            observer : CommandSender,
            description : List<String>? = null
        ) : ArgumentDisplayElement {
            val optionStr = if(argument.isOptional) {
                Attribute.writeLine(
                    inst.getAttribute(PARAM_FORMAT_OPTIONAL)
                        ?: throw NullPointerException("No configuration: $PARAM_FORMAT_OPTIONAL"),
                    argument.getParameterName()
                )
            } else {
                Attribute.writeLine(
                    inst.getAttribute(PARAM_FORMAT_REQUIREMENT)
                        ?: throw NullPointerException("No configuration: $PARAM_FORMAT_REQUIREMENT"),
                    argument.getParameterName()
                )
            }
            var permissionStr = argument.getPermission().getSubstantialPermission()
            permissionStr = if(argument.getPermission().hasPermission(observer)) {
                "&4$permissionStr"
            } else {
                "&a$permissionStr"
            }

            var descriptionStr = ""
            if(argument.getDescription().isEmpty()) {
                descriptionStr = inst.getAttribute(PARAM_NO_PROVIDED_INFO) ?: throw NullPointerException("No configuration: $PARAM_NO_PROVIDED_INFO")
            }
            else {
                for(str in argument.getDescription()) {
                    descriptionStr += str
                    if(argument.getDescription().lastIndex != argument.getDescription().lastIndexOf(str)) {
                        descriptionStr += str + "\n"
                    }
                }
            }
            return ArgumentDisplayElement(optionStr, permissionStr, descriptionStr)
        }
    }
}