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

import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;

/**
 */
public class BreadcrumbPopulator<QUERY, STATE> implements Populator<BreadcrumbData<QUERY>, BreadcrumbData<STATE>>
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
	public void populate(final BreadcrumbData<QUERY> source, final BreadcrumbData<STATE> target)
	{
		target.setFacetCode(source.getFacetCode());
		target.setFacetName(source.getFacetName());
		target.setFacetValueCode(source.getFacetValueCode());
		target.setFacetValueName(source.getFacetValueName());

		if (source.getRemoveQuery() != null)
		{
			target.setRemoveQuery(getSearchStateConverter().convert(source.getRemoveQuery()));
		}

		if (source.getTruncateQuery() != null)
		{
			target.setTruncateQuery(getSearchStateConverter().convert(source.getTruncateQuery()));
		}
	}
}
