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

import java.util.function.Consumer;
import java.util.function.Supplier;

import io.undertow.Handlers;
import io.undertow.predicate.Predicate;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.wildfly.extension.undertow.FilterLocation;
import org.wildfly.extension.undertow.UndertowFilter;

/**
 * @author Tomaz Cerar (c) 2014 Red Hat Inc.
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
public class FilterService implements Service<UndertowFilter>, UndertowFilter {
    private final Consumer<UndertowFilter> serviceConsumer;
    private final Supplier<HandlerWrapper> wrapper;
    private final Supplier<FilterLocation> location;
    private final Predicate predicate;
    private final int priority;

    FilterService(final Consumer<UndertowFilter> serviceConsumer, final Supplier<HandlerWrapper> wrapper, final Supplier<FilterLocation> location, final Predicate predicate, final int priority) {
        this.serviceConsumer = serviceConsumer;
        this.wrapper = wrapper;
        this.location = location;
        this.predicate = predicate;
        this.priority = priority;
    }

    @Override
    public void start(final StartContext context) throws StartException {
        location.get().addFilter(this);
        serviceConsumer.accept(this);
    }

    @Override
    public void stop(final StopContext context) {
        serviceConsumer.accept(null);
        location.get().removeFilter(this);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public UndertowFilter getValue() {
        return this;
    }

    @Override
    public HttpHandler wrap(HttpHandler next) {
        HttpHandler handler = this.wrapper.get().wrap(next);
        return (this.predicate != null) ? Handlers.predicate(this.predicate, handler, next) : handler;
    }
}
