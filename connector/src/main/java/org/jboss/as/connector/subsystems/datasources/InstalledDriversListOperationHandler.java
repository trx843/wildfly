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

/**
 *
 */
package org.jboss.as.connector.subsystems.datasources;

import static org.jboss.as.connector.subsystems.datasources.Constants.DATASOURCE_CLASS_INFO;
import static org.jboss.as.connector.subsystems.datasources.Constants.DEPLOYMENT_NAME;
import static org.jboss.as.connector.subsystems.datasources.Constants.DRIVER_CLASS_NAME;
import static org.jboss.as.connector.subsystems.datasources.Constants.DRIVER_DATASOURCE_CLASS_NAME;
import static org.jboss.as.connector.subsystems.datasources.Constants.DRIVER_MAJOR_VERSION;
import static org.jboss.as.connector.subsystems.datasources.Constants.DRIVER_MINOR_VERSION;
import static org.jboss.as.connector.subsystems.datasources.Constants.DRIVER_MODULE_NAME;
import static org.jboss.as.connector.subsystems.datasources.Constants.DRIVER_XA_DATASOURCE_CLASS_NAME;
import static org.jboss.as.connector.subsystems.datasources.Constants.INSTALLED_DRIVER_NAME;
import static org.jboss.as.connector.subsystems.datasources.Constants.JDBC_COMPLIANT;
import static org.jboss.as.connector.subsystems.datasources.Constants.MODULE_SLOT;
import static org.jboss.as.connector.subsystems.datasources.Constants.PROFILE;
import static org.jboss.as.connector.subsystems.datasources.GetDataSourceClassInfoOperationHandler.dsClsInfoNode;

import org.jboss.as.connector.logging.ConnectorLogger;
import org.jboss.as.connector.services.driver.InstalledDriver;
import org.jboss.as.connector.services.driver.registry.DriverRegistry;
import org.jboss.as.connector.util.ConnectorServices;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.server.Services;
import org.jboss.as.server.moduleservice.ServiceModuleLoader;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceRegistry;

/**
 * Reads the "installed-drivers" attribute.
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public class InstalledDriversListOperationHandler implements OperationStepHandler {

    public static final InstalledDriversListOperationHandler INSTANCE = new InstalledDriversListOperationHandler();

    private InstalledDriversListOperationHandler() {
    }

    public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
        if (context.isNormalServer()) {
            context.addStep(new OperationStepHandler() {
                public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                    ServiceRegistry registry = context.getServiceRegistry(false);
                    DriverRegistry driverRegistry = (DriverRegistry)registry.getRequiredService(ConnectorServices.JDBC_DRIVER_REGISTRY_SERVICE).getValue();
                    ServiceModuleLoader serviceModuleLoader = (ServiceModuleLoader)registry.getRequiredService(Services.JBOSS_SERVICE_MODULE_LOADER).getValue();
                    Resource rootNode = context.readResourceFromRoot(PathAddress.EMPTY_ADDRESS, false);
                    ModelNode rootModel = rootNode.getModel();
                    String profile = rootModel.hasDefined("profile-name") ? rootModel.get("profile-name").asString() : null;

                    ModelNode result = context.getResult();
                    for (InstalledDriver driver : driverRegistry.getInstalledDrivers()) {
                        ModelNode driverNode = new ModelNode();
                        driverNode.get(INSTALLED_DRIVER_NAME.getName()).set(driver.getDriverName());
                        if (driver.isFromDeployment()) {
                            driverNode.get(DEPLOYMENT_NAME.getName()).set(driver.getDriverName());
                            driverNode.get(DRIVER_MODULE_NAME.getName());
                            driverNode.get(MODULE_SLOT.getName());
                            driverNode.get(DRIVER_DATASOURCE_CLASS_NAME.getName());
                            driverNode.get(DRIVER_XA_DATASOURCE_CLASS_NAME.getName());

                        } else {
                            driverNode.get(DEPLOYMENT_NAME.getName());
                            driverNode.get(DRIVER_MODULE_NAME.getName()).set(driver.getModuleName().getName());
                            driverNode.get(MODULE_SLOT.getName()).set(
                                    driver.getModuleName() != null ? driver.getModuleName().getSlot() : "");
                            driverNode.get(DRIVER_DATASOURCE_CLASS_NAME.getName()).set(
                                    driver.getDataSourceClassName() != null ? driver.getDataSourceClassName() : "");
                            driverNode.get(DRIVER_XA_DATASOURCE_CLASS_NAME.getName()).set(
                                    driver.getXaDataSourceClassName() != null ? driver.getXaDataSourceClassName() : "");

                        }
                        driverNode.get(DATASOURCE_CLASS_INFO.getName()).set(
                                dsClsInfoNode(serviceModuleLoader, driver.getModuleName(), driver.getDataSourceClassName(), driver.getXaDataSourceClassName()));
                        driverNode.get(DRIVER_CLASS_NAME.getName()).set(driver.getDriverClassName());
                        driverNode.get(DRIVER_MAJOR_VERSION.getName()).set(driver.getMajorVersion());
                        driverNode.get(DRIVER_MINOR_VERSION.getName()).set(driver.getMinorVersion());
                        driverNode.get(JDBC_COMPLIANT.getName()).set(driver.isJdbcCompliant());
                        if (profile != null) {
                            driverNode.get(PROFILE.getName()).set(profile);
                        }
                        result.add(driverNode);
                    }
                }
            }, OperationContext.Stage.RUNTIME);
        } else {
            context.getFailureDescription().set(ConnectorLogger.ROOT_LOGGER.noMetricsAvailable());
        }
    }
}
