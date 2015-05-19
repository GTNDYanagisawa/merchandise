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
package de.hybris.platform.cscockpit.services.search.generic;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cscockpit.model.data.DataObject;
import de.hybris.platform.cscockpit.services.search.CsSearchResult;
import de.hybris.platform.cscockpit.services.search.SearchException;
import de.hybris.platform.cscockpit.services.search.generic.query.DefaultCustomerSearchQueryBuilder;
import de.hybris.platform.cscockpit.services.search.impl.DefaultCsTextSearchCommand;
import de.hybris.platform.cscockpit.services.search.impl.DefaultPageable;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
@IntegrationTest
public class DefaultCsFlexibleSearchServiceTest extends ServicelayerTransactionalTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private SearchRestrictionService searchRestrictionService;

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	@Before
	public void setUp() throws Exception
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setName("demo");
		user.setUid("demo");
		modelService.save(user);
	}

	@Test
	public void testCustomerSearch() throws SearchException
	{
		final DefaultCsFlexibleSearchService<DefaultCsTextSearchCommand, ItemModel> searchService = new DefaultCsFlexibleSearchService<DefaultCsTextSearchCommand, ItemModel>();
		searchService.setFlexibleSearchService(flexibleSearchService);
		searchService.setSearchRestrictionService(searchRestrictionService);
		searchService.setSessionService(sessionService);
		searchService.setFlexibleSearchQueryBuilder(new DefaultCustomerSearchQueryBuilder());

		final DefaultCsTextSearchCommand command = new DefaultCsTextSearchCommand();
		command.setText(DefaultCustomerSearchQueryBuilder.TextField.Name, "demo");

		final DefaultPageable pageable = new DefaultPageable();
		pageable.setPageNumber(0); // zero is first page
		pageable.setPageSize(10);

		final CsSearchResult<DefaultCsTextSearchCommand, ItemModel> searchResult = searchService.search(command, pageable);

		Assert.assertEquals(1, searchResult.getTotalResultCount());
		Assert.assertEquals(1, searchResult.getTotalPages());
		Assert.assertNull(searchResult.getAvailableSorts());
		final List<DataObject<ItemModel>> result = searchResult.getResult();
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		final ItemModel item = result.get(0).getItem();
		Assert.assertNotNull(item);
		Assert.assertTrue(item instanceof CustomerModel);
		final CustomerModel customer = (CustomerModel) item;
		Assert.assertEquals("demo", customer.getUid());
	}
}
