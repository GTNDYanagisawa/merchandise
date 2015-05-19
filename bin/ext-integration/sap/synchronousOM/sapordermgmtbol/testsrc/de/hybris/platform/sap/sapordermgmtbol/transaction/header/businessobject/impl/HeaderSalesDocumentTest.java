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
package de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class HeaderSalesDocumentTest extends SapordermanagmentBolSpringJunitTest
{

	private HeaderSalesDocument classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		classUnderTest = (HeaderSalesDocument) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
	}

	@Test
	public void testRecallId()
	{
		final String recallId = "recallID";
		classUnderTest.setRecallId(recallId);
		assertEquals(recallId, classUnderTest.getRecallId());
	}

	@Test
	public void testRecallDesc()
	{
		final String recallDesc = "recallDesc";
		classUnderTest.setRecallDesc(recallDesc);
		assertEquals(recallDesc, classUnderTest.getRecallDesc());
	}

	@Test
	public void TestIsDocumentTypeQuotation()
	{
		classUnderTest.setDocumentType(DocumentType.ORDER);
		assertEquals(false, classUnderTest.isDocumentTypeQuotation());
		classUnderTest.setDocumentType(DocumentType.QUOTATION);
		assertEquals(true, classUnderTest.isDocumentTypeQuotation());
	}

	@Test
	public void TestIsDocumentTypeRFQ()
	{
		classUnderTest.setDocumentType(DocumentType.ORDER);
		assertEquals(false, classUnderTest.isDocumentTypeRFQ());
		classUnderTest.setDocumentType(DocumentType.RFQ);
		assertEquals(true, classUnderTest.isDocumentTypeRFQ());
	}

	@Test
	public void testGenericFactory()
	{
		assertNotNull(classUnderTest.getGenericFactory());
	}

}
