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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProfileProvider;
import org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Profile</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl#getProfileId <em>Profile Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl#getProviderStrategy <em>Provider Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl#getProvider <em>Provider</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MappingProfileImpl extends MinimalEObjectImpl.Container implements MappingProfile {
	/**
	 * The default value of the '{@link #getProfileId() <em>Profile Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProfileId()
	 * @generated
	 * @ordered
	 */
	protected static final String PROFILE_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProfileId() <em>Profile Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProfileId()
	 * @generated
	 * @ordered
	 */
	protected String profileId = PROFILE_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = "1.0";

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * The default value of the '{@link #getProviderStrategy() <em>Provider Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProviderStrategy()
	 * @generated
	 * @ordered
	 */
	protected static final ProviderStrategy PROVIDER_STRATEGY_EDEFAULT = ProviderStrategy.SEPARATE;

	/**
	 * The cached value of the '{@link #getProviderStrategy() <em>Provider Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProviderStrategy()
	 * @generated
	 * @ordered
	 */
	protected ProviderStrategy providerStrategy = PROVIDER_STRATEGY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProvider() <em>Provider</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProvider()
	 * @generated
	 * @ordered
	 */
	protected ProfileProvider provider;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MappingProfileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.MAPPING_PROFILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getProfileId() {
		return profileId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProfileId(String newProfileId) {
		String oldProfileId = profileId;
		profileId = newProfileId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.MAPPING_PROFILE__PROFILE_ID, oldProfileId, profileId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.MAPPING_PROFILE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.MAPPING_PROFILE__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.MAPPING_PROFILE__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProviderStrategy getProviderStrategy() {
		return providerStrategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProviderStrategy(ProviderStrategy newProviderStrategy) {
		ProviderStrategy oldProviderStrategy = providerStrategy;
		providerStrategy = newProviderStrategy == null ? PROVIDER_STRATEGY_EDEFAULT : newProviderStrategy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.MAPPING_PROFILE__PROVIDER_STRATEGY, oldProviderStrategy, providerStrategy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProfileProvider getProvider() {
		return provider;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProvider(ProfileProvider newProvider, NotificationChain msgs) {
		ProfileProvider oldProvider = provider;
		provider = newProvider;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MappingPackage.MAPPING_PROFILE__PROVIDER, oldProvider, newProvider);
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
	public void setProvider(ProfileProvider newProvider) {
		if (newProvider != provider) {
			NotificationChain msgs = null;
			if (provider != null)
				msgs = ((InternalEObject)provider).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MappingPackage.MAPPING_PROFILE__PROVIDER, null, msgs);
			if (newProvider != null)
				msgs = ((InternalEObject)newProvider).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MappingPackage.MAPPING_PROFILE__PROVIDER, null, msgs);
			msgs = basicSetProvider(newProvider, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.MAPPING_PROFILE__PROVIDER, newProvider, newProvider));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MappingPackage.MAPPING_PROFILE__PROVIDER:
				return basicSetProvider(null, msgs);
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
			case MappingPackage.MAPPING_PROFILE__PROFILE_ID:
				return getProfileId();
			case MappingPackage.MAPPING_PROFILE__NAME:
				return getName();
			case MappingPackage.MAPPING_PROFILE__DESCRIPTION:
				return getDescription();
			case MappingPackage.MAPPING_PROFILE__VERSION:
				return getVersion();
			case MappingPackage.MAPPING_PROFILE__PROVIDER_STRATEGY:
				return getProviderStrategy();
			case MappingPackage.MAPPING_PROFILE__PROVIDER:
				return getProvider();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MappingPackage.MAPPING_PROFILE__PROFILE_ID:
				setProfileId((String)newValue);
				return;
			case MappingPackage.MAPPING_PROFILE__NAME:
				setName((String)newValue);
				return;
			case MappingPackage.MAPPING_PROFILE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MappingPackage.MAPPING_PROFILE__VERSION:
				setVersion((String)newValue);
				return;
			case MappingPackage.MAPPING_PROFILE__PROVIDER_STRATEGY:
				setProviderStrategy((ProviderStrategy)newValue);
				return;
			case MappingPackage.MAPPING_PROFILE__PROVIDER:
				setProvider((ProfileProvider)newValue);
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
			case MappingPackage.MAPPING_PROFILE__PROFILE_ID:
				setProfileId(PROFILE_ID_EDEFAULT);
				return;
			case MappingPackage.MAPPING_PROFILE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MappingPackage.MAPPING_PROFILE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MappingPackage.MAPPING_PROFILE__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
			case MappingPackage.MAPPING_PROFILE__PROVIDER_STRATEGY:
				setProviderStrategy(PROVIDER_STRATEGY_EDEFAULT);
				return;
			case MappingPackage.MAPPING_PROFILE__PROVIDER:
				setProvider((ProfileProvider)null);
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
			case MappingPackage.MAPPING_PROFILE__PROFILE_ID:
				return PROFILE_ID_EDEFAULT == null ? profileId != null : !PROFILE_ID_EDEFAULT.equals(profileId);
			case MappingPackage.MAPPING_PROFILE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MappingPackage.MAPPING_PROFILE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MappingPackage.MAPPING_PROFILE__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
			case MappingPackage.MAPPING_PROFILE__PROVIDER_STRATEGY:
				return providerStrategy != PROVIDER_STRATEGY_EDEFAULT;
			case MappingPackage.MAPPING_PROFILE__PROVIDER:
				return provider != null;
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
		result.append(" (profileId: ");
		result.append(profileId);
		result.append(", name: ");
		result.append(name);
		result.append(", description: ");
		result.append(description);
		result.append(", version: ");
		result.append(version);
		result.append(", providerStrategy: ");
		result.append(providerStrategy);
		result.append(')');
		return result.toString();
	}

} //MappingProfileImpl
