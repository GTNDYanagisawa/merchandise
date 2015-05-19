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
package de.hybris.platform.sap.productconfig.facades;


/**
 * Helper for formatting characteristic values. The external format for UI Display is derived from the web-shops I18N
 * locale, which in turn depends on the browser's locale.
 */
public interface ValueFormatTranslator
{

	/**
	 * Converts a characteristic value into an internal format, which is used by the underlying API's.
	 * 
	 * @param uiType
	 *           uiType of the characteristic
	 * @param value
	 *           characteristic value in external format
	 * @return characteristic value in internal format
	 */
	public String parse(UiType uiType, String value);

	/**
	 * Converts a characteristic value into an external format suitable for UI-Display.
	 * 
	 * @param uiType
	 *           uiType of the characteristic
	 * @param singleValue
	 *           characteristic value in internal format
	 * @return characteristic value in external format
	 */
	public String format(UiType uiType, String singleValue);

}
