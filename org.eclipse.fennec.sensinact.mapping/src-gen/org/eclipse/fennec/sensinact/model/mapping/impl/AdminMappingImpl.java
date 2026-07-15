/*
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
package org.eclipse.fennec.sensinact.model.mapping.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.fennec.sensinact.model.mapping.AdminMapping;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Admin Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getFriendlyName <em>Friendly Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getFriendlyNameFeature <em>Friendly Name Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getProviderPackage <em>Provider Package</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getLatitude <em>Latitude</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getLongitude <em>Longitude</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getElevation <em>Elevation</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getLatitudeRef <em>Latitude Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getLongitudeRef <em>Longitude Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl#getElevationRef <em>Elevation Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AdminMappingImpl extends ServiceMappingImpl implements AdminMapping {
	/**
	 * The default value of the '{@link #getFriendlyName() <em>Friendly Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFriendlyName()
	 * @generated
	 * @ordered
	 */
	protected static final String FRIENDLY_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFriendlyName() <em>Friendly Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFriendlyName()
	 * @generated
	 * @ordered
	 */
	protected String friendlyName = FRIENDLY_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFriendlyNameFeature() <em>Friendly Name Feature</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFriendlyNameFeature()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> friendlyNameFeature;

	/**
	 * The cached value of the '{@link #getProviderPackage() <em>Provider Package</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProviderPackage()
	 * @generated
	 * @ordered
	 */
	protected EPackage providerPackage;

	/**
	 * The default value of the '{@link #getLatitude() <em>Latitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLatitude()
	 * @generated
	 * @ordered
	 */
	protected static final Double LATITUDE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLatitude() <em>Latitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLatitude()
	 * @generated
	 * @ordered
	 */
	protected Double latitude = LATITUDE_EDEFAULT;

	/**
	 * The default value of the '{@link #getLongitude() <em>Longitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLongitude()
	 * @generated
	 * @ordered
	 */
	protected static final Double LONGITUDE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLongitude() <em>Longitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLongitude()
	 * @generated
	 * @ordered
	 */
	protected Double longitude = LONGITUDE_EDEFAULT;

	/**
	 * The default value of the '{@link #getElevation() <em>Elevation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElevation()
	 * @generated
	 * @ordered
	 */
	protected static final Double ELEVATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getElevation() <em>Elevation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElevation()
	 * @generated
	 * @ordered
	 */
	protected Double elevation = ELEVATION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLatitudeRef() <em>Latitude Ref</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLatitudeRef()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> latitudeRef;

	/**
	 * The cached value of the '{@link #getLongitudeRef() <em>Longitude Ref</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLongitudeRef()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> longitudeRef;

	/**
	 * The cached value of the '{@link #getElevationRef() <em>Elevation Ref</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElevationRef()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> elevationRef;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AdminMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.ADMIN_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFriendlyName() {
		return friendlyName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFriendlyName(String newFriendlyName) {
		String oldFriendlyName = friendlyName;
		friendlyName = newFriendlyName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME, oldFriendlyName, friendlyName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getFriendlyNameFeature() {
		if (friendlyNameFeature == null) {
			friendlyNameFeature = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME_FEATURE);
		}
		return friendlyNameFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EPackage getProviderPackage() {
		if (providerPackage != null && providerPackage.eIsProxy()) {
			InternalEObject oldProviderPackage = (InternalEObject)providerPackage;
			providerPackage = (EPackage)eResolveProxy(oldProviderPackage);
			if (providerPackage != oldProviderPackage) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.ADMIN_MAPPING__PROVIDER_PACKAGE, oldProviderPackage, providerPackage));
			}
		}
		return providerPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EPackage basicGetProviderPackage() {
		return providerPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProviderPackage(EPackage newProviderPackage) {
		EPackage oldProviderPackage = providerPackage;
		providerPackage = newProviderPackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.ADMIN_MAPPING__PROVIDER_PACKAGE, oldProviderPackage, providerPackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLatitude(Double newLatitude) {
		Double oldLatitude = latitude;
		latitude = newLatitude;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.ADMIN_MAPPING__LATITUDE, oldLatitude, latitude));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLongitude(Double newLongitude) {
		Double oldLongitude = longitude;
		longitude = newLongitude;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.ADMIN_MAPPING__LONGITUDE, oldLongitude, longitude));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Double getElevation() {
		return elevation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setElevation(Double newElevation) {
		Double oldElevation = elevation;
		elevation = newElevation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.ADMIN_MAPPING__ELEVATION, oldElevation, elevation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getLatitudeRef() {
		if (latitudeRef == null) {
			latitudeRef = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, MappingPackage.ADMIN_MAPPING__LATITUDE_REF);
		}
		return latitudeRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getLongitudeRef() {
		if (longitudeRef == null) {
			longitudeRef = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, MappingPackage.ADMIN_MAPPING__LONGITUDE_REF);
		}
		return longitudeRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getElevationRef() {
		if (elevationRef == null) {
			elevationRef = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, MappingPackage.ADMIN_MAPPING__ELEVATION_REF);
		}
		return elevationRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME:
				return getFriendlyName();
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME_FEATURE:
				return getFriendlyNameFeature();
			case MappingPackage.ADMIN_MAPPING__PROVIDER_PACKAGE:
				if (resolve) return getProviderPackage();
				return basicGetProviderPackage();
			case MappingPackage.ADMIN_MAPPING__LATITUDE:
				return getLatitude();
			case MappingPackage.ADMIN_MAPPING__LONGITUDE:
				return getLongitude();
			case MappingPackage.ADMIN_MAPPING__ELEVATION:
				return getElevation();
			case MappingPackage.ADMIN_MAPPING__LATITUDE_REF:
				return getLatitudeRef();
			case MappingPackage.ADMIN_MAPPING__LONGITUDE_REF:
				return getLongitudeRef();
			case MappingPackage.ADMIN_MAPPING__ELEVATION_REF:
				return getElevationRef();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME:
				setFriendlyName((String)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME_FEATURE:
				getFriendlyNameFeature().clear();
				getFriendlyNameFeature().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__PROVIDER_PACKAGE:
				setProviderPackage((EPackage)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__LATITUDE:
				setLatitude((Double)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__LONGITUDE:
				setLongitude((Double)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__ELEVATION:
				setElevation((Double)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__LATITUDE_REF:
				getLatitudeRef().clear();
				getLatitudeRef().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__LONGITUDE_REF:
				getLongitudeRef().clear();
				getLongitudeRef().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
			case MappingPackage.ADMIN_MAPPING__ELEVATION_REF:
				getElevationRef().clear();
				getElevationRef().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME:
				setFriendlyName(FRIENDLY_NAME_EDEFAULT);
				return;
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME_FEATURE:
				getFriendlyNameFeature().clear();
				return;
			case MappingPackage.ADMIN_MAPPING__PROVIDER_PACKAGE:
				setProviderPackage((EPackage)null);
				return;
			case MappingPackage.ADMIN_MAPPING__LATITUDE:
				setLatitude(LATITUDE_EDEFAULT);
				return;
			case MappingPackage.ADMIN_MAPPING__LONGITUDE:
				setLongitude(LONGITUDE_EDEFAULT);
				return;
			case MappingPackage.ADMIN_MAPPING__ELEVATION:
				setElevation(ELEVATION_EDEFAULT);
				return;
			case MappingPackage.ADMIN_MAPPING__LATITUDE_REF:
				getLatitudeRef().clear();
				return;
			case MappingPackage.ADMIN_MAPPING__LONGITUDE_REF:
				getLongitudeRef().clear();
				return;
			case MappingPackage.ADMIN_MAPPING__ELEVATION_REF:
				getElevationRef().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME:
				return FRIENDLY_NAME_EDEFAULT == null ? friendlyName != null : !FRIENDLY_NAME_EDEFAULT.equals(friendlyName);
			case MappingPackage.ADMIN_MAPPING__FRIENDLY_NAME_FEATURE:
				return friendlyNameFeature != null && !friendlyNameFeature.isEmpty();
			case MappingPackage.ADMIN_MAPPING__PROVIDER_PACKAGE:
				return providerPackage != null;
			case MappingPackage.ADMIN_MAPPING__LATITUDE:
				return LATITUDE_EDEFAULT == null ? latitude != null : !LATITUDE_EDEFAULT.equals(latitude);
			case MappingPackage.ADMIN_MAPPING__LONGITUDE:
				return LONGITUDE_EDEFAULT == null ? longitude != null : !LONGITUDE_EDEFAULT.equals(longitude);
			case MappingPackage.ADMIN_MAPPING__ELEVATION:
				return ELEVATION_EDEFAULT == null ? elevation != null : !ELEVATION_EDEFAULT.equals(elevation);
			case MappingPackage.ADMIN_MAPPING__LATITUDE_REF:
				return latitudeRef != null && !latitudeRef.isEmpty();
			case MappingPackage.ADMIN_MAPPING__LONGITUDE_REF:
				return longitudeRef != null && !longitudeRef.isEmpty();
			case MappingPackage.ADMIN_MAPPING__ELEVATION_REF:
				return elevationRef != null && !elevationRef.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (friendlyName: ");
		result.append(friendlyName);
		result.append(", latitude: ");
		result.append(latitude);
		result.append(", longitude: ");
		result.append(longitude);
		result.append(", elevation: ");
		result.append(elevation);
		result.append(')');
		return result.toString();
	}

} //AdminMappingImpl
