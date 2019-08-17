package io.github.emputi.mc.miniaturengine.configuration

class Attribute
{
    companion object {
        fun writeLine(arg0: String, vararg args1: Any): String {
            return arg0.writeString(*args1)
        }

        private fun String.writeString(vararg args: Any): String
        {
            var result = this
            val regex = Regex(".*(\\{[0-9]})")
            val replaceElement = HashMap<Int, String>()
            for ((index, argument) in args.withIndex()) {
                val stringValue = argument.toString()
                replaceElement[index] = stringValue
            }
            val regexFind = regex.findAll(this)
            if (regexFind.count() == 0) return result
            for(indexString in replaceElement.keys) {
                val replaceValue = "{$indexString}"
                result = result.replace(replaceValue, replaceElement[indexString]!!)
            }
            return result
        }
    }
}