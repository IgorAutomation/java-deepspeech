package com.github.briandilley.deepspeech.util

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.logging.Logger
import java.util.zip.ZipInputStream

const val JAVA_LIBRARY_PATH     = "java.library.path"
const val JAVA_TEMP_DIR         = "java.io.tmpdir"
const val OS_ARCH               = "os.arch"
const val OS_NAME               = "os.name"

interface OperatingSystemDetector {
    fun detect(): String?
}

interface ArchitectureDetector {
    fun detect(): String?
}

object DefaultOperatingSystemDetector : OperatingSystemDetector {

    val operatingSystems = mapOf(
            JNI.WINDOWS to listOf(".*windows.*", ".*cygwin.*", ".*mingw.*", ".*msys.*"),
            JNI.DARWIN to listOf(".*mac.*", ".*darwin.*", ".*osx.*", ".*os x.*"),
            JNI.LINUX to listOf(".*linux.*"),
            JNI.SOLARIS to listOf(".*sunos.*", ".*solaris.*"),
            JNI.BSD to listOf(".*bsd.*"),
            JNI.OTHER to listOf(".*"))

    override fun detect(): String? {
        val osName = JNI.properties.getOrDefault(OS_NAME, "unknown").toLowerCase().trim()
        return operatingSystems
                .entries
                .flatMap { e -> e.value
                        .map { v -> e.key to v } }
                .firstOrNull { it.second.toRegex().matches(osName) }
                ?.first
    }
}

object DefaultArchitectureDetector : ArchitectureDetector {

    val architectures = mapOf(
            JNI.x86 to listOf("x86", "i386", "i486", "i586", "i686", "pentium"),
            JNI.x86_64 to listOf("x86_64", "x86 (64-bit)", "amd64", "em64t"),
            JNI.ia64 to listOf("ia64", "Itanium", "ia64w"),
            JNI.ia64_32 to listOf("ia64_32", "Itanium (32-bit mode)", "ia64n"),
            JNI.ppc to listOf("ppc", "PowerPC", "power", "powerpc", "power_pc", "power_rs"),
            JNI.ppc64 to listOf("ppc64", "PowerPC (64-bit)"),
            JNI.sparc to listOf("sparc", "SPARC"),
            JNI.sparcv9 to listOf("sparcv9", "SPARCv9 (64-bit)"),
            JNI.arm to listOf("arm"),
            JNI.arm64 to listOf("arm64"),
            JNI.armv6 to listOf("armv6"),
            JNI.armv7 to listOf("armv7"))

    override fun detect(): String? {
        val archName = JNI.properties.getOrDefault(OS_ARCH, "unknown").toLowerCase().trim()
        var ret = architectures
                .entries
                .flatMap { e -> e.value
                        .map { v -> e.key to v } }
                .firstOrNull { archName in it.second.toLowerCase().trim() }
                ?.first
                ?: return null

        if (ret != JNI.arm) {
            return ret
        }

        val cpuInfoFile = File("/proc/cpuinfo")

        if (cpuInfoFile.exists() && cpuInfoFile.canRead()) {
            val contents = cpuInfoFile.readText().toLowerCase().trim()
            when {
                JNI.armv6 in contents -> {
                    ret = JNI.armv6
                }
                JNI.armv7 in contents -> {
                    ret = JNI.armv7
                }
                JNI.armv6 in contents -> {
                    ret = JNI.armv6
                }
            }
        }

        return ret
    }
}

object JNI {

    const val WINDOWS   = "windows"
    const val DARWIN    = "darwin"
    const val LINUX     = "linux"
    const val SOLARIS   = "solaris"
    const val BSD       = "bsd"
    const val OTHER     = "other"
    const val UNKNOWN   = "unknowm"

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
    const val arm64     = "arm64"

    private val LOGGER = Logger.getLogger(JNI.javaClass.simpleName)

    private val _properties: MutableMap<String, String>             = mutableMapOf()
    private val _env: MutableMap<String, String>                    = mutableMapOf()
    private val _osDetectors: MutableList<OperatingSystemDetector>  = mutableListOf()
    private val _archDectors: MutableList<ArchitectureDetector>     = mutableListOf()
    private var _operatingSystem: String?                           = null
    private var _architecture: String?                              = null
    private var _tempDir: String?                                   = null

    init {
        configure()
    }

    @JvmStatic
    fun configure(
            properties: Map<String, String> = System.getProperties().toMutableMap()
                    .mapKeys { it.key.toString() }
                    .mapValues { it.value.toString() },
            env: Map<String, String> = System.getenv(),
            tempDir: String? = null,
            resourcePath: String = "/native") {

        this._properties.clear()
        this._properties.putAll(properties)

        this._env.clear()
        this._env.putAll(env)

        this._osDetectors.clear()
        this._osDetectors.add(DefaultOperatingSystemDetector)

        this._archDectors.clear()
        this._archDectors.add(DefaultArchitectureDetector)

        this._tempDir = tempDir
        this.resourcePath = resourcePath

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

    @JvmStatic
    val tempDir: String get() {

        if (_tempDir != null) {
            return _tempDir!!
        }

        _tempDir = properties.getOrDefault(
                JAVA_TEMP_DIR,
                System.getProperty(JAVA_TEMP_DIR, "/tmp"))

        return _tempDir!!
    }

    var resourcePath: String = "/native"
        private set(value) {
            if (value.trim().isNotEmpty() && !value.trim().startsWith("/")) {
                throw IllegalArgumentException("Resource path must be empty or start with /");
            }
            field = value
        }

    @JvmStatic
    @Synchronized
    fun getPackageResourcePath(packageName: String, version: String? = null, path: String? = null): String {
        return "$resourcePath${path ?: ""}/$operatingSystem-$architecture/$packageName${version?.let { "-$it" } ?: ""}.zip"
    }

    @JvmStatic
    @Synchronized
    fun extractPackage(packageName: String, version: String? = null, path: String? = null): Boolean {
        if (path != null
                && path.trim().isNotEmpty()
                && !path.trim().startsWith("/")) {
            throw IllegalArgumentException("Path must be empty or start with /");
        }

        val destPath = File(tempDir, packageName)
        val packagePath = getPackageResourcePath(packageName, version, path)

        LOGGER.fine("Attempting to load package from $packagePath")

        val archiveStream = JNI.javaClass.getResourceAsStream(packagePath)
                ?: throw FileNotFoundException("Unable to find native library in classpath: $packagePath")

        val libPaths = mutableSetOf<File>()
        ZipInputStream(archiveStream).use { zipStream ->
            var entry = zipStream.getNextEntry()
            while (entry != null) {
                val tmpFile = File(destPath, entry.name)
                if (entry.isDirectory) {
                    LOGGER.fine("Creating directory: ${tmpFile.path}")
                    if (!(tmpFile.isDirectory || tmpFile.mkdirs())) {
                        LOGGER.severe("Unable to create directory ${tmpFile.path}")
                        return false
                    }
                } else {
                    LOGGER.fine("Extracting native library: ${tmpFile.path}")
                    tmpFile.parentFile.mkdirs()
                    FileOutputStream(tmpFile).use(zipStream::copyTo)
                    zipStream.closeEntry()
                    libPaths.add(tmpFile.parentFile.canonicalFile)
                }
                entry = zipStream.getNextEntry()
            }
        }

        val systemLibPaths = properties.getOrDefault(JAVA_LIBRARY_PATH, "")
                .split(File.pathSeparator)
                .map(::File)
                .filter { it !in libPaths }
                .toMutableList()

        systemLibPaths.addAll(libPaths)

        val systemLibPathsString = systemLibPaths
                .joinToString(separator = File.pathSeparator) { it.path }

        System.setProperty(JAVA_LIBRARY_PATH, systemLibPathsString);

        try {
            val fieldSysPath = ClassLoader::class.java.getDeclaredField("sys_paths")
            fieldSysPath.isAccessible = true
            fieldSysPath.set(null, null)
        } catch (e: IllegalAccessException) {
            throw IllegalStateException("Unable to clear system path cache", e)
        } catch (e: NoSuchFieldException) {
            throw IllegalStateException("Unable to clear system path cache", e)
        }

        LOGGER.fine("$JAVA_LIBRARY_PATH: ${System.getProperty(JAVA_LIBRARY_PATH)}");

        return true
    }

}
