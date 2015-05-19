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
package de.hybris.platform.solrfacetsearch.converters.populator;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryParameterModel;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultIndexedTypeFlexibleSearchQueryPopulatorTest
{
	private static final String PARAMETER_PROVIDER_ID = "testParameterProvider";
	private static final String QUERY = "Select blablabla";
	private static final String USER_ID = "testUser";
	private DefaultIndexedTypeFlexibleSearchQueryPopulator populator;
	private SolrIndexerQueryModel source;
	private IndexedTypeFlexibleSearchQuery target;
	private UserModel user;

	@Before
	public void setUp()
	{
		populator = new DefaultIndexedTypeFlexibleSearchQueryPopulator();
		source = new SolrIndexerQueryModel();
		target = new IndexedTypeFlexibleSearchQuery();
		user = new UserModel();
		user.setUid(USER_ID);
	}

	@Test
	public void testPopulateFromEmptySource()
	{
		populator.populate(source, target);
		Assert.assertFalse(target.isInjectCurrentDate());
		Assert.assertFalse(target.isInjectCurrentTime());
		Assert.assertFalse(target.isInjectLastIndexTime());
		Assert.assertNull(target.getParameterProviderId());
		assertThat(target.getParameters()).isEmpty();
		Assert.assertNull(target.getQuery());
		Assert.assertNull(target.getType());
		Assert.assertEquals("anonymous", target.getUserId());
	}

	@Test
	public void testPopulateFromSource()
	{
		source.setInjectCurrentDate(true);
		source.setInjectCurrentTime(true);
		source.setInjectLastIndexTime(true);
		source.setParameterProvider(PARAMETER_PROVIDER_ID);
		final List<SolrIndexerQueryParameterModel> solrQueryParameters = Arrays.asList(createParameter("param1", "value1"),
				createParameter("param2", "value2"));
		source.setSolrIndexerQueryParameters(solrQueryParameters);
		source.setQuery(QUERY);
		source.setType(IndexerOperationValues.FULL);
		source.setUser(user);

		populator.populate(source, target);

		Assert.assertTrue(target.isInjectCurrentDate());
		Assert.assertTrue(target.isInjectCurrentTime());
		Assert.assertTrue(target.isInjectLastIndexTime());
		Assert.assertEquals(PARAMETER_PROVIDER_ID, target.getParameterProviderId());

		assertThat(target.getParameters()).hasSize(2);

		Assert.assertEquals("value1", target.getParameters().get("param1"));
		Assert.assertEquals("value2", target.getParameters().get("param2"));
		Assert.assertEquals(QUERY, target.getQuery());
		Assert.assertEquals(IndexOperation.FULL, target.getType());
		Assert.assertEquals(USER_ID, target.getUserId());
	}

	private SolrIndexerQueryParameterModel createParameter(final String key, final String value)
	{
		final SolrIndexerQueryParameterModel solrIndexerQueryParameterModel = new SolrIndexerQueryParameterModel();
		solrIndexerQueryParameterModel.setName(key);
		solrIndexerQueryParameterModel.setValue(value);
		return solrIndexerQueryParameterModel;
	}
}
