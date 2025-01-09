package me.itzisonn_.meazy.registry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Identifier used by registries
 *
 * @see Registry
 * @see RegistryEntry
 */
@Getter
@EqualsAndHashCode
public class RegistryIdentifier {
    /**
     * Identifier's namespace
     */
    private final String namespace;
    /**
     * Identifier's id
     */
    private final String id;

    private RegistryIdentifier(String namespace, String id) throws NullPointerException, IllegalArgumentException {
        if (namespace == null || id == null) throw new NullPointerException("Neither namespace nor id can't be null");
        if (!namespace.matches(Utils.IDENTIFIER_REGEX) || !id.matches(Utils.IDENTIFIER_REGEX)) throw new IllegalArgumentException("Invalid namespace or id present");
        this.namespace = namespace;
        this.id = id;
    }

    /**
     * Creates new RegistryIdentifier
     *
     * @param namespace Identifier's namespace that matches {@link Utils#IDENTIFIER_REGEX}
     * @param id Identifier's id that matches {@link Utils#IDENTIFIER_REGEX}
     * @return New RegistryIdentifier
     *
     * @throws NullPointerException If either namespace or id is null
     * @throws IllegalArgumentException If namespace or id doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public static RegistryIdentifier of(String namespace, String id) throws NullPointerException, IllegalArgumentException {
        return new RegistryIdentifier(namespace, id);
    }

    /**
     * Creates new RegistryIdentifier
     *
     * @param identifier Identifier in format 'namespace:id' where both namespace and id match {@link Utils#IDENTIFIER_REGEX}
     * @return New RegistryIdentifier
     *
     * @throws NullPointerException If given identifier is null
     * @throws IllegalArgumentException If given identifier isn't in required format
     */
    public static RegistryIdentifier of(String identifier) throws NullPointerException, IllegalArgumentException {
        if (identifier == null) throw new IllegalArgumentException("Identifier can't be null");

        Matcher matcher = Pattern.compile("(.+):(.+)").matcher(identifier);
        if (matcher.find()) {
            if (matcher.group(1).matches(Utils.IDENTIFIER_REGEX) && matcher.group(2).matches(Utils.IDENTIFIER_REGEX))
                return new RegistryIdentifier(
                    matcher.group(1),
                    matcher.group(2));
        }

        throw new IllegalArgumentException("Invalid identifier '" + identifier + "'");
    }

    /**
     * Creates new RegistryIdentifier with 'meazy' namespace
     * <p>
     * <i>Recommended to use {@link #of(String, String)} or {@link #of(String)}
     * because 'meazy' namespace belongs to core identifiers</i>
     *
     * @param id Identifier's id that matches {@link Utils#IDENTIFIER_REGEX}
     * @return New RegistryIdentifier
     *
     * @throws NullPointerException If id is null
     * @throws IllegalArgumentException If id doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public static RegistryIdentifier ofDefault(String id) throws NullPointerException, IllegalArgumentException {
        return new RegistryIdentifier("meazy", id);
    }

    /**
     * Gives string in format 'namespace:id'
     *
     * @return String representation of this RegistryIdentifier
     */
    @Override
    public String toString() {
        return namespace + ":" + id;
    }
}