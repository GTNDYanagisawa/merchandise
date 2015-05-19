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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesTransactionsUtil;

import java.math.BigDecimal;
import java.util.Iterator;


/**
 * Class containing general utility methods for sales transactions module and important constants <br>
 * 
 * @version 1.0
 */
public class SalesTransactionsUtilImpl implements SalesTransactionsUtil
{

	static final protected Log4JWrapper sapLogger = Log4JWrapper.getInstance(SalesTransactionsUtilImpl.class.getName());

	protected int defaultEmptyLinesBackend = 5;

	protected int defaultEmptyLinesJava = 1;

	@Override
	public void deleteEmptyItems(final ItemList itemList)
	{

		if (itemList != null)
		{

			final Iterator<Item> iterator = itemList.iterator();

			while (iterator.hasNext())
			{
				final Item item = iterator.next();

				// remove non-subitems with quantity 0 or no entered product
				if (TechKey.isEmpty(item.getParentId())
						&& (item.getQuantity().compareTo(BigDecimal.ZERO) == 0 || (item.isProductEmpty() && TechKey.isEmpty(item
								.getTechKey()))))
				{
					iterator.remove();
				}
			}
		}

	}


	@Override
	public String removeLeadingZeros(final String input)
	{
		return (input != null) ? PATTERN_REPLACE_ZEROS.matcher(input).replaceFirst(REPLACEMENT_EMPTY_STRING) : null;
	}

}
