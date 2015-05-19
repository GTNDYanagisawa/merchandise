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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;

import com.sap.tc.logging.Severity;


/**
 * This class encapsulates operations on customizing used for CRM and ERP. <br>
 * 
 */
public class CustomizingHelper
{
	/**
	 * Number of decimals used by JCO and ABAP backend (DDIC).
	 */
	public static final int DEFAULT_NUMBER_OF_DECIMALS = 2;
	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(CustomizingHelper.class.getName());

	/**
	 * Gets the number of decimals customized for this currency.<br>
	 * 
	 * @param converter
	 *           converter
	 * @param currency
	 *           currency to check
	 * @return number of decimals
	 */
	public static int getNumberOfDecimals(final Converter converter, final String currency)
	{
		int numberOfDecimals;
		if (null == converter)
		{
			numberOfDecimals = DEFAULT_NUMBER_OF_DECIMALS;
			// no exception is thrown to keep testability
			sapLogger.log(LogSeverity.DEBUG, LogCategories.APPLICATIONS, "Unable to determine number of decimals");
		}
		else if (null == currency || currency.isEmpty())
		{
			// can be the case e.g. for missing authorizations/invalid documents
			numberOfDecimals = DEFAULT_NUMBER_OF_DECIMALS;
		}
		else
		{
			try
			{
				numberOfDecimals = converter.getCurrencyScale(currency);
			}
			catch (final BusinessObjectException e)
			{
				numberOfDecimals = DEFAULT_NUMBER_OF_DECIMALS;
				sapLogger.traceThrowable(Severity.ERROR, "Unable to determine number of decimals", e);
			}
		}
		return numberOfDecimals;
	}

}
