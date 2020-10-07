package com.brambolt.text;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * Simple templating alternative that can be used when an engine like
 * Velocity is overkill.
 */
public class Templates {

    /**
     * The default template variable delimiter.
     */
    public static final String DEFAULT_DELIMITER = "@";

    /**
     * Instantiates the parameter template with the parameter bindings using
     * the default delimiter.
     *
     * @param template The template to instantiate
     * @param bindings The bindings to apply
     * @return The instantiated template
     * @see #bind
     */
    public static String bind(String template, Map<?, ?> bindings) {
        return bind(template, bindings, DEFAULT_DELIMITER);
    }

    /**
     * Instantiates the parameter template with the parameter bindings using
     * the default delimiter.
     *
     * <p>A template is string content like</p>
     * <pre>
     *     "Some text with a @variable@.
     * </pre>
     *
     * <p>The bindings are a simple map like <code>[variable: value]</code>.</p>
     *
     * <p>The result would be</p>
     * <pre>
     *     "Some text with a value.
     * </pre>
     *
     * @param template The string content with template variables
     * @param bindings The bindings to instantiate the template with
     * @param delimiter The delimiter identifying the template variables
     * @return The content produced by instantiating the template with the bindings
     */
    public static String bind(String template, Map<?, ?> bindings, String delimiter) {
        if (null == template)
            return null;
        if (null == bindings || bindings.isEmpty())
            return template;
        String bound = template;
        for (Map.Entry<?, ?> binding : bindings.entrySet()) {
            String key = binding.getKey().toString();
            String pattern = delimiter + key + delimiter;
            Object value = binding.getValue();
            if (null == value)
                throw new IllegalStateException("No value binding for key: " + key);
            String replacement = Matcher.quoteReplacement(binding.getValue().toString());
            bound = bound.replaceAll(pattern, replacement);
        }
        return bound;
    }
}
