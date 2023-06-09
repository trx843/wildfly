/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
import java.util.stream.Collectors;

import org.jboss.as.clustering.controller.CapabilityProvider;
import org.jboss.as.clustering.controller.ChildResourceDefinition;
import org.jboss.as.clustering.controller.FunctionExecutorRegistry;
import org.jboss.as.clustering.controller.ResourceDescriptor;
import org.jboss.as.clustering.controller.ResourceServiceConfigurator;
import org.jboss.as.clustering.controller.ResourceServiceConfiguratorFactory;
import org.jboss.as.clustering.controller.ResourceServiceHandler;
import org.jboss.as.clustering.controller.SimpleResourceRegistrar;
import org.jboss.as.clustering.controller.UnaryRequirementCapability;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jgroups.JChannel;
import org.wildfly.clustering.jgroups.spi.JGroupsRequirement;
import org.wildfly.clustering.server.service.ClusteringRequirement;
import org.wildfly.clustering.service.UnaryRequirement;

/**
 * Definition of a fork resource.
 * @author Paul Ferraro
 */
public class ForkResourceDefinition extends ChildResourceDefinition<ManagementResourceRegistration> {

    public static final PathElement WILDCARD_PATH = pathElement(PathElement.WILDCARD_VALUE);

    public static PathElement pathElement(String name) {
        return PathElement.pathElement("fork", name);
    }

    enum Capability implements CapabilityProvider {
        FORK_CHANNEL(JGroupsRequirement.CHANNEL),
        FORK_CHANNEL_CLUSTER(JGroupsRequirement.CHANNEL_CLUSTER),
        FORK_CHANNEL_FACTORY(JGroupsRequirement.CHANNEL_FACTORY),
        FORK_CHANNEL_MODULE(JGroupsRequirement.CHANNEL_MODULE),
        FORK_CHANNEL_SOURCE(JGroupsRequirement.CHANNEL_SOURCE),
        ;
        private final org.jboss.as.clustering.controller.Capability capability;

        Capability(UnaryRequirement requirement) {
            this.capability = new UnaryRequirementCapability(requirement);
        }

        @Override
        public org.jboss.as.clustering.controller.Capability getCapability() {
            return this.capability;
        }
    }

    static class ForkChannelFactoryServiceConfiguratorFactory implements ResourceServiceConfiguratorFactory {
        @Override
        public ResourceServiceConfigurator createServiceConfigurator(PathAddress address) {
            return new ForkChannelFactoryServiceConfigurator(Capability.FORK_CHANNEL_FACTORY, address);
        }
    }

    private final FunctionExecutorRegistry<JChannel> executors;

    ForkResourceDefinition(FunctionExecutorRegistry<JChannel> executors) {
        super(WILDCARD_PATH, JGroupsExtension.SUBSYSTEM_RESOLVER.createChildResolver(WILDCARD_PATH));
        this.executors = executors;
    }

    @Override
    public ManagementResourceRegistration register(ManagementResourceRegistration parent) {
        ManagementResourceRegistration registration = parent.registerSubModel(this);

        ResourceDescriptor descriptor = new ResourceDescriptor(this.getResourceDescriptionResolver())
                .addCapabilities(Capability.class)
                .addCapabilities(EnumSet.allOf(ClusteringRequirement.class).stream().map(UnaryRequirementCapability::new).collect(Collectors.toList()))
                ;
        ResourceServiceConfiguratorFactory serviceConfiguratorFactory = new ForkChannelFactoryServiceConfiguratorFactory();
        ResourceServiceHandler handler = new ForkServiceHandler(serviceConfiguratorFactory);
        new SimpleResourceRegistrar(descriptor, handler).register(registration);

        new ProtocolResourceRegistrar(serviceConfiguratorFactory, new ForkProtocolRuntimeResourceRegistration(this.executors)).register(registration);

        return registration;
    }
}
