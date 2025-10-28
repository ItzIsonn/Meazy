package me.itzisonn_.meazy.runtime.native_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that method corresponds to meazy function with given id (or auto)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Function {
    /**
     * @return Id of native method in code
     */
    String value() default "";
}
