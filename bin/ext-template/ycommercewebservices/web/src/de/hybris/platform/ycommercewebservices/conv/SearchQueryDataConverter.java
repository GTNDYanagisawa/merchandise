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
 */
package de.hybris.platform.ycommercewebservices.conv;

import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * SearchQuery data converter renders a {@link SolrSearchQueryData} as
 * 
 * <pre>
 * {@code 
 * <currentQuery>a:relevance</currentQuery>
 * }
 * </pre>
 */
public class SearchQueryDataConverter extends AbstractRedirectableConverter
{
	private Converter<SolrSearchQueryData, SearchQueryData> solrSearchQueryEncoder;

	protected Converter<SolrSearchQueryData, SearchQueryData> getSolrSearchQueryEncoder()
	{
		return solrSearchQueryEncoder;
	}

	@Required
	public void setSolrSearchQueryEncoder(final Converter<SolrSearchQueryData, SearchQueryData> solrSearchQueryEncoder)
	{
		this.solrSearchQueryEncoder = solrSearchQueryEncoder;
	}

	@Override
	public boolean canConvert(final Class type)
	{
		return type == SolrSearchQueryData.class;
	}

	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context)
	{
		final String query = getSolrSearchQueryEncoder().convert((SolrSearchQueryData) source).getValue();
		writer.setValue(query);
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context)
	{
		return getTargetConverter().unmarshal(reader, context);
	}

	@Override
	public Class getConvertedClass()
	{
		return SolrSearchQueryData.class;
	}
}
