/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2021, Red Hat, Inc., and individual contributors
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

package org.wildfly.clustering.ejb.timer;

import java.lang.reflect.Method;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.wildfly.clustering.ee.Batch;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.ee.Restartable;

/**
 * Manages creation, retrieval, and scheduling of timers.
 * @author Paul Ferraro
 * @param <I> the timer identifier type
 * @param <B> the batch type
 */
public interface TimerManager<I, B extends Batch> extends Restartable {

    Timer<I> createTimer(I id, IntervalTimerConfiguration config, Object context);

    Timer<I> createTimer(I id, ScheduleTimerConfiguration config, Object context);

    Timer<I> createTimer(I id, ScheduleTimerConfiguration config, Object context, Method method, int index);

    Timer<I> getTimer(I id);

    Stream<I> getActiveTimers();

    Batcher<B> getBatcher();

    Supplier<I> getIdentifierFactory();
}
