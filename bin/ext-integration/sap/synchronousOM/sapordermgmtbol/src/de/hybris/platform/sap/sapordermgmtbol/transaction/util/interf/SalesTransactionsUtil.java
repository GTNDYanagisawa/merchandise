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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import java.util.regex.Pattern;


/**
 * Utility class for salestransactions module.<br>
 * 
 */
public interface SalesTransactionsUtil
{

	/**
	 * Constant for Authority Check - display authority
	 */
	public static final String AUTH_ACTVT_DISPLAY = "display";
	/**
	 * Constant for Authority Check - change authority
	 */
	public static final String AUTH_ACTVT_EDIT = "change";
	/**
	 * Constant for Authority Check - create authority
	 */
	public static final String AUTH_ACTVT_CREATE = "create";
	/**
	 * Constant for Authority Check - delete authority
	 */
	public static final String AUTH_ACTVT_DELETE = "delete";

	/**
	 * Pattern for removing leading zeros in a String. Sufficient to compile this exactly one time.
	 */
	public static final Pattern PATTERN_REPLACE_ZEROS = Pattern.compile("\\A0+");

	/**
	 * Just an empty string.
	 */
	public static final String REPLACEMENT_EMPTY_STRING = "";


	/**
	 * Name of the search description used for the order search.
	 */
	public static final String SEARCH_NAME_ORDER = "SearchCriteria_B2B_Order";

	/**
	 * Deletes empty items from the item list, so that no empty items are transfered to the backend. An empty item is a
	 * main item (no subitem), where quantity is 0 or no product was entered.
	 * 
	 * @param itemList
	 *           to clean up
	 */
	public void deleteEmptyItems(ItemList itemList);

	/**
	 * Removes leading zeros from a string. Use full when dealing with Id's that might have leading zeros,
	 * 
	 * @param input
	 *           and id string, should only contain digits
	 * @return input string without leading zeros
	 */
	public String removeLeadingZeros(String input);

}
