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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.RelationQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.TranslationResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.spring.TenantScope;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;


@Ignore
public class MockFlexibleSearchService extends AbstractBusinessService implements FlexibleSearchService
{
	@Override
	public <T extends Object> T getModelByExample(final T example)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T extends Object> List<T> getModelsByExample(final T example)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> SearchResult<T> search(final FlexibleSearchQuery searchQuery)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> SearchResult<T> search(final String query)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> SearchResult<T> search(final String query, final Map<String, ? extends Object> queryParams)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> SearchResult<T> searchRelation(final ItemModel model, final String attribute, final int start, final int count)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> SearchResult<T> searchRelation(final RelationQuery query)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T extends Object> T searchUnique(final FlexibleSearchQuery searchQuery)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public TranslationResult translate(final FlexibleSearchQuery searchQuery)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}
}
