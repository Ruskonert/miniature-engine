package io.github.emputi.mc.miniaturengine.command

import io.github.emputi.mc.miniaturengine.thread.PluginCallback

@ExperimentalUnsignedTypes
class StatusError
{
    private val codeResult : UInt
    private val callbackResult : (PluginCallback<Any?, Any?>)?

    constructor(code : UInt) {
        this.codeResult = code
        this.callbackResult = null
    }

    constructor(callback : PluginCallback<Any?, Any?>) {
        this.codeResult = UInt.MAX_VALUE
        this.callbackResult = callback
    }
}
