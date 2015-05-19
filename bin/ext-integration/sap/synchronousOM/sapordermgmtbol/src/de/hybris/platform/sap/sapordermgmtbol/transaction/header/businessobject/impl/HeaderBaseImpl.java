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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.HeaderBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

/**
 * Common Header Information for all objects of the bo layer that are considered
 * to be sales documents.
 * 
 */
public class HeaderBaseImpl extends SimpleHeaderImpl implements HeaderBase {

	private static final int STRING_BUFFER_INIT_SIZE_FOR_TOSTRING = 440;

	/**
	 * Document can be changed?
	 */
	protected boolean changeable;
	/**
	 * Changed date
	 */
	protected Date changedAt;
	/**
	 * Created date
	 */
	protected Date createdAt;
	/**
	 * Currency
	 */
	protected String currency;
	/**
	 * Division
	 */
	protected String division;
	/**
	 * Distribution channel
	 */
	protected String disChannel;
	/**
	 * Document type
	 */
	protected DocumentType documentType;

	/**
	 * Freight value
	 */
	protected BigDecimal freightValue;
	/**
	 * Gross value
	 */
	protected BigDecimal grossValue;
	/**
	 * Net value
	 */
	protected BigDecimal netValue;
	/**
	 * Net value without freight
	 */
	protected BigDecimal netValueWOFreight;
	/**
	 * Gross value without freight
	 */
	protected BigDecimal grossValueWOFreight;
	/**
	 * Total value
	 */
	protected BigDecimal totalValue;
	/**
	 * Partner list
	 */
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST)
	protected PartnerList partnerList;
	/**
	 * Process type
	 */
	protected String processType;
	/**
	 * Process type description
	 */
	protected String processTypeDesc;
	/**
	 * Sales document number from back end
	 */
	protected String salesDocNumber;
	/**
	 * Purchase order number
	 */
	protected String purchaseOrderExt;
	/**
	 * Posting date
	 */
	protected Date postingDate;
	/**
	 * Origin of sales document
	 */
	protected String salesDocumentsOrigin;
	/**
	 * Sales organization
	 */
	protected String salesOrg;
	/**
	 * Sales office
	 */
	protected String salesOffice;
	/**
	 * Required delivery date
	 */
	protected Date reqDeliveryDate;

	/**
	 * Valid from date
	 */
	protected Date validFrom;
	/**
	 * Valid to date
	 */
	protected Date validTo;

	/**
	 * Shipping condition
	 */
	protected String shipCond;

	/**
	 * Tax value
	 */
	protected BigDecimal taxValue;
	/**
	 * Total discount
	 */
	protected BigDecimal totalDiscount;
	/**
	 * Delivery Priority
	 */
	protected String deliveryPriority;
	/**
	 * List of predecessors
	 */
	protected List<ConnectedDocument> predecessorList = new ArrayList<ConnectedDocument>(
			0);
	/**
	 * List of successors
	 */
	protected List<ConnectedDocument> successorList = new ArrayList<ConnectedDocument>(
			0);

	@Override
	public void addPredecessor(final ConnectedDocument predecessorData) {
		if (predecessorData != null) {
			predecessorList.add(predecessorData);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase
	 * #addSuccessor(com.sap.wec.app.common.module.transaction.businessobject
	 * .interf.ConnectedDocument)
	 */
	@Override
	public void addSuccessor(final ConnectedDocument successorData) {
		if (successorData != null) {
			successorList.add(successorData);
		}
	}

	/**
	 * Drops the state of the object. All reference fields, except partnerList,
	 * are set to null, all primitive types are set to the default values they
	 * would have after the creation of a new instance. Use this method to reset
	 * the state to the state a newly created object would have. The advantage
	 * is, that the overhead caused by the normal object creation is omitted.
	 */
	@Override
	public void clear() {
		super.clear();
		changedAt = null;
		handle = null;
		createdAt = null;
		currency = null;
		division = null;
		disChannel = null;
		documentType = null;
		freightValue = null;
		grossValue = null;
		netValue = null;
		partnerList.clearList();
		processType = null;
		salesDocNumber = null;
		purchaseOrderExt = null;
		postingDate = null;
		salesDocumentsOrigin = null;
		salesOrg = null;
		salesOffice = null;
		reqDeliveryDate = null;
		shipCond = null;
		taxValue = null;
		deliveryPriority = null;
		changeable = false;
		removeExtensionDataValues();
		predecessorList.clear();
		successorList.clear();
		validFrom = null;
		validTo = null;

	}

	/**
	 * Instead of a shallow-copy this returns a deep-copy of this
	 * <tt>HeaderBaseImpl</tt> instance.
	 * 
	 * @return a deep-copy of this HeaderBase
	 */
	@Override
	public Object clone() {

		HeaderBaseImpl myClone;
		myClone = (HeaderBaseImpl) super.clone();

		// primitives / immutable are copied by objects clone

		// clone mutable objects
		if (partnerList != null) {
			myClone.partnerList = partnerList.clone();
		}

		if (reqDeliveryDate != null) {
			myClone.reqDeliveryDate = (Date) reqDeliveryDate.clone();
		}

		// duplicate all lists, clone method of the ArrayList
		// is not suitable for this task, because it creates only a shallow
		// (not-deep) copy

		if (predecessorList != null) {
			myClone.predecessorList = new ArrayList<ConnectedDocument>(
					predecessorList.size());
			for (final ConnectedDocument conDoc : predecessorList) {
				myClone.predecessorList.add((ConnectedDocument) conDoc.clone());
			}
		}

		if (successorList != null) {
			myClone.successorList = new ArrayList<ConnectedDocument>(
					successorList.size());
			for (final ConnectedDocument conDoc : successorList) {
				myClone.successorList.add((ConnectedDocument) conDoc.clone());
			}
		}

		return myClone;
	}

	@Override
	public Text createText() {
		return (Text) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_TEXT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#createConnectedDocumentData()
	 */
	@Override
	public ConnectedDocument createConnectedDocument() {
		// get the help object

		final ConnectedDocument connectedDocument = (ConnectedDocument) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_CONNECTED_DOCUMENT);
		return connectedDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getChangedAt()
	 */
	@Override
	public Date getChangedAt() {
		return changedAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getCreatedAt()
	 */
	@Override
	public Date getCreatedAt() {
		return createdAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getCurrency()
	 */
	@Override
	public String getCurrency() {
		return currency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getDeliveryPriority()
	 */
	@Override
	public String getDeliveryPriority() {
		return deliveryPriority;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getDisChannel()
	 */
	@Override
	public String getDisChannel() {
		return disChannel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getDivision()
	 */
	@Override
	public String getDivision() {
		return division;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getDocumentType()
	 */
	@Override
	public DocumentType getDocumentType() {
		return documentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getFreightValue()
	 */
	@Override
	public BigDecimal getFreightValue() {
		return freightValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getGrossValue()
	 */
	@Override
	public BigDecimal getGrossValue() {
		return grossValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getNetValue()
	 */
	@Override
	public BigDecimal getNetValue() {
		return netValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getNetValueWOFreight()
	 */
	@Override
	public BigDecimal getNetValueWOFreight() {
		return netValueWOFreight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getPartnerId(java.lang.String)
	 */
	@Override
	public String getPartnerId(final String partnerFunction) {

		if (partnerList != null) {
			final PartnerListEntry partner = partnerList
					.getPartnerData(partnerFunction);
			if (partner != null) {
				return partner.getPartnerId();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getPartnerKey(java.lang.String)
	 */
	@Override
	public TechKey getPartnerKey(final String partnerFunction) {

		if (partnerList != null) {
			final PartnerListEntry partner = partnerList
					.getPartnerData(partnerFunction);
			if (partner != null) {
				return partner.getPartnerTechKey();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getPartnerList()
	 */
	@Override
	public PartnerList getPartnerList() {
		return partnerList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getPostingDate()
	 */
	@Override
	public Date getPostingDate() {
		return postingDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getPredecessorList()
	 */
	@Override
	public List<ConnectedDocument> getPredecessorList() {
		return predecessorList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getProcessType()
	 */
	@Override
	public String getProcessType() {
		return processType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getProcessTypeDesc()
	 */
	@Override
	public String getProcessTypeDesc() {
		return processTypeDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getPurchaseOrderExt()
	 */
	@Override
	public String getPurchaseOrderExt() {
		return purchaseOrderExt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getReqDeliveryDate()
	 */
	@Override
	public Date getReqDeliveryDate() {
		return reqDeliveryDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getSalesDocNumber()
	 */
	@Override
	public String getSalesDocNumber() {
		return salesDocNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getSalesDocumentsOrigin()
	 */
	@Override
	public String getSalesDocumentsOrigin() {
		return salesDocumentsOrigin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getSalesOffice()
	 */
	@Override
	public String getSalesOffice() {
		return salesOffice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getSalesOrg()
	 */
	@Override
	public String getSalesOrg() {
		return salesOrg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getShipCond()
	 */
	@Override
	public String getShipCond() {
		return shipCond;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getSuccessorList()
	 */
	@Override
	public List<ConnectedDocument> getSuccessorList() {
		return successorList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getTaxValue()
	 */
	@Override
	public BigDecimal getTaxValue() {
		return taxValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getTotalDiscount()
	 */
	@Override
	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#isChangeable()
	 */
	@Override
	public boolean isChangeable() {
		return changeable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setChangeable(boolean)
	 */
	@Override
	public void setChangeable(final boolean changeable) {
		this.changeable = changeable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setChangedAt(java.util.Date)
	 */
	@Override
	public void setChangedAt(final Date changedAt) {

		this.changedAt = changedAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setChangedAt(java.lang.String)
	 */
	// public void setChangedAt(String changedAt) {
	// this.changedAt = changedAt;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setCreatedAt(java.util.Date)
	 */
	@Override
	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setCreatedAt(java.lang.String)
	 */
	// public void setCreatedAt(String createdAt) {
	// this.createdAt = createdAt;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setCurrency(java.lang.String)
	 */
	@Override
	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setDeliveryPriority(java.lang.String)
	 */
	@Override
	public void setDeliveryPriority(final String deliveryPriority) {
		this.deliveryPriority = deliveryPriority;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setDisChannel(java.lang.String)
	 */
	@Override
	public void setDisChannel(final String disChannel) {
		this.disChannel = disChannel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setDivision(java.lang.String)
	 */
	@Override
	public void setDivision(final String division) {
		this.division = division;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setDocumentType(java.lang.String)
	 */
	@Override
	public void setDocumentType(final DocumentType documentType) {
		this.documentType = documentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setFreightValue(java.math.BigDecimal)
	 */
	@Override
	public void setFreightValue(final BigDecimal freightValue) {
		this.freightValue = freightValue;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setGrossValue(java.math.BigDecimal)
	 */
	@Override
	public void setGrossValue(final BigDecimal grossValue) {
		this.grossValue = grossValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setNetValue(java.math.BigDecimal)
	 */
	@Override
	public void setNetValue(final BigDecimal netValue) {
		this.netValue = netValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setNetValueWOFreight(java.math.BigDecimal)
	 */
	@Override
	public void setNetValueWOFreight(final BigDecimal netValueWOFreight) {
		this.netValueWOFreight = netValueWOFreight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase
	 * #setPartnerList(com.sap.wec.app.common.module.transaction.order
	 * .businessobject.interf.PartnerList)
	 */
	@Override
	public void setPartnerList(final PartnerList partnerList) {
		if (partnerList != null) {
			this.partnerList = partnerList;
		} else {
			this.partnerList.clearList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setPostingDate(java.util.Date)
	 */
	@Override
	public void setPostingDate(final Date postingDate) {
		this.postingDate = postingDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setProcessType(java.lang.String)
	 */
	@Override
	public void setProcessType(final String processType) {
		this.processType = processType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setProcessTypeDesc(java.lang.String)
	 */
	@Override
	public void setProcessTypeDesc(final String processTypeDesc) {
		this.processTypeDesc = processTypeDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setPurchaseOrderExt(java.lang.String)
	 */
	@Override
	public void setPurchaseOrderExt(final String purchaseOrderExt) {
		this.purchaseOrderExt = purchaseOrderExt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setReqDeliveryDate(java.util.Date)
	 */
	@Override
	public void setReqDeliveryDate(final Date reqDeliveryDate) {

		if (null == reqDeliveryDate) {
			this.reqDeliveryDate = null;
		} else {
			this.reqDeliveryDate = consolidateDate(reqDeliveryDate);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setSalesDocNumber(java.lang.String)
	 */
	@Override
	public void setSalesDocNumber(final String salesDocNumber) {
		this.salesDocNumber = salesDocNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setSalesDocumentsOrigin(java.lang.String)
	 */
	@Override
	public void setSalesDocumentsOrigin(final String salesDocOrigin) {
		salesDocumentsOrigin = salesDocOrigin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setSalesOffice(java.lang.String)
	 */
	@Override
	public void setSalesOffice(final String salesOffice) {
		this.salesOffice = salesOffice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setSalesOrg(java.lang.String)
	 */
	@Override
	public void setSalesOrg(final String salesOrg) {
		this.salesOrg = salesOrg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setShipCond(java.lang.String)
	 */
	@Override
	public void setShipCond(final String shipCond) {
		this.shipCond = shipCond;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setTaxValue(java.math.BigDecimal)
	 */
	@Override
	public void setTaxValue(final BigDecimal taxValue) {
		this.taxValue = taxValue;
	}

	/**
	 * Sets the key for the document and sets the docuemnt to dirty, so that a
	 * re-read is enforced with netx read.
	 * 
	 * @param techKey
	 *            the techKey to be set
	 */
	@Override
	public void setTechKey(final TechKey techKey) {
		super.setTechKey(techKey);
		setDirty(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setTotalDiscount(java.math.BigDecimal)
	 */
	@Override
	public void setTotalDiscount(final BigDecimal totalDiscount) {
		if (null == totalDiscount) {
			this.totalDiscount = BigDecimal.ZERO;
			// ?
		} else {
			this.totalDiscount = totalDiscount;
		}
	}

	/**
	 * Returns a simplifies string representation of the object. Useful for
	 * debugging/logging purpose, but not for display on the User Interface.
	 * 
	 * @return object as string
	 */
	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer(
				STRING_BUFFER_INIT_SIZE_FOR_TOSTRING);
		buf.append("HeaderBase [");
		buf.append("changeable=\"");
		buf.append("changeable");
		buf.append('\"');
		buf.append(", changedAt=\"");
		buf.append(changedAt);
		buf.append('\"');
		buf.append(", createdAt=\"");
		buf.append(createdAt);
		buf.append('\"');
		buf.append(", currency=\"");
		buf.append(currency);
		buf.append('\"');
		buf.append(", description=\"");
		buf.append(getDescription());
		buf.append('\"');
		buf.append(", division=\"");
		buf.append(division);
		buf.append('\"');
		buf.append(", disChannel=\"");
		buf.append(disChannel);
		buf.append('\"');
		buf.append(", freightValue=\"");
		buf.append(freightValue);
		buf.append('\"');
		buf.append(", grossValue=\"");
		buf.append(grossValue);
		buf.append('\"');
		buf.append(", netValue=\"");
		buf.append(netValue);
		buf.append('\"');
		buf.append(", processType=\"");
		buf.append(processType);
		buf.append('\"');
		buf.append(", salesDocNumber=\"");
		buf.append(salesDocNumber);
		buf.append('\"');
		buf.append(", partnerList=\"");
		buf.append(partnerList.toString());
		buf.append('\"');
		buf.append(", purchaseOrderExt=\"");
		buf.append(purchaseOrderExt);
		buf.append('\"');
		buf.append(", postingDate=\"");
		buf.append(postingDate);
		buf.append('\"');
		buf.append(", salesDocumentsOrigin=\"");
		buf.append(salesDocumentsOrigin);
		buf.append('\"');
		buf.append(", salesOrg=\"");
		buf.append(salesOrg);
		buf.append('\"');
		buf.append(", salesOffice=\"");
		buf.append(salesOffice);
		buf.append('\"');
		buf.append(", reqDeliveryDate=\"");
		buf.append(reqDeliveryDate);
		buf.append('\"');
		buf.append(", shipCond=\"");
		buf.append(shipCond);
		buf.append('\"');
		buf.append(", taxValue=\"");
		buf.append(taxValue);
		buf.append('\"');
		buf.append(", text=\"");
		buf.append(getText());
		buf.append('\"');
		buf.append(", techKey=\"");
		buf.append(techKey);
		buf.append('\"');
		buf.append(", deliveryPriority=\"");
		buf.append(deliveryPriority);
		buf.append('\"');
		buf.append(']');
		return buf.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getGrossValueWOFreight()
	 */
	@Override
	public BigDecimal getGrossValueWOFreight() {
		return grossValueWOFreight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setGrossValueWOFreight(java.math.BigDecimal)
	 */
	@Override
	public void setGrossValueWOFreight(final BigDecimal grossValueWOFreight) {
		this.grossValueWOFreight = grossValueWOFreight;
	}

	/**
	 * If year >9999: Set it to 9999
	 * 
	 * @param date
	 * @return Changed date (if needed)
	 */
	protected Date consolidateDate(Date date) {
		final int maxYear = 9999;
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		final int year = calendar.get(Calendar.YEAR);
		if (year > maxYear) {
			calendar.set(Calendar.YEAR, maxYear);
		}
		date = calendar.getTime();

		return date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getValidFrom()
	 */
	@Override
	public Date getValidFrom() {
		return validFrom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#getValidTo()
	 */
	@Override
	public Date getValidTo() {
		return validTo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setValidFrom(java.util.Date)
	 */
	@Override
	public void setValidFrom(final Date validFrom) {
		this.validFrom = validFrom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject
	 * .interf .HeaderBase#setValidTo(java.util.Date)
	 */
	@Override
	public void setValidTo(final Date validTo) {
		this.validTo = validTo;
	}

}
