package org.wildstang.framework;

/**
 * Contains utility functions used across the framework.
 */
public class CoreUtils {

    /**
     * Throws a NullPointerException is p_param is null.
     * @param p_param Object to check if null.
     * @param p_message Message to use in exception.
     * @throws NullPointerException
     */
    public static void checkNotNull(Object p_param, String p_message) {
        if (p_param == null) {
            throw new NullPointerException(p_message);
        }
    }
}