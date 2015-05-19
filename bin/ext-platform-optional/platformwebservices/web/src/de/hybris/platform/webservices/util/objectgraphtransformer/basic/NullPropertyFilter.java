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
package de.hybris.platform.webservices.util.objectgraphtransformer.basic;

import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyFilter;


public class NullPropertyFilter implements PropertyFilter
{
	private boolean filterNullValues = false;

	public NullPropertyFilter()
	{
		this(true);
	}

	public NullPropertyFilter(final boolean enabled)
	{
		this.filterNullValues = enabled;
	}

	/**
	 * @return the filterNullValues
	 */
	public boolean isFilterNullValues()
	{
		return filterNullValues;
	}

	/**
	 * @param filterNullValues the filterNullValues to set
	 */
	public void setFilterNullValues(final boolean filterNullValues)
	{
		this.filterNullValues = filterNullValues;
	}

	/*
	 * (non-Javadoc)
	 * @seede.hybris.platform.webservices.objectgraphtransformer.PropertyFilter#isFiltered(de.hybris.platform.webservices.
	 * objectgraphtransformer.ObjectGraphContext, java.lang.Object)
	 */
	@Override
	public boolean isFiltered(final PropertyContext ctx, final Object value)
	{
		return (value == null) && isFilterNullValues();
	}




}
