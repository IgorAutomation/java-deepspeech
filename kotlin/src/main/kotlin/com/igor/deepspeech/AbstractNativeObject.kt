package com.igor.deepspeech

open class AbstractNativeObject
    @JvmOverloads constructor(nativeStatePtr: Long) {

    companion object {
        init {
            DeepSpeechNative.prepareNativeLibraries()
        }
    }

    var nativeStatePtr: Long = nativeStatePtr
        private set

    external fun dispose()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AbstractNativeObject
        if (nativeStatePtr != other.nativeStatePtr) return false
        return true
    }

    override fun hashCode(): Int {
        return nativeStatePtr.hashCode()
    }

    override fun toString(): String {
        return "${this::class.simpleName}(nativeStatePtr=$nativeStatePtr)"
    }

}
