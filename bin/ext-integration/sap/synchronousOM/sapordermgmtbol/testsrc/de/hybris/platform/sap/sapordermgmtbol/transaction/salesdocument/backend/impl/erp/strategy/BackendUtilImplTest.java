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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.sap.conn.jco.JCoTable;


@SuppressWarnings("javadoc")
public class BackendUtilImplTest extends JCORecTestBase
{
	BackendUtilImpl classUnderTest = null;
	JCoTable tableForPreprocessing = null;

	ItemList items = new ItemListImpl();

	private final Item item = new ItemSalesDoc();
	private final String posnr = "000010";
	private final String product = "WEC_DRAGON_CAR";

	private void readTablesFromRepository()
	{

		try
		{
			final JCoMockRepository testRepository = getJCORepository("jcoReposBase");
			tableForPreprocessing = testRepository.getTable("TT_MESSAGES_PREPROCESS");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}

	@Before
	public void init()
	{
		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BACKEND_UTIL);
		readTablesFromRepository();

		item.setProductId(product);
		item.setNumberInt(new BigDecimal(posnr).intValue());
		items.add(item);
	}

	@Test
	public void testInstance()
	{
		assertNotNull(classUnderTest);
	}


	@Test
	public void testFindItem()
	{
		final Item itemInList = classUnderTest.findItem(items, posnr);
		assertEquals(this.item, itemInList);
	}
}
