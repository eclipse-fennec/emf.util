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
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Dient als "Telefonbuch", um Funktionsnamen (Strings) aus der XMI-Datei
 * auf ausführbare Java-Logik (Supplier oder Function) abzubilden.
 */
class FunctionRegistryImpl {
    private final Map<String, Object> registry = new HashMap<>();

    public <T> void registerFunction(String name, T function) {
        registry.put(name, function);
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, Class<T> type) {
        Object function = registry.get(name);
        if (type.isInstance(function)) {
            return (T) function;
        }
        throw new IllegalArgumentException("Keine Funktion vom Typ " + type.getSimpleName() + " für den Namen '" + name + "' registriert.");
    }
}
