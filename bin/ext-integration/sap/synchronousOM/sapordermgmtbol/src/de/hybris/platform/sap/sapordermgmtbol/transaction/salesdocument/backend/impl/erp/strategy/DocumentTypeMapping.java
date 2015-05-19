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

import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;


/**
 * Class to Map R3 backend specific documents to Ecommerce specific ones.
 **/
public class DocumentTypeMapping
{

	/**
	 * Method maps the R3 type (VBTYP) to document type.
	 * 
	 * @param type
	 *           (R3 type)
	 * @return String document type
	 */
	public static DocumentType getDocumentTypeByProcess(final String type)
	{
		DocumentType retVal = null;

		if (RFCConstants.TRANSACTION_TYPE_ORDER.equals(type))
		{
			retVal = DocumentType.ORDER;
		}
		else if (RFCConstants.TRANSACTION_TYPE_DELIVERY.equals(type))
		{
			retVal = DocumentType.DELIVERY;
		}
		else if (RFCConstants.TRANSACTION_TYPE_INVOICE.equals(type))
		{
			retVal = DocumentType.INVOICE;
		}
		else if (RFCConstants.TRANSACTION_TYPE_INVOICE_CANCEL.equals(type))
		{
			retVal = DocumentType.INVOICE_CNC;
		}
		else if (RFCConstants.TRANSACTION_TYPE_CREDIT_MEMO.equals(type))
		{
			retVal = DocumentType.CREDIT_MEMO;
		}
		else if (RFCConstants.TRANSACTION_TYPE_CREDIT_MEMO_CANCEL.equals(type))
		{
			retVal = DocumentType.CREDIT_MEMO_CNC;
		}
		else if (RFCConstants.TRANSACTION_TYPE_QUOTATION.equals(type))
		{
			retVal = DocumentType.QUOTATION;
		}
		else if (RFCConstants.TRANSACTION_TYPE_INQUIRY.equals(type))
		{
			retVal = DocumentType.RFQ;
		}

		return retVal;
	}

	/**
	 * Method maps the R3 type (TRANS_GROUP or TRVOG) to document type.
	 * 
	 * @param type
	 *           (R3 type)
	 * @return String document type
	 */
	public static DocumentType getDocumentTypeByTransactionGroup(final String type)
	{
		DocumentType retVal = null;

		if (RFCConstants.TRANSACTION_GROUP_ORDER.equals(type))
		{
			retVal = DocumentType.ORDER;
		}
		else if (RFCConstants.TRANSACTION_GROUP_QUOTATION.equals(type))
		{
			retVal = DocumentType.QUOTATION;
		}
		else if (RFCConstants.TRANSACTION_GROUP_INQUIRY.equals(type))
		{
			retVal = DocumentType.RFQ;
		}

		return retVal;
	}
}
