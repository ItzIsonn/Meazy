package me.itzisonn_.meazy.registry.multiple_entry;

import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link MultipleEntryRegistry} with entries stored in {@link List}
 *
 * @param <T> Entries' type
 */
public class OrderedRegistry<T> implements MultipleEntryRegistry<T> {
    private final List<RegistryEntry<T>> entries = new ArrayList<>();

    /**
     * Registers new entry at the end of the list
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     *
     * @throws NullPointerException If given identifier or value is null
     * @throws IllegalArgumentException When not overridable entry with given identifier is already registered
     */
    @Override
    public void register(RegistryIdentifier identifier, T value, boolean overridable) throws NullPointerException, IllegalArgumentException {
        register(identifier, value, overridable, entries.size());
    }

    /**
     * Registers new entry at given index
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     * @param index Index of new entry
     *
     * @throws NullPointerException If given identifier or value is null
     * @throws IllegalArgumentException When not overridable entry with given identifier is already registered or
     *                                  when given index is out of range
     */
    public void register(RegistryIdentifier identifier, T value, boolean overridable, int index) throws NullPointerException, IllegalArgumentException {
        if (identifier == null) throw new NullPointerException("Identifier can't be null");
        if (value == null) throw new NullPointerException("Value can't be null");
        if (index < 0 || index > entries.size()) throw new IllegalArgumentException("Entry's index is out of range");

        RegistryEntry<T> entry = getEntry(identifier);
        if (entry != null && !entry.isOverrideable()) throw new IllegalArgumentException("Entry with identifier " + identifier + " already exists!");
        entries.add(index, new RegistryEntry<>(identifier, value, overridable));
    }

    /**
     * Registers new entry at the start of the list
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     *
     * @throws NullPointerException If given identifier or value is null
     * @throws IllegalArgumentException When not overridable entry with given identifier is already registered
     */
    public void registerFirst(RegistryIdentifier identifier, T value, boolean overridable) throws NullPointerException, IllegalArgumentException {
        register(identifier, value, overridable, 0);
    }

    /**
     * Registers new entry before entry with given identifier
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     * @param beforeIdentifier Before entry with which identifier should this entry be registered
     *
     * @throws NullPointerException If given identifier, value or beforeIdentifier is null
     * @throws IllegalArgumentException When not overridable entry with given identifier is already registered or
     *                                  when this registry doesn't contain entry with given beforeIdentifier
     */
    public void registerBefore(RegistryIdentifier identifier, T value, boolean overridable, RegistryIdentifier beforeIdentifier) throws NullPointerException, IllegalArgumentException {
        if (beforeIdentifier == null) throw new NullPointerException("BeforeIdentifier can't be null");

        RegistryEntry<T> afterEntry = getEntry(beforeIdentifier);
        if (afterEntry == null) throw new IllegalArgumentException("This registry doesn't contain entry with identifier " + beforeIdentifier);
        int index = entries.indexOf(afterEntry);

        register(identifier, value, overridable, index);
    }

    /**
     * Registers new entry after entry with given identifier
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     * @param afterIdentifier After entry with which identifier should this entry be registered
     *
     * @throws NullPointerException If given identifier, value or afterIdentifier is null
     * @throws IllegalArgumentException When not overridable entry with given identifier is already registered or
     *                                  when this registry doesn't contain entry with given afterIdentifier
     */
    public void registerAfter(RegistryIdentifier identifier, T value, boolean overridable, RegistryIdentifier afterIdentifier) throws NullPointerException, IllegalArgumentException {
        if (afterIdentifier == null) throw new NullPointerException("AfterIdentifier can't be null");

        RegistryEntry<T> afterEntry = getEntry(afterIdentifier);
        if (afterEntry == null) throw new IllegalArgumentException("This registry doesn't contain entry with identifier " + afterIdentifier);
        int index = entries.indexOf(afterEntry);

        register(identifier, value, overridable, index + 1);
    }

    /**
     * Registers new entry before entry with given value
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     * @param beforeValue Before entry with which value should this entry be registered
     *
     * @throws NullPointerException If given identifier, value or beforeValue is null
     * @throws IllegalArgumentException When not overridable entry with given identifier is already registered or
     *                                  when this registry doesn't contain entry with given beforeValue
     */
    public void registerBefore(RegistryIdentifier identifier, T value, boolean overridable, T beforeValue) throws NullPointerException, IllegalArgumentException {
        if (beforeValue == null) throw new NullPointerException("BeforeValue can't be null");

        RegistryEntry<T> afterEntry = getEntry(beforeValue);
        if (afterEntry == null) throw new IllegalArgumentException("This registry doesn't contain entry with value " + beforeValue);
        int index = entries.indexOf(afterEntry);

        register(identifier, value, overridable, index);
    }

    /**
     * Registers new entry after entry with given value
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     * @param afterValue After entry with which value should this entry be registered
     *
     * @throws NullPointerException If given identifier, value or afterValue is null
     * @throws IllegalArgumentException When not overridable entry with given identifier is already registered or
     *                                  when this registry doesn't contain entry with given afterValue
     */
    public void registerAfter(RegistryIdentifier identifier, T value, boolean overridable, T afterValue) throws NullPointerException, IllegalArgumentException {
        if (afterValue == null) throw new NullPointerException("AfterValue can't be null");

        RegistryEntry<T> afterEntry = getEntry(afterValue);
        if (afterEntry == null) throw new IllegalArgumentException("This registry doesn't contain entry with value " + afterValue);
        int index = entries.indexOf(afterEntry);

        register(identifier, value, overridable, index + 1);
    }

    @Override
    public List<RegistryEntry<T>> getEntries() {
        return List.copyOf(entries);
    }

    @Override
    public RegistryEntry<T> getEntry(RegistryIdentifier identifier) {
        if (identifier == null) throw new NullPointerException("Identifier can't be null");

        for (RegistryEntry<T> entry : entries) {
            if (entry.getIdentifier().equals(identifier)) return entry;
        }

        return null;
    }

    @Override
    public RegistryEntry<T> getEntry(T value) {
        if (value == null) throw new NullPointerException("Value can't be null");

        for (RegistryEntry<T> entry : entries) {
            if (entry.getValue().equals(value)) return entry;
        }

        return null;
    }

    /**
     * Finds an entry at given index
     *
     * @param index Entry's index
     * @return Entry at given index
     *
     * @throws IllegalArgumentException If given index is out of range
     */
    public RegistryEntry<T> getEntry(int index) {
        if (index < 0 || index > entries.size()) throw new IllegalArgumentException("Entry's index is out of range");

        return entries.get(index);
    }

    /**
     * Finds an entry before entry with given identifier
     *
     * @param identifier Entry's identifier
     * @return Entry before entry with given identifier
     *
     * @throws IllegalArgumentException When this registry doesn't contain entry with given identifier
     */
    public RegistryEntry<T> getEntryBefore(RegistryIdentifier identifier) {
        RegistryEntry<T> entry = getEntry(identifier);
        if (entry == null) throw new IllegalArgumentException("This registry doesn't contain entry with identifier " + identifier);
        return entries.get(entries.indexOf(entry) - 1);
    }

    /**
     * Finds an entry after entry with given identifier
     *
     * @param identifier Entry's identifier
     * @return Entry after entry with given identifier
     *
     * @throws IllegalArgumentException When this registry doesn't contain entry with given identifier
     */
    public RegistryEntry<T> getEntryAfter(RegistryIdentifier identifier) {
        RegistryEntry<T> entry = getEntry(identifier);
        if (entry == null) throw new IllegalArgumentException("This registry doesn't contain entry with identifier " + identifier);
        return entries.get(entries.indexOf(entry) + 1);
    }

    /**
     * Finds an entry before entry with given value
     *
     * @param value Entry's value
     * @return Entry before entry with given value
     *
     * @throws IllegalArgumentException When this registry doesn't contain entry with given value
     */
    public RegistryEntry<T> getEntryBefore(T value) {
        RegistryEntry<T> entry = getEntry(value);
        if (entry == null) throw new IllegalArgumentException("This registry doesn't contain entry with value " + value);
        return entries.get(entries.indexOf(entry) - 1);
    }

    /**
     * Finds an entry after entry with given value
     *
     * @param value Entry's value
     * @return Entry after entry with given value
     *
     * @throws IllegalArgumentException When this registry doesn't contain entry with given value
     */
    public RegistryEntry<T> getEntryAfter(T value) {
        RegistryEntry<T> entry = getEntry(value);
        if (entry == null) throw new IllegalArgumentException("This registry doesn't contain entry with value " + value);
        return entries.get(entries.indexOf(entry) + 1);
    }
}