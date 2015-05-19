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

package de.hybris.platform.sap.orderexchange.mocks;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.RelationQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.TranslationResult;

import java.util.List;
import java.util.Map;


/**
 * Mock to be used for spring tests
 */
public class MockFlexibleSearchService implements FlexibleSearchService
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#getModelByExample(java.lang.Object)
	 */
	public <T> T getModelByExample(final T arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#getModelsByExample(java.lang.Object)
	 */
	public <T> List<T> getModelsByExample(final T arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#search(de.hybris.platform.servicelayer.search.
	 * FlexibleSearchQuery)
	 */
	public <T> SearchResult<T> search(final FlexibleSearchQuery arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#search(java.lang.String)
	 */
	public <T> SearchResult<T> search(final String arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#search(java.lang.String, java.util.Map)
	 */
	public <T> SearchResult<T> search(final String arg0, final Map<String, ? extends Object> arg1)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.servicelayer.search.FlexibleSearchService#searchRelation(de.hybris.platform.servicelayer.search
	 * .RelationQuery)
	 */
	public <T> SearchResult<T> searchRelation(final RelationQuery arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.servicelayer.search.FlexibleSearchService#searchRelation(de.hybris.platform.core.model.ItemModel
	 * , java.lang.String, int, int)
	 */
	public <T> SearchResult<T> searchRelation(final ItemModel arg0, final String arg1, final int arg2, final int arg3)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.servicelayer.search.FlexibleSearchService#searchUnique(de.hybris.platform.servicelayer.search
	 * .FlexibleSearchQuery)
	 */
	public <T> T searchUnique(final FlexibleSearchQuery arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.servicelayer.search.FlexibleSearchService#translate(de.hybris.platform.servicelayer.search.
	 * FlexibleSearchQuery)
	 */
	public TranslationResult translate(final FlexibleSearchQuery arg0)
	{
		return null;
	}

}
