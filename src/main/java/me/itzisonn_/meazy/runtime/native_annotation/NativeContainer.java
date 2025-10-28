package me.itzisonn_.meazy.runtime.native_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that class contains methods that is used by native statements
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeContainer {
    /**
     * @return From which files this class's methods are accessible
     */
    String[] value();
}
