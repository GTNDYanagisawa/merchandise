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
package de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf;

import java.math.BigDecimal;
import java.util.Date;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BusinessStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.StatusObject;


/**
 * This interface provides header data of a SalesDocument. The usual way to access it is calling method
 * SalesDocument.getHeader().
 * 
 */
public interface Header extends HeaderBase, StatusObject
{

	/**
	 * Constant defining that the number of items of a document is unknown. Thus it must be determined by the size of the
	 * itemList.
	 */
	public static final long NO_OF_ITEMS_UNKNOWN = 0;

	/**
	 * Constant defining that no delivery priority is selected
	 */
	public static final String NO_DELIVERY_PRIORITY = "00";

	/**
	 * Gets the billing status of the Document.
	 * 
	 * @return Billing status
	 */
	public BusinessStatus getBillingStatus();

	/**
	 * Gets the Bill-To-Party of the document
	 * 
	 * @return Bill-To-Party
	 */
	public BillTo getBillTo();

	/**
	 * Get DELIVERY status. Might be different from OVERALL status.
	 * 
	 * @return one of the possible status values represented by the constants defined in this class with the names
	 *         <code>DOCUMENT_COMPLETION_*</code>.
	 */
	public String getDeliveryStatus();

	/**
	 * Get the incoterms1
	 * 
	 * @return String the incoterms1
	 */
	public String getIncoTerms1();

	/**
	 * Get the incoterms1 description
	 * 
	 * @return String the incoterms1 description
	 */
	public String getIncoTerms1Desc();

	/**
	 * Get the incoterms2.
	 * 
	 * @return String the incoterms2.
	 */
	public String getIncoTerms2();

	/**
	 * Get the IPC document id.
	 * 
	 * @return the IPC document id
	 */
	public TechKey getIpcDocumentId();

	/**
	 * get the aggregated overall status
	 * 
	 * @return overall status
	 */
	public OverallStatus getOverallStatus();

	/**
	 * get the payment types
	 * 
	 * @return payment types
	 */
	public String getPaymentTerms();

	/**
	 * Get the payment terms description.
	 * 
	 * @return String the payment terms description.
	 */
	public String getPaymentTermsDesc();

	/**
	 * Get the pricing date
	 * 
	 * @return the pricing date
	 */
	public Date getPricingDate();

	/**
	 * Returns the recall description
	 * 
	 * @return the recall description
	 */
	public String getRecallDesc();

	/**
	 * Returns the recall id
	 * 
	 * @return the recall id
	 */
	public String getRecallId();

	/**
	 * Returns shipping manual price condition
	 * 
	 * @return shipping manual price condition
	 */
	public String getShippingManualPriceCondition();

	/**
	 * get the business status
	 * 
	 * @return business status
	 */
	public BusinessStatus getShippingStatus();

	/**
	 * Get the ship to information as an backend layer interface.
	 * 
	 * @return the ship to information
	 */
	public ShipTo getShipTo();

	/**
	 * Returns total manual price condition
	 * 
	 * @return total manual price condition
	 */
	public String getTotalManualPriceCondition();

	/**
	 * Determines whether or not, the document's delivery status is COMPLETED.
	 * 
	 * @return <code>true</code> if the object is in status COMPLETED, otherwise <code>false</code>.
	 */
	public boolean isDeliveryStatusCompleted();

	/**
	 * Determines whether or not, the document's delivery status is INPROCESS.
	 * 
	 * @return <code>true</code> if the object is in status INPROCESS, otherwise <code>false</code>.
	 */
	public boolean isDeliveryStatusInProcess();

	/**
	 * Determines whether or not, the document's delivery status is OPEN.
	 * 
	 * @return <code>true</code> if the object is in status OPEN, otherwise <code>false</code>.
	 */
	public boolean isDeliveryStatusOpen();

	/**
	 * Determines whether or not, the document's type is ORDER.
	 * 
	 * @return <code>true</code> if the type is ORDER, otherwise <code>false</code>.
	 */
	public boolean isDocumentTypeOrder();

	/**
	 * Sets the Bill-To-Party into the the document.
	 * 
	 * @param billTo
	 *           bill-to party
	 */
	public void setBillTo(BillTo billTo);

	/**
	 * Set DELIVERY status to completed
	 */
	public void setDeliveryStatusCompleted();

	/**
	 * Set DELIVERY status to in process
	 */
	public void setDeliveryStatusInProcess();

	/**
	 * Set DELIVERY status to open
	 */
	public void setDeliveryStatusOpen();

	/**
	 * Set the incoterms1.
	 * 
	 * @param incoTerms1
	 *           the incoterms1 to be set.
	 */
	public void setIncoTerms1(String incoTerms1);

	/**
	 * Set the incoterms1 description
	 * 
	 * @param incoTerms1Desc
	 *           the incoterms1 description to be set.
	 */
	public void setIncoTerms1Desc(String incoTerms1Desc);

	/**
	 * Set the incoterms2.
	 * 
	 * @param incoTerms2
	 *           the incoterms2 to be set.
	 */
	public void setIncoTerms2(String incoTerms2);

	/**
	 * Set the IPC document id.
	 * 
	 * @param ipcDocumentId
	 *           the IPC document id to be set
	 */
	public void setIpcDocumentId(TechKey ipcDocumentId);


	/**
	 * Set the payment terms.
	 * 
	 * @param paymentTerms
	 *           the payment terms to be set.
	 */
	public void setPaymentTerms(String paymentTerms);

	/**
	 * Set the payment terms description
	 * 
	 * @param paymentTermsDesc
	 *           the payment terms description to be set.
	 */
	public void setPaymentTermsDesc(String paymentTermsDesc);

	/**
	 * Set the pricing date.
	 * 
	 * @param pricingDate
	 *           the pricing date
	 */
	public void setPricingDate(Date pricingDate);

	/**
	 * Sets the recall description
	 * 
	 * @param recallDesc
	 *           the recall description
	 */
	public void setRecallDesc(String recallDesc);

	/**
	 * Sets the recall id
	 * 
	 * @param recallId
	 *           the recall id
	 */
	public void setRecallId(String recallId);

	/**
	 * Sets the shipping manual price condition
	 * 
	 * @param priceType
	 *           new shipping manual price condition
	 */
	public void setShippingManualPriceCondition(String priceType);

	/**
	 * Sets the default ship to for the header. This ship to is used, if no special information is set at the item level.
	 * 
	 * @param shipToData
	 *           The ship to, to be set
	 */
	public void setShipTo(ShipTo shipToData);

	/**
	 * Sets the total manual price condition
	 * 
	 * @param priceType
	 *           manual price conidtion
	 */
	public void setTotalManualPriceCondition(String priceType);

	/**
	 * Return the value that has to be deducted of the payment amount.
	 * 
	 * @return BigDecimal
	 */
	public BigDecimal getPaymentDeduction();

	/**
	 * Set the value that has to be deducted of the payment amount.
	 * 
	 * @param value
	 *           amount to be deducted
	 */
	public void setPaymentDeduction(BigDecimal value);

	/**
	 * Determines whether or not, the document's type is QUOTATION.
	 * 
	 * @return <code>true</code> if the type is QUOTATION, otherwise <code>false</code>.
	 */
	public boolean isDocumentTypeQuotation();

	/**
	 * Determines whether or not, the document's type is RFQ.
	 * 
	 * @return <code>true</code> if the type is RFQ, otherwise <code>false</code>.
	 */
	public boolean isDocumentTypeRFQ();

}