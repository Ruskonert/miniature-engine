package io.github.emputi.mc.miniaturengine.thread

import com.google.common.collect.ArrayListMultimap
import io.github.emputi.mc.miniaturengine.application.Bootstrapper
import io.github.emputi.mc.miniaturengine.command.Handle
import io.github.emputi.mc.miniaturengine.PluginHandler
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask

abstract class BukkitThreadSynchronise : PluginHandler, Handle, Listener
{
    companion object {
        // Registers a BukkitThreadSynchronise object. It cans keep some work sustainable through that list.
        private val registeredFramework : ArrayListMultimap<Bootstrapper, BukkitThreadSynchronise> = ArrayListMultimap.create()
        fun getRegisterFramework(handlePlugin : Bootstrapper) : List<BukkitThreadSynchronise> = registeredFramework.get(handlePlugin)
    }

    // Specify the delay before starting the operation.
    var delay  : Long        = 0L;    protected set

    // Specify the work cycle time.
    var period : Long        = 0L;    protected set

    // Determines this task is synchronized.
    var isSync : Boolean     = true;  protected set

    // Specifies the Task type of job scheduler.
    var task   : BukkitTask? = null;  private set

    // Specifies the ID of the job scheduler.
    val taskId : Int get() = if(this.task == null) -1 else this.task!!.taskId

    // Specifies the plugin to manage the scheduler.
    var activePlugin: Bootstrapper? = null
    fun hasActivePlugin(): Boolean = this.activePlugin != null

    override fun setEnable(plugin: Bootstrapper?)
    {
        this.activePlugin = plugin
        this.setEnable(plugin != null)
    }

    override fun isEnabled(): Boolean
    {
        for (core in registeredFramework[this.activePlugin])
            if (core == this) return true
        return false
    }

    override fun setEnable(active: Boolean)
    {
        this.preLoad(active)
        this.loadRegisterListener(active)
        this.executeTask0(active)
        this.finLoad(active)
        if (active) {
            if (!this.isEnabled()) registeredFramework.put(this.activePlugin, this)
        }
        else {
            if (this.isEnabled())  registeredFramework.remove(this.activePlugin, this)
        }
    }

     protected open fun executeTask0(active : Boolean, handleInstance : Any? = null) : Any? {
         if(! this.hasActivePlugin()) throw NullPointerException("The active plugin is null!")
         val plugin = this.activePlugin!!
         val func : () -> Unit = fun() { this.executeTask(handleInstance)}
         try {
             if (this.isSync) {
                 this.task = plugin.server.scheduler.runTaskTimer(plugin, func, this.delay, this.period)
             }
             else {
                 this.task = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, func, this.delay, this.period)
             }
         }
         catch(e : Exception) {
            e.printStackTrace()
            return false
         }
         return true
    }

    private fun loadRegisterListener(active: Boolean)
    {
        if (active) {
            val plugin : Bootstrapper = this.activePlugin!!
            if (plugin.isEnabled) Bukkit.getPluginManager().registerEvents(this, plugin)
        }
        else {
            HandlerList.unregisterAll(this)
        }
    }

    protected open fun preLoad(active: Boolean) {}
    protected open fun finLoad(active: Boolean) {}
}