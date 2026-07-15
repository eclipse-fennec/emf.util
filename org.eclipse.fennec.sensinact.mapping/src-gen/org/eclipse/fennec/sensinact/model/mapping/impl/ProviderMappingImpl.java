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
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.sensinact.model.mapping.AdminMapping;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.fennec.sensinact.model.mapping.ServiceMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Provider Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl#getProviderClasses <em>Provider Classes</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl#getServices <em>Services</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl#getAdmin <em>Admin</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl#isProviderTimestamp <em>Provider Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl#getProfile <em>Profile</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProviderMappingImpl extends MappingImpl implements ProviderMapping {
	/**
	 * The cached value of the '{@link #getProviderClasses() <em>Provider Classes</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProviderClasses()
	 * @generated
	 * @ordered
	 */
	protected EList<EClass> providerClasses;

	/**
	 * The cached value of the '{@link #getServices() <em>Services</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServices()
	 * @generated
	 * @ordered
	 */
	protected EList<ServiceMapping> services;

	/**
	 * The cached value of the '{@link #getAdmin() <em>Admin</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAdmin()
	 * @generated
	 * @ordered
	 */
	protected AdminMapping admin;

	/**
	 * The default value of the '{@link #isProviderTimestamp() <em>Provider Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isProviderTimestamp()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PROVIDER_TIMESTAMP_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isProviderTimestamp() <em>Provider Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isProviderTimestamp()
	 * @generated
	 * @ordered
	 */
	protected boolean providerTimestamp = PROVIDER_TIMESTAMP_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProfile() <em>Profile</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProfile()
	 * @generated
	 * @ordered
	 */
	protected MappingProfile profile;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProviderMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.PROVIDER_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EClass> getProviderClasses() {
		if (providerClasses == null) {
			providerClasses = new EObjectResolvingEList<EClass>(EClass.class, this, MappingPackage.PROVIDER_MAPPING__PROVIDER_CLASSES);
		}
		return providerClasses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ServiceMapping> getServices() {
		if (services == null) {
			services = new EObjectContainmentEList<ServiceMapping>(ServiceMapping.class, this, MappingPackage.PROVIDER_MAPPING__SERVICES);
		}
		return services;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AdminMapping getAdmin() {
		return admin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAdmin(AdminMapping newAdmin, NotificationChain msgs) {
		AdminMapping oldAdmin = admin;
		admin = newAdmin;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MappingPackage.PROVIDER_MAPPING__ADMIN, oldAdmin, newAdmin);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAdmin(AdminMapping newAdmin) {
		if (newAdmin != admin) {
			NotificationChain msgs = null;
			if (admin != null)
				msgs = ((InternalEObject)admin).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MappingPackage.PROVIDER_MAPPING__ADMIN, null, msgs);
			if (newAdmin != null)
				msgs = ((InternalEObject)newAdmin).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MappingPackage.PROVIDER_MAPPING__ADMIN, null, msgs);
			msgs = basicSetAdmin(newAdmin, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROVIDER_MAPPING__ADMIN, newAdmin, newAdmin));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isProviderTimestamp() {
		return providerTimestamp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProviderTimestamp(boolean newProviderTimestamp) {
		boolean oldProviderTimestamp = providerTimestamp;
		providerTimestamp = newProviderTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROVIDER_MAPPING__PROVIDER_TIMESTAMP, oldProviderTimestamp, providerTimestamp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MappingProfile getProfile() {
		if (profile != null && profile.eIsProxy()) {
			InternalEObject oldProfile = (InternalEObject)profile;
			profile = (MappingProfile)eResolveProxy(oldProfile);
			if (profile != oldProfile) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.PROVIDER_MAPPING__PROFILE, oldProfile, profile));
			}
		}
		return profile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MappingProfile basicGetProfile() {
		return profile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProfile(MappingProfile newProfile) {
		MappingProfile oldProfile = profile;
		profile = newProfile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROVIDER_MAPPING__PROFILE, oldProfile, profile));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MappingPackage.PROVIDER_MAPPING__SERVICES:
				return ((InternalEList<?>)getServices()).basicRemove(otherEnd, msgs);
			case MappingPackage.PROVIDER_MAPPING__ADMIN:
				return basicSetAdmin(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_CLASSES:
				return getProviderClasses();
			case MappingPackage.PROVIDER_MAPPING__SERVICES:
				return getServices();
			case MappingPackage.PROVIDER_MAPPING__ADMIN:
				return getAdmin();
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_TIMESTAMP:
				return isProviderTimestamp();
			case MappingPackage.PROVIDER_MAPPING__PROFILE:
				if (resolve) return getProfile();
				return basicGetProfile();
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
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_CLASSES:
				getProviderClasses().clear();
				getProviderClasses().addAll((Collection<? extends EClass>)newValue);
				return;
			case MappingPackage.PROVIDER_MAPPING__SERVICES:
				getServices().clear();
				getServices().addAll((Collection<? extends ServiceMapping>)newValue);
				return;
			case MappingPackage.PROVIDER_MAPPING__ADMIN:
				setAdmin((AdminMapping)newValue);
				return;
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_TIMESTAMP:
				setProviderTimestamp((Boolean)newValue);
				return;
			case MappingPackage.PROVIDER_MAPPING__PROFILE:
				setProfile((MappingProfile)newValue);
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
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_CLASSES:
				getProviderClasses().clear();
				return;
			case MappingPackage.PROVIDER_MAPPING__SERVICES:
				getServices().clear();
				return;
			case MappingPackage.PROVIDER_MAPPING__ADMIN:
				setAdmin((AdminMapping)null);
				return;
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_TIMESTAMP:
				setProviderTimestamp(PROVIDER_TIMESTAMP_EDEFAULT);
				return;
			case MappingPackage.PROVIDER_MAPPING__PROFILE:
				setProfile((MappingProfile)null);
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
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_CLASSES:
				return providerClasses != null && !providerClasses.isEmpty();
			case MappingPackage.PROVIDER_MAPPING__SERVICES:
				return services != null && !services.isEmpty();
			case MappingPackage.PROVIDER_MAPPING__ADMIN:
				return admin != null;
			case MappingPackage.PROVIDER_MAPPING__PROVIDER_TIMESTAMP:
				return providerTimestamp != PROVIDER_TIMESTAMP_EDEFAULT;
			case MappingPackage.PROVIDER_MAPPING__PROFILE:
				return profile != null;
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
		result.append(" (providerTimestamp: ");
		result.append(providerTimestamp);
		result.append(')');
		return result.toString();
	}

} //ProviderMappingImpl
