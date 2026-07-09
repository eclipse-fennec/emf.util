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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Mapping edge cases")
class MappingEdgeTest {

	private static EAttribute attr(String name, org.eclipse.emf.ecore.EClassifier type) {
		EAttribute a = EcoreFactory.eINSTANCE.createEAttribute();
		a.setName(name);
		a.setEType(type);
		return a;
	}

	@Test
	@DisplayName("transient features are neither described nor serialized")
	void transientFeaturesSkipped() {
		EcorePackage ec = EcorePackage.eINSTANCE;
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("tr");
		pkg.setNsURI("http://example.org/tr");
		pkg.setNsPrefix("tr");
		EClass node = EcoreFactory.eINSTANCE.createEClass();
		node.setName("Node");
		EAttribute name = attr("name", ec.getEString());
		EAttribute cache = attr("cache", ec.getEString());
		cache.setTransient(true);
		node.getEStructuralFeatures().add(name);
		node.getEStructuralFeatures().add(cache);
		pkg.getEClassifiers().add(node);

		ProtobufSchema schema = ProtobufSchema.forPackage(pkg);
		assertThat(schema.descriptorFor(node).findFieldByName("name")).isNotNull();
		assertThat(schema.descriptorFor(node).findFieldByName("cache")).isNull();

		EObject n = pkg.getEFactoryInstance().create(node);
		n.eSet(name, "keep");
		n.eSet(cache, "runtime-only");
		EObject read = schema.reader().fromBytes(schema.writer().toBytes(n));

		assertThat(read.eGet(name)).isEqualTo("keep");
		assertThat(read.eIsSet(cache)).isFalse();
	}

	@Test
	@DisplayName("rejects feature-name clashes from multiple inheritance with a clear error")
	void duplicateFeatureNameRejected() {
		EcorePackage ec = EcorePackage.eINSTANCE;
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("mi");
		pkg.setNsURI("http://example.org/mi");
		pkg.setNsPrefix("mi");

		EClass a = EcoreFactory.eINSTANCE.createEClass();
		a.setName("A");
		a.getEStructuralFeatures().add(attr("value", ec.getEString()));
		EClass b = EcoreFactory.eINSTANCE.createEClass();
		b.setName("B");
		b.getEStructuralFeatures().add(attr("value", ec.getEInt()));
		EClass c = EcoreFactory.eINSTANCE.createEClass();
		c.setName("C");
		c.getESuperTypes().add(a);
		c.getESuperTypes().add(b);

		pkg.getEClassifiers().add(a);
		pkg.getEClassifiers().add(b);
		pkg.getEClassifiers().add(c);

		assertThatThrownBy(() -> ProtobufSchema.forPackage(pkg))
				.isInstanceOf(ProtobufException.class)
				.hasMessageContaining("Duplicate feature name 'value'");
	}

	private static void annotate(EStructuralFeature f, String key, String value) {
		EAnnotation a = f.getEAnnotation(ProtobufAnnotations.SOURCE);
		if (a == null) {
			a = EcoreFactory.eINSTANCE.createEAnnotation();
			a.setSource(ProtobufAnnotations.SOURCE);
			f.getEAnnotations().add(a);
		}
		a.getDetails().put(key, value);
	}

	@Test
	@DisplayName("an 'ignore'-annotated feature gets no field and is not (de)serialized")
	void ignoredFeatureDropped() {
		EcorePackage ec = EcorePackage.eINSTANCE;
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("ig");
		pkg.setNsURI("http://example.org/ig");
		pkg.setNsPrefix("ig");
		EClass node = EcoreFactory.eINSTANCE.createEClass();
		node.setName("Node");
		EAttribute name = attr("name", ec.getEString());
		EAttribute secret = attr("secret", ec.getEString());
		annotate(secret, ProtobufAnnotations.KEY_IGNORE, "true");
		node.getEStructuralFeatures().add(name);
		node.getEStructuralFeatures().add(secret);
		pkg.getEClassifiers().add(node);

		ProtobufSchema schema = ProtobufSchema.forPackage(pkg);
		assertThat(schema.descriptorFor(node).findFieldByName("secret")).isNull();

		EObject n = pkg.getEFactoryInstance().create(node);
		n.eSet(name, "n");
		n.eSet(secret, "hidden");
		EObject read = schema.reader().fromBytes(schema.writer().toBytes(n));
		assertThat(read.eIsSet(secret)).isFalse();
	}

	@Test
	@DisplayName("a transient feature forced via annotation round-trips")
	void forcedTransientRoundTrips() {
		EcorePackage ec = EcorePackage.eINSTANCE;
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("fo");
		pkg.setNsURI("http://example.org/fo");
		pkg.setNsPrefix("fo");
		EClass node = EcoreFactory.eINSTANCE.createEClass();
		node.setName("Node");
		EAttribute cache = attr("cache", ec.getEString());
		cache.setTransient(true);
		annotate(cache, ProtobufAnnotations.KEY_FORCE_WRITE, "true");
		annotate(cache, ProtobufAnnotations.KEY_FORCE_READ, "true");
		node.getEStructuralFeatures().add(cache);
		pkg.getEClassifiers().add(node);

		ProtobufSchema schema = ProtobufSchema.forPackage(pkg);
		assertThat(schema.descriptorFor(node).findFieldByName("cache")).isNotNull();

		EObject n = pkg.getEFactoryInstance().create(node);
		n.eSet(cache, "forced");
		EObject read = schema.reader().fromBytes(schema.writer().toBytes(n));
		assertThat(read.eGet(cache)).isEqualTo("forced");
	}
}
