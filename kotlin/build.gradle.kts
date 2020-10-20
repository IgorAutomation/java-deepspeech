import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val GENERATED_SOURCE_DIR = "${project.rootDir}/native/src/main/generated"
val NATIVE_ARCHITECTURES = listOf(
        "default",
        "linux-x64",
        "linux-arm64",
        "linux-armv6",
        "linux-armv7")

fun buildNative(arch: String = "default") {
    val nativeWorkDir = "${project.rootDir}/native/cmake-build-$arch-Release"
    exec {
        workingDir("${project.rootDir}/native")
        if (arch == "default") {
            commandLine("sh", "./scripts/build.sh")
        } else {
            commandLine("sh", "./scripts/cross-build.sh", "-a", arch)
        }
    }
    val exit = exec {
        workingDir("${nativeWorkDir}/package")
        commandLine("zip", "-1", "-r", "-u", "${nativeWorkDir}/libs-${arch}.zip", "./")
        isIgnoreExitValue = true
    }.exitValue
    if (exit !=0 && exit != 12) {
        throw GradleException("Zip exit was not 0 or 12")
    }
}

plugins {
    kotlin("jvm") version "1.4.10"
    idea
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<KotlinCompile> {

    inputs.files(fileTree("${project.rootDir}/native/src/main"))
    NATIVE_ARCHITECTURES.forEach {
        outputs.files(fileTree("${project.rootDir}/native/cmake-build-$it-Release"))
        outputs.file("${project.rootDir}/native/cmake-build-$it-Release/libs-${it}.zip")
    }

    doLast {
        exec {
            commandLine(
                "${project.rootDir}/tools/gjavah.sh",
                "-d", GENERATED_SOURCE_DIR,
                "-classpath", (sourceSets["main"].runtimeClasspath + sourceSets["main"].output).filter { it.exists() }.asPath,
                sourceSets["main"].output.asFileTree.matching { include("**/*.class") }.joinToString(separator = " "))
        }
        buildNative()
        NATIVE_ARCHITECTURES.forEach {
            buildNative(it)
        }
    }
}

tasks.clean {
    doLast {
        if (file(GENERATED_SOURCE_DIR).exists()) {
            println("Deleting $GENERATED_SOURCE_DIR")
            file(GENERATED_SOURCE_DIR).deleteRecursively()
        }
        NATIVE_ARCHITECTURES.forEach {
            val dir = "${project.rootDir}/native/cmake-build-$it-Release"
            if (file(dir).exists()) {
                println("Deleting $dir")
                file(dir).deleteRecursively()
            }
        }
    }
}
