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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class DocumentTypeMappingTest
{

	@Test
	public void testGetDocumentTypeByTransactionGroupForQuotation()
	{
		assertEquals(DocumentType.QUOTATION,
				DocumentTypeMapping.getDocumentTypeByTransactionGroup(RFCConstants.TRANSACTION_GROUP_QUOTATION));
	}

	@Test
	public void testGetDocumentTypeByProcessType()
	{
		assertEquals(DocumentType.QUOTATION, DocumentTypeMapping.getDocumentTypeByProcess(RFCConstants.TRANSACTION_TYPE_QUOTATION));
		assertEquals(DocumentType.RFQ, DocumentTypeMapping.getDocumentTypeByProcess(RFCConstants.TRANSACTION_TYPE_INQUIRY));
	}

	@Test
	public void testGetDocumentTypeByTransactionGroupForRFQ()
	{
		assertEquals(DocumentType.RFQ,
				DocumentTypeMapping.getDocumentTypeByTransactionGroup(RFCConstants.TRANSACTION_GROUP_INQUIRY));
	}


}
