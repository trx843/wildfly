/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.marshalling;

import java.io.IOException;
import java.util.function.BiConsumer;

import org.junit.Assert;

/**
 * Generic interface for various marshalling testers.
 * @author Paul Ferraro
 */
public interface Tester<T> {

    default void test(T subject) throws IOException {
        this.test(subject, Assert::assertEquals);
    }

    /**
     * Same as {@link #test(Object)}, but additionally validates equality of hash code.
     * @param subject a test subject
     * @throws IOException if marshalling of the test subject fails
     */
    default void testKey(T subject) throws IOException {
        this.test(subject, (value1, value2) -> {
            Assert.assertEquals(value1, value2);
            Assert.assertEquals(value1.hashCode(), value2.hashCode());
        });
    }

    void test(T subject, BiConsumer<T, T> assertion) throws IOException;
}
