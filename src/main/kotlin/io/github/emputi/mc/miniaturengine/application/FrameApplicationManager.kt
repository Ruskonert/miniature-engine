package io.github.emputi.mc.miniaturengine.application

import io.github.emputi.mc.miniaturengine.external.jni.NativeLibraryUtil
import io.github.emputi.mc.miniaturengine.external.jni.NativeLoader

typealias WORD = Int
class FrameApplicationManager private constructor() {

    external fun clearConsoleWindow() : Boolean

    external fun setConsoleColors(attribs : WORD) : Boolean

    init {
        var libname = LIBNAME
        when(NativeLibraryUtil.getArchitecture()) {
            NativeLibraryUtil.Architecture.WINDOWS_64,
            NativeLibraryUtil.Architecture.LINUX_64,
            NativeLibraryUtil.Architecture.OSX_64 -> libname += "_x64"
        }
        NativeLoader.loadLibrary(libname, "/", Bootstrapper.BootstrapperBase!!.dataFolder.path)
    }
    companion object {
        private const val LIBNAME = "Application.FrameApplicationManager"
        @JvmStatic
        val Util: FrameApplicationManager = FrameApplicationManager()
    }
}