package me.itzisonn_.meazy.runtime;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ClassLoaderHelper {
    private static final DynamicClassLoader CLASS_LOADER = new DynamicClassLoader();

    private ClassLoaderHelper() {}

    public static Class<?> defineClass(byte[] bytecode) {
        return CLASS_LOADER.define(bytecode);
    }

    private static class DynamicClassLoader extends ClassLoader {
        public Class<?> define(byte[] bytecode) {
            return defineClass(null, bytecode, 0, bytecode.length);
        }
    }
}