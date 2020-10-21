package com.github.briandilley.deepspeech.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class JNITest {

    @Test
    fun `Ensure that operatingSystem returns an acceptable answer`() {

        val osName = (System.getProperty("os.name") ?: "").toLowerCase().trim()
        if ("linux" in osName) {
            assertEquals(JNI.LINUX, JNI.operatingSystem)

        } else if ("mac" in osName
                || "osx" in osName
                || "os x" in osName
                || "darwin" in osName) {
            assertEquals(JNI.MACOS, JNI.operatingSystem)

        } else if ("win" in osName) {
            assertEquals(JNI.WINDOWS, JNI.operatingSystem)

        } else {
            assertEquals(JNI.UNKNOWN, JNI.operatingSystem)
        }

    }

}
