package net.glasslauncher.mods.gcapi3.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TerribleOrderPreservingMultimap<A, B> implements Map<A, B> {
    private final Map<A, List<B>> entries = new HashMap<>();
    private final List<B> values = new ArrayList<>();


    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return entries.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        //noinspection SuspiciousMethodCalls Trust me this isn't sussy at all, it's just me being lazy as fuck.
        return values.contains(value);
    }

    @Override
    public B get(Object key) {
        List<B> entry = entries.get(key);
        if (entry == null || entry.isEmpty()) {
            return null;
        }
        return entry.get(0);
    }

    /**
     * This doesn't remove the old value, it adds another entry.
     */
    @Nullable
    @Override
    public B put(A key, B value) {
        entries.computeIfAbsent(key, (a -> new ArrayList<>()));
        entries.get(key).add(value);
        values.add(value);
        return value;
    }

    @Override
    public B remove(Object key) {
        //noinspection SuspiciousMethodCalls Not sus, just lazy
        values.removeAll(entries.get(key));
        List<B> entry = entries.remove(key);
        if (entry == null || entry.isEmpty()) {
            return null;
        }
        return entry.get(0);
    }

    @Override
    public void putAll(@NotNull Map<? extends A, ? extends B> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        values.clear();
        entries.clear();
    }

    @NotNull
    @Override
    public Set<A> keySet() {
        return entries.keySet();
    }

    @NotNull
    @Override
    public Collection<B> values() {
        return new ArrayList<>(values);
    }

    @NotNull
    @Override
    public Set<Entry<A, B>> entrySet() {
        return entries.entrySet().stream().flatMap(aListEntry -> {
            Set<Entry<A, B>> set = new HashSet<>();
            for (B entry : aListEntry.getValue()) {
                set.add(new TerribleEntry(aListEntry.getKey(), entry));
            }
            return set.stream();
        }).collect(Collectors.toSet());
    }

    private class TerribleEntry implements Entry<A, B> {
        A key;
        B value;

        public TerribleEntry(A key, B value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public A getKey() {
            return key;
        }

        @Override
        public B getValue() {
            return value;
        }

        @Override
        public B setValue(B value) {
            this.value = value;
            return value;
        }
    }
}
