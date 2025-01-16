package me.itzisonn_.meazy.registry.multiple_entry;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Pair
 *
 * @param <K> Key's type
 * @param <V> Value's type
 */
@Getter
@EqualsAndHashCode
public class Pair<K, V> {
    /**
     * Key
     */
    private final K key;
    /**
     * Value
     */
    private final V value;

    /**
     * Pair constructor
     *
     * @param key Key
     * @param value Value
     *
     * @throws NullPointerException If key is null
     */
    public Pair(K key, V value) throws NullPointerException {
        if (key == null) throw new NullPointerException("Key can't be null");

        this.key = key;
        this.value = value;
    }
}