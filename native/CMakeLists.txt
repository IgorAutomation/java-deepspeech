cmake_minimum_required(VERSION 3.13)
#set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_C_STANDARD 11)

project(java_deepspeech C CXX)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/package)
set(RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/package)
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/package)

# Determine the platform.

if(NOT "${PACKAGE_NAME}")
    if("${CMAKE_SYSTEM_NAME}" STREQUAL "Darwin")
        set(OS_MACOS 1)
        set(OS_POSIX 1)
        set(PACKAGE_NAME darwin-x86_64)
        add_definitions(-DOS_MACOS -DOS_POSIX -DUSE_GLFW)

    elseif("${CMAKE_SYSTEM_NAME}" STREQUAL "Linux")
        set(OS_LINUX 1)
        set(OS_POSIX 1)
        set(PACKAGE_NAME linux-x86_64)
        add_definitions(-DOS_LINUX -DOS_POSIX -DUSE_EGL)

    endif()
else()


endif()

if(CMAKE_BUILD_TYPE MATCHES Debug)
    add_definitions(-DDEBUG=1 -D_DEBUG=1)
    set(CMAKE_CONFIGURATION_TYPES Debug)

elseif(CMAKE_BUILD_TYPE MATCHES Release)
    add_definitions(-DDEBUG=0 -D_DEBUG=0)
    set(CMAKE_CONFIGURATION_TYPES Release)
endif()

# support PKG
find_package(PkgConfig REQUIRED)

if (EXISTS "/usr/lib/jvm/java-12-openjdk-amd64")
    set(JAVA_HOME "/usr/lib/jvm/java-12-openjdk-amd64")
    set(JAVA_AWT_LIBRARY "/usr/lib/jvm/java-12-openjdk-amd64/lib/libawt.so")
    set(JAVA_INCLUDE_PATH "/usr/lib/jvm/java-12-openjdk-amd64/include")
    set(JAVA_AWT_INCLUDE_PATH "/usr/lib/jvm/java-12-openjdk-amd64/include")

elseif (EXISTS "/usr/lib/jvm/java-11-openjdk-amd64")
    set(JAVA_HOME "/usr/lib/jvm/java-11-openjdk-amd64")
    set(JAVA_AWT_LIBRARY "/usr/lib/jvm/java-11-openjdk-amd64/lib/libawt.so")
    set(JAVA_INCLUDE_PATH "/usr/lib/jvm/java-11-openjdk-amd64/include")
    set(JAVA_AWT_INCLUDE_PATH "/usr/lib/jvm/java-11-openjdk-amd64/include")

elseif (EXISTS "/usr/lib/jvm/java-10-openjdk-amd64")
    set(JAVA_HOME "/usr/lib/jvm/java-10-openjdk-amd64")
    set(JAVA_AWT_LIBRARY "/usr/lib/jvm/java-10-openjdk-amd64/lib/libawt.so")
    set(JAVA_INCLUDE_PATH "/usr/lib/jvm/java-10-openjdk-amd64/include")
    set(JAVA_AWT_INCLUDE_PATH "/usr/lib/jvm/java-10-openjdk-amd64/include")

elseif (EXISTS "/usr/lib/jvm/java-9-openjdk-amd64")
    set(JAVA_HOME "/usr/lib/jvm/java-9-openjdk-amd64")
    set(JAVA_AWT_LIBRARY "/usr/lib/jvm/java-9-openjdk-amd64/lib/libawt.so")
    set(JAVA_INCLUDE_PATH "/usr/lib/jvm/java-9-openjdk-amd64/include")
    set(JAVA_AWT_INCLUDE_PATH "/usr/lib/jvm/java-9-openjdk-amd64/include")

elseif (EXISTS "/usr/lib/jvm/java-8-openjdk-amd64")
    set(JAVA_HOME "/usr/lib/jvm/java-8-openjdk-amd64")
    set(JAVA_AWT_LIBRARY "/usr/lib/jvm/java-8-openjdk-amd64/lib/libawt.so")
    set(JAVA_INCLUDE_PATH "/usr/lib/jvm/java-8-openjdk-amd64/include")
    set(JAVA_AWT_INCLUDE_PATH "/usr/lib/jvm/java-8-openjdk-amd64/include")

endif()

set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY NotNeeded)
#set(JAVA_INCLUDE_PATH2 NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
find_package(JNI REQUIRED)

#utfcpp
set(UTFCPP_INCLUDE_DIRS "${CMAKE_CURRENT_LIST_DIR}/src/thirdparty/utfcpp/source")

message(STATUS "CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}")
message(STATUS "RUNTIME_OUTPUT_DIRECTORY ${RUNTIME_OUTPUT_DIRECTORY}")
message(STATUS "CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_ARCHIVE_OUTPUT_DIRECTORY}")
message(STATUS "JAVA_HOME ${JAVA_HOME}")
message(STATUS "UTFCPP_INCLUDE_DIRS ${UTFCPP_INCLUDE_DIRS}")

# the projects
add_subdirectory(src/main)
