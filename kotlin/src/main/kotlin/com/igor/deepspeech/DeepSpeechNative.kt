package com.igor.deepspeech

import com.igor.deepspeech.util.JNI
import java.io.File

object DeepSpeechNative {

    @JvmField
    val PACKAGE_NAME = "java_deepspeech_jni"

    @JvmField
    val NATIVE_LIB_EXTRACT_DIR = JNI.getPackageResourcePath(PACKAGE_NAME)

    private var extracted = false
    private var initialized = false

    @JvmStatic
    fun prepareNativeLibraries() {
        extractNativeLibraries()
        initializeNativeLibraries()
    }

    @JvmStatic
    external fun configureNativeLibraries()

    @JvmStatic
    @Synchronized
    private fun initializeNativeLibraries() {
        if (initialized) {
            return
        }
        initialized = try {
            configureNativeLibraries()
            true
        } catch (t: Throwable) {
            throw RuntimeException(t)
        }
    }

    @JvmStatic
    @Synchronized
    private fun extractNativeLibraries() {
        if (extracted) {
            return
        }
        extracted = try {
            File(NATIVE_LIB_EXTRACT_DIR).deleteRecursively()
            if (!JNI.extractPackage(PACKAGE_NAME)) {
                throw RuntimeException("extractLibs returned false")
            }
            System.loadLibrary(PACKAGE_NAME)
            true
        } catch (t: Throwable) {
            throw RuntimeException(t)
        }
    }
}
