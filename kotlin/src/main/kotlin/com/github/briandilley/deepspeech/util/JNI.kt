package com.github.briandilley.deepspeech.util


interface OperatingSystemDetector {
    fun detect(): String?
}

object DefaultOperatingSystemDetector : OperatingSystemDetector {
    override fun detect(): String? {
        val osName = JNI.osName
        return when {
            // linux
            ("linux" in osName) -> JNI.LINUX

            // mac
            ("mac" in osName) -> JNI.MACOS
            ("osx" in osName) -> JNI.MACOS
            ("os x" in osName) -> JNI.MACOS
            ("darwin" in osName) -> JNI.MACOS

            // windows
            ("win" in osName) -> JNI.WINDOWS
            else -> null
        }
    }
}

object JNI {

    const val LINUX     = "Linux"
    const val MACOS     = "MacOS"
    const val WINDOWS   = "Windows"
    const val UNKNOWN   = "Unknown"

    const val x86       = "x86"
    const val x86_64    = "x86_64"

    private val _properties: MutableMap<String, String>             = mutableMapOf()
    private val _env: MutableMap<String, String>                    = mutableMapOf()
    private val _osDetectors: MutableList<OperatingSystemDetector>  = mutableListOf()
    private var _operatingSystem: String?                           = null

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

        this._operatingSystem = null
    }

    @JvmStatic
    fun addOperatingSystemDetector(detector: OperatingSystemDetector)
            = this._osDetectors.add(detector)

    @JvmStatic
    fun removeOperatingSystemDetector(detector: OperatingSystemDetector): Boolean
            = this._osDetectors.remove(detector)

    @JvmStatic
    val properties: Map<String, String> get() = this._properties

    @JvmStatic
    val env: Map<String, String> get() = this._env

    @JvmStatic
    val osName: String get() = properties.getOrDefault("os.name", "unknown").toLowerCase().trim()

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


}
