package io.github.emputi.mc.miniaturengine.configuration

class ParameterConfiguration
{
    companion object {
        private var CONFIGURATION : ParameterConfiguration = ParameterConfiguration()
        fun configurationInst() : ParameterConfiguration {
            return CONFIGURATION
        }
        init {
            CONFIGURATION.conf["Parameter.Format.Optional"] = "&e[{0}]"
            CONFIGURATION.conf["Parameter.Format.Requirement"] = "&e<{0}>"
            CONFIGURATION.conf["Parameter.Format.NoProvidedDescription"] = "No description"
        }
    }
    val conf : HashMap<String, String> = HashMap()
    fun getAttribute(attributeName : String) : String? {
        return conf[attributeName]
    }
}