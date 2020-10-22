
#include "AbstractNativeObject.h"
#include "JniUtil.h"
#include "generated/com_igor_deepspeech_DeepSpeechNative.h"

using namespace java_deepspeech::jni;

/**
 * Class:     com_igor_deepspeech_AbstractNativeObject
 * Method:    dispose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_igor_deepspeech_AbstractNativeObject_dispose
        (JNIEnv* env, jobject obj) {

    auto ptr = get_native_state_pointer(env, obj);
    if (ptr == 0) {
        return;
    }

    try {
        dispose_java_native_object_ptr(ptr);
    } catch(...) {
        string className = get_class_name(env, obj);
        THROW_RUNTIME_EXCEPTION_JAVA(env, "Unable to dispose of a " + className);
        return;
    }
}