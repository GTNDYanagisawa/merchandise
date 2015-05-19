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
package de.hybris.platform.cockpit.services.query;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.DemoTest;
import de.hybris.platform.cockpit.jalo.CockpitTest;
import de.hybris.platform.cockpit.model.CockpitSavedQueryModel;
import de.hybris.platform.cockpit.model.search.Query;
import de.hybris.platform.cockpit.model.search.SearchType;
import de.hybris.platform.cockpit.model.search.impl.DefaultSearchType;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Demonstrates usage of the SavedQueryService
 */
@DemoTest
public class SavedQueryServiceTest extends CockpitTest
{
	private static final Logger LOG = Logger.getLogger(SavedQueryServiceTest.class);
	private static final int start = 0;
	private static final int count = 10;

	@Resource
	private UserService userService;

	@Resource
	private SavedQueryService savedQueryService;

	private UserModel user;

	/**
	 * prepares some test data, before execution of every test<br/>
	 */
	@Before
	public void setUp() throws Exception
	{
		userService = applicationContext.getBean("userService", UserService.class);
		savedQueryService = applicationContext.getBean("savedQueryService", SavedQueryService.class);
	}

	/**
	 * Demonstrates how to create, save, and rename saved queries:<br/>
	 * - Builds the <code>CockpitSavedQueryModel</code> for 2 queries<br/>
	 * - Publish the saved queries<br/>
	 * - Retrieve all saved queries<br/>
	 */
	@Test
	public void testManageSavedQueries()
	{
		final int numberOfTestQueries = 2;
		user = userService.getAdminUser();

		final Collection searchTypes1 = new ArrayList();
		final Collection searchTypes2 = new ArrayList();
		final SearchType searchType1 = new DefaultSearchType("Customer");
		final SearchType searchType2 = new DefaultSearchType("Address");
		searchTypes1.add(searchType1);
		searchTypes2.add(searchType2);
		final Query query1 = new Query(searchTypes1, null, start, count);
		final Query query2 = new Query(searchTypes2, null, start, count);

		final CockpitSavedQueryModel savedQuery1 = savedQueryService.createSavedQuery("query1", query1, user);
		assertNotNull(savedQuery1);
		final CockpitSavedQueryModel savedQuery2 = savedQueryService.createSavedQuery("query2", query2, user);
		assertNotNull(savedQuery2);
		savedQueryService.publishSavedQuery(savedQuery1);
		savedQueryService.publishSavedQuery(savedQuery2);

		// Verify the queries were saved
		final Collection<CockpitSavedQueryModel> savedQueries = savedQueryService.getSavedQueries(null, user);
		assertNotNull(savedQueries);
		final int size = savedQueries.size();
		Assert.assertEquals(numberOfTestQueries, size);
		LOG.info("number of saved queries: " + size);
	}

	/**
	 * Demonstrates how to create, save, and rename saved query:<br/>
	 * - Builds the <code>CockpitSavedQueryModel</code> for 1 query<br/>
	 * - Publish the saved queries<br/>
	 * - Retrieve all saved queries<br/>
	 * - Take the saved query, rename it, then save the changes<br/>
	 */
	@Test
	public void testRenameSavedQuery()
	{
		final int numberOfTestQueries = 1;
		user = userService.getAdminUser();

		final Collection searchTypes1 = new ArrayList();
		final SearchType searchType1 = new DefaultSearchType("Customer");
		searchTypes1.add(searchType1);
		final Query query1 = new Query(searchTypes1, null, start, count);

		final CockpitSavedQueryModel savedQuery1 = savedQueryService.createSavedQuery("query1", query1, user);
		assertNotNull(savedQuery1);
		savedQueryService.publishSavedQuery(savedQuery1);

		// Verify the queries were saved
		Collection<CockpitSavedQueryModel> savedQueries = savedQueryService.getSavedQueries(null, user);
		assertNotNull(savedQueries);
		final int size = savedQueries.size();
		Assert.assertEquals(numberOfTestQueries, size);
		LOG.info("number of saved queries: " + size);

		// Rename a saved query
		final String newLabel = "renamedQuery";
		Iterator iterator = savedQueries.iterator();
		final CockpitSavedQueryModel queryToBeRenamed = (CockpitSavedQueryModel) iterator.next();
		savedQueryService.renameQuery(queryToBeRenamed, newLabel);

		// Verify the saved query was renamed
		savedQueries = savedQueryService.getSavedQueries(null, user);
		assertNotNull(savedQueries);
		iterator = savedQueries.iterator();
		final CockpitSavedQueryModel queryResult = (CockpitSavedQueryModel) iterator.next();
		Assert.assertEquals(newLabel, queryResult.getLabel());
	}


}
