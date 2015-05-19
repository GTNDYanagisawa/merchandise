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
package de.hybris.platform.solrfacetsearch.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.PK;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class SearchResultTest extends AbstractSolrIntegrationTest
{
	@Resource
	protected PriceService priceService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
	}

	@Override
	protected void prepareIndexForTest() throws Exception
	{
		dropIndex();
	}

	/**
	 * This case tests {@link SearchResult}.getResultPKs() method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetResultPK() throws Exception
	{
		indexerService.performFullIndex(facetSearchConfig);
		final PK expectedPK = productService.getProductForCode(hwOnline, "HW2300-2356").getPk();
		query.setCatalogVersion(hwOnline);
		query.searchInField("code", "HW2300-2356");
		final SearchResult result = facetSearchService.search(query);
		final List<PK> resultPK = result.getResultPKs();
		assertNotNull("Resulting PK cannot be null", resultPK);
		assertTrue("Resulting PK list should be of size 1", resultPK.size() == 1);
		assertEquals("Resulting Pk not as expected", expectedPK, resultPK.get(0));
	}

	/**
	 * This case tests {@link SearchResult}.getResultCodes() method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetResultCode() throws Exception
	{
		indexerService.performFullIndex(facetSearchConfig);
		final String expectedCode = productService.getProductForCode(hwOnline, "HW2300-2356").getCode();
		query.setCatalogVersion(hwOnline);
		query.searchInField("code", "HW2300-2356");
		final SearchResult result = facetSearchService.search(query);
		final List<String> resutCodes = result.getResultCodes();
		assertNotNull("Resulting code cannot be null", resutCodes);
		assertTrue("Resulting code list should be of size 1", resutCodes.size() == 1);
		assertEquals("Resulting code not as expected", expectedCode, resutCodes.get(0));

	}

	@Test
	public void testSearchResultSerializable() throws Exception
	{
		final SearchResult resultIn = facetSearchService.search(query);

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(resultIn);
		out.close();

		final ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		final SearchResult resultOut = (SearchResult) in.readObject();
		in.close();
		assertNotNull(resultOut);
	}

}
