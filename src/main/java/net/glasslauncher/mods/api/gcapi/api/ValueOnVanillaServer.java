package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.*;


/**
 * Adding this to a config field will reset it to this value on joining a vanilla server, and return it to what it was on leaving.
 * Use the load listeners if you want to do something more fancy.
 *
 * Due to limitations, you cannot use 0 (false-y) values on ints, floats, or booleans.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ValueOnVanillaServer {
    String stringValue();
    int integerValue();
    float floatValue();
    boolean booleanValue();
}
