package net.glasslauncher.mods.gcapi3.api;


import net.glasslauncher.mods.gcapi3.impl.GlassYamlWrapper;

import java.lang.reflect.*;

/**
 * Implement in custom types. This listener does not support java builtins because I value what little remains of my sanity.
 */
public interface FieldModifiedListener {

    /**
     * @param field The field. Use the field name as the yaml key if setting values in there.
     * @param glassYamlWrapper The config file at the level the field is at.
     * @param eventSource {@link net.glasslauncher.mods.gcapi3.impl.EventStorage.EventSource}
     */
    void fieldModified(Field field, GlassYamlWrapper glassYamlWrapper, int eventSource);
}
