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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.StatusObject;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Represents the backend's view of the items of a shopping basket.
 * 
 */
public interface Item extends ItemBase, StatusObject
{

	/**
	 * Return value for back-end Delivery meaning product can be delivered completely.
	 */
	public static final String DELIVERY_IN_STOCK = "instock";

	/**
	 * Return value for back-end Delivery meaning product can be delivered partly.
	 */
	public static final String DELIVERY_PARTLY = "limited";

	/**
	 * Return value for back-end Delivery meaning product delivery is delayed
	 */
	public static final String DELIVERY_DELAYED = "delayed";

	/**
	 * Return value for back-end Delivery meaning product can not be delivered
	 */
	public static final String DELIVERY_OUT_OF_STOCK = "notinstock";

	/**
	 * Creates a new AlternativProductListData.
	 * 
	 * @return AlternativProductListData
	 */
	public AlternativeProductList createAlternativProductList();

	/**
	 * Returns the alternativProductList.
	 * 
	 * @return AlternativProductListData
	 */
	public AlternativeProductList getAlternativProductList();

	/**
	 * returns the billing status of the item
	 * 
	 * @return the actual billing statuts of the item
	 */
	public BillingStatus getBillingStatus();

	/**
	 * Returns the overall status
	 * 
	 * @return the overall item status
	 */
	public OverallStatus getOverallStatus();

	/**
	 * Returns the parentHandle, that is the handle of the parent, if the position is a sub position
	 * 
	 * @return String the parentHandle
	 */
	public String getParentHandle();

	/**
	 * Get the payment terms.
	 * 
	 * @return paymentTerms the payment terms set.
	 */
	public String getPaymentTerms();

	/**
	 * Get the date which is used to calculate prices in IPC
	 * 
	 * @return date relevant for pricing
	 */
	public Date getPricingDate();

	/**
	 * Get The shipping status
	 * 
	 * @return the shipping status of the item
	 */
	public ShippingStatus getShippingStatus();

	/**
	 * Returns the shipTo associated with this item
	 * 
	 * @return the shipTo to which this item will be shipped to
	 */
	public ShipTo getShipTo();

	/**
	 * Returns the substitutionReasonId.
	 * 
	 * @return String
	 */
	public String getSubstitutionReasonId();

	/**
	 * Returns the systemProductId.
	 * 
	 * @return String
	 */
	public String getSystemProductId();

	/**
	 * @return true if the item can be changed
	 */
	public boolean isChangeAllowed();

	/**
	 * This method returns a flag, that indicates if the item is copied from another item, e.g. when an order is created
	 * from an order template If so, this flag might be used, to suppress things like campaign determination, etc. for
	 * the copied item.
	 * 
	 * @return true if this item is copied from another item false else
	 */
	public boolean isCopiedFromOtherItem();

	/**
	 * Determines whether the item is a free good by checking the item usage.
	 * 
	 * @return <code>true</code>, only if this item is a FreeGood
	 */
	public boolean isFreeGood();

	/**
	 * Indicates whether the item is originated from catalog.
	 * 
	 * @return <code>true</code> if the item is from catalog; otherwise <code>false</code>.
	 */
	public boolean isFromCatalog();

	/**
	 * Checks whether this item can be merged with the given item.
	 * 
	 * @param toMerge
	 *           the item this item should be merged with
	 * @return <code>true</code>, only if this item can be merged with the given item
	 */
	public boolean isMergeSupported(Item toMerge);

	/**
	 * Sets the alternativProductListData.
	 * 
	 * @param alternativProductList
	 *           The alternativProductList to set
	 */
	public void setAlternativProductList(AlternativeProductList alternativProductList);

	/**
	 * Indicate whether the item is originated from catalog.
	 * 
	 * @param fromCatalog
	 *           should be set to <code>true</code> if the item originated from catalog
	 */
	public void setFromCatalog(boolean fromCatalog);

	/**
	 * Sets the parentHandle, that is the handle of the parent, if the position is a sub position
	 * 
	 * @param parentHandle
	 *           the new value for the parentHandle
	 */
	public void setParentHandle(String parentHandle);

	/**
	 * Set the payment terms.
	 * 
	 * @param paymentTerms
	 *           the payment terms to be set.
	 */
	public void setPaymentTerms(String paymentTerms);

	/**
	 * set the date which is used to calculate prices in IPC
	 * 
	 * @param pricingDate
	 *           date which should be used for pricing
	 */
	public void setPricingDate(Date pricingDate);

	/**
	 * Sets the shiptTo for this item.
	 * 
	 * @param shipTo
	 *           shipTo to which the item will be shipped
	 */
	public void setShipTo(ShipTo shipTo);

	/**
	 * Sets the substitutionReasonId.
	 * 
	 * @param substitutionReasonId
	 *           The substitutionReasonId to set
	 */
	public void setSubstitutionReasonId(String substitutionReasonId);

	/**
	 * Sets the systemProductId.
	 * 
	 * @param systemProductId
	 *           The systemProductId to set
	 */
	public void setSystemProductId(String systemProductId);

	/**
	 * Sets rejection/cancellation reason for the order.<br>
	 * 
	 * @param rejection
	 *           cancellation reason (key)
	 */
	public void setRejectionCode(String rejection);

	/**
	 * Gets rejection/cancellation reason for the order.<br>
	 * 
	 * @return cancellation reason (key)
	 */
	public String getRejectionCode();

	/**
	 * This getter is dependent on the quantity and the free quantity of this product.<br>
	 * 
	 * @return the quantity that must be paid
	 */
	public BigDecimal getQuantityToPay();

	/**
	 * This method sets a flag, that indicates if the item is copied from another item, e.g. when an order is created
	 * from an order template If so, this flag might be used, to suppress things like campaign determination, etc. for
	 * the copied item.
	 * 
	 * @param isCopiedFromOtherItem
	 *           true when item is copied
	 */
	public void setCopiedFromOtherItem(boolean isCopiedFromOtherItem);

	/**
	 * Get the processing status of the sales document item. The processing status equates GBSTA field in ERP back end<br>
	 * Used for the definition of possible cancellation
	 * 
	 * @return BusinessStatus
	 */
	public ProcessingStatus getProcessingStatus();

	/**
	 * Get the processing status of the sales document item. The processing status equates GBSTA field in ERP back end<br>
	 * 
	 * @param processingStatus
	 *           ProcessingStatus
	 */
	public void setProcessingStatus(ProcessingStatus processingStatus);




	/**
	 * Apply alternative product, e.g. product to be substituted with .<br>
	 * 
	 * @param productGUID
	 *           GUID of the applied product
	 * @param productID
	 *           ID of the applied product
	 * @throws CommunicationException
	 *            in case of an back-end error
	 */
	public void applyAlternativeProduct(TechKey productGUID, String productID) throws CommunicationException;

	/**
	 * Indicates whether the item is a subitem (has a parent) or not <br>
	 * 
	 * @return true if subitem
	 */
	public boolean isSubItem();

	/**
	 * Return product configuration representation
	 * 
	 * @return Product configuration
	 */
	public ConfigModel getProductConfiguration();

	/**
	 * Sets product configuration
	 * 
	 * @param configModel
	 */
	void setProductConfiguration(ConfigModel configModel);

	/**
	 * States that the configuration is dirty, i.e. needs to be sent to the backend
	 * 
	 * @param productConfigurationDirty
	 */
	void setProductConfigurationDirty(boolean productConfigurationDirty);

	/**
	 * Do we need to send the product configuration to the back end?
	 * 
	 * @return Configuration is dirty
	 */
	boolean isProductConfigurationDirty();

	/**
	 * Sets external representation of configuration
	 * 
	 * @param externalConfiguration
	 */
	void setProductConfigurationExternal(Configuration externalConfiguration);

	/**
	 * @return External representation of product configuration
	 */
	Configuration getProductConfigurationExternal();





}
