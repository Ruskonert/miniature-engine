package io.github.emputi.mc.miniaturengine.configuration.command

class ParameterConfiguration
{
    companion object {
        private lateinit var CONFIGURATION : ParameterConfiguration
        fun configurationInst() : ParameterConfiguration {
            return CONFIGURATION
        }
    }
    init {
        CONFIGURATION = ParameterConfiguration()
        CONFIGURATION.conf["Parameter.Format.Optional"] = "&e[$0]"
        CONFIGURATION.conf["Parameter.Format.Requirement"] = "&e<$0>"
    }
    val conf : HashMap<String, String> = HashMap()
    fun getAttribute(attributeName : String) : String? {
        return conf[attributeName]
    }
}