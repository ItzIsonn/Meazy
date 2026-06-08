package me.itzisonn_.meazy.runtime

class ClassLoaderWrapper {
    private val classLoader = DynamicClassLoader()

    fun defineClass(bytecode: ByteArray): Class<*> {
        return classLoader.define(bytecode)
    }

    private class DynamicClassLoader : ClassLoader() {
        fun define(bytecode: ByteArray): Class<*> {
            return defineClass(null, bytecode, 0, bytecode.size)
        }
    }
}