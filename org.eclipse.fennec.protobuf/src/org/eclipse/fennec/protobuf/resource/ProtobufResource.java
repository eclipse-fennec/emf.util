/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.protobuf.ProtobufException;
import org.eclipse.fennec.protobuf.ProtobufSchema;
import org.eclipse.fennec.protobuf.ProtobufSchemaCache;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 * An EMF {@link org.eclipse.emf.ecore.resource.Resource Resource} whose payload
 * is Google Protocol Buffers. It plugs into the ordinary EMF API — create it
 * through a {@link ResourceSet} bound to the {@code protobin} extension and use
 * {@code save(...)} / {@code load(...)} as usual.
 * <p>
 * Because Protobuf messages carry no type information, each root object is
 * framed with a small self-describing envelope (its {@code EPackage} nsURI and
 * {@code EClass} name) so {@code load} can reconstruct the exact type. The
 * referenced {@code EPackage} must be registered in the resource set's package
 * registry (or the global registry) at load time. Descriptors are derived from a
 * {@link ProtobufSchema}; pass a shared {@link ProtobufSchemaCache} via
 * {@link #OPTION_SCHEMA_CACHE} to reuse them across resources.
 */
public class ProtobufResource extends ResourceImpl {

	/** Conventional file extension for Protobuf resources. */
	public static final String FILE_EXTENSION = "protobin";

	/** Conventional content type for Protobuf resources. */
	public static final String CONTENT_TYPE = "application/x-protobuf";

	/**
	 * Save/load option key: a shared {@link ProtobufSchemaCache} (e.g. one per
	 * {@code ResourceSet}, set via {@code resourceSet.getLoadOptions()} /
	 * {@code getSaveOptions()}) so schemas are not re-derived for every resource.
	 * When absent, each resource uses its own private cache.
	 */
	public static final String OPTION_SCHEMA_CACHE = "protobuf.schema.cache";

	private final ProtobufSchemaCache localCache = new ProtobufSchemaCache();

	public ProtobufResource(URI uri) {
		super(uri);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		try {
			CodedOutputStream out = CodedOutputStream.newInstance(outputStream);
			List<EObject> roots = getContents();
			out.writeInt32NoTag(roots.size());
			for (EObject root : roots) {
				EClass eClass = root.eClass();
				EPackage ePackage = eClass.getEPackage();
				if (ePackage == null || ePackage.getNsURI() == null) {
					throw new ProtobufException("Root object of type " + eClass.getName()
							+ " has no registered EPackage / nsURI");
				}
				out.writeStringNoTag(ePackage.getNsURI());
				out.writeStringNoTag(eClass.getName());
				out.writeByteArrayNoTag(schema(ePackage, options).writer().toBytes(root));
			}
			out.flush();
		} catch (RuntimeException e) {
			throw record("save", e);
		}
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		try {
			CodedInputStream in = CodedInputStream.newInstance(inputStream);
			int count = in.readInt32();
			if (count < 0) {
				throw new ProtobufException("Corrupt Protobuf resource: negative root count " + count);
			}
			for (int i = 0; i < count; i++) {
				String nsURI = in.readString();
				String className = in.readString();
				byte[] bytes = in.readByteArray();
				EClass eClass = resolve(nsURI, className);
				getContents().add(schema(eClass.getEPackage(), options).reader().fromBytes(bytes, eClass));
			}
		} catch (RuntimeException e) {
			throw record("load", e);
		}
	}

	/** Records the failure as an EMF resource error and wraps it as an {@link IOException}. */
	private IOException record(String operation, RuntimeException cause) {
		getErrors().add(new ProtobufDiagnostic(cause.getMessage(),
				getURI() == null ? null : getURI().toString()));
		return new IOException("Failed to " + operation + " Protobuf resource " + getURI(), cause);
	}

	private ProtobufSchema schema(EPackage ePackage, Map<?, ?> options) {
		return cache(options).get(ePackage);
	}

	/** The shared cache from the options map if present, otherwise this resource's own. */
	private ProtobufSchemaCache cache(Map<?, ?> options) {
		Object shared = options == null ? null : options.get(OPTION_SCHEMA_CACHE);
		return shared instanceof ProtobufSchemaCache c ? c : localCache;
	}

	private EClass resolve(String nsURI, String className) {
		ResourceSet rs = getResourceSet();
		EPackage.Registry registry = rs != null ? rs.getPackageRegistry() : EPackage.Registry.INSTANCE;
		EPackage ePackage = registry.getEPackage(nsURI);
		if (ePackage == null) {
			throw new ProtobufException("No EPackage registered for nsURI " + nsURI
					+ " (register it in the ResourceSet's package registry before loading)");
		}
		EClassifier classifier = ePackage.getEClassifier(className);
		if (!(classifier instanceof EClass eClass)) {
			throw new ProtobufException("No EClass '" + className + "' in package " + nsURI);
		}
		return eClass;
	}
}
