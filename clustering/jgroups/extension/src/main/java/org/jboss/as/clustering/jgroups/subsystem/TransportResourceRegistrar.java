/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.as.clustering.jgroups.subsystem;

import java.util.EnumSet;

import org.jboss.as.clustering.controller.ManagementRegistrar;
import org.jboss.as.clustering.controller.ResourceServiceConfiguratorFactory;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

/**
 * Registers transport definitions, including any definition overrides.
 * @author Paul Ferraro
 */
public class TransportResourceRegistrar implements ManagementRegistrar<ManagementResourceRegistration> {

    enum MulticastTransport {
        UDP;
    }

    enum SocketTransport {
        TCP, TCP_NIO2;
    }

    private final ResourceServiceConfiguratorFactory parentServiceConfiguratorFactory;

    public TransportResourceRegistrar(ResourceServiceConfiguratorFactory parentServiceConfiguratorFactory) {
        this.parentServiceConfiguratorFactory = parentServiceConfiguratorFactory;
    }

    @Override
    public void register(ManagementResourceRegistration registration) {
        new TransportResourceDefinition(this.parentServiceConfiguratorFactory).register(registration);

        for (MulticastTransport transport : EnumSet.allOf(MulticastTransport.class)) {
            new TransportResourceDefinition(transport.name(), MulticastTransportConfigurationServiceConfigurator::new, this.parentServiceConfiguratorFactory).register(registration);
        }

        for (SocketTransport transport : EnumSet.allOf(SocketTransport.class)) {
            new SocketTransportResourceDefinition(transport.name(), SocketTransportConfigurationServiceConfigurator::new, this.parentServiceConfiguratorFactory).register(registration);
        }
    }
}
