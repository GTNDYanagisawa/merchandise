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
package de.hybris.platform.sap.sapordermgmtbol.transaction.backend.interf;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * With this interface the OrderStatus Object can communicate with the backend
 * 
 */
public interface CurrencyConverterBackend extends BackendBusinessObject
{

	/**
	 * Convert the currency from the sap to iso formats or viceversa
	 * 
	 * @param saptoiso
	 *           boolean variable which represents the direction of conversion true implies sap->iso or false implies
	 *           iso->sap
	 * @param currency
	 *           currency to convert
	 * @return String the converted uom value to the required format
	 * @throws BackendException
	 *            Is raised when an error in the backend occurs
	 */
	public String convertCurrency(boolean saptoiso, String currency) throws BackendException;

}
