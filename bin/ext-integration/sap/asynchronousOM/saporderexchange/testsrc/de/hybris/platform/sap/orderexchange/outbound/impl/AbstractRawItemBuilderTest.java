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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class AbstractRawItemBuilderTest
{
	class CsvBuilderForTest extends AbstractRawItemBuilder<OrderModel>
	{

		@Override
		protected Logger getLogger()
		{
			return null;
		}
	}

	private CsvBuilderForTest cut;

	@Mock
	private RawItemContributor<OrderModel> contributor1;

	@Mock
	private RawItemContributor<OrderModel> contributor2;

	@Before
	public void setUp()
	{
		cut = new CsvBuilderForTest();
		MockitoAnnotations.initMocks(this);
		Mockito.when(contributor1.getColumns()).thenReturn(new HashSet<String>(Arrays.asList("Col1", "Col2")));
		Mockito.when(contributor2.getColumns()).thenReturn(new HashSet<String>(Arrays.asList("Col1", "Col3")));
		final Map<String, Object> fieldMap1 = new HashMap<>();
		fieldMap1.put("Col1", "A");
		fieldMap1.put("Col2", "B");
		final List<Map<String, Object>> csvList1 = Arrays.asList(fieldMap1);
		Mockito.when(contributor1.createRows(Mockito.any(OrderModel.class))).thenReturn(csvList1);
		final Map<String, Object> fieldMap2 = new HashMap<>();
		fieldMap2.put("Col1", "A");
		fieldMap2.put("Col3", "C");
		final List<Map<String, Object>> csvList2 = Arrays.asList(fieldMap2);
		Mockito.when(contributor2.createRows(Mockito.any(OrderModel.class))).thenReturn(csvList2);

	}

	@Test
	public void testRegister1Contributor() throws Exception
	{
		cut.setContributors(Arrays.asList(contributor1));
		Assert.assertEquals(1, cut.getContributors().size());
		Assert.assertEquals(2, cut.getColumns().size());
	}

	@Test
	public void testRegister2ContributorWithCommonColumns() throws Exception
	{
		cut.setContributors(Arrays.asList(contributor1, contributor2));
		Assert.assertEquals(2, cut.getContributors().size());
		Assert.assertEquals(3, cut.getColumns().size());
	}


}
