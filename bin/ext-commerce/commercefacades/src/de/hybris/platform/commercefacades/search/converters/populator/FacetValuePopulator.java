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
package de.hybris.platform.commercefacades.search.converters.populator;

import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;

/**
 */
public class FacetValuePopulator<QUERY, STATE> implements Populator<FacetValueData<QUERY>, FacetValueData<STATE>>
{
	private Converter<QUERY, STATE> searchStateConverter;

	protected Converter<QUERY, STATE> getSearchStateConverter()
	{
		return searchStateConverter;
	}

	@Required
	public void setSearchStateConverter(final Converter<QUERY, STATE> searchStateConverter)
	{
		this.searchStateConverter = searchStateConverter;
	}

	@Override
	public void populate(final FacetValueData<QUERY> source, final FacetValueData<STATE> target)
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setCount(source.getCount());
		target.setSelected(source.isSelected());

		if (source.getQuery() != null)
		{
			target.setQuery(getSearchStateConverter().convert(source.getQuery()));
		}
	}
}
