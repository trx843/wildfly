/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2021, Red Hat, Inc., and individual contributors
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

package org.wildfly.clustering.ejb.infinispan;

import org.infinispan.protostream.SerializationContextInitializer;
import org.wildfly.clustering.ejb.cache.bean.BeanSerializationContextInitializer;
import org.wildfly.clustering.ejb.client.EJBClientSerializationContextInitializer;
import org.wildfly.clustering.ejb.infinispan.bean.InfinispanBeanSerializationContextInitializer;
import org.wildfly.clustering.ejb.infinispan.network.NetworkEJBSerializationContextInitializer;
import org.wildfly.clustering.ejb.infinispan.network.NetworkMarshallingProvider;
import org.wildfly.clustering.ejb.infinispan.timer.TimerSerializationContextInitializer;
import org.wildfly.clustering.marshalling.protostream.ProviderSerializationContextInitializer;
import org.wildfly.clustering.marshalling.protostream.SerializationContextInitializerProvider;

/**
 * {@link SerializationContextInitializer} provider for this module.
 * @author Paul Ferraro
 */
public enum EJBSerializationContextInitializerProvider implements SerializationContextInitializerProvider {

    NETWORK(new ProviderSerializationContextInitializer<>("org.jboss.as.network.proto", NetworkMarshallingProvider.class)),
    INFINISPAN_NETWORK(new NetworkEJBSerializationContextInitializer()),
    EJB_CLIENT(new EJBClientSerializationContextInitializer()),
    BEAN(new BeanSerializationContextInitializer()),
    INFINISPAN(new InfinispanBeanSerializationContextInitializer()),
    TIMER(new TimerSerializationContextInitializer()),
    ;

    private final SerializationContextInitializer initializer;

    EJBSerializationContextInitializerProvider(SerializationContextInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public SerializationContextInitializer getInitializer() {
        return this.initializer;
    }
}
