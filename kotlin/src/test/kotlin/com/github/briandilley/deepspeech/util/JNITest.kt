package com.github.briandilley.deepspeech.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class JNITest {

    @Test
    fun `Ensure that operatingSystem returns an acceptable answer`() {

        JNI.init(properties = mapOf("os.name" to "Redhat Linux"))
        assertEquals(JNI.LINUX, JNI.operatingSystem)

        JNI.init(properties = mapOf("os.name" to "Linux"))
        assertEquals(JNI.LINUX, JNI.operatingSystem)

        JNI.init(properties = mapOf("os.name" to "macintosh"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.init(properties = mapOf("os.name" to "MacOS"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.init(properties = mapOf("os.name" to "osx"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.init(properties = mapOf("os.name" to "Some sorta miggity with Os X"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.init(properties = mapOf("os.name" to "darwin"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.init(properties = mapOf("os.name" to "Windows 10"))
        assertEquals(JNI.WINDOWS, JNI.operatingSystem)
    }

    @Test
    fun `Ensure that architecture returns an acceptable answer`() {

        DefaultArchitectureDetector.architectures.entries
                .flatMap { e -> e.value
                        .map { v -> e.key to v } }
                .shuffled()
                .forEach {
                    JNI.init(properties = mapOf("os.arch" to it.second))
                    assertEquals(it.first, JNI.architecture)
                }
    }

    @Test
    fun `Output some stuff`() {
        JNI.init()
        println("===============================================")
        println("  JNI.operatingSystem: ${JNI.operatingSystem}")
        println("  JNI.architecture: ${JNI.architecture}")
        println("===============================================")
    }

}
