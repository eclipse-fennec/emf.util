# Battery Sensor Profile Example

This directory demonstrates how different LoRaWAN sensor devices can use the same mapping profile to provide consistent battery level information in SensiNact.

## Files

### battery-sensor-profile.xmi
A simple mapping profile that defines a standard structure for battery-powered sensor devices:

- **Profile ID**: `battery-sensor`
- **Required Services**: 
  - `admin` - Basic device administration (optional location and friendly name)
  - `battery` - Battery status information
- **Required Resources**:
  - `battery.level` - Battery level in volts (Double, unit: V)

### dragino-battery-mapping.xmi
Maps the Dragino LSE01 sensor to the battery sensor profile:

- **Mapping ID**: `dragino-battery-sensor`
- **Source**: `DraginoLSE01Uplink/object/batV` (EDouble)
- **Target**: `battery.level` resource
- **Navigation**: Uses two-step feature path navigation to reach the battery voltage value

### em310udl-battery-mapping.xmi
Maps the EM310UDL sensor to the same battery sensor profile:

- **Mapping ID**: `em310udl-battery-sensor`
- **Source**: `EM310UDLUplink/object/battery` (EDouble)
- **Target**: `battery.level` resource  
- **Navigation**: Uses two-step feature path navigation to reach the battery value

## Key Benefits Demonstrated

1. **Vendor Independence**: Two different sensor manufacturers (Dragino and EM310UDL) map to the same profile
2. **Consistent Interface**: Applications can read battery level from `battery.level` regardless of the underlying sensor
3. **Different Field Names**: Source fields (`batV` vs `battery`) are normalized to the same target structure
4. **Navigation Flexibility**: Both mappings use feature path navigation but access differently structured data
5. **Unified Provider Strategy**: Multiple sensor types contribute to a single provider for simplified application access

## Data Flow

### Dragino LSE01
```
DraginoLSE01Uplink
└── object (DecodedObject)
    └── batV (EDouble) → battery.level
```

### EM310UDL
```
EM310UDLUplink
└── object (DecodedObject)
    └── battery (EDouble) → battery.level
```

Both mappings contribute to the same unified SensiNact provider:
```
Provider: battery-sensor (unified from dragino-battery-sensor + em310udl-battery-sensor)
├── admin/
└── battery/
    └── level (Double, V)
```

The profile uses `providerStrategy="UNIFIED"` which means both mappings, despite having different mapping IDs (`dragino-battery-sensor` and `em310udl-battery-sensor`), will both contribute to the same SensiNact provider using the profile's `providerId` (`battery-sensor`).

## Provider Strategy

The mapping profile supports two provider strategies:

- **SEPARATE** (default): Each mapping creates its own provider instance
  - Would create separate `dragino-battery-sensor` and `em310udl-battery-sensor` providers
  - Applications need to know which specific sensor type to query
  - Provider ID = Mapping ID

- **UNIFIED**: All mappings using this profile contribute to a single provider
  - Both `dragino-battery-sensor` and `em310udl-battery-sensor` mappings contribute to the same `battery-sensor` provider
  - Applications can query a single provider regardless of underlying sensor types
  - Provider ID = Profile's `providerId` (from ProfileProvider)
  - Sensor data is aggregated under the common provider structure

## Usage

Applications can read battery levels from both sensor types using the same unified provider:

```java
// Single unified provider for all battery sensors
SensinactProvider provider = twin.getProvider("battery-sensor");

// Same interface regardless of underlying sensor type
Service batteryService = provider.getService("battery");
Resource levelResource = batteryService.getResource("level");
Double batteryLevel = (Double) levelResource.getValue();
```

This example showcases how mapping profiles enable vendor-agnostic IoT device integration through standardized SensiNact provider structures.