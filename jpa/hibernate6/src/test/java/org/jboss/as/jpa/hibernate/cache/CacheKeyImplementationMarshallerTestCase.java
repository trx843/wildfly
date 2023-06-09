/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2023, Red Hat, Inc., and individual contributors
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

package org.jboss.as.jpa.hibernate.cache;

import java.io.IOException;
import java.util.UUID;

import org.hibernate.cache.internal.CacheKeyImplementation;
import org.junit.Test;
import org.wildfly.clustering.marshalling.Tester;
import org.wildfly.clustering.marshalling.protostream.ProtoStreamTesterFactory;

/**
 * Unit test for {@link CacheKeyImplementationMarshaller}.
 * @author Paul Ferraro
 */
public class CacheKeyImplementationMarshallerTestCase {

    @Test
    public void test() throws IOException {
        Tester<CacheKeyImplementation> tester = ProtoStreamTesterFactory.INSTANCE.createTester();
        UUID id = UUID.randomUUID();
        String entity = "foo";
        String tenant = "bar";
        tester.testKey(new CacheKeyImplementation(id, entity, tenant, id.hashCode()));
        tester.testKey(new CacheKeyImplementation(id, entity, tenant, 1));
    }
}
