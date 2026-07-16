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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reference Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl#getFilter <em>Filter</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl#isExclude <em>Exclude</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl#getReferenceMappings <em>Reference Mappings</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl#getTargetEClass <em>Target EClass</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReferenceMappingImpl extends FeatureMappingImpl implements ReferenceMapping {
	/**
	 * The cached value of the '{@link #getFilter() <em>Filter</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFilter()
	 * @generated
	 * @ordered
	 */
	protected EList<EAttribute> filter;

	/**
	 * The default value of the '{@link #isExclude() <em>Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExclude()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXCLUDE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isExclude() <em>Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExclude()
	 * @generated
	 * @ordered
	 */
	protected boolean exclude = EXCLUDE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getReferenceMappings() <em>Reference Mappings</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferenceMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<ReferenceMapping> referenceMappings;

	/**
	 * The cached value of the '{@link #getTargetEClass() <em>Target EClass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetEClass()
	 * @generated
	 * @ordered
	 */
	protected EClass targetEClass;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReferenceMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.REFERENCE_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EAttribute> getFilter() {
		if (filter == null) {
			filter = new EObjectResolvingEList<EAttribute>(EAttribute.class, this, MappingPackage.REFERENCE_MAPPING__FILTER);
		}
		return filter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isExclude() {
		return exclude;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExclude(boolean newExclude) {
		boolean oldExclude = exclude;
		exclude = newExclude;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.REFERENCE_MAPPING__EXCLUDE, oldExclude, exclude));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ReferenceMapping> getReferenceMappings() {
		if (referenceMappings == null) {
			referenceMappings = new EObjectContainmentEList<ReferenceMapping>(ReferenceMapping.class, this, MappingPackage.REFERENCE_MAPPING__REFERENCE_MAPPINGS);
		}
		return referenceMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTargetEClass() {
		if (targetEClass != null && targetEClass.eIsProxy()) {
			InternalEObject oldTargetEClass = (InternalEObject)targetEClass;
			targetEClass = (EClass)eResolveProxy(oldTargetEClass);
			if (targetEClass != oldTargetEClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.REFERENCE_MAPPING__TARGET_ECLASS, oldTargetEClass, targetEClass));
			}
		}
		return targetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass basicGetTargetEClass() {
		return targetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTargetEClass(EClass newTargetEClass) {
		EClass oldTargetEClass = targetEClass;
		targetEClass = newTargetEClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.REFERENCE_MAPPING__TARGET_ECLASS, oldTargetEClass, targetEClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MappingPackage.REFERENCE_MAPPING__REFERENCE_MAPPINGS:
				return ((InternalEList<?>)getReferenceMappings()).basicRemove(otherEnd, msgs);
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
			case MappingPackage.REFERENCE_MAPPING__FILTER:
				return getFilter();
			case MappingPackage.REFERENCE_MAPPING__EXCLUDE:
				return isExclude();
			case MappingPackage.REFERENCE_MAPPING__REFERENCE_MAPPINGS:
				return getReferenceMappings();
			case MappingPackage.REFERENCE_MAPPING__TARGET_ECLASS:
				if (resolve) return getTargetEClass();
				return basicGetTargetEClass();
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
			case MappingPackage.REFERENCE_MAPPING__FILTER:
				getFilter().clear();
				getFilter().addAll((Collection<? extends EAttribute>)newValue);
				return;
			case MappingPackage.REFERENCE_MAPPING__EXCLUDE:
				setExclude((Boolean)newValue);
				return;
			case MappingPackage.REFERENCE_MAPPING__REFERENCE_MAPPINGS:
				getReferenceMappings().clear();
				getReferenceMappings().addAll((Collection<? extends ReferenceMapping>)newValue);
				return;
			case MappingPackage.REFERENCE_MAPPING__TARGET_ECLASS:
				setTargetEClass((EClass)newValue);
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
			case MappingPackage.REFERENCE_MAPPING__FILTER:
				getFilter().clear();
				return;
			case MappingPackage.REFERENCE_MAPPING__EXCLUDE:
				setExclude(EXCLUDE_EDEFAULT);
				return;
			case MappingPackage.REFERENCE_MAPPING__REFERENCE_MAPPINGS:
				getReferenceMappings().clear();
				return;
			case MappingPackage.REFERENCE_MAPPING__TARGET_ECLASS:
				setTargetEClass((EClass)null);
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
			case MappingPackage.REFERENCE_MAPPING__FILTER:
				return filter != null && !filter.isEmpty();
			case MappingPackage.REFERENCE_MAPPING__EXCLUDE:
				return exclude != EXCLUDE_EDEFAULT;
			case MappingPackage.REFERENCE_MAPPING__REFERENCE_MAPPINGS:
				return referenceMappings != null && !referenceMappings.isEmpty();
			case MappingPackage.REFERENCE_MAPPING__TARGET_ECLASS:
				return targetEClass != null;
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
		result.append(" (exclude: ");
		result.append(exclude);
		result.append(')');
		return result.toString();
	}

} //ReferenceMappingImpl
