package io.github.emputi.mc.miniaturengine.configuration

import com.google.gson.JsonParseException

class ConfigurationException : JsonParseException
{
    constructor(msg : String) : super(msg)

    constructor(msg : String, cause : Throwable) : super(msg, cause)
}