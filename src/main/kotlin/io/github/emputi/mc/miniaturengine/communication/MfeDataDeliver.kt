package io.github.emputi.mc.miniaturengine.communication

import io.github.emputi.mc.miniaturengine.external.jni.NativeLoader
import java.io.File

typealias MINATURE_RETURN = Int
open class MfeDataDeliver
{
    private constructor()
    {
        NativeLoader.loadLibrary("MiniatureEngine.Mfe")
    }
    companion object {
        @JvmStatic
        var Util : MfeDataDeliver = MfeDataDeliver(); private set
    }

    fun obfuscate(patternRepeat: Int, readFilePath: String, outputFilePath: String) : MINATURE_RETURN
    {
        val target = File(readFilePath)
        if(!(target.isFile && target.exists())) {
            throw MfeException("MFE file: $readFilePath doesn't exist, Please check your file")
        }
        if(!this.isValidMfeFile(readFilePath)) {
            throw MfeException("MFE file: $readFilePath is not valid MFE file, Maybe it damaged or create abnormally")
        }
        if(!this.isValidMfeChecksum(readFilePath)) {
            throw MfeException("MFE file: $readFilePath, RC pass failed")
        }
        val publicKeyType = this.getPublicKey()
        return if(this.obfuscate0(patternRepeat, publicKeyType, readFilePath, outputFilePath) == 0) 0 else 238
    }

    protected external fun getPublicKey() : String

    @Synchronized
    private external fun obfuscate0(patternRepeat : Int, seed : String, readFilePath : String, outputFilePath : String) : MINATURE_RETURN

    external fun isValidMfeFile(target : String) : Boolean

    external fun isValidMfeChecksum(target : String) : Boolean

    external fun initializeHeader(newTarget : String)
}
