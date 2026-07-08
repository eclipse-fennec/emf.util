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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.protobuf.resource.ProtobufResource;
import org.eclipse.fennec.protobuf.resource.ProtobufResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("ProtobufResource save/load through the EMF Resource API")
class ProtobufResourceTest {

	private ShopModel model;

	@BeforeEach
	void setUp() {
		model = new ShopModel();
	}

	private ResourceSet newResourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(ShopModel.NS_URI, model.pkg);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(ProtobufResource.FILE_EXTENSION, new ProtobufResourceFactory());
		return rs;
	}

	private EObject sampleCategory() {
		EObject category = model.newCategory();
		category.eSet(model.categoryName, "Food");
		EObject milk = model.newProduct();
		milk.eSet(model.productName, "Milk");
		milk.eSet(model.productPrice, 1.29d);
		EObject bread = model.newProduct();
		bread.eSet(model.productName, "Bread");
		@SuppressWarnings("unchecked")
		List<EObject> products = (List<EObject>) category.eGet(model.categoryProducts);
		products.add(milk);
		products.add(bread);
		return category;
	}

	@Test
	@DisplayName("saves and loads via a file URL, bound by the 'protobin' extension")
	void fileUrlRoundTrip(@TempDir Path tmp) throws Exception {
		URI uri = URI.createFileURI(tmp.resolve("shop.protobin").toString());

		Resource out = newResourceSet().createResource(uri);
		assertThat(out).isInstanceOf(ProtobufResource.class);
		out.getContents().add(sampleCategory());
		out.save(null);

		assertThat(Files.size(Path.of(uri.toFileString()))).isPositive();

		// Fresh resource set — nothing shared but the (registered) package.
		Resource in = newResourceSet().createResource(uri);
		in.load(null);

		assertThat(in.getContents()).hasSize(1);
		EObject category = in.getContents().get(0);
		assertThat(category.eClass()).isEqualTo(model.category);
		assertThat(category.eGet(model.categoryName)).isEqualTo("Food");
		@SuppressWarnings("unchecked")
		List<EObject> products = (List<EObject>) category.eGet(model.categoryProducts);
		assertThat(products).hasSize(2);
		assertThat(products.get(0).eGet(model.productName)).isEqualTo("Milk");
		assertThat(products.get(0).eGet(model.productPrice)).isEqualTo(1.29d);
	}

	@Test
	@DisplayName("saves and loads through streams")
	void streamRoundTrip() throws Exception {
		Resource out = newResourceSet().createResource(URI.createURI("shop.protobin"));
		out.getContents().add(sampleCategory());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		out.save(baos, null);

		Resource in = newResourceSet().createResource(URI.createURI("shop.protobin"));
		in.load(new ByteArrayInputStream(baos.toByteArray()), null);

		assertThat(in.getContents()).hasSize(1);
		assertThat(in.getContents().get(0).eGet(model.categoryName)).isEqualTo("Food");
	}

	@Test
	@DisplayName("supports multiple root objects in one resource")
	void multipleRoots() throws Exception {
		Resource out = newResourceSet().createResource(URI.createURI("multi.protobin"));
		EObject a = model.newCategory();
		a.eSet(model.categoryName, "A");
		EObject b = model.newCategory();
		b.eSet(model.categoryName, "B");
		out.getContents().add(a);
		out.getContents().add(b);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		out.save(baos, null);

		Resource in = newResourceSet().createResource(URI.createURI("multi.protobin"));
		in.load(new ByteArrayInputStream(baos.toByteArray()), null);

		assertThat(in.getContents()).hasSize(2);
		assertThat(in.getContents().get(0).eGet(model.categoryName)).isEqualTo("A");
		assertThat(in.getContents().get(1).eGet(model.categoryName)).isEqualTo("B");
	}

	@Test
	@DisplayName("resolves same-resource references after reloading under a different URI")
	void sameResourceReferenceResolves() throws Exception {
		EObject category = model.newCategory();
		category.eSet(model.categoryName, "Food");
		EObject milk = model.newProduct();
		milk.eSet(model.productName, "Milk");
		milk.eSet(model.productCategory, category); // non-containment ref to a same-resource object
		@SuppressWarnings("unchecked")
		List<EObject> products = (List<EObject>) category.eGet(model.categoryProducts);
		products.add(milk);

		Resource out = newResourceSet().createResource(URI.createURI("a.protobin"));
		out.getContents().add(category);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		out.save(baos, null);

		// Load under a DIFFERENT URI: a relative href must still resolve into the loaded graph.
		Resource in = newResourceSet().createResource(URI.createURI("b.protobin"));
		in.load(new ByteArrayInputStream(baos.toByteArray()), null);

		EObject loadedCategory = in.getContents().get(0);
		@SuppressWarnings("unchecked")
		EObject loadedMilk = ((List<EObject>) loadedCategory.eGet(model.categoryProducts)).get(0);
		EObject ref = (EObject) loadedMilk.eGet(model.productCategory);

		assertThat(ref.eIsProxy()).isFalse();
		assertThat(ref).isSameAs(loadedCategory);
	}

	@Test
	@DisplayName("reuses a shared schema cache passed via options")
	void sharedSchemaCacheOption() throws Exception {
		ProtobufSchemaCache cache = new ProtobufSchemaCache();
		Map<Object, Object> options = new HashMap<>();
		options.put(ProtobufResource.OPTION_SCHEMA_CACHE, cache);

		Resource r1 = newResourceSet().createResource(URI.createURI("a.protobin"));
		r1.getContents().add(sampleCategory());
		r1.save(new ByteArrayOutputStream(), options);

		Resource r2 = newResourceSet().createResource(URI.createURI("b.protobin"));
		r2.getContents().add(sampleCategory());
		r2.save(new ByteArrayOutputStream(), options);

		// Both resources derived the shop schema through the one shared cache.
		assertThat(cache.size()).isEqualTo(1);
	}
}
