cmake_minimum_required(VERSION 3.13)
#set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_C_STANDARD 11)

project(streamhub C)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/package)
set(RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/package)
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/package)

message(STATUS "CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}")
message(STATUS "RUNTIME_OUTPUT_DIRECTORY ${RUNTIME_OUTPUT_DIRECTORY}")
message(STATUS "CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_ARCHIVE_OUTPUT_DIRECTORY}")

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

find_package(JNI REQUIRED)

# the projects
add_subdirectory(src/main)
