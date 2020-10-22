
#include "AbstractNativeObject.h"

using namespace java_deepspeech::jni;

AbstractNativeObject::~AbstractNativeObject() {
    this->clearReferences();
}

AbstractNativeObject::AbstractNativeObject(const std::string& javaTypeName) :
        javaTypeName(javaTypeName),
        references() {
}

AbstractNativeObject::AbstractNativeObject(const AbstractNativeObject& other) :
        javaTypeName(other.javaTypeName),
        references(other.references) {
}

AbstractNativeObject::AbstractNativeObject(AbstractNativeObject&& other) noexcept :
        javaTypeName(nullptr),
        references() {
    swap(*this, other);
}

AbstractNativeObject& AbstractNativeObject::operator=(AbstractNativeObject other) {
    swap(*this, other);
    return *this;
}


void java_deepspeech::jni::swap(AbstractNativeObject& lhs, AbstractNativeObject& rhs) {
    std::swap(lhs.javaTypeName, rhs.javaTypeName);
    std::swap(lhs.references, rhs.references);
}

const std::string& AbstractNativeObject::getJavaTypeName() {
    return javaTypeName;
}

bool AbstractNativeObject::hasReference(const std::string& key) {
    return this->references.find(key) != this->references.end();
}

jobject AbstractNativeObject::addReference(const std::string& key, jobject obj) {
    if (obj != nullptr) {
        jni_WithAttachedEnv(obj = env->NewGlobalRef(obj))
        this->references[key] = obj;
    }
    return obj;
}

jobject AbstractNativeObject::getReference(const std::string& key, const std::function<jobject()>& func) {

    auto ret = (jobject)nullptr;

    auto pair = this->references.find(key);
    if (pair != this->references.end()) {
        ret = pair->second;
    }

    if (ret != nullptr) {
        jni_WithAttachedEnv(
            if (env->IsSameObject(ret, nullptr)) {
                ret = (jobject)nullptr;
            })
    }

    if (ret == nullptr && func != nullptr) {
        ret = func();
        ret = this->addReference(key, ret);
    }

    return ret;
}

jobject AbstractNativeObject::updateReference(const std::string& key, jobject value) {
    this->removeReference(key);
    return this->getReference(key, [value]() { return value; });
}

bool AbstractNativeObject::removeReference(const std::string& key) {
    auto pair = this->references.find(key);
    if (pair == this->references.end()) {
        return false;
    }
    this->references.erase(pair);
    jni_WithAttachedEnv(env->DeleteGlobalRef(pair->second))
    return true;
}

void AbstractNativeObject::clearReferences() {
    for (auto itr = this->references.begin(); itr != this->references.end(); itr++) {
        jni_WithAttachedEnv(env->DeleteGlobalRef(itr->second))
    }
    this->references.clear();
}


struct State {
    std::shared_ptr<AbstractNativeObject> ptr;
};

jlong java_deepspeech::jni::make_java_native_object_ptr(std::shared_ptr<AbstractNativeObject> state) {
    auto* ret = new State();
    ret->ptr = std::move(state);
    return (jlong)ret;
}

std::shared_ptr<AbstractNativeObject> java_deepspeech::jni::get_abstract_native_object(JNIEnv *env, jobject obj) {
    if (obj == nullptr) {
        return nullptr;
    }
    static jclass clazz = jni_FindClass(env, clazz, AbstractNativeObject_JAVA_TYPE_NAME)
    static jfieldID nativeStatePtr = nullptr;
    if (nativeStatePtr == nullptr) {
        nativeStatePtr = env->GetFieldID(clazz, "nativeStatePtr", "J");
    }
    jlong ptr = env->GetLongField(obj, nativeStatePtr);
    return get_abstract_native_object(ptr);
}

std::shared_ptr<AbstractNativeObject> java_deepspeech::jni::get_abstract_native_object(jlong ptr) {
    if (ptr == 0) {
        return nullptr;
    }
    auto* state = (State*)ptr;
    return state->ptr;
}

void java_deepspeech::jni::dispose_java_native_object_ptr(jlong ptr) {
    if (ptr == 0) {
        return;
    }
    auto* state = (State*)ptr;
    state->ptr = nullptr;
    delete state;
}

void java_deepspeech::jni::set_abstract_native_object(JNIEnv* env, jobject obj, const std::shared_ptr<AbstractNativeObject>& nativeObject) {
    static jclass clazz = jni_FindClass(env, clazz, AbstractNativeObject_JAVA_TYPE_NAME)
    static jfieldID nativeStatePtr = nullptr;
    if (nativeStatePtr == nullptr) {
        nativeStatePtr = env->GetFieldID(clazz, "nativeStatePtr", "J");
    }

    jlong ptr = env->GetLongField(obj, nativeStatePtr);

    State* state = nullptr;
    if (ptr == 0 && nativeObject == nullptr) {
        return;

    } else if (ptr == 0 && nativeObject != nullptr) {
        state = new State();
        state->ptr = nativeObject;

    } else if (ptr != 0 && nativeObject == nullptr) {
        state = (State*)ptr;
        state->ptr = nullptr;
        delete state;
        state = nullptr;

    } else if (ptr != 0 && nativeObject != nullptr) {
        state = (State*)ptr;
        state->ptr = nativeObject;
    }

    env->SetLongField(obj, nativeStatePtr, (jlong)state);
}
