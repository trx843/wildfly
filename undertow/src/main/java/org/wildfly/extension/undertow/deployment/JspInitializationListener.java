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
package org.wildfly.extension.undertow.deployment;

import org.apache.jasper.runtime.JspApplicationContextImpl;
import org.jboss.as.web.common.ExpressionFactoryWrapper;
import org.wildfly.extension.undertow.ImportedClassELResolver;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.jsp.JspApplicationContext;
import jakarta.servlet.jsp.JspFactory;
import java.util.List;
import org.wildfly.security.manager.WildFlySecurityManager;

/**
 * Listener that sets up the {@link JspApplicationContext} with any wrapped EL expression factories and also
 * setting up any relevant {@link jakarta.el.ELResolver}s
 *
 * @author Stuart Douglas
 */
public class JspInitializationListener implements ServletContextListener {

    public static final String CONTEXT_KEY = "org.jboss.as.web.deployment.JspInitializationListener.wrappers";
    private static final String DISABLE_IMPORTED_CLASS_EL_RESOLVER_PROPERTY = "org.wildfly.extension.undertow.deployment.disableImportedClassELResolver";

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        // if the servlet version is 3.1 or higher, setup a ELResolver which allows usage of static fields java.lang.*
        final ServletContext servletContext = sce.getServletContext();
        final JspApplicationContext jspApplicationContext = JspFactory.getDefaultFactory().getJspApplicationContext(servletContext);
        boolean disableImportedClassELResolver = Boolean.parseBoolean(
                WildFlySecurityManager.getSystemPropertiesPrivileged().getProperty(DISABLE_IMPORTED_CLASS_EL_RESOLVER_PROPERTY));
        if (!disableImportedClassELResolver &&
                (servletContext.getEffectiveMajorVersion() > 3 ||
                (servletContext.getEffectiveMajorVersion() == 3 && servletContext.getEffectiveMinorVersion() >= 1))) {
            jspApplicationContext.addELResolver(new ImportedClassELResolver());
        }
        // setup a wrapped JspApplicationContext if there are any EL expression factory wrappers for this servlet context
        final List<ExpressionFactoryWrapper> expressionFactoryWrappers = (List<ExpressionFactoryWrapper>) sce.getServletContext().getAttribute(CONTEXT_KEY);
        if (expressionFactoryWrappers != null && !expressionFactoryWrappers.isEmpty()) {
            final JspApplicationContextWrapper jspApplicationContextWrapper = new JspApplicationContextWrapper(JspApplicationContextImpl.getInstance(servletContext), expressionFactoryWrappers, sce.getServletContext());
            sce.getServletContext().setAttribute(JspApplicationContextImpl.class.getName(), jspApplicationContextWrapper);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

    }
}
