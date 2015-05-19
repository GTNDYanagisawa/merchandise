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
package de.hybris.platform.acceleratorservices.dataimport.batch.decorator;

import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;

import java.util.Map;


/**
 * Decorator which fill name of customer in case it is blank. Name for customer is taken from email address. It assume
 * that email address is in the column previous to customer name column
 */
public class CustomerNameDecorator extends AbstractImpExCSVCellDecorator
{
	private static final String SPLIT_SIGN = "@";

	@Override
	public String decorate(final int position, final Map<Integer, String> srcLine)
	{
		String name = srcLine.get(Integer.valueOf(position));
		if (name == null || name.trim().isEmpty())
		{
			final Integer emailPosition = Integer.valueOf(position - 1);
			final String email = srcLine.get(emailPosition);
			if (email != null)
			{
				name = email.split(SPLIT_SIGN)[0];
			}
		}

		return name;
	}

}
