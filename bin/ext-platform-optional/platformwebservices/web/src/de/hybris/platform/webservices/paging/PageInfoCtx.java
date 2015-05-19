/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.paging;

public class PageInfoCtx
{
	public static final Integer DEFAULT_PAGE_SIZE = Integer.valueOf(20);
	public static final String DEFAULT_SORT_ORDER = "pk";
	public static final boolean DEFAULT_SUBTYPES = true;

	private String collectionPropertyName;

	private Integer pageNumber;

	// "0" means first page
	private Integer pageSize;

	private String sortProperty;

	// currently deactivated
	private int totalSize;

	private boolean decending;

	private String query;

	private boolean subtypes;

	public PageInfoCtx()
	{
		this(Integer.valueOf(0), DEFAULT_PAGE_SIZE, DEFAULT_SORT_ORDER, null, true);
	}

	public PageInfoCtx(final Integer pageNumber, final Integer pageSize, final String sortProperty, final String query,
			final boolean subtypes)
	{
		super();
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		if (sortProperty != null)
		{
			setSortProperty(sortProperty);
		}
		else
		{
			this.sortProperty = DEFAULT_SORT_ORDER;
		}
		this.query = query;
		this.subtypes = subtypes;
	}

	/**
	 * @return the pageNumber
	 */
	public Integer getPageNumber()
	{
		return pageNumber;
	}

	/**
	 * @param pageNumber
	 *           the pageNumber to set
	 */
	public void setPageNumber(final Integer pageNumber)
	{
		this.pageNumber = pageNumber;
	}

	/**
	 * @return the pageSize
	 */
	public Integer getPageSize()
	{
		return pageSize;
	}

	/**
	 * @param pageSize
	 *           the pageSize to set
	 */
	public void setPageSize(final Integer pageSize)
	{
		this.pageSize = pageSize;
	}


	/**
	 * @return the sortProperty
	 */
	public String getSortProperty()
	{
		return sortProperty;
	}

	/**
	 * @param sortProperty
	 *           the sortProperty to set
	 */
	public final void setSortProperty(final String sortProperty)
	{
		if (sortProperty != null)
		{
			if (sortProperty.endsWith(" asc"))
			{
				this.sortProperty = sortProperty.substring(0, sortProperty.length() - 4);
				setDescending(false);
			}
			else if (sortProperty.endsWith(" desc"))
			{
				this.sortProperty = sortProperty.substring(0, sortProperty.length() - 5);
				setDescending(true);
			}
			else
			{
				this.sortProperty = sortProperty;
			}
		}
	}

	/**
	 * @return the totalSize
	 */
	public int getTotalSize()
	{
		return totalSize;
	}

	/**
	 * @param totalSize
	 *           the totalSize to set
	 */
	public void setTotalSize(final int totalSize)
	{
		this.totalSize = totalSize;
	}

	/**
	 * @return the descending
	 */
	public boolean isDescending()
	{
		return decending;
	}

	/**
	 * @param descending
	 *           the descending to set
	 */
	public void setDescending(final boolean descending)
	{
		this.decending = descending;
	}

	/**
	 * @param query
	 *           the query to set
	 */
	public void setQuery(final String query)
	{
		this.query = query;
	}

	/**
	 * @return the query
	 */
	public String getQuery()
	{
		return query;
	}

	/**
	 * @param subtypes
	 *           the subtypes to set
	 */
	public void setSubtypes(final boolean subtypes)
	{
		this.subtypes = subtypes;
	}

	/**
	 * @return the subtypes
	 */
	public boolean isSubtypes()
	{
		return subtypes;
	}

	/**
	 * @param collectionPropertyName
	 *           the collectionPropertyName to set
	 */
	public void setCollectionPropertyName(final String collectionPropertyName)
	{
		this.collectionPropertyName = collectionPropertyName;
	}

	/**
	 * @return the collectionPropertyName
	 */
	public String getCollectionPropertyName()
	{
		return collectionPropertyName;
	}

}
