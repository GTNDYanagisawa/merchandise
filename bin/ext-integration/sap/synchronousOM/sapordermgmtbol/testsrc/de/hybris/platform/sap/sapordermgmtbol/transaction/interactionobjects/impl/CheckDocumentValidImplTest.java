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
package de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class CheckDocumentValidImplTest extends SapordermanagmentBolSpringJunitTest
{

	private CheckDocumentValidImpl classUnderTest;
	private SalesDocument salesDoc;
	private Item item1;
	private Item item2;



	@Override
	@Before
	public void setUp()
	{
		classUnderTest = (CheckDocumentValidImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_INT_CHECK_DOCUMENT_VALID);
		salesDoc = (SalesDocument) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);
		item1 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		item2 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		salesDoc.addItem(item1);
		salesDoc.addItem(item2);


	}



	@Test
	public void testCollectHeaderMessages()
	{
		salesDoc.addMessage(new Message(0));
		salesDoc.getHeader().addMessage(new Message(0));

		assertEquals(2, classUnderTest.collectHeaderMessages(salesDoc).size());
	}

	@Test
	public void testIsDocumentSupported() throws CommunicationException
	{
		salesDoc.getHeader().setDocumentType(DocumentType.BASKET);
		assertTrue(classUnderTest.isDocumentSupported(salesDoc, DocumentType.BASKET));

		salesDoc.getHeader().setDocumentType(DocumentType.ORDER);
		assertFalse(classUnderTest.isDocumentSupported(salesDoc, DocumentType.BASKET));

		salesDoc.getHeader().setDocumentType(DocumentType.ORDER);
		assertTrue(classUnderTest.isDocumentSupported(salesDoc, DocumentType.ORDER));

		salesDoc.getHeader().setDocumentType(DocumentType.QUOTATION);
		assertFalse(classUnderTest.isDocumentSupported(salesDoc, DocumentType.ORDER));

		assertFalse(classUnderTest.isDocumentSupported(salesDoc, DocumentType.UNKNOWN));
		assertFalse(classUnderTest.isDocumentSupported(salesDoc, DocumentType.LEAD));
		assertFalse(classUnderTest.isDocumentSupported(salesDoc, DocumentType.RETURNS));

	}

}
