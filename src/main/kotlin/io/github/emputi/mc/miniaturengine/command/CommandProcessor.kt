package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.PluginHandler
import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.impl.CommandProcessorImpl
import io.github.emputi.mc.miniaturengine.command.parameter.CommandParameterException
import io.github.emputi.mc.miniaturengine.command.parameter.ICommandParameter
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandDefaultArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.CommandOptionalArgument
import io.github.emputi.mc.miniaturengine.command.parameter.argument.util.ArgumentDisplayElement
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement
import io.github.emputi.mc.miniaturengine.communication.SerializableEntity
import io.github.emputi.mc.miniaturengine.configuration.ParameterConfiguration
import io.github.emputi.mc.miniaturengine.policy.Permission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

abstract class CommandProcessor : SerializableEntity, ICommandParameter<CommandProcessor>, PluginHandler, CommandProcessorDelegate
{
    override fun isEnabled(): Boolean {
        return true
    }

    override fun setEnable(active: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setEnable(plugin: Bootstrapper?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val argumentConfiguration : ArrayList<ParameterElement> = ArrayList()
    fun addParameterOfArgument(parameterElement: ParameterElement) {
        parameterElement.getPermission().setBasePermission(this.permission)
        argumentConfiguration.add(parameterElement)
    }
    fun addParameterOfArgument(index : Int, parameterElement: ParameterElement) {
        parameterElement.getPermission().setBasePermission(this.permission)
        this.argumentConfiguration[index] = parameterElement
    }

    private var delegate : Bootstrapper
    fun getDelegate() : Bootstrapper = this.delegate

    private val command : String
    fun getCommandName() : String = this.command

    constructor(command : String, delegate : Bootstrapper = Bootstrapper.BootstrapperBase!!) : this(command, delegate, null)
    constructor(command : String, delegate: Bootstrapper = Bootstrapper.BootstrapperBase!!, alias: List<String>? = null) {
        this.command = command
        this.delegate = delegate
        this.permission = Permission("${this.delegate.name}.$command")
        if(alias != null) this.alias.addAll(alias)
    }


    private var _previous0 : CommandProcessor? = null
    override fun getDelegateCommand(): CommandProcessor? {
        return this._previous0
    }

    private val child : MutableList<CommandProcessor> = ArrayList()
    fun getChild() : List<CommandProcessor> = this.child
    protected fun addChild(command : CommandProcessor) {
        val command0 = command
        command0._previous0 = this
        this.setPermissionForChild(command0)
        this.child.add(command0)
    }

    @Synchronized
    protected fun invokeChildCommand(
        commandId : String,
        sender : CommandSender,
        commandArgument: List<CommandArgument>,
        commandOptionalArgument: List<CommandOptionalArgument>,
        commandDefaultArgument: CommandDefaultArgument
    ) : Boolean {
        for(c in this.child) {
            if(c.command == commandId) {
                return c.invoke(sender, commandArgument, commandOptionalArgument, commandDefaultArgument)
            }
        }
        return false
    }

    private fun setPermissionForChild(command0 : CommandProcessor) {
        val permission = command0.permission
        permission.setBasePermission(this.permission)
    }

    final override fun getParameterName(): String = this.command
    final override fun getValue(): CommandProcessor = this

    protected val alias : MutableList<String> = ArrayList()
    fun getAliases(): MutableList<String> = this.alias

    protected var permission : Permission
    set(value) {
        field = value
        if(this.child.isNotEmpty()) {
            for(ch in this.child) {
                ch.permission.setBasePermission(field)
            }
        }
    }
    fun getCommandPermission() : Permission = this.permission


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
                return this.invoke0(sender, args, args2, args3)
            }
        }

        val matched = this.searchChildCommand(arguments[0])
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
        return this.invoke0(sender, args, args2, args3)
    }

    @Suppress("LiftReturnOrAssignment")
    fun medicateCommand() : CommandProcessorImpl {
        val medicatedMethodImpl = CommandProcessorImpl.isMedicated(this)
        if(medicatedMethodImpl == null) {
            val commandProcessorImpl = CommandProcessorImpl(this.command, this)
            CommandProcessorImpl.queueActivateImpl(commandProcessorImpl)
            return commandProcessorImpl
        }
        else return medicatedMethodImpl
    }

    private fun invoke0(sender : CommandSender, args: List<CommandArgument>, optionalArgs: List<CommandOptionalArgument>,
                              defaultArgs : CommandDefaultArgument) : Boolean {
        val mode : String = if(this.usingNamedArgument) "Named" else "Numeric"
        Bukkit.getConsoleSender().sendMessage("§a===================================")
        Bukkit.getConsoleSender().sendMessage("§aReferenced by: §d${this::class.java.name}")
        Bukkit.getConsoleSender().sendMessage("§aCommand mode: §e$mode")
        Bukkit.getConsoleSender().sendMessage("§a[The arguments information]")
        if(args.isNotEmpty()) {
            for(argument in args)
                Bukkit.getConsoleSender().sendMessage("§f[${argument.getParameterName()}] §e-> §9${argument.getArgument().second}")
        }
        else {
            Bukkit.getConsoleSender().sendMessage("§fNo provided about argument information!")
        }
        Bukkit.getConsoleSender().sendMessage("§a===================================")

        var strPermission = this.permission.getSubstantialPermission()

        if(this._previous0 == null) {
            if(! strPermission.contains(".")) {
                strPermission += ".help"
            }
        }

        if(!sender.hasPermission(strPermission)) {
            sender.sendMessage("§4You have not permission of this command: ${this.permission.getSubstantialPermission()}")
            return false
        }

        return this.invoke(sender, args, optionalArgs, defaultArgs)
    }

    protected open fun invoke(sender : CommandSender, args: List<CommandArgument>, optionalArgs: List<CommandOptionalArgument>,
                              defaultArgs : CommandDefaultArgument) : Boolean { return true }

    protected open fun invokeUnknown(sender : CommandSender, args: List<CommandArgument>, optionalArgs: List<CommandOptionalArgument>,
                              defaultArgs : CommandDefaultArgument) : Boolean { return true }

    protected open fun invokeWithoutArgument(sender : CommandSender) : Boolean { return true }

    protected open fun invokeInvalidArgument(sender : CommandSender, args: List<CommandArgument>, optionalArgs: List<CommandOptionalArgument>,
                                             defaultArgs : CommandDefaultArgument, causeOf : CommandParameterException) : Boolean { return true }

    companion object {
        /**
         * Examine the arguments of a command and replace it with an element that can be displayed on the screen.
         * @param command
         * @param observer
         * @return
         */
        fun investigateCommandArguments(
            command: CommandProcessor,
            observer: CommandSender = Bukkit.getConsoleSender()
        ): List<ArgumentDisplayElement> {
            val list = ArrayList<ArgumentDisplayElement>()
            val inst = ParameterConfiguration.configurationInst()

            if (command.argumentConfiguration.isNotEmpty()) {
                // It configures string format following like this:
                // /command <requirement's name> [arg1's name] [arg2's name]
                for (argument in command.argumentConfiguration) {
                    val displayElement = ArgumentDisplayElement.create(inst, argument, observer)
                    list.add(displayElement)
                }
            }
            return list
        }
    }

    private fun interpretStringDelimiter(sender : CommandSender, arguments: MutableList<String>) : String {
        var next = ""
        val stringToken = Regex("^\".*")
        if(arguments.isEmpty()) return next
        if(stringToken.matches(arguments[0])) {
            val stringEndToken = Regex(".*\"$")
            while (true) {
                val target = arguments[0]
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
                    if (arguments.lastIndex == arguments.indexOf(target)) {
                        sender.sendMessage("§cStarted with double quotes, but didn't find the quote of end -> '$next'")
                        throw CommandParameterException("mismatching about end of string \"")
                    }
                    arguments.remove(target)
                }
            }
            next = next.substring(2, next.lastIndex)
            return next
        }
        else {
            next = arguments[0]
            arguments.removeAt(0)
            return next
        }
    }

    private fun internalExecuteConfiguration(sender: CommandSender, arguments : MutableList<String>) : List<ICommandParameter<*>>
    {
        val defaultArguments = CommandDefaultArgument()
        val processedCommandArguments = ArrayList<ICommandParameter<*>>()
        var entireString = ""; arguments.forEach { entireString += " $it" }
        Bukkit.getConsoleSender().sendMessage("§f${sender.name} command §aexecuted: §f[${this.command}$entireString]")
        if(argumentConfiguration.isNotEmpty()) {
            if(this.usingNamedArgument) {
                while(arguments.size != 0) {
                    val argument = arguments.first()
                    val isArgumentName = Validator.validateArgumentNaming(argument)
                    if(isArgumentName) {
                        var next: String
                        val matchedParameterElement = this.matchArgumentFromName(argument)
                            ?: throw CommandException("Not found '$argument', are you sure configured this named parameter element?")
                        if(arguments.indexOf(argument) == argument.lastIndex) {
                            next = argument
                            arguments.remove(argument)
                            /*
                            if(matchedParameterElement.isOptional) {
                                sender.sendMessage("§eWarning: You entered the argument -> $argument, value is null but is optional. skipping")
                            }
                            else {
                                sender.sendMessage("§cYou entered the argument -> '$argument', but that requires value!")
                                throw CommandException("$argument -> requires value")
                            }
                             */
                        }
                        else {
                            next = interpretStringDelimiter(sender, arguments.subList(1, arguments.size))
                            arguments.remove(argument)
                        }


                        if(Validator.validateIsNotConfigureNaming(next)) {
                            val element = CommandArgument(matchedParameterElement, next)
                            for(pca in processedCommandArguments) {
                                if(pca is CommandArgument) {
                                    if(pca.getArgument().first == matchedParameterElement) {
                                        sender.sendMessage("§eWarning: Ignored previous value caused of duplicate argument name: §f[${matchedParameterElement.getParameterName()}]")
                                        processedCommandArguments.remove(pca)
                                        break
                                    }
                                }
                            }
                            processedCommandArguments.add(element)
                        }
                        else {
                            /*
                             Oh no! the matched value of argument is null. it may causes exception like entered this command:
                             /command -arg1 -arg2 "hello world" // where is value of "arg1"?? arg1 is not optional argument.
                             */
                            // Warning: the matched value of this argument is null.
                            sender.sendMessage("§eWarning: §fthe matched value of this argument is null.")
                            processedCommandArguments.add(
                                CommandArgument(
                                    matchedParameterElement, ""
                                )
                            )
                        }
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
                val argumentLastIndex = arguments.lastIndex
                for((index, value) in this.argumentConfiguration.withIndex()) {
                    if(index > argumentLastIndex) {
                        if(value.isOptional) processedCommandArguments.add(CommandArgument(value, ""))
                        else {
                            throw CommandParameterException("the parameter element must non-null because of not optional")
                        }
                    }
                    else {
                        processedCommandArguments.add(CommandArgument(value, interpretStringDelimiter(sender, arguments)))
                    }
                }
            }
        }
        else {
            for(value in arguments) {
                defaultArguments.addValue(value)
            }
        }
        if(usingNamedArgument) {
            val intrinsicsCheckNotMissing = fun() {
                val result = Validator.validateIsFilledAllRequirement(processedCommandArguments, argumentConfiguration)
                if (result.isNotEmpty()) {
                    for (reason in result) {
                        sender.sendMessage("§cYou missed the requirement argument: [${reason.getParameterName()}]")
                    }
                    throw CommandParameterException("Missed value of requirement argument(s)")
                }
            };intrinsicsCheckNotMissing()
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

    private fun searchChildCommand(value : String) : CommandProcessor? {
        if(this.child.isEmpty()) return null
        for(child in this.child) {
            if(child.command == value || child.alias.contains(value)) return child
        }
        return null
    }

    sealed class Validator {
        companion object {
            fun validateIsFilledAllRequirement(victim : Iterable<ICommandParameter<*>>, focus : MutableList<ParameterElement>) : Collection<ParameterElement> {
                val configuredElement : ArrayList<ParameterElement> = ArrayList()
                val specificFocus = focus.filterNot { it.isOptional }.toMutableList()
                val specificVictim = victim.filterIsInstance(CommandArgument::class.java)
                specificFocus.forEach {
                    for(value in specificVictim) {
                        if(it == value.getArgument().first) {
                            configuredElement.add(it)
                            break
                        }
                    }
                }
                specificFocus.removeAll(configuredElement)
                return specificFocus
        }
            fun validateIsNotConfigureNaming(value : String) : Boolean = !validateOptionalNaming(value) and !validateArgumentNaming(value)
            fun validateArgumentNaming(value : String) : Boolean = value.startsWith("-") and !this.validateOptionalNaming(value)
            fun validateOptionalNaming(value : String) : Boolean = value.startsWith("--")
        }
    }
}