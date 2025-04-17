/*
 * MIT License
 *
 * Copyright (c) nayrid.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.nayrid.event.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Utilities for working with and parsing annotations.
 *
 * @since 1.0.0
 */
@NullMarked
public final class AnnotationUtil {

    private static final ConcurrentHashMap<AnnotatedElement, Optional<Key>> KEY_CACHE = new ConcurrentHashMap<>();

    private AnnotationUtil() {
    }

    /**
     * Gets the key from an annotated element with {@link AnnoKey}.
     *
     * @param element the annotated element
     * @return the key
     * @since 1.0.0
     */
    public static Optional<Key> key(final AnnotatedElement element) {
        return KEY_CACHE.computeIfAbsent(element, AnnotationUtil::computeKey);
    }

    @SuppressWarnings("DataFlowIssue")
    private static Optional<Key> computeKey(final AnnotatedElement element) {
        if (!element.isAnnotationPresent(AnnoKey.class)) {
            return Optional.empty();
        }
        return Optional.ofNullable(transformAnnoKey(element.getAnnotation(AnnoKey.class)));
    }

    private static @Nullable Key transformAnnoKey(final AnnoKey annoKey) {
        try {
            return Key.key(annoKey.namespace(), annoKey.value());
        } catch (final InvalidKeyException invalidKeyException) {
            return null;
        }
    }

    /**
     * Clears the key cache.
     *
     * @since 1.0.0
     */
    public static void clearCache() {
        KEY_CACHE.clear();
    }

}
