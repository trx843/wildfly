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
package org.jboss.as.ejb3.component.stateful;

import java.util.Map;

import org.jboss.as.ee.component.ComponentView;
import org.jboss.as.ee.component.ViewInstanceFactory;
import org.jboss.as.naming.ManagedReference;
import org.jboss.as.naming.ValueManagedReference;
import org.jboss.ejb.client.Affinity;
import org.jboss.ejb.client.EJBClient;
import org.jboss.ejb.client.EJBIdentifier;
import org.jboss.ejb.client.SessionID;
import org.jboss.ejb.client.StatefulEJBLocator;
import org.jboss.ejb.client.StatelessEJBLocator;

/**
 * @author Stuart Douglas
 */
public class StatefulRemoteViewInstanceFactory implements ViewInstanceFactory {

    private final EJBIdentifier identifier;

    public StatefulRemoteViewInstanceFactory(final String applicationName, final String moduleName, final String distinctName, final String beanName) {
        this(new EJBIdentifier(applicationName == null ? "" : applicationName, moduleName, beanName, distinctName));
    }

    public StatefulRemoteViewInstanceFactory(final EJBIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public ManagedReference createViewInstance(final ComponentView componentView, final Map<Object, Object> contextData) throws Exception {
        SessionID sessionID = (SessionID) contextData.get(SessionID.class);
        final StatefulEJBLocator<?> statefulEJBLocator;
        final StatefulSessionComponent statefulSessionComponent = (StatefulSessionComponent) componentView.getComponent();
        if (sessionID == null) {
            statefulEJBLocator = EJBClient.createSession(StatelessEJBLocator.create(componentView.getViewClass(), identifier, Affinity.LOCAL));
        } else {
            statefulEJBLocator = StatefulEJBLocator.create(componentView.getViewClass(), identifier, sessionID, statefulSessionComponent.getCache().getStrongAffinity());
        }
        final Object ejbProxy = EJBClient.createProxy(statefulEJBLocator);
        return new ValueManagedReference(ejbProxy);
    }


}
