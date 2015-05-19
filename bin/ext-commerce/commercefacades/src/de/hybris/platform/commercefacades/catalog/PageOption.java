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
package de.hybris.platform.commercefacades.catalog;

import com.google.common.base.Preconditions;


public final class PageOption
{
	private final int pageNumber;
	private final int pageSize;
	private final boolean includeInformationAboutPagesField;

	private PageOption(final int currentPage, final int pageSize, final boolean includeInformationAboutPages)
	{
		this.pageNumber = currentPage;
		this.pageSize = pageSize;
		this.includeInformationAboutPagesField = includeInformationAboutPages;
	}

	public static PageOption createWithoutLimits()
	{
		return new PageOption(0, Integer.MAX_VALUE, false);
	}

	public static PageOption createForPageNumberAndPageSize(final int currentPage, final int pageSize)
	{
		Preconditions.checkArgument(currentPage >= 0);
		Preconditions.checkArgument(pageSize > 0);
		if (pageSize == Integer.MAX_VALUE)
		{
			return new PageOption(currentPage, pageSize, false);
		}
		else
		{
			return new PageOption(currentPage, pageSize, true);
		}

	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public int getPageStart()
	{
		return pageSize * pageNumber;
	}

	public boolean includeInformationAboutPages()
	{
		return includeInformationAboutPagesField;
	}
}
