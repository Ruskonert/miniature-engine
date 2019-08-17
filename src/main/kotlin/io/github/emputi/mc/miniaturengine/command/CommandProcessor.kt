package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.parameter.*
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement
import io.github.emputi.mc.miniaturengine.policy.Permission
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

abstract class CommandProcessor : ICommandParameter<CommandProcessor>
{
    private val argumentConfiguration : ArrayList<ParameterElement> = ArrayList()
    fun addParameterOfArgument(parameterElement: ParameterElement) {
        parameterElement.getPermission().setBasePermission(this.permission)
        argumentConfiguration.add(parameterElement)
    }
    fun addParameterOfArgument(index : Int, parameterElement: ParameterElement) {
        parameterElement.getPermission().setBasePermission(this.permission)
        this.argumentConfiguration[index] = parameterElement
    }

    private val delegate : Bootstrapper
    fun getDelegate() : Bootstrapper = this.delegate

    private val command : String
    constructor(command : String, delegate : Bootstrapper) : this(command, delegate, null)
    constructor(command : String,  delegate: Bootstrapper, vararg alias: String?) {
        this.command = command
        this.delegate = delegate
        this.permission = Permission("${this.delegate.name}.$command")
        this.alias = alias.toMutableList()
    }

    private var _previous0 : CommandProcessor? = null
    private val child : MutableList<CommandProcessor> = ArrayList()
    protected fun addChild(command : CommandProcessor) {
        val command0 = command
        command0._previous0 = this
        val permission = command0.permission
        permission.setBasePermission(this.permission)
        this.child.add(command0)
    }

    final override fun getParameterName(): String = this.command
    final override fun getValue(): CommandProcessor = this

    protected val alias : MutableList<String?>
    fun getAliases(): MutableList<String?> = this.alias

    protected var permission : Permission; fun getCommandPermission() : Permission = this.permission
    protected var usingNamedArgument : Boolean = false
    protected var executeConsole : Boolean = true
    fun execute(sender: CommandSender, arguments: List<String>) : Boolean
    {
        if(argumentConfiguration.size != 0 && child.isNotEmpty()) {
            throw CommandException("invalid configuration, this command has parameter argument(s) but also there's child command(s).")
        }
        if(sender is ConsoleCommandSender) {
            if (!executeConsole) {
                // Oh no, this command only for player, not console.
                throw CommandException("This command does not supported on console.")
            }
        }
        val matched = this.scarchChildCommand(arguments[0])
        if(matched != null) return matched.execute(sender, arguments.slice(IntRange(1, arguments.lastIndex)))
        val commandParameters = this.internalExecuteConfiguration(arguments.toMutableList())
        val args : ArrayList<CommandArgument> = ArrayList()
        val args2 : ArrayList<CommandOptionalArgument> = ArrayList()
        val args3 : ArrayList<CommandDefaultArgument> = ArrayList()
        if(commandParameters.isNotEmpty()) {
            for (cp in commandParameters) {
                when (cp) {
                    is CommandArgument -> args.add(cp)
                    is CommandOptionalArgument -> args2.add(cp)
                    is CommandDefaultArgument -> args3.add(cp)
                }
            }
        }
        return this.invoke(sender, args, args2, args3)
    }

    open fun invoke(sender : CommandSender, args: List<CommandArgument>, optionalArgs: List<CommandOptionalArgument>,
                    defaultArgs : List<CommandDefaultArgument>) : Boolean {
        throw NotImplementedError("stub!")
    }

    private fun internalExecuteConfiguration(arguments : MutableList<String>) : List<ICommandParameter<*>>
    {
        val defaultArguments = CommandDefaultArgument()
        val processedCommandArguments = ArrayList<ICommandParameter<*>>()
        if(argumentConfiguration.isNotEmpty()) {
            if(this.usingNamedArgument) {
                while(arguments.size != 0) {
                    val argument = arguments.first()
                    val isArgumentName = Validator.validateArgumentNaming(argument)
                    val matchedParameterElement = this.matchArgumentFromName(argument)
                        ?: throw CommandException("Not found '$argument', are you configured this named parameter element?")

                    if(isArgumentName) {
                        val next = arguments[1]
                        if(!Validator.validateOptionalNaming(next) and !Validator.validateArgumentNaming(next)) {
                            processedCommandArguments.add(
                                CommandArgument(
                                    matchedParameterElement,
                                    next
                                )
                            )
                            arguments.remove(next)
                        }
                        else {
                            /*
                             Oh no! the matched value of argument is null. it may causes exception like entered this command:
                             /command -arg1 -arg2 "hello world" // where is value of "arg1"?? arg1 is not optional argument.
                             */
                            // Warning: the matched value of this argument is null.
                            processedCommandArguments.add(
                                CommandArgument(
                                    matchedParameterElement, ""
                                )
                            )
                        }
                        arguments.remove(argument)
                    }
                    else {
                        val isOptionalName = Validator.validateOptionalNaming(argument)
                        if(isOptionalName) processedCommandArguments.add(CommandOptionalArgument(argument.substring(2)))
                        else defaultArguments.addValue(argument)
                        arguments.remove(argument)
                    }
                }
            }
            else {
                for((index, value) in this.argumentConfiguration.withIndex()) {
                    if(index >= arguments.lastIndex) {
                        if(value.isOptional) processedCommandArguments.add(CommandArgument(value, ""))
                        else {
                            throw CommandParameterException("the parameter element must non-null because of not optional")
                        }
                    }
                    else processedCommandArguments.add(CommandArgument(value, arguments[index]))
                }
            }
            processedCommandArguments.add(defaultArguments)
        }
        else {
            for(value in this.argumentConfiguration) {
                processedCommandArguments.add(CommandArgument(value, ""))
            }
        }
        return processedCommandArguments
    }

    private fun matchArgumentFromName(value : String) : ParameterElement? {
        val argumentName = value.removeRange(IntRange(0, 1))
        for(v in argumentConfiguration) {
            if(argumentName == v.getParameterName()) return v
        }
        return null
    }

    private fun scarchChildCommand(value : String) : CommandProcessor? {
        if(this.child.isEmpty()) return null
        for(child in this.child) {
            if(child.command == value || child.alias.contains(value)) return child
        }
        return null
    }

    sealed class Validator {
        companion object {
            fun validateArgumentNaming(value : String) : Boolean = value.startsWith("-") and !this.validateOptionalNaming(value)
            fun validateOptionalNaming(value : String) : Boolean = value.startsWith("--")
        }
    }
}