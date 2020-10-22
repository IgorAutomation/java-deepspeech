package com.github.briandilley.deepspeech.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class JNITest {

    @Test
    fun `Ensure that operatingSystem returns an acceptable answer`() {

        JNI.configure(properties = mapOf(OS_NAME to "Redhat Linux"))
        assertEquals(JNI.LINUX, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "Linux"))
        assertEquals(JNI.LINUX, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "macintosh"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "MacOS"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "osx"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "Some sorta miggity with Os X"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "darwin"))
        assertEquals(JNI.DARWIN, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "Windows 10"))
        assertEquals(JNI.WINDOWS, JNI.operatingSystem)

        JNI.configure(properties = mapOf(OS_NAME to "FreeBSD"))
        assertEquals(JNI.BSD, JNI.operatingSystem)
    }

    @Test
    fun `Ensure that architecture returns an acceptable answer`() {

        DefaultArchitectureDetector.architectures.entries
                .flatMap { e -> e.value
                        .map { v -> e.key to v } }
                .shuffled()
                .forEach {
                    JNI.configure(properties = mapOf(OS_ARCH to it.second))
                    assertEquals(it.first, JNI.architecture)
                }
    }

    @Test
    fun `Output some stuff`() {
        JNI.configure()
        println("===============================================")
        println("  JNI.operatingSystem: ${JNI.operatingSystem}")
        println("  JNI.architecture: ${JNI.architecture}")
        println("===============================================")
    }

    @Test
    fun `Can extract package`() {
        JNI.configure()
        JNI.extractPackage("java_deepspeech_jni")
    }

}
