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
package dragino;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Decoded Object</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dragino.DecodedObject#getBatV <em>Bat V</em>}</li>
 *   <li>{@link dragino.DecodedObject#getTemp_DS18B20 <em>Temp DS18B20</em>}</li>
 *   <li>{@link dragino.DecodedObject#getTemp_SOIL <em>Temp SOIL</em>}</li>
 *   <li>{@link dragino.DecodedObject#getWater_SOIL_f <em>Water SOIL f</em>}</li>
 *   <li>{@link dragino.DecodedObject#getConduct_SOIL <em>Conduct SOIL</em>}</li>
 *   <li>{@link dragino.DecodedObject#getI_flag <em>Iflag</em>}</li>
 *   <li>{@link dragino.DecodedObject#getWater_SOIL <em>Water SOIL</em>}</li>
 *   <li>{@link dragino.DecodedObject#getTemp_SOIL_f <em>Temp SOIL f</em>}</li>
 *   <li>{@link dragino.DecodedObject#getS_flag <em>Sflag</em>}</li>
 *   <li>{@link dragino.DecodedObject#getConduct_SOIL_f <em>Conduct SOIL f</em>}</li>
 *   <li>{@link dragino.DecodedObject#getMod <em>Mod</em>}</li>
 * </ul>
 *
 * @see dragino.DraginoPackage#getDecodedObject()
 * @model
 * @generated
 */
@ProviderType
public interface DecodedObject extends EObject {
	/**
	 * Returns the value of the '<em><b>Bat V</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bat V</em>' attribute.
	 * @see #setBatV(double)
	 * @see dragino.DraginoPackage#getDecodedObject_BatV()
	 * @model
	 * @generated
	 */
	double getBatV();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getBatV <em>Bat V</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bat V</em>' attribute.
	 * @see #getBatV()
	 * @generated
	 */
	void setBatV(double value);

	/**
	 * Returns the value of the '<em><b>Temp DS18B20</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Temp DS18B20</em>' attribute.
	 * @see #setTemp_DS18B20(String)
	 * @see dragino.DraginoPackage#getDecodedObject_Temp_DS18B20()
	 * @model
	 * @generated
	 */
	String getTemp_DS18B20();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getTemp_DS18B20 <em>Temp DS18B20</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Temp DS18B20</em>' attribute.
	 * @see #getTemp_DS18B20()
	 * @generated
	 */
	void setTemp_DS18B20(String value);

	/**
	 * Returns the value of the '<em><b>Temp SOIL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Temp SOIL</em>' attribute.
	 * @see #setTemp_SOIL(double)
	 * @see dragino.DraginoPackage#getDecodedObject_Temp_SOIL()
	 * @model
	 * @generated
	 */
	double getTemp_SOIL();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getTemp_SOIL <em>Temp SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Temp SOIL</em>' attribute.
	 * @see #getTemp_SOIL()
	 * @generated
	 */
	void setTemp_SOIL(double value);

	/**
	 * Returns the value of the '<em><b>Water SOIL f</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Water SOIL f</em>' attribute.
	 * @see #setWater_SOIL_f(double)
	 * @see dragino.DraginoPackage#getDecodedObject_Water_SOIL_f()
	 * @model
	 * @generated
	 */
	double getWater_SOIL_f();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getWater_SOIL_f <em>Water SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Water SOIL f</em>' attribute.
	 * @see #getWater_SOIL_f()
	 * @generated
	 */
	void setWater_SOIL_f(double value);

	/**
	 * Returns the value of the '<em><b>Conduct SOIL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conduct SOIL</em>' attribute.
	 * @see #setConduct_SOIL(double)
	 * @see dragino.DraginoPackage#getDecodedObject_Conduct_SOIL()
	 * @model
	 * @generated
	 */
	double getConduct_SOIL();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getConduct_SOIL <em>Conduct SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Conduct SOIL</em>' attribute.
	 * @see #getConduct_SOIL()
	 * @generated
	 */
	void setConduct_SOIL(double value);

	/**
	 * Returns the value of the '<em><b>Iflag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Iflag</em>' attribute.
	 * @see #setI_flag(double)
	 * @see dragino.DraginoPackage#getDecodedObject_I_flag()
	 * @model
	 * @generated
	 */
	double getI_flag();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getI_flag <em>Iflag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Iflag</em>' attribute.
	 * @see #getI_flag()
	 * @generated
	 */
	void setI_flag(double value);

	/**
	 * Returns the value of the '<em><b>Water SOIL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Water SOIL</em>' attribute.
	 * @see #setWater_SOIL(String)
	 * @see dragino.DraginoPackage#getDecodedObject_Water_SOIL()
	 * @model
	 * @generated
	 */
	String getWater_SOIL();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getWater_SOIL <em>Water SOIL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Water SOIL</em>' attribute.
	 * @see #getWater_SOIL()
	 * @generated
	 */
	void setWater_SOIL(String value);

	/**
	 * Returns the value of the '<em><b>Temp SOIL f</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Temp SOIL f</em>' attribute.
	 * @see #setTemp_SOIL_f(double)
	 * @see dragino.DraginoPackage#getDecodedObject_Temp_SOIL_f()
	 * @model
	 * @generated
	 */
	double getTemp_SOIL_f();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getTemp_SOIL_f <em>Temp SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Temp SOIL f</em>' attribute.
	 * @see #getTemp_SOIL_f()
	 * @generated
	 */
	void setTemp_SOIL_f(double value);

	/**
	 * Returns the value of the '<em><b>Sflag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sflag</em>' attribute.
	 * @see #setS_flag(double)
	 * @see dragino.DraginoPackage#getDecodedObject_S_flag()
	 * @model
	 * @generated
	 */
	double getS_flag();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getS_flag <em>Sflag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sflag</em>' attribute.
	 * @see #getS_flag()
	 * @generated
	 */
	void setS_flag(double value);

	/**
	 * Returns the value of the '<em><b>Conduct SOIL f</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conduct SOIL f</em>' attribute.
	 * @see #setConduct_SOIL_f(double)
	 * @see dragino.DraginoPackage#getDecodedObject_Conduct_SOIL_f()
	 * @model
	 * @generated
	 */
	double getConduct_SOIL_f();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getConduct_SOIL_f <em>Conduct SOIL f</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Conduct SOIL f</em>' attribute.
	 * @see #getConduct_SOIL_f()
	 * @generated
	 */
	void setConduct_SOIL_f(double value);

	/**
	 * Returns the value of the '<em><b>Mod</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mod</em>' attribute.
	 * @see #setMod(double)
	 * @see dragino.DraginoPackage#getDecodedObject_Mod()
	 * @model
	 * @generated
	 */
	double getMod();

	/**
	 * Sets the value of the '{@link dragino.DecodedObject#getMod <em>Mod</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mod</em>' attribute.
	 * @see #getMod()
	 * @generated
	 */
	void setMod(double value);

} // DecodedObject
