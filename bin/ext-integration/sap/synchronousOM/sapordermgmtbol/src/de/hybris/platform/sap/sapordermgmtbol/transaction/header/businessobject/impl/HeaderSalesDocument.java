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

import java.math.BigDecimal;
import java.util.Date;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ShippingStatusImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BusinessStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.UserStatusList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

/**
 * Common Header Information for all objects of the bo layer that are considered
 * to be sales documents.
 * 
 */
public class HeaderSalesDocument extends HeaderBaseImpl implements Header {

	private static final int STRING_BUFFER_INIT_SIZE_FOR_TOSTRING = 70;

	private ShipTo shipTo;
	private BillTo billTo;
	private TechKey ipcDocumentId;
	private String statusDelivery;
	private String paymentTerms;
	private String paymentTermsDesc;
	private String incoTerms1;
	private String incoTerms1Desc;
	private String incoTerms2;

	/**
	 * Service recall ID
	 */
	protected String recallId;
	/**
	 * Service recall description
	 */
	protected String recallDesc;

	// sales document status
	private ShippingStatus shipStatus;
	private BillingStatus billStatus;
	private OverallStatus overallStatus;

	/**
	 * List of user status
	 */
	protected UserStatusList userStatusList;

	/**
	 * Pricing date
	 */
	protected Date pricingDate;

	/**
	 * Total pricing
	 */
	private String totalPriceType = null;
	/**
	 * Shipping price type
	 */
	private String shippingPriceType = null;

	private BigDecimal paymentDeduction = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .impl. HeaderBaseImpl#clear()
	 */
	@Override
	public void clear() {
		super.clear();
		statusDelivery = null;
		shipTo = null;
		billTo = null;
		ipcDocumentId = null;
		paymentTerms = null;
		paymentTermsDesc = null;
		incoTerms1 = null;
		incoTerms1Desc = null;
		incoTerms2 = null;
		recallId = null;
		recallDesc = null;
		deliveryPriority = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .impl. HeaderBaseImpl#clone()
	 */
	@Override
	public Object clone() {
		final HeaderSalesDocument myClone = (HeaderSalesDocument) super.clone();

		// primitives / immutable are copied by objects clone

		// clone mutable objects
		if (billStatus != null) {
			myClone.billStatus = (BillingStatus) billStatus.clone();
		}
		if (billTo != null) {
			myClone.billTo = billTo.clone();
		}
		if (pricingDate != null) {
			myClone.pricingDate = (Date) pricingDate.clone();
		}
		if (processType != null) {
			myClone.setProcessType(processType);
		}
		if (shipStatus != null) {
			myClone.shipStatus = (ShippingStatusImpl) shipStatus.clone();
		}
		if (shipTo != null) {
			myClone.shipTo = shipTo.clone();
		}

		// duplicate all lists, clone method of the ArrayList
		// is not suitable for this task, because it creates only a shallow
		// (not-deep) copy

		return myClone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getBillingStatus()
	 */
	@Override
	public BusinessStatus getBillingStatus() {
		return billStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getBillTo()
	 */
	@Override
	public BillTo getBillTo() {
		return billTo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getDeliveryStatus()
	 */
	@Override
	public String getDeliveryStatus() {
		return statusDelivery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getIncoTerms1()
	 */
	@Override
	public String getIncoTerms1() {
		return incoTerms1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getIncoTerms1Desc()
	 */
	@Override
	public String getIncoTerms1Desc() {
		return incoTerms1Desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getIncoTerms2()
	 */
	@Override
	public String getIncoTerms2() {
		return incoTerms2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getIpcDocumentId()
	 */
	@Override
	public TechKey getIpcDocumentId() {
		return ipcDocumentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getOverallStatus()
	 */
	@Override
	public OverallStatus getOverallStatus() {
		return overallStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getPaymentTerms()
	 */
	@Override
	public String getPaymentTerms() {
		return paymentTerms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getPaymentTermsDesc()
	 */
	@Override
	public String getPaymentTermsDesc() {
		return paymentTermsDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getPricingDate()
	 */
	@Override
	public Date getPricingDate() {
		return pricingDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getRecallDesc()
	 */
	@Override
	public String getRecallDesc() {
		return recallDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getRecallId()
	 */
	@Override
	public String getRecallId() {
		return recallId;
	}

	/*
	 * Returns shipping manual price condition
	 */
	@Override
	public String getShippingManualPriceCondition() {
		return shippingPriceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getShippingStatus()
	 */
	@Override
	public BusinessStatus getShippingStatus() {
		return shipStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getShipTo()
	 */
	@Override
	public ShipTo getShipTo() {
		return shipTo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#getTotalManualPriceCondition()
	 */
	@Override
	public String getTotalManualPriceCondition() {
		return totalPriceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#isDeliveryStatusCompleted()
	 */
	@Override
	public boolean isDeliveryStatusCompleted() {
		return DOCUMENT_COMPLETION_STATUS_COMPLETED.equals(statusDelivery);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#isDeliveryStatusInProcess()
	 */
	@Override
	public boolean isDeliveryStatusInProcess() {
		return DOCUMENT_COMPLETION_STATUS_COMPLETED.equals(statusDelivery);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#isDeliveryStatusOpen()
	 */
	@Override
	public boolean isDeliveryStatusOpen() {
		return DOCUMENT_COMPLETION_STATUS_OPEN.equals(statusDelivery);
	}

	@Override
	public boolean isDocumentTypeOrder() {
		return DocumentType.ORDER.equals(documentType);
	}

	@Override
	public boolean isDocumentTypeQuotation() {
		return DocumentType.QUOTATION.equals(documentType);
	}

	@Override
	public boolean isDocumentTypeRFQ() {
		return DocumentType.RFQ.equals(documentType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf
	 * .StatusObject #
	 * setBillingStatus(com.sap.wec.app.common.module.transaction.businessobject
	 * .impl.BusinessStatus)
	 */
	@Override
	public void setBillingStatus(final BillingStatus billStatus) {
		this.billStatus = billStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header
	 * #setBillTo(com.sap.wec.app.common.module.transaction.businessobject
	 * .interf.BillTo)
	 */
	@Override
	public void setBillTo(final BillTo billTo) {
		this.billTo = billTo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setDeliveryStatusCompleted()
	 */
	@Override
	public void setDeliveryStatusCompleted() {
		statusDelivery = DOCUMENT_COMPLETION_STATUS_COMPLETED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setDeliveryStatusInProcess()
	 */
	@Override
	public void setDeliveryStatusInProcess() {
		statusDelivery = DOCUMENT_COMPLETION_STATUS_INPROCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setDeliveryStatusOpen()
	 */
	@Override
	public void setDeliveryStatusOpen() {
		statusDelivery = DOCUMENT_COMPLETION_STATUS_OPEN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setIncoTerms1(java.lang.String)
	 */
	@Override
	public void setIncoTerms1(final String incoTerms1) {
		this.incoTerms1 = incoTerms1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setIncoTerms1Desc(java.lang.String)
	 */
	@Override
	public void setIncoTerms1Desc(final String incoTerms1Desc) {
		this.incoTerms1Desc = incoTerms1Desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setIncoTerms2(java.lang.String)
	 */
	@Override
	public void setIncoTerms2(final String incoTerms2) {
		this.incoTerms2 = incoTerms2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setIpcDocumentId(com.sap.wec.tc.core.common.TechKey)
	 */
	@Override
	public void setIpcDocumentId(final TechKey ipcDocumentId) {
		this.ipcDocumentId = ipcDocumentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf
	 * .StatusObject #
	 * setOverallStatus(com.sap.wec.app.common.module.transaction.businessobject
	 * .impl.BusinessStatus)
	 */
	@Override
	public void setOverallStatus(final OverallStatus procStatus) {
		overallStatus = procStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setPaymentTerms(java.lang.String)
	 */
	@Override
	public void setPaymentTerms(final String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setPaymentTermsDesc(java.lang.String)
	 */
	@Override
	public void setPaymentTermsDesc(final String paymentTermsDesc) {
		this.paymentTermsDesc = paymentTermsDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setPricingDate(java.lang.String)
	 */
	@Override
	public void setPricingDate(final Date pricingDate) {
		this.pricingDate = pricingDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setRecallDesc(java.lang.String)
	 */
	@Override
	public void setRecallDesc(final String recallDesc) {
		this.recallDesc = recallDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header#setRecallId(java.lang.String)
	 */
	@Override
	public void setRecallId(final String recallId) {
		this.recallId = recallId;
	}

	/*
	 * Sets the shipping manual price condition
	 * 
	 * @param priceType new shipping manual price condition
	 */
	@Override
	public void setShippingManualPriceCondition(final String priceType) {
		shippingPriceType = priceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf
	 * .StatusObject #
	 * setShippingStatus(com.sap.wec.app.common.module.transaction
	 * .businessobject .impl.BusinessStatus)
	 */
	@Override
	public void setShippingStatus(final ShippingStatus shipStatus) {
		this.shipStatus = shipStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .Header
	 * #setShipTo(com.sap.wec.app.common.module.transaction.businessobject
	 * .interf.ShipTo)
	 */
	@Override
	public void setShipTo(final ShipTo shipTo) {
		this.shipTo = shipTo;
	}

	@Override
	public void setTotalManualPriceCondition(final String priceType) {
		totalPriceType = priceType;
	}

	/**
	 * Sets list of user statuses
	 * 
	 * @param userStatusList
	 */
	public void setUserStatusList(final UserStatusList userStatusList) {
		this.userStatusList = userStatusList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .impl. HeaderBaseImpl#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer(
				STRING_BUFFER_INIT_SIZE_FOR_TOSTRING);
		buf.append(super.toString());
		buf.append(" HeaderSalesDocument [");
		buf.append('\"');
		buf.append(", shipTo=\"");
		buf.append(shipTo);
		buf.append('\"');
		buf.append(", recallId=\"");
		buf.append(recallId);
		buf.append('\"');
		buf.append(", recallDesc=\"");
		buf.append(recallDesc);
		buf.append('\"');
		buf.append(']');
		return buf.toString();
	}

	@Override
	public BigDecimal getPaymentDeduction() {
		return paymentDeduction;
	}

	@Override
	public void setPaymentDeduction(final BigDecimal value) {
		paymentDeduction = value;
	}

	GenericFactory getGenericFactory() {
		return genericFactory;
	}

}
