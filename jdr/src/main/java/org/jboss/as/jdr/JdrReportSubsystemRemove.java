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

package org.jboss.as.jdr;

import static org.jboss.as.jdr.JdrReportSubsystemDefinition.JDR_CAPABILITY;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.dmr.ModelNode;

/**
 * Remove the JDR subsystem.
 *
 * @author Brian Stansberry
 */
public class JdrReportSubsystemRemove extends AbstractRemoveStepHandler {

    static final JdrReportSubsystemRemove INSTANCE = new JdrReportSubsystemRemove();

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) {
        context.removeService(JDR_CAPABILITY.getCapabilityServiceName());
    }

    @Override
    protected boolean requiresRuntime(OperationContext context) {
        return context.getProcessType().isServer();
    }
}