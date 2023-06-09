/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.wildfly.clustering.web.infinispan.session;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.function.Consumer;

import org.junit.Test;
import org.wildfly.clustering.Registration;
import org.wildfly.clustering.web.cache.session.SessionAttributesFactory;
import org.wildfly.clustering.web.cache.session.SessionFactory;
import org.wildfly.clustering.web.cache.session.SessionMetaDataFactory;
import org.wildfly.clustering.web.session.ImmutableSession;
import org.wildfly.clustering.web.session.ImmutableSessionAttributes;
import org.wildfly.clustering.web.session.ImmutableSessionMetaData;

/**
 * Unit test for {@link ExpiredSessionRemover}.
 *
 * @author Paul Ferraro
 */
public class ExpiredSessionRemoverTestCase {
    @Test
    public void test() {
        SessionFactory<Object, UUID, UUID, Object> factory = mock(SessionFactory.class);
        SessionMetaDataFactory<UUID> metaDataFactory = mock(SessionMetaDataFactory.class);
        SessionAttributesFactory<Object, UUID> attributesFactory = mock(SessionAttributesFactory.class);
        Consumer<ImmutableSession> listener = mock(Consumer.class);
        ImmutableSessionAttributes expiredAttributes = mock(ImmutableSessionAttributes.class);
        ImmutableSessionMetaData validMetaData = mock(ImmutableSessionMetaData.class);
        ImmutableSessionMetaData expiredMetaData = mock(ImmutableSessionMetaData.class);
        ImmutableSession expiredSession = mock(ImmutableSession.class);

        String missingSessionId = "missing";
        String expiredSessionId = "expired";
        String validSessionId = "valid";

        UUID expiredMetaDataValue = UUID.randomUUID();
        UUID expiredAttributesValue = UUID.randomUUID();
        UUID validMetaDataValue = UUID.randomUUID();

        ExpiredSessionRemover<Object, UUID, UUID, Object> subject = new ExpiredSessionRemover<>(factory);

        try (Registration regisration = subject.register(listener)) {
            when(factory.getMetaDataFactory()).thenReturn(metaDataFactory);
            when(factory.getAttributesFactory()).thenReturn(attributesFactory);
            when(metaDataFactory.tryValue(missingSessionId)).thenReturn(null);
            when(metaDataFactory.tryValue(expiredSessionId)).thenReturn(expiredMetaDataValue);
            when(metaDataFactory.tryValue(validSessionId)).thenReturn(validMetaDataValue);

            when(metaDataFactory.createImmutableSessionMetaData(expiredSessionId, expiredMetaDataValue)).thenReturn(expiredMetaData);
            when(metaDataFactory.createImmutableSessionMetaData(validSessionId, validMetaDataValue)).thenReturn(validMetaData);

            when(expiredMetaData.isExpired()).thenReturn(true);
            when(validMetaData.isExpired()).thenReturn(false);

            when(attributesFactory.findValue(expiredSessionId)).thenReturn(expiredAttributesValue);
            when(attributesFactory.createImmutableSessionAttributes(expiredSessionId, expiredAttributesValue)).thenReturn(expiredAttributes);
            when(factory.createImmutableSession(same(expiredSessionId), same(expiredMetaData), same(expiredAttributes))).thenReturn(expiredSession);

            subject.remove(missingSessionId);
            subject.remove(expiredSessionId);
            subject.remove(validSessionId);

            verify(factory).remove(expiredSessionId);
            verify(factory, never()).remove(missingSessionId);
            verify(factory, never()).remove(validSessionId);

            verify(listener).accept(expiredSession);
        }
    }
}
