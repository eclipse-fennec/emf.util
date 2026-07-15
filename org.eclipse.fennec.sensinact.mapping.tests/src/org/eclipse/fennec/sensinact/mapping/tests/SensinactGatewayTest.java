/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.sensinact.core.command.AbstractSensinactCommand;
import org.eclipse.sensinact.core.command.GatewayThread;
import org.eclipse.sensinact.core.model.SensinactModelManager;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.typedevent.annotations.RequireTypedEvent;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * 
 * @author mark
 * @since 11.07.2025
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@RequireTypedEvent
public class SensinactGatewayTest {
	
	@Test
	@DisplayName("Test that SensiNact GatewayThread is available")
	public void testGateWayThread(@InjectService GatewayThread gatewayThread) {
		assertNotNull(gatewayThread);
		gatewayThread.execute(new AbstractSensinactCommand<GatewayThread>() {

			@Override
			protected Promise<GatewayThread> call(SensinactDigitalTwin arg0, SensinactModelManager arg1,
					PromiseFactory arg2) {
				 
				return null;
			}
		});
	}

}
