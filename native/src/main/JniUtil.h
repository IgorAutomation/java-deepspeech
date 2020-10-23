#ifndef JAVA_DEEPSPEECH_JNIUTIL_H
#define JAVA_DEEPSPEECH_JNIUTIL_H

#include <jni.h>
#include <string>
#include <vector>
#include <utf8.h>
#include <cassert>

#ifndef JNI_VERSION_1_8
#define JNI_VERSION_1_8 0x00010008
#endif

using namespace std;

#define THROW_RUNTIME_EXCEPTION_JAVA(env, message) throw_java_exception(env, "java/lang/RuntimeException", message)
#define THROW_TIMEOUT_EXCEPTION_JAVA(env, message) throw_java_exception(env, "java/util/concurrent/TimeoutException", message)
#define THROW_ILLEGAL_STATE_EXCEPTION_JAVA(env, message) throw_java_exception(env, "java/lang/IllegalStateException", message)
#define THROW_OUT_OF_MEMORY_ERROR_JAVA(env, message) throw_java_exception(env, "java/lang/OutOfMemoryError", message)
#define THROW_NULL_POINTER_EXCEPTION_JAVA(env, message) throw_java_exception(env, "java/lang/NullPointerException", message)
#define THROW_FILE_NOT_FOUND_EXCEPTION_JAVA(env, message) throw_java_exception(env, "java/io/FileNotFoundException", message)
#define THROW_ILLEGAL_ARGUMENT_EXCEPTION_JAVA(env, message) throw_java_exception(env, "java/lang/IllegalArgumentException", message);

#define jni_GetObjectClass(env, variableName, obj) nullptr; \
    if (variableName == nullptr || is_null(env, variableName)) { \
        if (variableName != nullptr) { \
            env->DeleteWeakGlobalRef(variableName); \
            variableName = nullptr; \
        } \
        variableName = env->GetObjectClass(obj); \
        variableName = (jclass)env->NewWeakGlobalRef(variableName); \
    }

#define jni_FindClass(env, variableName, className) nullptr; \
    if (variableName == nullptr || is_null(env, variableName)) { \
        if (variableName != nullptr) { \
            env->DeleteWeakGlobalRef(variableName); \
            variableName = nullptr; \
        } \
        variableName = env->FindClass(className); \
        if (was_exception_thrown(env)) { \
            variableName = nullptr; \
        } \
        variableName = (jclass)env->NewWeakGlobalRef(variableName); \
    }

#define jni_GetMethodId(env, classVariableName, variableName, name, sig) nullptr; \
    if (variableName == nullptr) { \
         variableName = env->GetMethodID(classVariableName, name, sig); \
    }

#define jni_WithAttachedEnv(body) \
    JNIEnv* env = get_java_env(); \
    bool __detach = env == nullptr; \
    if (__detach) { \
        env = attach_java_env(); \
    } \
    body ;\
    if (__detach) { \
        detach_java_env(); \
    }

namespace java_deepspeech {
namespace jni {

    inline std::u16string get_utf16_string_from_java(JNIEnv* env, jstring string) {
        auto len = env->GetStringLength(string);
        auto* stringChars = env->GetStringChars(string, nullptr);
        std::u16string stringString;
        stringString.assign(stringChars, stringChars + len);
        env->ReleaseStringChars(string, stringChars);
        return stringString;
    }

    inline std::string get_utf8_string_from_java(JNIEnv* env, jstring string) {
        auto len = env->GetStringLength(string);
        auto* utf16 = env->GetStringChars(string, nullptr);
        try {
            vector<unsigned char> utf8result;
            utf8::utf16to8(utf16, utf16 + len, std::back_inserter(utf8result));
            env->ReleaseStringChars(string, utf16);
            return std::string(utf8result.begin(), utf8result.end());
        } catch(utf8::invalid_utf16& e) {
            std::string ret(utf16, utf16 + env->GetStringUTFLength(string));
            env->ReleaseStringChars(string, utf16);
            return ret;
        }
    }

    jlong get_native_state_pointer(JNIEnv *env, jobject obj);

    int get_direct_buffer_capacity(JNIEnv *env, jobject buffer);
    int get_direct_buffer_position(JNIEnv *env, jobject buffer);
    void* get_direct_buffer_address_at_position(JNIEnv *env, jobject buffer);
    int get_direct_buffer_remaining(JNIEnv *env, jobject buffer);
    int get_direct_buffer_limit(JNIEnv *env, jobject buffer);
    jobject set_direct_buffer_position(JNIEnv *env, jobject buffer, int position);
    jobject set_direct_buffer_limit(JNIEnv *env, jobject buffer, int limit);

    template<typename T>
    T* get_native_state_ptr(JNIEnv* env, jobject obj) {
        jlong ret = get_native_state_pointer(env, obj);
        return (T*)ret;
    }

    void set_native_state_pointer(JNIEnv* env, jobject obj, void* ptr);

    void init_jni(JavaVM* vm);

    JNIEnv* get_java_env();

    JavaVM* get_java_vm();

    JNIEnv* attach_java_env();

    void detach_java_env();

    bool was_exception_thrown(JNIEnv* env);

    bool is_null(JNIEnv* env, jobject obj);

    float unbox_float(JNIEnv* env, jobject value);

    string get_class_name(JNIEnv *env, jobject obj);

    jint throw_java_exception(JNIEnv *env, const string& type, const string& message);

    jint get_native_ordinal_by_java_enum(JNIEnv *env, const string& enumClass, const string& memberMethodName, jobject enumObject);

    jobject get_java_enum_by_native_ordinal(JNIEnv *env, const string& enumClass, const string& staticMethodName, jint ordinal);

    jint get_int_field_value(JNIEnv *env, jobject obj, const string& fieldName);
}
}
#endif
