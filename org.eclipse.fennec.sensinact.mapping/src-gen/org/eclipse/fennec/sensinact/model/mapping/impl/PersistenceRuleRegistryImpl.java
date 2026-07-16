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

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.sensinact.model.mapping.ChangeRule;
import org.eclipse.fennec.sensinact.model.mapping.DeletionRule;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Persistence Rule Registry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleRegistryImpl#getChangeRules <em>Change Rules</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleRegistryImpl#getDeletionRules <em>Deletion Rules</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PersistenceRuleRegistryImpl extends MinimalEObjectImpl.Container implements PersistenceRuleRegistry {
	/**
	 * The cached value of the '{@link #getChangeRules() <em>Change Rules</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChangeRules()
	 * @generated
	 * @ordered
	 */
	protected EList<ChangeRule> changeRules;

	/**
	 * The cached value of the '{@link #getDeletionRules() <em>Deletion Rules</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeletionRules()
	 * @generated
	 * @ordered
	 */
	protected EList<DeletionRule> deletionRules;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PersistenceRuleRegistryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.PERSISTENCE_RULE_REGISTRY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ChangeRule> getChangeRules() {
		if (changeRules == null) {
			changeRules = new EObjectContainmentEList<ChangeRule>(ChangeRule.class, this, MappingPackage.PERSISTENCE_RULE_REGISTRY__CHANGE_RULES);
		}
		return changeRules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DeletionRule> getDeletionRules() {
		if (deletionRules == null) {
			deletionRules = new EObjectContainmentEList<DeletionRule>(DeletionRule.class, this, MappingPackage.PERSISTENCE_RULE_REGISTRY__DELETION_RULES);
		}
		return deletionRules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__CHANGE_RULES:
				return ((InternalEList<?>)getChangeRules()).basicRemove(otherEnd, msgs);
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__DELETION_RULES:
				return ((InternalEList<?>)getDeletionRules()).basicRemove(otherEnd, msgs);
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
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__CHANGE_RULES:
				return getChangeRules();
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__DELETION_RULES:
				return getDeletionRules();
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
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__CHANGE_RULES:
				getChangeRules().clear();
				getChangeRules().addAll((Collection<? extends ChangeRule>)newValue);
				return;
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__DELETION_RULES:
				getDeletionRules().clear();
				getDeletionRules().addAll((Collection<? extends DeletionRule>)newValue);
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
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__CHANGE_RULES:
				getChangeRules().clear();
				return;
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__DELETION_RULES:
				getDeletionRules().clear();
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
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__CHANGE_RULES:
				return changeRules != null && !changeRules.isEmpty();
			case MappingPackage.PERSISTENCE_RULE_REGISTRY__DELETION_RULES:
				return deletionRules != null && !deletionRules.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //PersistenceRuleRegistryImpl
