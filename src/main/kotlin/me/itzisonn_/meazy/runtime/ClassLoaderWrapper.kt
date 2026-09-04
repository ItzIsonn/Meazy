package me.itzisonn_.meazy.runtime

class ClassLoaderWrapper {
    private val classLoader = DynamicClassLoader()

    fun defineClass(bytecode: ByteArray) = classLoader.define(bytecode)

    private class DynamicClassLoader : ClassLoader() {
        fun define(bytecode: ByteArray): Class<*> = defineClass(null, bytecode, 0, bytecode.size)
    }
}