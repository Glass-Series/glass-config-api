package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.*;

// TODO: Actually implement
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface LongDescription {

    /**
     * !!!Unimplemented!!!
     * To be used in the future for a scrollable full-screen explanation of your config entry or category.
     * @return TBD.
     */
    String value();
}
