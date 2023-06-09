/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

package org.wildfly.extension.clustering.server.group;

import org.kohsuke.MetaInfServices;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.server.service.LocalCacheServiceConfiguratorProvider;

/**
 * Provides the requisite builders for a non-clustered cache-based {@link Group} service.
 * @author Paul Ferraro
 */
@MetaInfServices({ LocalCacheServiceConfiguratorProvider.class, org.wildfly.clustering.server.service.group.LocalCacheGroupServiceConfiguratorProvider.class })
public class LocalCacheGroupServiceConfiguratorProvider extends CacheGroupServiceConfiguratorProvider implements org.wildfly.clustering.server.service.group.LocalCacheGroupServiceConfiguratorProvider {

    public LocalCacheGroupServiceConfiguratorProvider() {
        super((name, containerName, cacheName) -> new LocalGroupServiceConfigurator(name));
    }
}
