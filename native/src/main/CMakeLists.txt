project(java_deepspeech_jni C CXX)

# configure our project
add_library(java_deepspeech_jni SHARED
        JniUtil.h
        JniUtil.cpp

        AbstractNativeObject.h
        AbstractNativeObject.cpp
        AbstractNativeObject_jni.cpp

        DeepSpeechNative_jni.cpp)

set_target_properties(java_deepspeech_jni PROPERTIES
        C_STANDARD 99
        CXX_STANDARD 17
        #CXX_EXTENSIONS OFF
        #CXX_STANDARD_REQUIRED ON
        POSITION_INDEPENDENT_CODE ON)

target_include_directories(java_deepspeech_jni SYSTEM INTERFACE PUBLIC
        ${DEEP_SPEECH_INCLUDE_DIRS}
        ${UTFCPP_INCLUDE_DIRS}
        ${JNI_INCLUDE_DIRS})

#target_link_directories(java_deepspeech_jni PUBLIC
        #${DEEP_SPEECH_LIBRARY_DIRS})

#target_link_libraries(java_deepspeech_jni PUBLIC
#        ${DEEP_SPEECH_LIBRARIES})

