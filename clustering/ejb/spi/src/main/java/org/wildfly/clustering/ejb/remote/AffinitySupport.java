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
package org.wildfly.clustering.ejb.remote;

import org.jboss.ejb.client.Affinity;

/**
 * Defines the affinity requirements for remote clients.
 *
 * @author Paul Ferraro
 *
 * @param <I> the bean type
 */
public interface AffinitySupport<I> {
    /**
     * Returns the strong affinity for all invocations.
     * Strong affinity indicates a strict load balancing requirement.
     * @return an affinity
     */
    Affinity getStrongAffinity();

    /**
     * Returns the weak affinity of the specified bean identifier.
     * Weak affinity indicates a load balancing preference within the confines of the strong affinity.
     * @param id a bean identifier
     * @return an affinity
     */
    Affinity getWeakAffinity(I id);
}
