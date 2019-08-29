package io.github.emputi.mc.miniaturengine.application

import io.github.emputi.mc.miniaturengine.external.jni.NativeLoader

typealias WORD = Int
class FrameApplicationManager private constructor() {
    external fun clearConsoleWindow() : Boolean
    external fun setConsoleColors(attribs : WORD) : Boolean

    init {
        val is64bit: Boolean = if (System.getProperty("os.name").contains("Windows")) {
            System.getenv("ProgramFiles(x86)") != null
        } else {
            System.getProperty("os.arch").indexOf("64") != -1
        }
        val libname = if(is64bit) "Application.FrameApplicationManager_x64" else "Application.FrameApplicationManager"
        NativeLoader.loadLibrary(libname, "/", Bootstrapper.BootstrapperBase!!.dataFolder.path)
    }
    companion object {
        @JvmStatic
        val Util: FrameApplicationManager = FrameApplicationManager()
    }
}