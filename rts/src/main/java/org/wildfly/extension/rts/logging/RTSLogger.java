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

package org.wildfly.extension.rts.logging;

import jakarta.ws.rs.container.ContainerResponseContext;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

import static org.jboss.logging.Logger.Level.ERROR;

/**
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 *
 */
@MessageLogger(projectCode = "WFLYRTS", length = 4)
public interface RTSLogger extends BasicLogger {

    RTSLogger ROOT_LOGGER = Logger.getMessageLogger(RTSLogger.class, "org.wildfly.extension.rts");

    @Message(id = 1, value = "Can't import global transaction to wildfly transaction client.")
    IllegalStateException failueOnImportingGlobalTransactionFromWildflyClient(@Cause jakarta.transaction.SystemException se);

    @LogMessage(level = ERROR)
    @Message(id = 2, value = "Cannot get transaction status on handling response context %s")
    void cannotGetTransactionStatus(ContainerResponseContext responseCtx, @Cause Throwable cause);
}
