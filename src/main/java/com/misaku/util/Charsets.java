package com.misaku.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author misaku
 * @since 2021/2/23 12:24
 */
class Charsets {

    /**
     * Returns the given {@code charset} or the default Charset if {@code charset} is null.
     *
     * @param charset a Charset or null.
     * @return the given {@code charset} or the default Charset if {@code charset} is null.
     */
    static Charset toCharset(final Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    /**
     * Returns the given {@code charset} or the default Charset if {@code charset} is null.
     *
     * @param charsetName a Charset or null.
     * @return the given {@code charset} or the default Charset if {@code charset} is null.
     * @throws UnsupportedCharsetException If no support for the named charset is available in this instance of the Java
     *                                     virtual machine
     */
    static Charset toCharset(final String charsetName) {
        return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    /**
     * Returns the given {@code charset} or the default Charset if {@code charset} is null.
     *
     * @param charsetName a Charset or null.
     * @return the given {@code charset} or the default Charset if {@code charset} is null.
     */
    static String toCharsetName(final String charsetName) {
        return charsetName == null ? Charset.defaultCharset().name() : charsetName;
    }

}
