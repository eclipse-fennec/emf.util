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

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.Objects;

import java.util.function.Function;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.sensinact.model.mapping.ChangeRule;
import org.eclipse.fennec.sensinact.model.mapping.DeletionRule;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.NameMapping;
import org.eclipse.fennec.sensinact.model.mapping.ResourceMapping;
import org.eclipse.fennec.sensinact.model.mapping.TimestampMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getUnit <em>Unit</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getUnitFeature <em>Unit Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getValueFeature <em>Value Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getMid <em>Mid</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getExtraMetadata <em>Extra Metadata</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getDescriptionMapping <em>Description Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getChangeRule <em>Change Rule</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl#getDeletionRule <em>Deletion Rule</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ResourceMappingImpl extends EAttributeImpl implements ResourceMapping {
	/**
	 * The default value of the '{@link #getUnit() <em>Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnit()
	 * @generated
	 * @ordered
	 */
	protected static final String UNIT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUnit() <em>Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnit()
	 * @generated
	 * @ordered
	 */
	protected String unit = UNIT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getUnitFeature() <em>Unit Feature</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnitFeature()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> unitFeature;

	/**
	 * The cached value of the '{@link #getValueFeature() <em>Value Feature</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueFeature()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> valueFeature;

	/**
	 * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected TimestampMapping timestamp;

	/**
	 * The default value of the '{@link #getMid() <em>Mid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMid()
	 * @generated
	 * @ordered
	 */
	protected static final String MID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMid() <em>Mid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMid()
	 * @generated
	 * @ordered
	 */
	protected String mid = MID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExtraMetadata() <em>Extra Metadata</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExtraMetadata()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> extraMetadata;

	/**
	 * The cached value of the '{@link #getDescriptionMapping() <em>Description Mapping</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescriptionMapping()
	 * @generated
	 * @ordered
	 */
	protected NameMapping descriptionMapping;

	/**
	 * The cached value of the '{@link #getChangeRule() <em>Change Rule</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChangeRule()
	 * @generated
	 * @ordered
	 */
	protected ChangeRule changeRule;

	/**
	 * The cached value of the '{@link #getDeletionRule() <em>Deletion Rule</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeletionRule()
	 * @generated
	 * @ordered
	 */
	protected DeletionRule deletionRule;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ResourceMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.RESOURCE_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getUnit() {
		return unit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUnit(String newUnit) {
		String oldUnit = unit;
		unit = newUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.RESOURCE_MAPPING__UNIT, oldUnit, unit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getUnitFeature() {
		if (unitFeature == null) {
			unitFeature = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, MappingPackage.RESOURCE_MAPPING__UNIT_FEATURE);
		}
		return unitFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getValueFeature() {
		if (valueFeature == null) {
			valueFeature = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, MappingPackage.RESOURCE_MAPPING__VALUE_FEATURE);
		}
		return valueFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TimestampMapping getTimestamp() {
		if (timestamp != null && timestamp.eIsProxy()) {
			InternalEObject oldTimestamp = (InternalEObject)timestamp;
			timestamp = (TimestampMapping)eResolveProxy(oldTimestamp);
			if (timestamp != oldTimestamp) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.RESOURCE_MAPPING__TIMESTAMP, oldTimestamp, timestamp));
			}
		}
		return timestamp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimestampMapping basicGetTimestamp() {
		return timestamp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTimestamp(TimestampMapping newTimestamp) {
		TimestampMapping oldTimestamp = timestamp;
		timestamp = newTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.RESOURCE_MAPPING__TIMESTAMP, oldTimestamp, timestamp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMid() {
		return mid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMid(String newMid) {
		String oldMid = mid;
		mid = newMid;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.RESOURCE_MAPPING__MID, oldMid, mid));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, String> getExtraMetadata() {
		if (extraMetadata == null) {
			extraMetadata = new EcoreEMap<String,String>(MappingPackage.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, MappingPackage.RESOURCE_MAPPING__EXTRA_METADATA);
		}
		return extraMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NameMapping getDescriptionMapping() {
		if (descriptionMapping != null && descriptionMapping.eIsProxy()) {
			InternalEObject oldDescriptionMapping = (InternalEObject)descriptionMapping;
			descriptionMapping = (NameMapping)eResolveProxy(oldDescriptionMapping);
			if (descriptionMapping != oldDescriptionMapping) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.RESOURCE_MAPPING__DESCRIPTION_MAPPING, oldDescriptionMapping, descriptionMapping));
			}
		}
		return descriptionMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NameMapping basicGetDescriptionMapping() {
		return descriptionMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescriptionMapping(NameMapping newDescriptionMapping) {
		NameMapping oldDescriptionMapping = descriptionMapping;
		descriptionMapping = newDescriptionMapping;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.RESOURCE_MAPPING__DESCRIPTION_MAPPING, oldDescriptionMapping, descriptionMapping));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ChangeRule getChangeRule() {
		if (changeRule != null && changeRule.eIsProxy()) {
			InternalEObject oldChangeRule = (InternalEObject)changeRule;
			changeRule = (ChangeRule)eResolveProxy(oldChangeRule);
			if (changeRule != oldChangeRule) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.RESOURCE_MAPPING__CHANGE_RULE, oldChangeRule, changeRule));
			}
		}
		return changeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ChangeRule basicGetChangeRule() {
		return changeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setChangeRule(ChangeRule newChangeRule) {
		ChangeRule oldChangeRule = changeRule;
		changeRule = newChangeRule;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.RESOURCE_MAPPING__CHANGE_RULE, oldChangeRule, changeRule));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DeletionRule getDeletionRule() {
		if (deletionRule != null && deletionRule.eIsProxy()) {
			InternalEObject oldDeletionRule = (InternalEObject)deletionRule;
			deletionRule = (DeletionRule)eResolveProxy(oldDeletionRule);
			if (deletionRule != oldDeletionRule) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.RESOURCE_MAPPING__DELETION_RULE, oldDeletionRule, deletionRule));
			}
		}
		return deletionRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeletionRule basicGetDeletionRule() {
		return deletionRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeletionRule(DeletionRule newDeletionRule) {
		DeletionRule oldDeletionRule = deletionRule;
		deletionRule = newDeletionRule;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.RESOURCE_MAPPING__DELETION_RULE, oldDeletionRule, deletionRule));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void mapUnit(final EObject eobject, final Function<EObject, String> unitFunction) {
		Objects.requireNonNull(unitFunction);
		Objects.requireNonNull(eobject);
		setUnit(unitFunction.apply(eobject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MappingPackage.RESOURCE_MAPPING__EXTRA_METADATA:
				return ((InternalEList<?>)getExtraMetadata()).basicRemove(otherEnd, msgs);
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
			case MappingPackage.RESOURCE_MAPPING__UNIT:
				return getUnit();
			case MappingPackage.RESOURCE_MAPPING__UNIT_FEATURE:
				return getUnitFeature();
			case MappingPackage.RESOURCE_MAPPING__VALUE_FEATURE:
				return getValueFeature();
			case MappingPackage.RESOURCE_MAPPING__TIMESTAMP:
				if (resolve) return getTimestamp();
				return basicGetTimestamp();
			case MappingPackage.RESOURCE_MAPPING__MID:
				return getMid();
			case MappingPackage.RESOURCE_MAPPING__EXTRA_METADATA:
				if (coreType) return getExtraMetadata();
				else return getExtraMetadata().map();
			case MappingPackage.RESOURCE_MAPPING__DESCRIPTION_MAPPING:
				if (resolve) return getDescriptionMapping();
				return basicGetDescriptionMapping();
			case MappingPackage.RESOURCE_MAPPING__CHANGE_RULE:
				if (resolve) return getChangeRule();
				return basicGetChangeRule();
			case MappingPackage.RESOURCE_MAPPING__DELETION_RULE:
				if (resolve) return getDeletionRule();
				return basicGetDeletionRule();
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
			case MappingPackage.RESOURCE_MAPPING__UNIT:
				setUnit((String)newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__UNIT_FEATURE:
				getUnitFeature().clear();
				getUnitFeature().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__VALUE_FEATURE:
				getValueFeature().clear();
				getValueFeature().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__TIMESTAMP:
				setTimestamp((TimestampMapping)newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__MID:
				setMid((String)newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__EXTRA_METADATA:
				((EStructuralFeature.Setting)getExtraMetadata()).set(newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__DESCRIPTION_MAPPING:
				setDescriptionMapping((NameMapping)newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__CHANGE_RULE:
				setChangeRule((ChangeRule)newValue);
				return;
			case MappingPackage.RESOURCE_MAPPING__DELETION_RULE:
				setDeletionRule((DeletionRule)newValue);
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
			case MappingPackage.RESOURCE_MAPPING__UNIT:
				setUnit(UNIT_EDEFAULT);
				return;
			case MappingPackage.RESOURCE_MAPPING__UNIT_FEATURE:
				getUnitFeature().clear();
				return;
			case MappingPackage.RESOURCE_MAPPING__VALUE_FEATURE:
				getValueFeature().clear();
				return;
			case MappingPackage.RESOURCE_MAPPING__TIMESTAMP:
				setTimestamp((TimestampMapping)null);
				return;
			case MappingPackage.RESOURCE_MAPPING__MID:
				setMid(MID_EDEFAULT);
				return;
			case MappingPackage.RESOURCE_MAPPING__EXTRA_METADATA:
				getExtraMetadata().clear();
				return;
			case MappingPackage.RESOURCE_MAPPING__DESCRIPTION_MAPPING:
				setDescriptionMapping((NameMapping)null);
				return;
			case MappingPackage.RESOURCE_MAPPING__CHANGE_RULE:
				setChangeRule((ChangeRule)null);
				return;
			case MappingPackage.RESOURCE_MAPPING__DELETION_RULE:
				setDeletionRule((DeletionRule)null);
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
			case MappingPackage.RESOURCE_MAPPING__UNIT:
				return UNIT_EDEFAULT == null ? unit != null : !UNIT_EDEFAULT.equals(unit);
			case MappingPackage.RESOURCE_MAPPING__UNIT_FEATURE:
				return unitFeature != null && !unitFeature.isEmpty();
			case MappingPackage.RESOURCE_MAPPING__VALUE_FEATURE:
				return valueFeature != null && !valueFeature.isEmpty();
			case MappingPackage.RESOURCE_MAPPING__TIMESTAMP:
				return timestamp != null;
			case MappingPackage.RESOURCE_MAPPING__MID:
				return MID_EDEFAULT == null ? mid != null : !MID_EDEFAULT.equals(mid);
			case MappingPackage.RESOURCE_MAPPING__EXTRA_METADATA:
				return extraMetadata != null && !extraMetadata.isEmpty();
			case MappingPackage.RESOURCE_MAPPING__DESCRIPTION_MAPPING:
				return descriptionMapping != null;
			case MappingPackage.RESOURCE_MAPPING__CHANGE_RULE:
				return changeRule != null;
			case MappingPackage.RESOURCE_MAPPING__DELETION_RULE:
				return deletionRule != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case MappingPackage.RESOURCE_MAPPING___MAP_UNIT__EOBJECT_FUNCTION:
				mapUnit((EObject)arguments.get(0), (Function<EObject, String>)arguments.get(1));
				return null;
		}
		return super.eInvoke(operationID, arguments);
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
		result.append(" (unit: ");
		result.append(unit);
		result.append(", mid: ");
		result.append(mid);
		result.append(')');
		return result.toString();
	}

} //ResourceMappingImpl
