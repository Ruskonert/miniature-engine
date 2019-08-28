package io.github.emputi.mc.miniaturengine.thread

abstract class Task<P,R> : Runnable
{
    override fun run() {
        if(this.runTaskMethod == null) {

        }
        this.runTaskMethod!!()
    }

    protected var return0 : R? = null
    protected var runTaskMethod : (() -> Unit)? = null

    fun getRunningMethod() : (() -> Unit)? {
        return this.runTaskMethod
    }

    private var functionParameterObject0 : P? = null

    protected fun setFunctionParameterObject(param : P?) {
        this.functionParameterObject0 = param
    }

    fun getFunctionParameterObject() : P? {
        return this.functionParameterObject0
    }

    /**
     * Waits until it returns a value.
     * If Timeout is specified, it is automatically returned when the
     * milliseconds elapse.
     *
     * @param timeout The maximum time to wait for a function to return after action
     */
    fun getReturnAwait(timeout : Long = -1L): R? {
        val currentTime = System.currentTimeMillis()
        synchronized(this) {
            while(true) {
                if(System.currentTimeMillis() - currentTime >= timeout) {
                    return this.return0
                }
                if(! this.isRunning())
                    return this.return0

                if(this.isTerminated()) {
                    throw IllegalThreadStateException("Terminated unexpectedly while waiting for thread completed the task")
                }
            }
        }
    }

    /**
     * The actual method for implementing the callback function.
     * This differs depending on the scheduler and the code implementation.
     * Therefore, complete the corresponding method in the child class.
     *
     * @param functionParameter The parameters to reference in the callback function
     */
    protected abstract fun internalTask(functionParameter : P?)

    abstract fun isRunning(): Boolean

    abstract fun isTerminated(): Boolean

    abstract fun debug(message: String)

    abstract fun debugWithAttribute(message: String, attributes: Any?): Any?
}