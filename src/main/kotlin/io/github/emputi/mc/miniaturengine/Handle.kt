package io.github.emputi.mc.miniaturengine

interface Handle {
    fun executeTask(handleInstance : Any?) : Any?
}