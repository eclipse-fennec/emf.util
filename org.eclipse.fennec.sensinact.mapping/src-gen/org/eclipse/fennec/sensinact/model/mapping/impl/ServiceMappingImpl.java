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
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ResourceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ServiceMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Service Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ServiceMappingImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ServiceMappingImpl#getReferencedResource <em>Referenced Resource</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ServiceMappingImpl#getTemporaryResources <em>Temporary Resources</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ServiceMappingImpl extends MappingImpl implements ServiceMapping {
	/**
	 * The cached value of the '{@link #getResources() <em>Resources</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResources()
	 * @generated
	 * @ordered
	 */
	protected EList<ResourceMapping> resources;

	/**
	 * The cached value of the '{@link #getReferencedResource() <em>Referenced Resource</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferencedResource()
	 * @generated
	 * @ordered
	 */
	protected ReferenceMapping referencedResource;

	/**
	 * The cached value of the '{@link #getTemporaryResources() <em>Temporary Resources</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemporaryResources()
	 * @generated
	 * @ordered
	 */
	protected EList<ResourceMapping> temporaryResources;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ServiceMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.SERVICE_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ResourceMapping> getResources() {
		if (resources == null) {
			resources = new EObjectContainmentEList<ResourceMapping>(ResourceMapping.class, this, MappingPackage.SERVICE_MAPPING__RESOURCES);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReferenceMapping getReferencedResource() {
		if (referencedResource != null && referencedResource.eIsProxy()) {
			InternalEObject oldReferencedResource = (InternalEObject)referencedResource;
			referencedResource = (ReferenceMapping)eResolveProxy(oldReferencedResource);
			if (referencedResource != oldReferencedResource) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.SERVICE_MAPPING__REFERENCED_RESOURCE, oldReferencedResource, referencedResource));
			}
		}
		return referencedResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceMapping basicGetReferencedResource() {
		return referencedResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReferencedResource(ReferenceMapping newReferencedResource) {
		ReferenceMapping oldReferencedResource = referencedResource;
		referencedResource = newReferencedResource;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.SERVICE_MAPPING__REFERENCED_RESOURCE, oldReferencedResource, referencedResource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ResourceMapping> getTemporaryResources() {
		if (temporaryResources == null) {
			temporaryResources = new EObjectContainmentEList<ResourceMapping>(ResourceMapping.class, this, MappingPackage.SERVICE_MAPPING__TEMPORARY_RESOURCES);
		}
		return temporaryResources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MappingPackage.SERVICE_MAPPING__RESOURCES:
				return ((InternalEList<?>)getResources()).basicRemove(otherEnd, msgs);
			case MappingPackage.SERVICE_MAPPING__TEMPORARY_RESOURCES:
				return ((InternalEList<?>)getTemporaryResources()).basicRemove(otherEnd, msgs);
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
			case MappingPackage.SERVICE_MAPPING__RESOURCES:
				return getResources();
			case MappingPackage.SERVICE_MAPPING__REFERENCED_RESOURCE:
				if (resolve) return getReferencedResource();
				return basicGetReferencedResource();
			case MappingPackage.SERVICE_MAPPING__TEMPORARY_RESOURCES:
				return getTemporaryResources();
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
			case MappingPackage.SERVICE_MAPPING__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection<? extends ResourceMapping>)newValue);
				return;
			case MappingPackage.SERVICE_MAPPING__REFERENCED_RESOURCE:
				setReferencedResource((ReferenceMapping)newValue);
				return;
			case MappingPackage.SERVICE_MAPPING__TEMPORARY_RESOURCES:
				getTemporaryResources().clear();
				getTemporaryResources().addAll((Collection<? extends ResourceMapping>)newValue);
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
			case MappingPackage.SERVICE_MAPPING__RESOURCES:
				getResources().clear();
				return;
			case MappingPackage.SERVICE_MAPPING__REFERENCED_RESOURCE:
				setReferencedResource((ReferenceMapping)null);
				return;
			case MappingPackage.SERVICE_MAPPING__TEMPORARY_RESOURCES:
				getTemporaryResources().clear();
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
			case MappingPackage.SERVICE_MAPPING__RESOURCES:
				return resources != null && !resources.isEmpty();
			case MappingPackage.SERVICE_MAPPING__REFERENCED_RESOURCE:
				return referencedResource != null;
			case MappingPackage.SERVICE_MAPPING__TEMPORARY_RESOURCES:
				return temporaryResources != null && !temporaryResources.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ServiceMappingImpl
