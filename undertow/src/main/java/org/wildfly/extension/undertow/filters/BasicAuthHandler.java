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

package org.wildfly.extension.undertow.filters;

import java.util.Collection;
import java.util.Collections;

import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.server.HandlerWrapper;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * TODO: This appears to be useless, we should probably get rid of it, or replace it with something useful
 *
 *
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a> (c) 2013 Red Hat Inc.
 */
@Deprecated
public class BasicAuthHandler extends SimpleFilterDefinition {
    public static final PathElement PATH_ELEMENT = PathElement.pathElement("basic-auth");

    public static final AttributeDefinition SECURITY_DOMAIN = new SimpleAttributeDefinitionBuilder("security-domain", ModelType.STRING)
            .setRequired(true)
            .setRestartAllServices()
            .build();

    private BasicAuthHandler() {
        super(PATH_ELEMENT, BasicAuthHandler::createHandlerWrapper);
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.singleton(SECURITY_DOMAIN);
    }

    static HandlerWrapper createHandlerWrapper(OperationContext context, ModelNode model) {
        return AuthenticationCallHandler::new;
    }
}
