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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A small, self-written schema laid out like the DMTF CIM Schema distribution: a root file that
 * {@code #pragma include}s {@code qualifiers.mof} and one file per class in sub-directories. Class
 * and property names follow CIM so the mapping can be checked against familiar shapes (keys,
 * associations with narrowed {@code REF} overrides, methods), but the content is reduced and the
 * descriptions are our own.
 */
final class MiniCimSchema {

	static final String ROOT = "mini_schema.mof";

	private MiniCimSchema() {
	}

	static Map<String, String> files() {
		Map<String, String> f = new LinkedHashMap<>();
		f.put(ROOT, """
				// Root file of the test schema, in the layout of cim_schema_<version>.mof
				#pragma locale ("en_US")
				#pragma include ("qualifiers.mof")
				#pragma include ("Core/CIM_ManagedElement.mof")
				#pragma include ("Core/CIM_ManagedSystemElement.mof")
				#pragma include ("Core/CIM_LogicalElement.mof")
				#pragma include ("Core/CIM_LogicalDevice.mof")
				#pragma include ("Core/CIM_System.mof")
				#pragma include ("Core/CIM_Component.mof")
				#pragma include ("Core/CIM_SystemComponent.mof")
				#pragma include ("Core/CIM_SystemDevice.mof")
				#pragma include ("Core/CIM_ConcreteJob.mof")
				#pragma include ("Device/CIM_NetworkPort.mof")
				#pragma include ("Device/CIM_EthernetPort.mof")

				instance of CIM_EthernetPort as $eth0 {
				    DeviceID = "eth0";
				    OperationalStatus = {2};
				};
				""");
		f.put("qualifiers.mof", """
				Qualifier Abstract : boolean = false, Scope(class, association, indication), Flavor(Restricted);
				Qualifier Aggregate : boolean = false, Scope(reference), Flavor(DisableOverride);
				Qualifier Association : boolean = false, Scope(association), Flavor(DisableOverride);
				Qualifier Description : string = null, Scope(any), Flavor(EnableOverride, ToSubclass, Translatable);
				Qualifier IN : boolean = true, Scope(parameter), Flavor(DisableOverride);
				Qualifier Key : boolean = false, Scope(property, reference), Flavor(DisableOverride);
				Qualifier Max : uint32 = null, Scope(reference);
				Qualifier MaxLen : uint32 = null, Scope(property, method, parameter);
				Qualifier Min : uint32 = 0, Scope(reference);
				Qualifier OUT : boolean = false, Scope(parameter), Flavor(DisableOverride);
				Qualifier Override : string = null, Scope(property, reference, method), Flavor(Restricted);
				Qualifier Units : string = null, Scope(property, method, parameter), Flavor(Translatable);
				Qualifier ValueMap : string[], Scope(property, method, parameter);
				Qualifier Values : string[], Scope(property, method, parameter), Flavor(Translatable);
				Qualifier Version : string = null, Scope(class, association, indication), Flavor(Restricted, Translatable);
				""");
		f.put("Core/CIM_ManagedElement.mof", """
				/* Root of the test hierarchy. */
				[Abstract, Version ( "2.19.0" ),
				 Description ( "Root class of the "
				    "test hierarchy." )]
				class CIM_ManagedElement {
				      [Description ( "Opaque identifier." )]
				   string InstanceID;
				      [MaxLen ( 64 )]
				   string Caption;
				   string ElementName;
				};
				""");
		f.put("Core/CIM_ManagedSystemElement.mof", """
				[Abstract, Version ( "2.22.0" )]
				class CIM_ManagedSystemElement : CIM_ManagedElement {
				   datetime InstallDate;
				      [MaxLen ( 1024 )]
				   string Name;
				      [ValueMap { "0", "2", "3", "..", "0x8000.." },
				       Values { "Unknown", "OK", "Degraded", "DMTF Reserved", "Vendor \\"Reserved\\"" }]
				   uint16 OperationalStatus[];
				};
				""");
		f.put("Core/CIM_LogicalElement.mof", """
				[Abstract]
				class CIM_LogicalElement : CIM_ManagedSystemElement {
				};
				""");
		f.put("Core/CIM_LogicalDevice.mof", """
				[Abstract]
				class CIM_LogicalDevice : CIM_LogicalElement {
				      [Key, MaxLen ( 256 )]
				   string SystemCreationClassName;
				      [Key, MaxLen ( 256 )]
				   string SystemName;
				      [Key, MaxLen ( 256 )]
				   string CreationClassName;
				      [Key, MaxLen ( 64 )]
				   string DeviceID;
				   boolean PowerManagementSupported = false;
				   sint32 Priority = -0x10;

				      [Description ( "Requests a state change." ),
				       ValueMap { "0", "1", "4096" },
				       Values { "Completed", "Not Supported", "Job Started" }]
				   uint32 RequestStateChange(
				         [IN, ValueMap { "2", "3" }, Values { "Enabled", "Disabled" }]
				      uint16 RequestedState,
				         [IN ( false ), OUT]
				      CIM_ConcreteJob REF Job,
				         [IN]
				      datetime TimeoutPeriod);
				};
				""");
		f.put("Core/CIM_System.mof", """
				[Abstract]
				class CIM_System : CIM_LogicalElement {
				      [Key, MaxLen ( 256 )]
				   string CreationClassName;
				      [Key, Override ( "Name" ), MaxLen ( 256 ), Description ( "Name of the system." )]
				   string Name;
				   string Roles[];
				};
				""");
		f.put("Core/CIM_Component.mof", """
				[Association, Abstract, Aggregation]
				class CIM_Component {
				      [Key, Aggregate]
				   CIM_ManagedElement REF GroupComponent;
				      [Key]
				   CIM_ManagedElement REF PartComponent;
				};
				""");
		f.put("Core/CIM_SystemComponent.mof", """
				[Association, Aggregation]
				class CIM_SystemComponent : CIM_Component {
				      [Aggregate, Override ( "GroupComponent" )]
				   CIM_System REF GroupComponent;
				      [Override ( "PartComponent" )]
				   CIM_ManagedSystemElement REF PartComponent;
				};
				""");
		f.put("Core/CIM_SystemDevice.mof", """
				[Association, Aggregation]
				class CIM_SystemDevice : CIM_SystemComponent {
				      [Aggregate, Override ( "GroupComponent" ), Min ( 1 ), Max ( 1 )]
				   CIM_System REF GroupComponent;
				      [Override ( "PartComponent" )]
				   CIM_LogicalDevice REF PartComponent;
				};
				""");
		f.put("Core/CIM_ConcreteJob.mof", """
				class CIM_ConcreteJob : CIM_LogicalElement {
				      [Key, Override ( "InstanceID" )]
				   string InstanceID;
				   real32 PercentComplete;
				   real64 ElapsedSeconds = 1.5e1;
				   char16 Flag = 'x';
				   sint8 Delta;
				   sint64 Big;
				   uint8 Mask = 1010b;
				};
				""");
		f.put("Device/CIM_NetworkPort.mof", """
				class CIM_NetworkPort : CIM_LogicalDevice {
				      [Units ( "Bits per Second" )]
				   uint64 Speed;
				      [ValueMap { "0", "1", "2" }, Values { "Unknown", "Other", "Ethernet" }]
				   uint16 PortType;
				   string NetworkAddresses[];
				   uint8 PermanentAddressBytes[6];
				   uint64 MaxSpeed = 18446744073709551615;
				   string Tags[] = { "a", "b" };
				};
				""");
		f.put("Device/CIM_EthernetPort.mof", """
				class CIM_EthernetPort : CIM_NetworkPort {
				      [Override ( "PortType" ),
				       ValueMap { "0", "50", "51" },
				       Values { "Unknown", "10BaseT", "10-100BaseT" }]
				   uint16 PortType;
				   uint32 MaxDataSize;
				};
				""");
		return f;
	}

	/** Writes the schema below {@code dir} and returns the root file. */
	static Path write(Path dir) throws IOException {
		for (Map.Entry<String, String> e : files().entrySet()) {
			Path file = dir.resolve(e.getKey());
			Files.createDirectories(file.getParent());
			Files.writeString(file, e.getValue(), StandardCharsets.UTF_8);
		}
		return dir.resolve(ROOT);
	}
}
