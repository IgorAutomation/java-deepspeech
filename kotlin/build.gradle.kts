import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

val GENERATED_SOURCE_DIR = "${project.rootDir}/native/src/main/generated"
val HOST_ARCH = "${System.getProperty("os.name").toLowerCase().replace("mac os x", "darwin")}-${System.getProperty("os.arch").toLowerCase()}"
val NATIVE_ARCHITECTURES = setOf(
        HOST_ARCH,
        "darwin-x86_64",
        "linux-x86_64",
        "linux-x86",
        "linux-arm64",
        "linux-armv6",
        "linux-armv7")
val JAVA_TARGET_VERSION = "1.8"

fun buildNative(arch: String = HOST_ARCH) {
    val nativeWorkDir = "${project.rootDir}/native/cmake-build-$arch-Release"
    exec {
        workingDir("${project.rootDir}/native")
        environment["JAVA_HOME"] = System.getenv("JAVA_HOME")
        if (arch == HOST_ARCH) {
            commandLine("sh", "./scripts/build.sh", "-a", arch)
        } else {
            commandLine("sh", "./scripts/cross-build.sh", "-a", arch)
        }
    }
    val exit = exec {
        workingDir("${nativeWorkDir}/package")
        commandLine("zip", "-1", "-r", "-u", "${nativeWorkDir}/java_deepspeech_jni-${arch}.zip", "./")
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

    testImplementation("org.mockito:mockito-core:2.+")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks {

    /**
     * Custom logic on test
     */
    test {
        useJUnitPlatform()
        testLogging {
            exceptionFormat 	= FULL
            showExceptions 		= true
            showStackTraces 	= true
            showCauses 			= true
        }
        maxParallelForks = 1
        maxHeapSize = "2g"
    }

    /**
     * Custom logic on compile
     */
    compileKotlin {
        sourceCompatibility = JAVA_TARGET_VERSION
        targetCompatibility = JAVA_TARGET_VERSION

        kotlinOptions {
            jvmTarget = JAVA_TARGET_VERSION
        }

        inputs.files(fileTree("${project.rootDir}/native/src/main"))
        NATIVE_ARCHITECTURES.forEach {
            outputs.files(fileTree("${project.rootDir}/native/cmake-build-$it-Release"))
            outputs.file("${project.rootDir}/native/cmake-build-$it-Release/java_deepspeech_jni-${it}.zip")
            outputs.file("${project.rootDir}/kotlin/src/main/resources/native/${it}/java_deepspeech_jni.zip")
        }

        doLast {
            exec {
                commandLine(
                        "${project.rootDir}/tools/gjavah.sh",
                        "-d", GENERATED_SOURCE_DIR,
                        "-classpath", (sourceSets["main"].runtimeClasspath + sourceSets["main"].output).filter { it.exists() }.asPath,
                        sourceSets["main"].output.asFileTree.matching { include("**/*.class") }.joinToString(separator = " "))
            }
            NATIVE_ARCHITECTURES.forEach {
                buildNative(it)
            }
            NATIVE_ARCHITECTURES.forEach {
                File("${project.rootDir}/kotlin/src/main/resources/native").mkdirs()
                val from = File("${project.rootDir}/native/cmake-build-$it-Release/java_deepspeech_jni-${it}.zip")
                val to = File("${project.rootDir}/kotlin/src/main/resources/native/${it}/java_deepspeech_jni.zip")
                from.copyTo(to, overwrite = true)
            }
        }
    }

    /**
     * Custom logic on clean
     */
    clean {
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

}
