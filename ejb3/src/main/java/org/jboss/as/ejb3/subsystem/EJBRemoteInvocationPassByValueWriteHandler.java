/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.as.ejb3.subsystem;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.ejb3.remote.LocalTransportProvider;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;

/**
 * @author Jaikiran Pai
 */
class EJBRemoteInvocationPassByValueWriteHandler extends AbstractWriteAttributeHandler<Void> {

    public static final EJBRemoteInvocationPassByValueWriteHandler INSTANCE = new EJBRemoteInvocationPassByValueWriteHandler(EJB3SubsystemRootResourceDefinition.PASS_BY_VALUE);

    private final AttributeDefinition attributeDefinition;

    private EJBRemoteInvocationPassByValueWriteHandler(final AttributeDefinition attributeDefinition) {
        super(attributeDefinition);
        this.attributeDefinition = attributeDefinition;
    }

    @Override
    protected boolean applyUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
                                           ModelNode resolvedValue, ModelNode currentValue, HandbackHolder<Void> handbackHolder) throws OperationFailedException {
        final ModelNode model = context.readResource(PathAddress.EMPTY_ADDRESS).getModel();
        updateDefaultLocalEJBReceiverService(context, model);

        return false;
    }

    @Override
    protected void revertUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
                                         ModelNode valueToRestore, ModelNode valueToRevert, Void handback) throws OperationFailedException {
        final ModelNode restored = context.readResource(PathAddress.EMPTY_ADDRESS).getModel().clone();
        restored.get(attributeName).set(valueToRestore);
        updateDefaultLocalEJBReceiverService(context, restored);
    }

    void updateDefaultLocalEJBReceiverService(final OperationContext context, final ModelNode model) throws OperationFailedException {

        final ModelNode passByValueModel = this.attributeDefinition.resolveModelAttribute(context, model);
        final ServiceRegistry registry = context.getServiceRegistry(true);
        final ServiceName localTransportProviderServiceName;
        if (passByValueModel.isDefined()) {
            final boolean passByValue = passByValueModel.asBoolean(true);
            if (passByValue) {
                localTransportProviderServiceName = LocalTransportProvider.BY_VALUE_SERVICE_NAME;
            } else {
                localTransportProviderServiceName = LocalTransportProvider.BY_REFERENCE_SERVICE_NAME;
            }
        } else {
            localTransportProviderServiceName = LocalTransportProvider.BY_VALUE_SERVICE_NAME;
        }
        // uninstall the existing default local Jakarta Enterprise Beans receiver service
        final ServiceController<?> existingDefaultLocalEJBReceiverServiceController = registry.getService(LocalTransportProvider.DEFAULT_LOCAL_TRANSPORT_PROVIDER_SERVICE_NAME);
        if (existingDefaultLocalEJBReceiverServiceController != null) {
            context.removeService(existingDefaultLocalEJBReceiverServiceController);
        }
        // now install the new default local Jakarta Enterprise Beans receiver service which points to an existing Local Jakarta Enterprise Beans receiver service
        final ServiceName sn = LocalTransportProvider.DEFAULT_LOCAL_TRANSPORT_PROVIDER_SERVICE_NAME;
        final ServiceBuilder<?> sb = context.getServiceTarget().addService(sn);
        final Consumer<LocalTransportProvider> transportConsumer = sb.provides(sn);
        final Supplier<LocalTransportProvider> transportSupplier = sb.requires(localTransportProviderServiceName);
        sb.setInstance(new DefaultLocalTransportProviderService(transportConsumer, transportSupplier));
        sb.install();
    }

    private static final class DefaultLocalTransportProviderService implements Service {
        private final Consumer<LocalTransportProvider> transportConsumer;
        private final Supplier<LocalTransportProvider> transportSupplier;
        private DefaultLocalTransportProviderService(final Consumer<LocalTransportProvider> transportConsumer, final Supplier<LocalTransportProvider> transportSupplier) {
            this.transportConsumer = transportConsumer;
            this.transportSupplier = transportSupplier;
        }

        @Override
        public void start(final StartContext startContext) {
            transportConsumer.accept(transportSupplier.get());
        }

        @Override
        public void stop(final StopContext stopContext) {
            transportConsumer.accept(null);
        }
    }
}
