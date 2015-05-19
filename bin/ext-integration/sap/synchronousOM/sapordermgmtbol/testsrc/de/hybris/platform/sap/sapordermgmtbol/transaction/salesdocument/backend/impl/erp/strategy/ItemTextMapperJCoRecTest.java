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

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import org.junit.Test;

import com.sap.conn.jco.JCoTable;


@SuppressWarnings("javadoc")
public class ItemTextMapperJCoRecTest extends JCORecTestBase
{
	public ItemTextMapper cutItems;

	@Override
	public void setUp()
	{
		super.setUp();
		cutItems = (ItemTextMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_TEXT_MAPPER);
		cutItems.setConfigLangIso("D");
		cutItems.setConfigTextId("0");
	}


	@Test
	public void testRead() throws JCoRecException
	{

		final JCoMockRepository testRepository = getJCORepository("jcoErpWecModel2DMapperTextTest");
		final JCoTable table = testRepository.getTable("ET_ITEM_TEXT_COMV");
		final JCoTable ttObjInst = testRepository.getTable("TT_OBJINST");

		final Item item[] = new Item[2];
		final ItemList itemsList = new ItemListImpl();
		for (int i = 0; i < item.length; i++)
		{
			item[i] = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
			item[i].setHandle("I" + i);
			item[i].setTechKey(new TechKey(item[i].getHandle()));

			final Text text = item[i].createText();
			text.setHandle("T" + i);
			item[i].setText(text);

			itemsList.add(item[i]);
		}
		final ObjectInstances objInst = new ObjectInstances(ttObjInst);

		cutItems.read(table, objInst, itemsList);

		for (int i = 0; i < item.length; i++)
		{
			final String loc = "item " + i;
			final Text text = item[i].getText();

			assertNotNull(loc, text);
			assertEquals(loc, "The Item " + i + " Text", text.getText());
			assertEquals(loc, "T" + i, text.getHandle());
		}

	}

}
