package io.github.emputi.mc.miniaturengine.command

interface Handle {
    fun executeTask(handleInstance : Any?) : Any?
}