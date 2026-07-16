# Mapping Profiles

This directory contains mapping profiles that define standardized SensiNact provider structures for different types of sensor devices.

## Overview

Mapping profiles enable multiple vendor-specific sensor models to map to the same consistent SensiNact provider structure. This ensures that applications can interact with sensors from different manufacturers through a unified interface.

## Profile Structure

Each mapping profile defines:

- **Profile ID**: Unique identifier for the profile
- **Name**: Human-readable name
- **Description**: Purpose and usage information
- **Version**: Version for compatibility tracking
- **Provider Structure**: Required and optional services and resources

## Available Profiles

### temperature-sensor-profile.xmi
Standard profile for temperature sensor devices.

**Required Services:**
- `admin` - Device information and location
- `temperature` - Temperature readings with current value

**Optional Services:**
- `status` - Device status information (battery, signal strength)

**Resources:**
- `admin.friendlyName` - Device name (String)
- `admin.location` - Device location (String)
- `temperature.current` - Current temperature (Float, °C)
- `temperature.minimum` - Minimum temperature (Float, °C) [optional]
- `temperature.maximum` - Maximum temperature (Float, °C) [optional]
- `status.battery` - Battery level (Int, %) [optional]
- `status.signal` - Signal strength (Int, dBm) [optional]

### weather-station-profile.xmi
Standard profile for weather station devices.

**Required Services:**
- `admin` - Station information and location
- `temperature` - Temperature measurements
- `wind` - Wind speed and direction
- `pressure` - Atmospheric pressure

**Optional Services:**
- `cloud` - Cloud coverage information
- `rain` - Precipitation probability data
- `warning` - Weather warning information

## Usage

### 1. Create a Profile

```xml
<sensinactMapping:MappingProfile
    profileId="my-sensor-profile"
    name="My Sensor Profile"
    description="Profile for my sensor type"
    version="1.0">
    
    <provider providerId="my-sensor">
        <admin serviceId="admin" required="true" 
               requiresLocation="true" requiresFriendlyName="true">
            <!-- Define required admin resources -->
        </admin>
        
        <services serviceId="measurement" required="true">
            <!-- Define measurement resources -->
        </services>
    </provider>
</sensinactMapping:MappingProfile>
```

### 2. Reference Profile in Mapping

```xml
<sensinactMapping:ProviderMapping mid="my-vendor-sensor">
    <!-- Reference the profile -->
    <profile href="profiles/my-sensor-profile.xmi#/"/>
    
    <!-- Define mapping from domain model to profile structure -->
    <services mid="admin">
        <!-- Map admin service -->
    </services>
    
    <services mid="measurement">
        <!-- Map measurement service -->
    </services>
</sensinactMapping:ProviderMapping>
```

### 3. Validation

The mapping will be automatically validated against the profile:

- **Errors**: Missing required services or resources
- **Warnings**: Type or unit mismatches
- **Success**: Profile compliance confirmed

## Benefits

1. **Standardization**: Consistent provider structures across vendors
2. **Flexibility**: Easy to add new vendors without changing target structure
3. **Maintainability**: Central profile definitions
4. **Interoperability**: Applications can rely on consistent provider interfaces
5. **Validation**: Automatic compliance checking

## Example Use Case

Multiple temperature sensor vendors can all map to the `temperature-sensor-profile`:

- **Vendor A**: Direct attribute mapping
- **Vendor B**: Nested object navigation
- **Vendor C**: Different field names but same semantic meaning

All result in the same SensiNact provider structure, allowing applications to work with any vendor's sensor through the same interface.