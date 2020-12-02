package com.brambolt.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Convenience functions for working with maps.
 */
public class Maps {

    /**
     * A convenience method for creating maps.
     *
     * <p>The parameters are a sequence of key-value pairs, such that odd-numbered
     * parameters are the keys, and the following even-numbered parameters are
     * the values to assign to the respective keys.</p>
     *
     * <p>For example, <code>asMap&lt;Integer, Integer&gt;(1, 2, 3, 4)</code>
     * produces the map <code>[ 1: 2, 3: 4 ]</code>.</p>
     *
     * @param <K> The key type for the map to create
     * @param <V> The value type for the map to create
     * @param pairs The pairs for the map to create
     * @return The newly created map holding the pairs
     * @throws ArrayIndexOutOfBoundsException if the number of parameters is odd
     * @throws ClassCastException if an odd-numbered parameter is not of the key
     *        type <code>K</code>, or an even-numbered parameter is not of the
     *        value type <code>V</code>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> asMap(Object... pairs) {
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < pairs.length; ++i)
            map.put((K) pairs[i++], (V) pairs[i]);
        return map;
    }

    public static <K, V> String format(Map<K, V> map) {
        return format(map, "\n");
    }

    public static <K, V> String format(Map<K, V> map, String delimiter) {
        return map.keySet().stream().sorted()
            .map(key -> String.format("%s=%s", key, map.get(key)))
            .collect(Collectors.joining(delimiter));
    }

    public static Map<String, Object> merge(Map<String, Object> existing, Map<String, Object> overwrites) {
        Map<String, Object> results = new HashMap<>(existing);
        overwrites.forEach((k, v) -> results.merge(k, v, Maps::overwrite));
        return results;
    }

    @SuppressWarnings("unchecked")
    public static Object overwrite(Object v1, Object v2) {
        if (!(v1 instanceof Map))
            if (v2 instanceof Map)
                throw invalidOverwrite(v1, v2);
            else return v2;
        else {
            if (!(v2 instanceof Map))
                throw invalidOverwrite(v1, v2);
            else
                return merge((Map<String, Object>) v1, (Map<String, Object>) v2);
        }
    }

    private static IllegalStateException invalidOverwrite(Object v1, Object v2) {
        return new IllegalStateException(
            "Invalid replacement of " + v1.toString() + " with " + v2.toString());
    }

    public static Object segmentedGet(Map<String, Object> nested, String segmentedKey) {
        return segmentedGet(nested, convert(segmentedKey));
    }

    @SuppressWarnings("unchecked")
    public static Object segmentedGet(Map<String, Object> nested, List<String> segmentedKey) {
        if (null == segmentedKey)
            return nested;
        switch (segmentedKey.size()) {
            case 0:
                return nested;
            case 1:
                String key = segmentedKey.get(0);
                throwIfNotFound(nested, key);
                return nested.get(key);
            default:
                String segment = segmentedKey.get(0);
                throwIfNotFound(nested, segment);
                return segmentedGet(
                    (Map<String, Object>) nested.get(segment),
                    segmentedKey.subList(1, segmentedKey.size()));

        }
    }

    public static void throwIfNotFound(Map<String, Object> map, String key) {
        if (!map.containsKey(key))
            throw new NoSuchElementException(
                String.format("Not found: %s [%s]", key, format(map)));
    }

    public static List<String> convert(String segmentedKey) {
        return asList(segmentedKey.trim().split("\\."));
    }

    public static Map<String, Object> convert(Properties properties) {
        return convert(properties, getKeys(properties));
    }

    @SuppressWarnings("unchecked")
    public static List<String> getKeys(Properties properties) {
        return (List<String>) java.util.Collections.list(properties.propertyNames());
    }

    public static Map<String, Object> convert(Properties properties, Collection<String> keys) {
        return keys.stream().collect(
            HashMap::new,
            (Map<String, Object> map, String name) -> insert(properties, name, map),
            (Map<String, Object> m, Map<String, Object> u) -> {});
    }

    private static Map<String, Object> insert(Properties properties, String name, Map<String, Object> map) {
        return insert(properties, name, map, asList(name.split("\\.")));
    }

    /**
     * Modifies the parameter map in-place, by reading the property value
     * for the parameter name and creating a nested structure with the value
     * "at the bottom" of the nesting.
     *
     * For example, for the parameters <code>a.b.c.d</code>, <code>{}</code> and
     * <code>c.d</code>, and property value <code>X</code> for the
     * parameter name, the empty map will be modified to <code>{c:{d:X}}</code>.
     *
     * If no value is found the nesting is still created. If the previous example
     * is modified by removing the value <code>X</code> for <code>a.b.c.d</code>
     * then the resulting map will be <code>{c:{}}</code>. This is so the caller
     * can look up <code>a.b.c</code> without a null pointer exception.
     *
     * @param properties The underlying properties collection
     * @param name The property name to look up and use for nesting
     * @param map The out-parameter map to be modified
     * @param segments The segments to create the nesting from
     * @return The modified out-parameter map
     */
    private static Map<String, Object> insert(Properties properties, String name, Map<String, Object> map, List<String> segments) {
        if (segments.size() < 1)
            throw new IllegalArgumentException("Empty segments list [$name] [$map]");
        String key = segments.get(0);
        if (1 < segments.size())
            // We're not yet at the end of the segments list... insert the tail:
            insertTail(properties, name, map, key, segments.subList(1, segments.size()));
        else
            // We've reached the end of the segments list; time to put in the value:
            insertValue(name, map, key, properties.getProperty(name));
        // Return the map, modified to hold the parameter segments with the
        // value for the parameter name at the botton of the nesting:
        return map;
    }

    @SuppressWarnings("unchecked")
    private static void insertTail(Properties properties, String name, Map<String, Object> map, String key, List<String> tail) {
        if (!map.containsKey(key)) {
            // The parameter map does not have a value for the key; then
            // we create a new map that just contains the tail segments
            // with the last segment holding an empty map:
            map.put(key, insert(properties, name, new HashMap<>(), tail));
            return;
        }
        try {
            Object value = map.get(key);
            // The parameter map does have a value for the key; we attempt
            // adding the tail segments and throw an exception if the key
            // value is not a map, or there is a similar conflict further
            // down:
            insert(properties, name, (Map<String, Object>) value, tail);
        } catch (Throwable t) {
            // We can get a ClassCastException here, for example if the value
            // of the key `version` is 1, and we attempt to add `version.short`.
            //
            // Oops, the parameter segments are incompatible with the
            // existing properties; there is no way to proceed (because
            // overwriting is not allowed):
            throw new RuntimeException(String.format(
                "Property insertion for %s failed: [%s]", name, map.get(key)), t);
        }
    }

    private static void insertValue(String name, Map<String, Object> map, String key, String value) {
        if (null == value)
            return; // Nothing to do
        // Don't trim the value or check for the empty string
        if (map.containsKey(key))
            // There is a conflict (and overwriting is not allowed):
            throw new IllegalStateException("Second value found for property " + name + ": [" + map.get(key) + "] [" + value + "]");
        else
            // There is a value, and no conflict - insert it:
            map.put(key, value);
        // If there is no value then we do nothing
    }
}
