package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MaxLength {

    /**
     * The maximum length of the value(s) of your entry.
     * Defaults to 32 if the annotation is not set.
     * Numerical values ignore this.
     * @return int value deciding the max character length of your value.
     */
    int value();

    /**
     * The maximum array length of your entry. Ignored if entry is not an array or list.
     * @return int value deciding the maximum length of your array.
     */
    int arrayValue() default -1;

    /**
     * If the array should be fixed size. !!DOES NOT PROTECT AGAINST DIRECT CODE CHANGES!!
     * @return boolean value.
     */
    boolean fixedArray() default false;
}
