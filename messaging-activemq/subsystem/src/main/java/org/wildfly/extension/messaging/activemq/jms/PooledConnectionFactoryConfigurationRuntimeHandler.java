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

package org.wildfly.extension.messaging.activemq.jms;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.PathAddress;
import org.jboss.dmr.ModelNode;

/**
 * Read handler for deployed Jakarta Messaging pooled connection factories
 *
 * @author Stuart Douglas
 */
public class PooledConnectionFactoryConfigurationRuntimeHandler extends AbstractJMSRuntimeHandler<ModelNode> {

    public static final PooledConnectionFactoryConfigurationRuntimeHandler INSTANCE = new PooledConnectionFactoryConfigurationRuntimeHandler(false);
    public static final PooledConnectionFactoryConfigurationRuntimeHandler EXTERNAL_INSTANCE = new PooledConnectionFactoryConfigurationRuntimeHandler(true);

    private final boolean external;

    private PooledConnectionFactoryConfigurationRuntimeHandler(final boolean external) {
        this.external = external;
    }

    @Override
    protected void executeReadAttribute(final String attributeName, final OperationContext context, final ModelNode connectionFactory, final PathAddress address, final boolean includeDefault) {
        if (connectionFactory.hasDefined(attributeName)) {
            context.getResult().set(connectionFactory.get(attributeName));
        } else {
            ConnectionFactoryAttribute attribute = external ? ExternalPooledConnectionFactoryDefinition.getAttributesMap().get(attributeName) : PooledConnectionFactoryDefinition.getAttributesMap().get(attributeName);
            if (attribute != null && attribute.getDefinition().getDefaultValue() != null && attribute.getDefinition().getDefaultValue().isDefined()) {
                context.getResult().set(attribute.getDefinition().getDefaultValue());
            }
        }
    }

}
