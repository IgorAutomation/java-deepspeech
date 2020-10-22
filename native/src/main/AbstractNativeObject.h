#ifndef JAVA_DEEPSPEECH_ABSTRACTNATIVEOBJECT_H
#define JAVA_DEEPSPEECH_ABSTRACTNATIVEOBJECT_H

#include <string>
#include <memory>
#include <utility>
#include <thread>
#include <functional>
#include <map>
#include <jni.h>

#include "JniUtil.h"

#define AbstractNativeObject_JAVA_TYPE_NAME "com/igor/deepspeech/AbstractNativeObject"

using namespace java_deepspeech::jni;

namespace java_deepspeech::jni {


    class AbstractNativeObject {
    public:
        virtual ~AbstractNativeObject();
        AbstractNativeObject() = delete;
        explicit AbstractNativeObject(const std::string& javaTypeName);
        AbstractNativeObject(const AbstractNativeObject& other);

        AbstractNativeObject(AbstractNativeObject&& other) noexcept;
        AbstractNativeObject& operator=(AbstractNativeObject other);
        friend void swap(AbstractNativeObject& lhs, AbstractNativeObject& rhs);

        /**
         * Returns this native object's underlying java type name.
         */
        const std::string& getJavaTypeName();

        bool hasReference(const std::string& key);
        jobject addReference(const std::string& key, jobject ref);
        jobject getReference(const std::string& key, const std::function<jobject()>& func = nullptr);
        jobject updateReference(const std::string& key, jobject value);
        bool removeReference(const std::string& key);
        void clearReferences();

    protected:
        std::string javaTypeName;
        std::map<const std::string, jobject> references;
    };
    void swap(AbstractNativeObject& lhs, AbstractNativeObject& rhs);

    std::shared_ptr<AbstractNativeObject> get_abstract_native_object(JNIEnv* env, jobject obj);

    std::shared_ptr<AbstractNativeObject> get_abstract_native_object(jlong ptr);

    void set_abstract_native_object(JNIEnv* env, jobject obj, const std::shared_ptr<AbstractNativeObject>& state);

    jlong make_java_native_object_ptr(std::shared_ptr<AbstractNativeObject> state);

    void dispose_java_native_object_ptr(jlong ptr);

    /*
    template<typename T, typename std::enable_if<std::is_base_of<AbstractNativeObject, T>::value>::type* = nullptr>
    jlong make_java_native_object_ptr(std::shared_ptr<T> state) {
        return make_java_abstract_native_object_ptr(state);
    }
     */

    template<typename T, typename std::enable_if<std::is_base_of<AbstractNativeObject, T>::value>::type* = nullptr>
    void set_native_object(JNIEnv* env, jobject obj, std::shared_ptr<T> state) {
        set_abstract_native_object(env, obj, state);
    }

    template<typename T, typename std::enable_if<std::is_base_of<AbstractNativeObject, T>::value>::type* = nullptr>
    std::shared_ptr<T> get_native_object(JNIEnv* env, jobject obj) {
        return std::static_pointer_cast<T>(get_abstract_native_object(env, obj));
    }

}

#endif //JAVA_DEEPSPEECH_ABSTRACTNATIVEOBJECT_H
