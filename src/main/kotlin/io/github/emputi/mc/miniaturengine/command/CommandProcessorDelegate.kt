package io.github.emputi.mc.miniaturengine.command

interface CommandProcessorDelegate {
    fun getDelegateCommand() : CommandProcessor?
}