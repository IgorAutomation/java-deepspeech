package com.github.briandilley.deepspeech.util

import java.io.File


interface OperatingSystemDetector {
    fun detect(): String?
}

interface ArchitectureDetector {
    fun detect(): String?
}

object DefaultOperatingSystemDetector : OperatingSystemDetector {

    val oses = mapOf(
            JNI.WINDOWS to listOf(".*windows.*"),
            JNI.DARWIN  to listOf(".*mac.*", ".*darwin.*", ".*osx.*", ".*os x.*"),
            JNI.LINUX   to listOf(".*linux.*"),
            JNI.SOLARIS to listOf(".*sunos.*", ".*solaris.*"),
            JNI.CYGWIN  to listOf(".*cygwin.*"),
            JNI.MINGW   to listOf(".*mingw.*"),
            JNI.MSYS    to listOf(".*msys.*"),
            JNI.OTHER   to listOf(".*"))

    override fun detect(): String? {
        val osName = JNI.properties.getOrDefault("os.name", "unknown").toLowerCase().trim()
        return oses
                .entries
                .flatMap { e -> e.value
                        .map { v -> e.key to v } }
                .firstOrNull { it.second.toRegex().matches(osName) }
                ?.first
    }
}

object DefaultArchitectureDetector : ArchitectureDetector {

    val architectures = mapOf(
            JNI.x86     to listOf("x86", "i386", "i486", "i586", "i686", "pentium"),
            JNI.x86_64  to listOf("x86_64", "x86 (64-bit)", "amd64", "em64t"),
            JNI.ia64    to listOf("ia64", "Itanium", "ia64w"),
            JNI.ia64_32 to listOf("ia64_32", "Itanium (32-bit mode)", "ia64n"),
            JNI.ppc     to listOf("ppc", "PowerPC", "power", "powerpc", "power_pc", "power_rs"),
            JNI.ppc64   to listOf("ppc64", "PowerPC (64-bit)"),
            JNI.sparc   to listOf("sparc", "SPARC"),
            JNI.sparcv9 to listOf("sparcv9", "SPARCv9 (64-bit)"),
            JNI.arm     to listOf("arm"),
            JNI.armv6   to listOf("armv6"),
            JNI.armv7   to listOf("armv7"))

    override fun detect(): String? {
        val osArchitecture = JNI.properties.getOrDefault("os.arch", "unknown").toLowerCase().trim()
        var ret = architectures
                .entries
                .flatMap { e -> e.value
                        .map { v -> e.key to v } }
                .firstOrNull { osArchitecture == it.second.toLowerCase().trim() }
                ?.first
                ?: return null

        if (ret != JNI.arm) {
            return ret
        }

        val cpuInfoFile = File("/proc/cpuinfo")

        if (cpuInfoFile.exists() && cpuInfoFile.canRead()) {
            val contents = cpuInfoFile.readText().toLowerCase().trim()
            if (JNI.armv6 in contents) {
                ret = JNI.armv6
            } else if (JNI.armv7 in contents) {
                ret = JNI.armv7
            }
        }

        return ret
    }
}

object JNI {

    const val WINDOWS   = "Windows"
    const val DARWIN    = "Darwin"
    const val LINUX     = "Linux"
    const val SOLARIS   = "Solaris"
    const val CYGWIN    = "Cygwin"
    const val MINGW     = "Mingw"
    const val MSYS      = "Msys"
    const val OTHER     = "Other"
    const val UNKNOWN   = "Unknowm"

    const val x86       = "x86"
    const val x86_64    = "x86_64"
    const val ia64      = "ia64"
    const val ia64_32   = "ia64_32"
    const val ppc       = "ppc"
    const val ppc64     = "ppc64"
    const val sparc     = "sparc"
    const val sparcv9   = "sparcv9"
    const val arm       = "arm"
    const val armv6     = "armv6"
    const val armv7     = "armv7"

    private val _properties: MutableMap<String, String>             = mutableMapOf()
    private val _env: MutableMap<String, String>                    = mutableMapOf()
    private val _osDetectors: MutableList<OperatingSystemDetector>  = mutableListOf()
    private val _archDectors: MutableList<ArchitectureDetector>     = mutableListOf()
    private var _operatingSystem: String?                           = null
    private var _architecture: String?                              = null

    init {
        init()
    }

    @JvmStatic
    fun init(
            properties: Map<String, String> = System.getProperties().toMutableMap()
                    .mapKeys { it.key.toString() }
                    .mapValues { it.value.toString() },
            env: Map<String, String> = System.getenv()) {

        this._properties.clear()
        this._properties.putAll(properties)

        this._env.clear()
        this._env.putAll(env)

        this._osDetectors.clear()
        this._osDetectors.add(DefaultOperatingSystemDetector)

        this._archDectors.clear()
        this._archDectors.add(DefaultArchitectureDetector)

        this._operatingSystem = null
        this._architecture = null
    }

    @JvmStatic
    fun addOperatingSystemDetector(detector: OperatingSystemDetector)
            = this._osDetectors.add(detector)

    @JvmStatic
    fun removeOperatingSystemDetector(detector: OperatingSystemDetector): Boolean
            = this._osDetectors.remove(detector)

    @JvmStatic
    fun addArchitectureDetector(detector: ArchitectureDetector)
            = this._archDectors.add(detector)

    @JvmStatic
    fun removeArchitectureDetector(detector: ArchitectureDetector): Boolean
            = this._archDectors.remove(detector)

    @JvmStatic
    val properties: Map<String, String> get() = this._properties

    @JvmStatic
    val env: Map<String, String> get() = this._env

    @JvmStatic
    val operatingSystem: String get() {

        if (_operatingSystem != null) {
            return _operatingSystem!!
        }

        _operatingSystem = this._osDetectors
                .map { it.detect() }
                .firstOrNull() ?: UNKNOWN

        return _operatingSystem!!
    }

    @JvmStatic
    val architecture: String get() {

        if (_architecture != null) {
            return _architecture!!
        }

        _architecture = this._archDectors
                .map { it.detect() }
                .firstOrNull() ?: UNKNOWN

        return _architecture!!
    }


}
