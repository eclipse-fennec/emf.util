/*
 * ******************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 * ******************************************************************
 */
package org.eclipse.fennec.sensinact.mapping;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.eclipse.sensinact.core.model.SensinactModelManager;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;
import org.eclipse.fennec.sensinact.mapping.impl.ProviderModelSensinactMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for ProviderModelMapper general functionality.
 * Tests basic mapper operations including registration, unregistration, and error handling.
 */
@ExtendWith(MockitoExtension.class)
public class ProviderModelMapperTest {

	@Mock
	private SensinactDigitalTwin twin;
	@Mock
	private SensinactModelManager mmgr;

	@Test
	@DisplayName("Constructor should handle null profile registry")
	void constructor_withNullProfileRegistry_doesNotThrow() {
		// Execute & Verify - this should not throw since null profile registry is acceptable
		assertDoesNotThrow(() -> new ProviderModelSensinactMapper.Factory(null).createMapper(null, null));
	}

	@Test
	@DisplayName("registerModelMapping() should handle null mapping gracefully")
	void registerModelMapping_withNullMapping_doesNotThrow() {
		// Create mapper with null dependencies for this specific test
		ProviderModelSensinactMapper mapper = new ProviderModelSensinactMapper.Factory(null).createMapper(null, null);

		// Execute & Verify
		assertDoesNotThrow(() -> mapper.registerModelMapping(null));
	}

	@Test
	@DisplayName("unregisterModelMapping() should handle null mapping gracefully")
	void unregisterModelMapping_withNullMapping_doesNotThrow() {
		// Create mapper with null dependencies for this specific test
		ProviderModelSensinactMapper mapper = new ProviderModelSensinactMapper.Factory(null).createMapper(null, null);
		// Execute & Verify
		assertDoesNotThrow(() -> mapper.unregisterModelMapping(null));
	}

}
