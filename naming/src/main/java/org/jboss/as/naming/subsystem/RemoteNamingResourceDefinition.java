/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.as.naming.subsystem;

import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.RuntimePackageDependency;

/**
 * A {@link org.jboss.as.controller.ResourceDefinition} for JNDI bindings
 */
public class RemoteNamingResourceDefinition extends SimpleResourceDefinition {

    private static final String JBOSS_REMOTING = "org.jboss.remoting";
    static final String REMOTING_ENDPOINT_CAPABILITY_NAME = "org.wildfly.remoting.endpoint";

    public static final RuntimeCapability<Void> REMOTE_NAMING_CAPABILITY = RuntimeCapability.Builder.of("org.wildfly.naming.remote")
            .addRequirements(REMOTING_ENDPOINT_CAPABILITY_NAME)
            .build();

    RemoteNamingResourceDefinition() {
        super(new Parameters(NamingSubsystemModel.REMOTE_NAMING_PATH, NamingExtension.getResourceDescriptionResolver(NamingSubsystemModel.REMOTE_NAMING))
                .setAddHandler(RemoteNamingAdd.INSTANCE)
                .setRemoveHandler(RemoteNamingRemove.INSTANCE)
                .addCapabilities(REMOTE_NAMING_CAPABILITY));
    }

    @Override
    public void registerAdditionalRuntimePackages(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerAdditionalRuntimePackages(RuntimePackageDependency.required(JBOSS_REMOTING));
    }
}
