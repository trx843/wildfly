/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.appclient.deployment;

import static org.jboss.as.appclient.subsystem.AppClientSubsystemResourceDefinition.APPCLIENT_CAPABILITY;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

import org.jboss.as.appclient.component.ApplicationClientComponentDescription;
import org.jboss.as.appclient.logging.AppClientLogger;
import org.jboss.as.appclient.service.ApplicationClientDeploymentService;
import org.jboss.as.appclient.service.ApplicationClientStartService;
import org.jboss.as.ee.component.Component;
import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.SetupAction;
import org.jboss.as.server.deployment.reflect.ClassReflectionIndex;
import org.jboss.as.server.deployment.reflect.DeploymentReflectionIndex;
import org.jboss.metadata.appclient.spec.ApplicationClientMetaData;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceBuilder;

/**
 * Processor that starts an application client deployment
 *
 * @author Stuart Douglas
 */
public class ApplicationClientStartProcessor implements DeploymentUnitProcessor {

    private final String[] parameters;

    public ApplicationClientStartProcessor(final String[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        final EEModuleDescription moduleDescription = deploymentUnit.getAttachment(org.jboss.as.ee.component.Attachments.EE_MODULE_DESCRIPTION);
        final ApplicationClientMetaData appClientData = deploymentUnit.getAttachment(AppClientAttachments.APPLICATION_CLIENT_META_DATA);
        final DeploymentReflectionIndex deploymentReflectionIndex = deploymentUnit.getAttachment(Attachments.REFLECTION_INDEX);
        final Module module = deploymentUnit.getAttachment(Attachments.MODULE);

        Boolean activate = deploymentUnit.getAttachment(AppClientAttachments.START_APP_CLIENT);
        if (activate == null || !activate) {
            return;
        }
        final Class<?> mainClass = deploymentUnit.getAttachment(AppClientAttachments.MAIN_CLASS);
        if (mainClass == null) {
            throw AppClientLogger.ROOT_LOGGER.cannotStartAppClient(deploymentUnit.getName());
        }
        final ApplicationClientComponentDescription component = deploymentUnit.getAttachment(AppClientAttachments.APPLICATION_CLIENT_COMPONENT);

        Method mainMethod = null;
        Class<?> klass = mainClass;
        while (klass != Object.class) {
            final ClassReflectionIndex index = deploymentReflectionIndex.getClassIndex(klass);
            mainMethod = index.getMethod(void.class, "main", String[].class);
            if (mainMethod != null) {
                break;
            }
            klass = klass.getSuperclass();
        }
        if (mainMethod == null) {
            throw AppClientLogger.ROOT_LOGGER.cannotStartAppClient(deploymentUnit.getName(), mainClass);
        }

        final List<SetupAction> setupActions = deploymentUnit.getAttachmentList(org.jboss.as.ee.component.Attachments.OTHER_EE_SETUP_ACTIONS);

        ServiceBuilder<?> builder = phaseContext.getServiceTarget()
                .addService(deploymentUnit.getServiceName().append(ApplicationClientStartService.SERVICE_NAME));
        Supplier<ApplicationClientDeploymentService> acdsSupplier = builder.requires(APPCLIENT_CAPABILITY.getCapabilityServiceName());
        Supplier<Component> componentSupplier = builder.requires(component.getCreateServiceName());

        final ApplicationClientStartService startService = new ApplicationClientStartService(mainMethod, parameters, moduleDescription.getNamespaceContextSelector(),
                            module.getClassLoader(), setupActions, acdsSupplier, componentSupplier);

        builder.setInstance(startService).install();
    }
}
