/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.sapcommonbol.common.backendobject.interf;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;





/**
 * Converter backend interface
 * 
 */
public interface ConverterBackend extends BackendBusinessObject
{

	/**
	 * ID for converter BE object, see backendobject-config.xml
	 */
	public static String BE_TYPE = "Converter";

	/**
	 * Converting unit key to language dependent ID. <br>
	 * E.g. ST to PC in English
	 * 
	 * @param unitKey
	 *           SAP unit key
	 * @return language dependent unit ID
	 * @throws BackendException
	 */
	public String convertUnitKey2UnitID(String unitKey) throws BackendException;

	/**
	 * Converting language dependent unit ID to unit key
	 * 
	 * @param unitID
	 *           language dependent unit ID e.g PC in English
	 * @return SAP unit key e.g. ST for piece
	 * @throws BackendException
	 */
	public String convertUnitID2UnitKey(String unitID) throws BackendException;



	/**
	 * Get currency scale. In standard e.g. <li>USD: 2 <li>EUR: 2 <li>JPY: 0
	 * 
	 * @param sapCurrencyCode
	 *           SAP currency code, note that depending on customizing also non-ISO codes may occur
	 * @return number of decimals for UI display and validation
	 * @throws BackendException
	 */
	public int getCurrencyScale(String sapCurrencyCode) throws BackendException;

	/**
	 * Get unit scale.
	 * 
	 * @param unitKey
	 *           SAP unit key
	 * @return number of decimals for UI display and validation
	 * @throws BackendException
	 */
	public int getUnitScale(String unitKey) throws BackendException;



	/**
	 * Loads UOM's per language. Called from cache loaders on BO level<br>
	 * 
	 * @param applicationID
	 * @param language
	 *           Language in SAP format (1 place)
	 * @return Map of UOM's and their descriptions
	 * @throws BackendException
	 */
	public Object loadUOMsByLanguageFromBackend(String applicationID, String language) throws BackendException;

	/**
	 * Loads currencies per language. Called from cache loaders on BO level<br>
	 * 
	 * @param applicationID
	 * @param language
	 *           Language in SAP format (1 place)
	 * @return Map of currencies and their decimal format
	 * @throws BackendException
	 */
	public Object loadCurrenciesByLanguageFromBackend(String applicationID, String language) throws BackendException;

}
