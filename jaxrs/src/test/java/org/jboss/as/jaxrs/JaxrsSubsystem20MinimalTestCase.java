/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.jaxrs;

import java.io.IOException;

import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;

/**
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author <a href="mailto:ema@rehdat.com>Jim Ma</a>
 * @author <a href="mailto:alessio.soldano@jboss.com>Alessio Soldano</a>
 * @author <a href="rsigal@redhat.com>Ron Sigal</a>
 */
public class JaxrsSubsystem20MinimalTestCase extends AbstractSubsystemBaseTest {

    public JaxrsSubsystem20MinimalTestCase() {
        super(JaxrsExtension.SUBSYSTEM_NAME, new JaxrsExtension());
    }

    @Override
    protected String getSubsystemXml() throws IOException {
        return readResource("jaxrs-2.0_minimal.xml");
    }

    @Override
    protected String getSubsystemXsdPath() throws Exception {
        return "schema/jboss-as-jaxrs_2_0.xsd";
    }

    @Override
    public void testSubsystem() throws Exception {
        standardSubsystemTest(null, false);
    }
}
