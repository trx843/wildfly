/*
 *
 *  JBoss, Home of Professional Open Source.
 *  Copyright 2013, Red Hat, Inc., and individual contributors
 *  as indicated by the @author tags. See the copyright.txt file in the
 *  distribution for a full listing of individual contributors.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * /
 */

package org.jboss.as.mail.extension;

import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.as.controller.PersistentResourceXMLParser;

/**
 * @author Tomaz Cerar (c) 2017 Red Hat Inc.
 */
class MailSubsystemParser4_0 extends PersistentResourceXMLParser {

    @Override
    public PersistentResourceXMLDescription getParserDescription() {
        return builder(MailExtension.SUBSYSTEM_PATH, Namespace.MAIL_4_0.getUriString())
                .addChild(
                        builder(MailExtension.MAIL_SESSION_PATH)
                                .addAttributes(MailSessionDefinition.DEBUG, MailSessionDefinition.JNDI_NAME, MailSessionDefinition.FROM)
                                .addChild(
                                        builder(MailSubsystemModel.SMTP_SERVER_PATH)
                                                .addAttributes(MailServerDefinition.OUTBOUND_SOCKET_BINDING_REF, MailServerDefinition.SSL, MailServerDefinition.TLS, MailServerDefinition.USERNAME, MailServerDefinition.PASSWORD, MailServerDefinition.CREDENTIAL_REFERENCE)
                                                .setXmlElementName(MailSubsystemModel.SMTP_SERVER)

                                )
                                .addChild(
                                        builder(MailSubsystemModel.POP3_SERVER_PATH)
                                                .addAttributes(MailServerDefinition.OUTBOUND_SOCKET_BINDING_REF, MailServerDefinition.SSL, MailServerDefinition.TLS, MailServerDefinition.USERNAME, MailServerDefinition.PASSWORD, MailServerDefinition.CREDENTIAL_REFERENCE)
                                                .setXmlElementName(MailSubsystemModel.POP3_SERVER)
                                )
                                .addChild(
                                        builder(MailSubsystemModel.IMAP_SERVER_PATH)
                                                .addAttributes(MailServerDefinition.OUTBOUND_SOCKET_BINDING_REF, MailServerDefinition.SSL, MailServerDefinition.TLS, MailServerDefinition.USERNAME, MailServerDefinition.PASSWORD, MailServerDefinition.CREDENTIAL_REFERENCE)
                                                .setXmlElementName(MailSubsystemModel.IMAP_SERVER)
                                )
                                .addChild(
                                        builder(MailSubsystemModel.CUSTOM_SERVER_PATH)
                                                .addAttributes(MailServerDefinition.OUTBOUND_SOCKET_BINDING_REF_OPTIONAL, MailServerDefinition.SSL, MailServerDefinition.TLS, MailServerDefinition.USERNAME, MailServerDefinition.PASSWORD, MailServerDefinition.CREDENTIAL_REFERENCE, MailServerDefinition.PROPERTIES)
                                                .setXmlElementName(MailSubsystemModel.CUSTOM_SERVER)
                                )
                )
                .build();
    }
}
