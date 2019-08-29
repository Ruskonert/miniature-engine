package io.github.emputi.mc.miniaturengine.communication

typealias MINATURE_RETURN = Int
open class MinatureDataDeliver
{
    constructor(patternRepeat: Int) {

    }

    @Synchronized
    private external fun obfuscate(patternRepeat : Int, seed : String, readFilePath : String, outputFilePath : String) : MINATURE_RETURN

    external fun isValidOfsFile(target : String) : Boolean

    external fun isValidOfsChecksum(target : String) : Boolean

    external fun initializeOfsHeader(newTarget : String)
}
