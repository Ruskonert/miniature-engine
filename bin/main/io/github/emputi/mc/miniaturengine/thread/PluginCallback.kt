package io.github.emputi.mc.miniaturengine.thread

import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * PluginCallback can implements the callback-function on Bukkit-based plugin.
 * @author Ruskonert
 * @since 0.1.0
 */
open class PluginCallback<P, R> : Task<P,R>, Future<R?>
{
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        if(this.internalTaskScheduler == null) return false
        this.internalTaskScheduler!!.cancel()
        return true
    }

    @Deprecated("this method is not implemented, Use instead getReturnAwait", level = DeprecationLevel.ERROR)
    override fun get(timeout: Long, unit: TimeUnit): R? {
        throw NotImplementedError("Not implemented.")
    }

    override fun get(): R? {
        synchronized(this) {
            return this.return0
        }
    }

    override fun isDone(): Boolean {
        return !this.isUnexpected and !this.isRunning()
    }

    override fun isCancelled(): Boolean {
        if(this.internalTaskScheduler == null) return true
        return this.internalTaskScheduler!!.isCancelled
    }

    /**
     * An actual method in which the callback function is called.
     */
    private var taskMethodConstructor : (P?) -> R

    /**
     * The scheduler that responsible for the thread, which is mostly an object that runs on BukkitCore.
     * Bukkit-based plugins don't support external threads.
     * Therefore, It needs to manage threads using only this object.
     */
    private var scheduler : BukkitScheduler

    /**
     * A target that manage the bukkit plugin.
     */
    private var plugin : Plugin

    /**
     * A task id of processing thread.
     */
    private var taskId : Int = -1

    /**
     * The thread id created by implementing the Callback function.
     */
    private var internalTaskScheduler : BukkitTask? = null

    /**
     * Determines the thread is unexpectedly terminated  or occurred an exception.
     */
    private var isUnexpected : Boolean = false

    constructor(taskMethodConstructor: (P?) -> R, parameter: P?)
            : this(Bootstrapper.BootstrapperBase!!, Bukkit.getScheduler(), taskMethodConstructor, parameter)

    constructor(taskMethodConstructor: (P?) -> R)
            : this(Bootstrapper.BootstrapperBase!!, Bukkit.getScheduler(), taskMethodConstructor, null)

    constructor(plugin : Plugin, taskMethodConstructor: (P?) -> R)
            : this(plugin, plugin.server.scheduler, taskMethodConstructor, null)

    constructor(plugin : Plugin, taskMethodConstructor: (P?) -> R, parameter: P?)
            : this(plugin, plugin.server.scheduler, taskMethodConstructor, parameter)

    /**
     * Constructs an object that can be return the value with callback method-type.
     * @param plugin
     * @param scheduler
     * @param taskMethodConstructor
     * @param parameter
     */
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(plugin : Plugin, scheduler : BukkitScheduler, taskMethodConstructor : (P?) -> R, parameter : P?) {
        this.plugin = plugin
        this.scheduler = scheduler
        this.taskMethodConstructor = taskMethodConstructor

        this.setFunctionParameterObject(parameter)
        this.internalTask(this.getFunctionParameterObject())
    }

    override fun internalTask(functionParameter: P?) {
        val scheduler = this.scheduler
        val func = fun() { this.return0 = this.taskMethodConstructor(functionParameter) }
        this.runTaskMethod = func
        scheduler.runTask(this.plugin, fun() {
            this.internalTaskScheduler = scheduler.runTask(this.plugin, func)
            if(this.internalTaskScheduler == null) {
                debug("The task scheduler was not generated!"); this.isUnexpected = true; return
            }

            while(true) {
                if(this.internalTaskScheduler!!.isCancelled) {
                    this.isUnexpected = true
                    break
                }
                if(!scheduler.isCurrentlyRunning(this.internalTaskScheduler!!.taskId)) break
            }
        })
    }

    /**
     *
     * @param message
     */
    override fun debug(message : String) {
        Bukkit.getConsoleSender().sendMessage(message)
    }

    /**
     *
     * @param message
     * @param attributes
     */
    @Deprecated("attributes not implemented yet, Use instead PluginCallback#debug.")
    override fun debugWithAttribute(message : String, attributes : Any?) : Any? {
        this.debug(message)
        return null
    }

    /**
     * Returns true the callback function still running.
     * @return
     */
    override fun isRunning() : Boolean {
        if(this.taskId == -1) return false
        if(this.internalTaskScheduler == null) return false
        return this.scheduler.isCurrentlyRunning(this.taskId)
    }

    /**
     * Returns true the callback function was unexpectedly terminated or exit caused by unknown.
     * @return
     */
    override fun isTerminated() : Boolean {
        return this.isUnexpected
    }
}