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
package dragino.impl;

import dragino.DecodedObject;
import dragino.DraginoPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Decoded Object</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getBatV <em>Bat V</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getTemp_DS18B20 <em>Temp DS18B20</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getTemp_SOIL <em>Temp SOIL</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getWater_SOIL_f <em>Water SOIL f</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getConduct_SOIL <em>Conduct SOIL</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getI_flag <em>Iflag</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getWater_SOIL <em>Water SOIL</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getTemp_SOIL_f <em>Temp SOIL f</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getS_flag <em>Sflag</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getConduct_SOIL_f <em>Conduct SOIL f</em>}</li>
 *   <li>{@link dragino.impl.DecodedObjectImpl#getMod <em>Mod</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DecodedObjectImpl extends MinimalEObjectImpl.Container implements DecodedObject {
	/**
	 * The default value of the '{@link #getBatV() <em>Bat V</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBatV()
	 * @generated
	 * @ordered
	 */
	protected static final double BAT_V_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getBatV() <em>Bat V</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBatV()
	 * @generated
	 * @ordered
	 */
	protected double batV = BAT_V_EDEFAULT;

	/**
	 * The default value of the '{@link #getTemp_DS18B20() <em>Temp DS18B20</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemp_DS18B20()
	 * @generated
	 * @ordered
	 */
	protected static final String TEMP_DS18B20_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTemp_DS18B20() <em>Temp DS18B20</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemp_DS18B20()
	 * @generated
	 * @ordered
	 */
	protected String temp_DS18B20 = TEMP_DS18B20_EDEFAULT;

	/**
	 * The default value of the '{@link #getTemp_SOIL() <em>Temp SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemp_SOIL()
	 * @generated
	 * @ordered
	 */
	protected static final double TEMP_SOIL_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getTemp_SOIL() <em>Temp SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemp_SOIL()
	 * @generated
	 * @ordered
	 */
	protected double temp_SOIL = TEMP_SOIL_EDEFAULT;

	/**
	 * The default value of the '{@link #getWater_SOIL_f() <em>Water SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWater_SOIL_f()
	 * @generated
	 * @ordered
	 */
	protected static final double WATER_SOIL_F_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getWater_SOIL_f() <em>Water SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWater_SOIL_f()
	 * @generated
	 * @ordered
	 */
	protected double water_SOIL_f = WATER_SOIL_F_EDEFAULT;

	/**
	 * The default value of the '{@link #getConduct_SOIL() <em>Conduct SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConduct_SOIL()
	 * @generated
	 * @ordered
	 */
	protected static final double CONDUCT_SOIL_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getConduct_SOIL() <em>Conduct SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConduct_SOIL()
	 * @generated
	 * @ordered
	 */
	protected double conduct_SOIL = CONDUCT_SOIL_EDEFAULT;

	/**
	 * The default value of the '{@link #getI_flag() <em>Iflag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getI_flag()
	 * @generated
	 * @ordered
	 */
	protected static final double IFLAG_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getI_flag() <em>Iflag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getI_flag()
	 * @generated
	 * @ordered
	 */
	protected double i_flag = IFLAG_EDEFAULT;

	/**
	 * The default value of the '{@link #getWater_SOIL() <em>Water SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWater_SOIL()
	 * @generated
	 * @ordered
	 */
	protected static final String WATER_SOIL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWater_SOIL() <em>Water SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWater_SOIL()
	 * @generated
	 * @ordered
	 */
	protected String water_SOIL = WATER_SOIL_EDEFAULT;

	/**
	 * The default value of the '{@link #getTemp_SOIL_f() <em>Temp SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemp_SOIL_f()
	 * @generated
	 * @ordered
	 */
	protected static final double TEMP_SOIL_F_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getTemp_SOIL_f() <em>Temp SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemp_SOIL_f()
	 * @generated
	 * @ordered
	 */
	protected double temp_SOIL_f = TEMP_SOIL_F_EDEFAULT;

	/**
	 * The default value of the '{@link #getS_flag() <em>Sflag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getS_flag()
	 * @generated
	 * @ordered
	 */
	protected static final double SFLAG_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getS_flag() <em>Sflag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getS_flag()
	 * @generated
	 * @ordered
	 */
	protected double s_flag = SFLAG_EDEFAULT;

	/**
	 * The default value of the '{@link #getConduct_SOIL_f() <em>Conduct SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConduct_SOIL_f()
	 * @generated
	 * @ordered
	 */
	protected static final double CONDUCT_SOIL_F_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getConduct_SOIL_f() <em>Conduct SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConduct_SOIL_f()
	 * @generated
	 * @ordered
	 */
	protected double conduct_SOIL_f = CONDUCT_SOIL_F_EDEFAULT;

	/**
	 * The default value of the '{@link #getMod() <em>Mod</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMod()
	 * @generated
	 * @ordered
	 */
	protected static final double MOD_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getMod() <em>Mod</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMod()
	 * @generated
	 * @ordered
	 */
	protected double mod = MOD_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DecodedObjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DraginoPackage.Literals.DECODED_OBJECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getBatV() {
		return batV;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBatV(double newBatV) {
		double oldBatV = batV;
		batV = newBatV;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__BAT_V, oldBatV, batV));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTemp_DS18B20() {
		return temp_DS18B20;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTemp_DS18B20(String newTemp_DS18B20) {
		String oldTemp_DS18B20 = temp_DS18B20;
		temp_DS18B20 = newTemp_DS18B20;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__TEMP_DS18B20, oldTemp_DS18B20, temp_DS18B20));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getTemp_SOIL() {
		return temp_SOIL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTemp_SOIL(double newTemp_SOIL) {
		double oldTemp_SOIL = temp_SOIL;
		temp_SOIL = newTemp_SOIL;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__TEMP_SOIL, oldTemp_SOIL, temp_SOIL));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getWater_SOIL_f() {
		return water_SOIL_f;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWater_SOIL_f(double newWater_SOIL_f) {
		double oldWater_SOIL_f = water_SOIL_f;
		water_SOIL_f = newWater_SOIL_f;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__WATER_SOIL_F, oldWater_SOIL_f, water_SOIL_f));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getConduct_SOIL() {
		return conduct_SOIL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConduct_SOIL(double newConduct_SOIL) {
		double oldConduct_SOIL = conduct_SOIL;
		conduct_SOIL = newConduct_SOIL;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL, oldConduct_SOIL, conduct_SOIL));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getI_flag() {
		return i_flag;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setI_flag(double newI_flag) {
		double oldI_flag = i_flag;
		i_flag = newI_flag;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__IFLAG, oldI_flag, i_flag));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getWater_SOIL() {
		return water_SOIL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWater_SOIL(String newWater_SOIL) {
		String oldWater_SOIL = water_SOIL;
		water_SOIL = newWater_SOIL;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__WATER_SOIL, oldWater_SOIL, water_SOIL));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getTemp_SOIL_f() {
		return temp_SOIL_f;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTemp_SOIL_f(double newTemp_SOIL_f) {
		double oldTemp_SOIL_f = temp_SOIL_f;
		temp_SOIL_f = newTemp_SOIL_f;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__TEMP_SOIL_F, oldTemp_SOIL_f, temp_SOIL_f));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getS_flag() {
		return s_flag;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setS_flag(double newS_flag) {
		double oldS_flag = s_flag;
		s_flag = newS_flag;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__SFLAG, oldS_flag, s_flag));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getConduct_SOIL_f() {
		return conduct_SOIL_f;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConduct_SOIL_f(double newConduct_SOIL_f) {
		double oldConduct_SOIL_f = conduct_SOIL_f;
		conduct_SOIL_f = newConduct_SOIL_f;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL_F, oldConduct_SOIL_f, conduct_SOIL_f));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getMod() {
		return mod;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMod(double newMod) {
		double oldMod = mod;
		mod = newMod;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DraginoPackage.DECODED_OBJECT__MOD, oldMod, mod));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DraginoPackage.DECODED_OBJECT__BAT_V:
				return getBatV();
			case DraginoPackage.DECODED_OBJECT__TEMP_DS18B20:
				return getTemp_DS18B20();
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL:
				return getTemp_SOIL();
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL_F:
				return getWater_SOIL_f();
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL:
				return getConduct_SOIL();
			case DraginoPackage.DECODED_OBJECT__IFLAG:
				return getI_flag();
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL:
				return getWater_SOIL();
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL_F:
				return getTemp_SOIL_f();
			case DraginoPackage.DECODED_OBJECT__SFLAG:
				return getS_flag();
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL_F:
				return getConduct_SOIL_f();
			case DraginoPackage.DECODED_OBJECT__MOD:
				return getMod();
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
			case DraginoPackage.DECODED_OBJECT__BAT_V:
				setBatV((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__TEMP_DS18B20:
				setTemp_DS18B20((String)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL:
				setTemp_SOIL((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL_F:
				setWater_SOIL_f((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL:
				setConduct_SOIL((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__IFLAG:
				setI_flag((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL:
				setWater_SOIL((String)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL_F:
				setTemp_SOIL_f((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__SFLAG:
				setS_flag((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL_F:
				setConduct_SOIL_f((Double)newValue);
				return;
			case DraginoPackage.DECODED_OBJECT__MOD:
				setMod((Double)newValue);
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
			case DraginoPackage.DECODED_OBJECT__BAT_V:
				setBatV(BAT_V_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__TEMP_DS18B20:
				setTemp_DS18B20(TEMP_DS18B20_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL:
				setTemp_SOIL(TEMP_SOIL_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL_F:
				setWater_SOIL_f(WATER_SOIL_F_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL:
				setConduct_SOIL(CONDUCT_SOIL_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__IFLAG:
				setI_flag(IFLAG_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL:
				setWater_SOIL(WATER_SOIL_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL_F:
				setTemp_SOIL_f(TEMP_SOIL_F_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__SFLAG:
				setS_flag(SFLAG_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL_F:
				setConduct_SOIL_f(CONDUCT_SOIL_F_EDEFAULT);
				return;
			case DraginoPackage.DECODED_OBJECT__MOD:
				setMod(MOD_EDEFAULT);
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
			case DraginoPackage.DECODED_OBJECT__BAT_V:
				return batV != BAT_V_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__TEMP_DS18B20:
				return TEMP_DS18B20_EDEFAULT == null ? temp_DS18B20 != null : !TEMP_DS18B20_EDEFAULT.equals(temp_DS18B20);
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL:
				return temp_SOIL != TEMP_SOIL_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL_F:
				return water_SOIL_f != WATER_SOIL_F_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL:
				return conduct_SOIL != CONDUCT_SOIL_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__IFLAG:
				return i_flag != IFLAG_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__WATER_SOIL:
				return WATER_SOIL_EDEFAULT == null ? water_SOIL != null : !WATER_SOIL_EDEFAULT.equals(water_SOIL);
			case DraginoPackage.DECODED_OBJECT__TEMP_SOIL_F:
				return temp_SOIL_f != TEMP_SOIL_F_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__SFLAG:
				return s_flag != SFLAG_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__CONDUCT_SOIL_F:
				return conduct_SOIL_f != CONDUCT_SOIL_F_EDEFAULT;
			case DraginoPackage.DECODED_OBJECT__MOD:
				return mod != MOD_EDEFAULT;
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
		result.append(" (batV: ");
		result.append(batV);
		result.append(", temp_DS18B20: ");
		result.append(temp_DS18B20);
		result.append(", temp_SOIL: ");
		result.append(temp_SOIL);
		result.append(", water_SOIL_f: ");
		result.append(water_SOIL_f);
		result.append(", conduct_SOIL: ");
		result.append(conduct_SOIL);
		result.append(", i_flag: ");
		result.append(i_flag);
		result.append(", water_SOIL: ");
		result.append(water_SOIL);
		result.append(", temp_SOIL_f: ");
		result.append(temp_SOIL_f);
		result.append(", s_flag: ");
		result.append(s_flag);
		result.append(", conduct_SOIL_f: ");
		result.append(conduct_SOIL_f);
		result.append(", mod: ");
		result.append(mod);
		result.append(')');
		return result.toString();
	}

} //DecodedObjectImpl
