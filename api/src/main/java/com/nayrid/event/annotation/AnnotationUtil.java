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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.lang.reflect.AnnotatedElement;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
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

    private static final Cache<AnnotatedElement, Optional<Key>> ANNOTATED_KEY_CACHE = Caffeine.newBuilder()
        .maximumSize(250)
        .expireAfterAccess(Duration.ofSeconds(150))
        .expireAfterWrite(Duration.ofMinutes(5))
        .build();

    /**
     * Gets the key from an annotated element with {@link AnnoKey}.
     *
     * @param element the annotated element
     * @return the key
     * @since 1.0.0
     */
    public static Optional<Key> key(final AnnotatedElement element) {
        return ANNOTATED_KEY_CACHE.get(element, el -> {
            if (!el.isAnnotationPresent(AnnoKey.class)) {
                return Optional.empty();
            }

            return Optional.ofNullable(key(Objects.requireNonNull(el.getAnnotation(AnnoKey.class))));
        });
    }

    private static @Nullable Key key(final AnnoKey annoKey) {
        try {
            return Key.key(annoKey.namespace(), annoKey.value());
        } catch (final InvalidKeyException invalidKeyException) {
            return null;
        }
    }

    private AnnotationUtil() {
    }

}
