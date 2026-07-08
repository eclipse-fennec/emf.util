/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Enum mapping edge cases")
class EnumMappingTest {

	/** Builds a package with an enum that has NO zero literal (LOW=1, HIGH=2). */
	private static EPackage buildModel() {
		EcoreFactory ef = EcoreFactory.eINSTANCE;
		EPackage pkg = ef.createEPackage();
		pkg.setName("tasks");
		pkg.setNsURI("http://example.org/tasks");
		pkg.setNsPrefix("tasks");

		EEnum priority = ef.createEEnum();
		priority.setName("Priority");
		addLiteral(ef, priority, "LOW", 1);
		addLiteral(ef, priority, "HIGH", 2);

		EClass task = ef.createEClass();
		task.setName("Task");
		EAttribute level = ef.createEAttribute();
		level.setName("level");
		level.setEType(priority);
		task.getEStructuralFeatures().add(level);

		pkg.getEClassifiers().add(priority);
		pkg.getEClassifiers().add(task);
		return pkg;
	}

	private static void addLiteral(EcoreFactory ef, EEnum e, String name, int value) {
		EEnumLiteral l = ef.createEEnumLiteral();
		l.setName(name);
		l.setValue(value);
		e.getELiterals().add(l);
	}

	@Test
	@DisplayName("synthesizes a zero value for a proto3 enum that lacks one")
	void syntheticZeroValue() {
		EPackage pkg = buildModel();
		String proto = ProtobufSchema.forPackage(pkg).toProtoSource();
		assertThat(proto).contains("enum Priority {");
		assertThat(proto).contains("Priority_UNSPECIFIED = 0;");
		assertThat(proto).contains("LOW = 1;");
		assertThat(proto).contains("HIGH = 2;");
	}

	@Test
	@DisplayName("round-trips an enum-valued attribute")
	void enumRoundTrip() {
		EPackage pkg = buildModel();
		ProtobufSchema schema = ProtobufSchema.forPackage(pkg);
		EClass task = (EClass) pkg.getEClassifier("Task");
		EAttribute level = (EAttribute) task.getEStructuralFeature("level");
		EEnum priority = (EEnum) pkg.getEClassifier("Priority");

		EObject t = pkg.getEFactoryInstance().create(task);
		t.eSet(level, pkg.getEFactoryInstance().createFromString(priority, "HIGH"));

		byte[] bytes = schema.writer().toBytes(t);
		EObject read = schema.reader().fromBytes(bytes, task);

		assertThat(pkg.getEFactoryInstance().convertToString(priority, read.eGet(level))).isEqualTo("HIGH");
	}
}
