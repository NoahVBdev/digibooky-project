package org.metrunui.digibooky.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Checks if a text matches a simple wildcard expression.
 * <p>
 * In the expression, {@code *} means "zero or more characters". All other characters
 * are matched exactly as they are, also characters like {@code .} or {@code +}.
 * Matching ignores upper and lower case, and the whole text has to match.
 * </p>
 * <p>
 * Examples:
 * <ul>
 *     <li>{@code "978*"} matches {@code "9780321146533"}</li>
 *     <li>{@code "*Development"} matches {@code "Test Driven Development"}</li>
 *     <li>{@code "Test"} does not match {@code "Test Driven Development"} (no {@code *} means an exact match)</li>
 * </ul>
 * </p>
 */
public final class WildCardMatcher {
    private final Pattern pattern;

    private WildCardMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Creates a new matcher for the given wildcard expression.
     * <p>
     * The expression is turned into a regex once, so the matcher can be reused
     * to check many values.
     * </p>
     *
     * @param wildCardExpression the expression to match against, for example {@code "978*"}
     * @return a matcher for the given expression
     * @throws NullPointerException if {@code wildCardExpression} is {@code null}
     */
    public static WildCardMatcher of(String wildCardExpression) {
        Objects.requireNonNull(wildCardExpression, "wildcardExpression must not be null");

        String regex = Arrays.stream(wildCardExpression.split("\\*", -1))
                // split with -1 keeps trailing empty parts
                .map(Pattern::quote)
                // everything except * is literal
                .collect(Collectors.joining(".*"));

        return new WildCardMatcher(Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
    }

    /**
     * Checks if the given value matches the wildcard expression of this matcher.
     *
     * @param value the text to check, can be {@code null}
     * @return {@code true} if the value matches, {@code false} if it does not match or is {@code null}
     */
    public boolean matches(String value) {
        return value != null && pattern.matcher(value).matches();
    }
}
