
#include "JniUtil.h"
#include "generated/com_igor_deepspeech_DeepSpeechNative.h"

using namespace java_deepspeech::jni;

/**
 * Class:     com_igor_deepspeech_DeepSpeechNative
 * Method:    configureNativeLibraries
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_igor_deepspeech_DeepSpeechNative_configureNativeLibraries
        (JNIEnv* env, jclass clazz) {
    JavaVM* vm = nullptr;
    env->GetJavaVM(&vm);
    init_jni(vm);
}
