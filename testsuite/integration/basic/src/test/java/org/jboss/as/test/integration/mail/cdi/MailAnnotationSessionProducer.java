/*
 * Copyright 2023 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.test.integration.mail.cdi;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.mail.MailSessionDefinition;
import jakarta.mail.Session;

@MailSessionDefinition(
        name = "java:/mail/test-mail-cdi-session-1",
        host = "localhost",
        transportProtocol = "smtp",
        properties = {
                "mail.smtp.host=localhost",
                "mail.smtp.port=5554",
                "mail.smtp.sendpartial=true",
                "mail.debug=true"
        }
)
@MailSessionDefinition(
        name = "java:/mail/test-mail-cdi-session-2",
        host = "localhost",
        transportProtocol = "smtp",
        properties = {
                "mail.smtp.host=localhost",
                "mail.smtp.port=5555",
                "mail.smtp.sendpartial=true",
                "mail.debug=true"
        })
@ApplicationScoped
public class MailAnnotationSessionProducer {

    @Produces
    @Resource(mappedName = "java:/mail/test-mail-cdi-session-1")
    private Session sessionFieldProducer;

    @Resource(mappedName = "java:/mail/test-mail-cdi-session-2")
    private Session sessionMethodProducer;

    @Produces
    @MethodInjectQualifier
    public Session methodProducer() {
        return sessionMethodProducer;
    }
}
