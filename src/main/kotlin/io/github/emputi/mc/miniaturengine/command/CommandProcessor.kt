package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.impl.CommandProcessorImpl
import io.github.emputi.mc.miniaturengine.command.parameter.*
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
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
    constructor(command : String, delegate : Bootstrapper = Bootstrapper.BootstrapperBase!!) : this(command, delegate, null)
    constructor(command : String, delegate: Bootstrapper = Bootstrapper.BootstrapperBase!!, alias: List<String>? = null) {
        this.command = command
        this.delegate = delegate
        this.permission = Permission("${this.delegate.name}.$command")
        if(alias != null) this.alias.addAll(alias)
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

    protected val alias : MutableList<String> = ArrayList()
    fun getAliases(): MutableList<String> = this.alias

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
        val args : ArrayList<CommandArgument> = ArrayList()
        val args2 : ArrayList<CommandOptionalArgument> = ArrayList()
        @Suppress("RedundantExplicitType")
        var args3 : CommandDefaultArgument = CommandDefaultArgument()

        if(arguments.isEmpty()) {
            if(this.child.isNotEmpty()) {
                // It needs to output the child commands.
                throw CommandException("It needs to output the child commands :)")
            }
            else {
                if(argumentConfiguration.size != 0) {
                    if(this.usingNamedArgument) {
                        for(ac in argumentConfiguration) {
                            if(! ac.isOptional) {
                                sender.sendMessage("the parameter '${ac.getParameterName()}' is required value")
                                return false
                            }
                        }
                    }
                    else {
                        val chkFirstRequirement = argumentConfiguration[0]
                        if(! chkFirstRequirement.isOptional) {
                            sender.sendMessage("the parameter '${chkFirstRequirement.getParameterName()}' is required value")
                            return false
                        }
                    }
                }
                return this.invoke(sender, args, args2, args3)
            }
        }

        val matched = this.scarchChildCommand(arguments[0])
        if(matched != null) return matched.execute(sender, arguments.slice(IntRange(1, arguments.lastIndex)))
        val commandParameters = this.internalExecuteConfiguration(sender, arguments.toMutableList())
        if(commandParameters.isNotEmpty()) {
            for (cp in commandParameters) {
                when (cp) {
                    is CommandArgument -> args.add(cp)
                    is CommandOptionalArgument -> args2.add(cp)
                    is CommandDefaultArgument -> args3 = cp
                }
            }
        }
        return this.invoke(sender, args, args2, args3)
    }

    fun medicateCommand() : CommandProcessorImpl {
        val medicatedMethodImpl = CommandProcessorImpl.isMedicated(this)
        if(medicatedMethodImpl == null) {
            val commandProcessorImpl = CommandProcessorImpl(this.command, this)
            CommandProcessorImpl.queueActivateImpl(commandProcessorImpl)
            return commandProcessorImpl
        }
        else return medicatedMethodImpl
    }

    protected open fun invoke(sender : CommandSender, args: List<CommandArgument>, optionalArgs: List<CommandOptionalArgument>,
                              defaultArgs : CommandDefaultArgument) : Boolean {
        throw NotImplementedError("stub!")
    }

    private fun indicesQuoteOf(sender : CommandSender, current : String, arguments: MutableList<String>) : String {
        var next = arguments[1]
        if (current.lastIndex != 1) {
            val stringToken = Regex("^\".*")
            if (stringToken.matches(next)) {
                arguments.remove(current)
                val stringEndToken = Regex(".*\"$")
                while (true) {
                    val target = arguments[1]
                    if (!Validator.validateIsNotConfigureNaming(target)) {
                        sender.sendMessage("§cDouble quotes opened, But next is the configure value -> '$next'")
                        throw CommandParameterException("Double quotes opened, But next is the configure value")
                    }
                    next += " $target"
                    if (stringEndToken.matches(target)) {
                        arguments.remove(target)
                        break
                    }
                    else {
                        if (arguments.lastIndex == 2) {
                            sender.sendMessage("§cStarted with double quotes, but didn't find the quote of end -> '$next'")
                            throw CommandParameterException("mismatching about end of string \"")
                        }
                        arguments.remove(target)
                    }
                }
                next = next.removePrefix("\"")
                next = next.removeSuffix("\"")
            } else {
                arguments.remove(next)
            }
        } else {
            arguments.remove(next)
        }
        return next
    }

    private fun internalExecuteConfiguration(sender: CommandSender, arguments : MutableList<String>) : List<ICommandParameter<*>>
    {
        val defaultArguments = CommandDefaultArgument()
        val processedCommandArguments = ArrayList<ICommandParameter<*>>()
        if(argumentConfiguration.isNotEmpty()) {
            if(this.usingNamedArgument) {
                while(arguments.size != 0) {
                    val argument = arguments.first()
                    val isArgumentName = Validator.validateArgumentNaming(argument)

                    if(isArgumentName) {
                        var next = ""
                        val matchedParameterElement = this.matchArgumentFromName(argument)
                            ?: throw CommandException("Not found '$argument', are you sure configured this named parameter element?")
                        if(arguments.indexOf(argument) == arguments.lastIndex) {
                            if(matchedParameterElement.isOptional) {
                                sender.sendMessage("§eWarning: You entered the argument -> $argument, value is null but is optional. skipping")
                            }
                            else {
                                sender.sendMessage("§cYou entered the argument -> '$argument', but that requires value!")
                                throw CommandException("$argument -> requires value")
                            }
                        }
                        else {
                            next = indicesQuoteOf(sender, argument, arguments)
                        }

                        if(Validator.validateIsNotConfigureNaming(next)) {
                            processedCommandArguments.add(CommandArgument(matchedParameterElement, next))
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
                        if(isOptionalName) processedCommandArguments.add(
                            CommandOptionalArgument(
                                argument.substring(2)
                            )
                        )
                        else defaultArguments.addValue(argument)
                        arguments.remove(argument)
                    }
                }
            }
            else {
                for((index, value) in this.argumentConfiguration.withIndex()) {
                    if(index > arguments.lastIndex) {
                        if(value.isOptional) processedCommandArguments.add(
                            CommandArgument(
                                value,
                                ""
                            )
                        )
                        else {
                            throw CommandParameterException("the parameter element must non-null because of not optional")
                        }
                    }
                    else {
                        processedCommandArguments.add(
                            CommandArgument(
                                value,
                                indicesQuoteOf(sender, arguments[index], arguments)
                            )

                        )
                    }
                }
            }
        }
        else {
            for(value in arguments) {
                defaultArguments.addValue(value)
            }
        }
        processedCommandArguments.add(defaultArguments)
        return processedCommandArguments
    }

    private fun matchArgumentFromName(value : String) : ParameterElement? {
        val argumentName = value.removePrefix("-")
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
            fun validateIsNotConfigureNaming(value : String) : Boolean = !validateOptionalNaming(value) and !validateArgumentNaming(value)
            fun validateArgumentNaming(value : String) : Boolean = value.startsWith("-") and !this.validateOptionalNaming(value)
            fun validateOptionalNaming(value : String) : Boolean = value.startsWith("--")
        }
    }
}