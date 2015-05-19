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
package de.hybris.platform.cockpit.daos;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.daos.TypeDefinitionDao.TypeDefinition;
import de.hybris.platform.cockpit.daos.impl.DefaultTypeDefinitionDao;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TypeDefinitionDaoTest
{
	@Mock
	private FlexibleSearchService flexibleSearchService;

	private DefaultTypeDefinitionDao typeDefinitionDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		typeDefinitionDao = new DefaultTypeDefinitionDao();
		typeDefinitionDao.setFlexibleSearchService(flexibleSearchService);
	}

	/**
	 * Test method for {@link de.hybris.platform.cockpit.daos.TypeDefinitionDao#findAllTypeDefinitions()}.
	 */
	@Test
	public void testFindAllTypeDefinitions()
	{
		final List resList = new ArrayList();

		resList.add(createResultEntry("testTypeCode1", "testName1", PK.fromLong(2), PK.fromLong(1)));
		resList.add(createResultEntry("testTypeCode2", "testName2", PK.fromLong(3), PK.fromLong(1)));

		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);

		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);

		//assert that we have a list of proper wrapped TypeDefinitions
		final List<TypeDefinition> allTypeDefinitions = typeDefinitionDao.findAllTypeDefinitions();
		Assert.assertTrue("Wrong size of result, expected 2 but got " + allTypeDefinitions.size(), allTypeDefinitions.size() == 2);

		final Iterator<TypeDefinition> iterator = allTypeDefinitions.iterator();
		final TypeDefinition typeDef1 = iterator.next();
		Assert.assertEquals("testTypeCode1", typeDef1.getCode());
		Assert.assertEquals("testName1", typeDef1.getName());
		Assert.assertTrue(typeDef1.getPk().getLongValue() == 2);
		Assert.assertTrue(typeDef1.getSupertypePk().getLongValue() == 1);


		final TypeDefinition typeDef2 = iterator.next();
		Assert.assertEquals("testTypeCode2", typeDef2.getCode());
		Assert.assertEquals("testName2", typeDef2.getName());
		Assert.assertTrue(typeDef2.getPk().getLongValue() == 3);
		Assert.assertTrue(typeDef2.getSupertypePk().getLongValue() == 1);

	}


	protected List<Object> createResultEntry(final String code, final String name, final PK pk, final PK supertypePk)
	{
		final List<Object> ret = new ArrayList<Object>();
		ret.add(pk);
		ret.add(code);
		ret.add(name);
		ret.add(supertypePk);
		return ret;
	}

}
