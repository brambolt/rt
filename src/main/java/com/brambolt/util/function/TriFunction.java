package com.brambolt.util.function;

import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <S> the type of the third argument to the function
 * @param <R> the type of the result of the function
 **
 * @see java.util.function.BiFunction
 */
@FunctionalInterface
public interface TriFunction<T, U, S, R> {


    /**
     * Applies this function to the parameters.
     *
     * @param t the first argument
     * @param u the second argument
     * @param s the third argument
     * @return the function result
     */
    R apply(T t, U u, S s);
}
