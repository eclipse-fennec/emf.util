/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("MofImporter: DMTF MOF schema (with #pragma include) -> Ecore")
class MofImporterTest {

	@TempDir
	static Path dir;

	static MofImport result;

	@BeforeAll
	static void importSchema() throws IOException {
		result = MofImporter.importFrom(MiniCimSchema.write(dir));
	}

	private static EClass eClass(String name) {
		EClass c = result.getEClass(name);
		assertThat(c).as("EClass %s", name).isNotNull();
		return c;
	}

	private static EStructuralFeature feature(EClass c, String name) {
		EStructuralFeature f = c.getEStructuralFeature(name);
		assertThat(f).as("feature %s.%s", c.getName(), name).isNotNull();
		return f;
	}

	@Test
	@DisplayName("all classes of all included files land in one CIM package, without warnings")
	void packagesAndClasses() {
		assertThat(result.warnings()).isEmpty();
		assertThat(result.packages()).extracting(EPackage::getName).containsExactly("CIM");
		EPackage cim = result.getPackage("CIM");
		assertThat(cim.getNsURI()).isEqualTo("urn:mof:CIM");
		assertThat(cim.getNsPrefix()).isEqualTo("cim");
		assertThat(result.getEClasses()).extracting(EClass::getName).containsExactlyInAnyOrder(
				"CIM_ManagedElement", "CIM_ManagedSystemElement", "CIM_LogicalElement", "CIM_LogicalDevice",
				"CIM_System", "CIM_Component", "CIM_SystemComponent", "CIM_SystemDevice", "CIM_ConcreteJob",
				"CIM_NetworkPort", "CIM_EthernetPort");
		assertThat(result.getEClass("cim_ethernetport")).isSameAs(eClass("CIM_EthernetPort"));
	}

	@Test
	@DisplayName("the result is a valid Ecore model")
	void validates() {
		ResourceSet rs = new ResourceSetImpl();
		Resource r = new ResourceImpl(org.eclipse.emf.common.util.URI.createURI("mem:/cim.ecore"));
		rs.getResources().add(r);
		r.getContents().addAll(result.packages());
		for (EPackage p : result.packages()) {
			Diagnostic d = Diagnostician.INSTANCE.validate(p);
			assertThat(d.getSeverity()).as(d.toString()).isLessThan(Diagnostic.ERROR);
		}
	}

	@Test
	@DisplayName("superclass -> eSuperTypes, Abstract -> abstract")
	void hierarchy() {
		assertThat(eClass("CIM_EthernetPort").getESuperTypes()).containsExactly(eClass("CIM_NetworkPort"));
		assertThat(eClass("CIM_LogicalDevice").getEAllSuperTypes()).containsExactly(eClass("CIM_ManagedElement"),
				eClass("CIM_ManagedSystemElement"), eClass("CIM_LogicalElement"));
		assertThat(eClass("CIM_ManagedElement").isAbstract()).isTrue();
		assertThat(eClass("CIM_LogicalDevice").isAbstract()).isTrue();
		assertThat(eClass("CIM_NetworkPort").isAbstract()).isFalse();
		// Abstract is a declared qualifier, so it is kept too
		assertThat(MofAnnotations.isSet(eClass("CIM_ManagedElement"), "abstract")).isTrue();
	}

	@Test
	@DisplayName("CIM intrinsic types map to object data types; cimType keeps the original")
	void typeMapping() {
		EClass job = eClass("CIM_ConcreteJob");
		EcorePackage ec = EcorePackage.eINSTANCE;
		assertThat(feature(job, "PercentComplete").getEType()).isEqualTo(ec.getEFloatObject());
		assertThat(feature(job, "ElapsedSeconds").getEType()).isEqualTo(ec.getEDoubleObject());
		assertThat(feature(job, "Flag").getEType()).isEqualTo(ec.getECharacterObject());
		assertThat(feature(job, "Delta").getEType()).isEqualTo(ec.getEIntegerObject());
		assertThat(feature(job, "Big").getEType()).isEqualTo(ec.getELongObject());
		assertThat(feature(job, "Mask").getEType()).isEqualTo(ec.getEIntegerObject());

		EClass port = eClass("CIM_NetworkPort");
		assertThat(feature(port, "Speed").getEType()).isEqualTo(ec.getELongObject());
		assertThat(feature(port, "PortType").getEType()).isEqualTo(ec.getEIntegerObject());
		assertThat(MofAnnotations.cimType(feature(port, "Speed"))).isEqualTo("uint64");

		EStructuralFeature installDate = feature(eClass("CIM_ManagedSystemElement"), "InstallDate");
		assertThat(installDate.getEType()).isEqualTo(ec.getEString());
		assertThat(MofAnnotations.cimType(installDate)).isEqualTo("datetime");
		assertThat(feature(eClass("CIM_LogicalDevice"), "PowerManagementSupported").getEType())
				.isEqualTo(ec.getEBooleanObject());
	}

	@Test
	@DisplayName("arrays are unbounded and non-unique; fixed-size arrays keep their size")
	void arrays() {
		EStructuralFeature status = feature(eClass("CIM_ManagedSystemElement"), "OperationalStatus");
		assertThat(status.getUpperBound()).isEqualTo(-1);
		assertThat(status.isUnique()).isFalse();
		assertThat(MofAnnotations.cimType(status)).isEqualTo("uint16[]");

		EStructuralFeature bytes = feature(eClass("CIM_NetworkPort"), "PermanentAddressBytes");
		assertThat(bytes.getUpperBound()).isEqualTo(6);
		assertThat(MofAnnotations.cimType(bytes)).isEqualTo("uint8[6]");
	}

	@Test
	@DisplayName("default values: literal when representable, annotation otherwise")
	void defaults() {
		EAttribute pm = (EAttribute) feature(eClass("CIM_LogicalDevice"), "PowerManagementSupported");
		assertThat(pm.getDefaultValueLiteral()).isEqualTo("false");
		assertThat(pm.getDefaultValue()).isEqualTo(Boolean.FALSE);
		assertThat(((EAttribute) feature(eClass("CIM_LogicalDevice"), "Priority")).getDefaultValue()).isEqualTo(-16);
		assertThat(((EAttribute) feature(eClass("CIM_ConcreteJob"), "Mask")).getDefaultValue()).isEqualTo(10);
		assertThat(((EAttribute) feature(eClass("CIM_ConcreteJob"), "ElapsedSeconds")).getDefaultValue())
				.isEqualTo(15.0);
		assertThat(((EAttribute) feature(eClass("CIM_ConcreteJob"), "Flag")).getDefaultValue()).isEqualTo('x');

		EAttribute max = (EAttribute) feature(eClass("CIM_NetworkPort"), "MaxSpeed");
		assertThat(max.getDefaultValueLiteral()).isNull(); // above Long.MAX_VALUE
		assertThat(MofAnnotations.qualifier(max, MofAnnotations.DEFAULT)).isEqualTo("18446744073709551615");

		EStructuralFeature tags = feature(eClass("CIM_NetworkPort"), "Tags");
		assertThat(MofAnnotations.qualifierValues(tags, MofAnnotations.DEFAULT)).containsExactly("a", "b");
	}

	@Test
	@DisplayName("Description -> GenModel documentation; other qualifiers -> CIM annotation")
	void qualifiers() {
		EClass me = eClass("CIM_ManagedElement");
		assertThat(EcoreUtil.getDocumentation(me)).isEqualTo("Root class of the test hierarchy.");
		assertThat(MofAnnotations.qualifier(me, "Version")).isEqualTo("2.19.0");
		assertThat(MofAnnotations.qualifier(me, "Description")).isNull();
		assertThat(EcoreUtil.getDocumentation(feature(me, "InstanceID"))).isEqualTo("Opaque identifier.");
		assertThat(MofAnnotations.qualifier(feature(me, "Caption"), "MaxLen")).isEqualTo("64");

		EStructuralFeature status = feature(eClass("CIM_ManagedSystemElement"), "OperationalStatus");
		assertThat(MofAnnotations.qualifierValues(status, "ValueMap")).containsExactly("0", "2", "3", "..",
				"0x8000..");
		assertThat(MofAnnotations.qualifierValues(status, "values")).containsExactly("Unknown", "OK", "Degraded",
				"DMTF Reserved", "Vendor \"Reserved\"");
		assertThat(MofAnnotations.qualifier(feature(eClass("CIM_NetworkPort"), "Speed"), "Units"))
				.isEqualTo("Bits per Second");
	}

	@Test
	@DisplayName("REF properties -> non-containment references to the declared class")
	void associations() {
		EClass component = eClass("CIM_Component");
		assertThat(MofAnnotations.isSet(component, "Association")).isTrue();
		EReference group = (EReference) feature(component, "GroupComponent");
		assertThat(group.isContainment()).isFalse();
		assertThat(group.getEType()).isEqualTo(eClass("CIM_ManagedElement"));
		assertThat(group.getUpperBound()).isEqualTo(1);
		assertThat(MofAnnotations.cimType(group)).isEqualTo("CIM_ManagedElement REF");
		assertThat(MofAnnotations.isSet(group, "Aggregate")).isTrue();
		assertThat(MofAnnotations.keys(component)).containsExactly("GroupComponent", "PartComponent");
	}

	@Test
	@DisplayName("overrides are recorded on the subclass instead of redefining the feature")
	void overrides() {
		EClass systemDevice = eClass("CIM_SystemDevice");
		assertThat(systemDevice.getEStructuralFeatures()).isEmpty();
		assertThat(systemDevice.getEAllStructuralFeatures()).extracting(EStructuralFeature::getName)
				.containsExactly("GroupComponent", "PartComponent");
		assertThat(MofAnnotations.qualifier(systemDevice, "override.PartComponent"))
				.isEqualTo("CIM_LogicalDevice REF");
		assertThat(MofAnnotations.qualifier(systemDevice, "override.GroupComponent")).isEqualTo("CIM_System REF");
		assertThat(MofAnnotations.qualifier(systemDevice, "override.GroupComponent.Max")).isEqualTo("1");
		assertThat(MofAnnotations.qualifier(systemDevice, "override.GroupComponent.Override")).isNull();
		// keys are inherited
		assertThat(MofAnnotations.keys(systemDevice)).containsExactly("GroupComponent", "PartComponent");

		EClass ethernet = eClass("CIM_EthernetPort");
		assertThat(ethernet.getEStructuralFeatures()).extracting(EStructuralFeature::getName)
				.containsExactly("MaxDataSize");
		assertThat(MofAnnotations.qualifier(ethernet, "override.PortType")).isEqualTo("uint16");
		assertThat(MofAnnotations.qualifierValues(ethernet, "override.PortType.Values")).containsExactly("Unknown",
				"10BaseT", "10-100BaseT");

		EClass system = eClass("CIM_System");
		assertThat(MofAnnotations.qualifier(system, "override.Name.Description")).isEqualTo("Name of the system.");
	}

	@Test
	@DisplayName("Key properties -> keys annotation, inherited first, overrides adding keys")
	void keys() {
		assertThat(MofAnnotations.keys(eClass("CIM_EthernetPort"))).containsExactly("SystemCreationClassName",
				"SystemName", "CreationClassName", "DeviceID");
		// Name is inherited from CIM_ManagedSystemElement and becomes a key by override
		assertThat(MofAnnotations.keys(eClass("CIM_System"))).containsExactly("CreationClassName", "Name");
		assertThat(MofAnnotations.keys(eClass("CIM_ConcreteJob"))).containsExactly("InstanceID");
		assertThat(MofAnnotations.keys(eClass("CIM_ManagedElement"))).isEmpty();
		assertThat(MofAnnotations.isSet(feature(eClass("CIM_LogicalDevice"), "DeviceID"), "Key")).isTrue();
	}

	@Test
	@DisplayName("methods -> EOperations with typed EParameters")
	void methods() {
		EOperation op = eClass("CIM_LogicalDevice").getEOperations().get(0);
		assertThat(op.getName()).isEqualTo("RequestStateChange");
		assertThat(op.getEType()).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
		assertThat(EcoreUtil.getDocumentation(op)).isEqualTo("Requests a state change.");
		assertThat(MofAnnotations.qualifierValues(op, "ValueMap")).containsExactly("0", "1", "4096");
		assertThat(op.getEParameters()).extracting(EParameter::getName).containsExactly("RequestedState", "Job",
				"TimeoutPeriod");
		EParameter job = op.getEParameters().get(1);
		assertThat(job.getEType()).isEqualTo(eClass("CIM_ConcreteJob"));
		assertThat(MofAnnotations.qualifier(job, "IN")).isEqualTo("false");
		assertThat(MofAnnotations.isSet(job, "OUT")).isTrue();
		assertThat(MofAnnotations.cimType(job)).isEqualTo("CIM_ConcreteJob REF");
		EParameter state = op.getEParameters().get(0);
		assertThat(MofAnnotations.qualifierValues(state, "Values")).containsExactly("Enabled", "Disabled");
		assertThat(MofAnnotations.cimType(op.getEParameters().get(2))).isEqualTo("datetime");
	}

	@Test
	@DisplayName("a subset import pulls in superclasses and REF targets only")
	void subset() throws IOException {
		MofImport subset = MofImporter.importFrom(dir.resolve(MiniCimSchema.ROOT),
				ImportOptions.defaults().withClasses(List.of("cim_systemdevice")));
		assertThat(subset.warnings()).isEmpty();
		assertThat(subset.getEClasses()).extracting(EClass::getName).containsExactlyInAnyOrder("CIM_ManagedElement",
				"CIM_ManagedSystemElement", "CIM_LogicalElement", "CIM_LogicalDevice", "CIM_System",
				"CIM_Component", "CIM_SystemComponent", "CIM_SystemDevice", "CIM_ConcreteJob");
	}

	@Test
	@DisplayName("one package can be forced, with custom nsURI")
	void singlePackage() {
		MofImport single = MofImporter.importFrom(dir.resolve(MiniCimSchema.ROOT), ImportOptions.defaults()
				.withPackageName(c -> "cim").withNsUri(p -> "http://example.org/" + p).withNsPrefix(p -> "c"));
		assertThat(single.packages()).hasSize(1);
		assertThat(single.packages().get(0).getNsURI()).isEqualTo("http://example.org/cim");
		assertThat(single.packages().get(0).getNsPrefix()).isEqualTo("c");
	}

	@Test
	@DisplayName("the resource factory yields the packages through a ResourceSet")
	void resourceFactory() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(MofResourceFactory.EXTENSION,
				new MofResourceFactory());
		Resource r = rs.getResource(
				org.eclipse.emf.common.util.URI.createFileURI(dir.resolve(MiniCimSchema.ROOT).toString()), true);
		assertThat(r.getContents()).hasSize(1);
		EPackage cim = (EPackage) r.getContents().get(0);
		assertThat(cim.getEClassifier("CIM_SystemDevice")).isInstanceOf(EClass.class);
		assertThat(r.getWarnings()).isEmpty();
	}
}
