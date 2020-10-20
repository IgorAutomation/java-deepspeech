project(java_deepspeech_jni C CXX)

# configure our project
add_library(java_deepspeech_jni SHARED
        native.h
        native.c)

set_target_properties(java_deepspeech_jni PROPERTIES
        C_STANDARD 99
        POSITION_INDEPENDENT_CODE ON)

#target_link_directories(java_deepspeech_jni PUBLIC
#        ${DEEP_SPEECH_LIBRARY_DIRS})

#target_link_libraries(java_deepspeech_jni PUBLIC
#        ${DEEP_SPEECH_LIBRARIES})

