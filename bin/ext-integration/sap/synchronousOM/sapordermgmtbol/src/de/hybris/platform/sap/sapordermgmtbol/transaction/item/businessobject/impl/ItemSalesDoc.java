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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.PrettyPrinter;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.UserStatusList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProductList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;


/**
 * Item of a sales document. Each sales document consists of a header and a number of items. This items are represented
 * by this class.
 * 
 */
public class ItemSalesDoc extends ItemBaseImpl implements Item
{

	/**
	 * System product ID
	 */
	protected String systemProductId;
	/**
	 * Reason for substitution ID
	 */
	protected String substitutionReasonId;
	/**
	 * ShipTo
	 */
	protected ShipTo shipToLine;
	/**
	 * Does a delivery exist
	 */
	protected boolean deliveryExists = false;

	/**
	 * List of alternative products
	 */
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_ALTERNATIVE_PRODUCT_LIST)
	protected AlternativeProductList alternativProductList;
	/**
	 * Item was copied from another one
	 */
	protected boolean copiedFromOtherItem = false;
	/**
	 * Payment terms
	 */
	protected String paymentTerms;
	/**
	 * Item is from catalog
	 */
	protected boolean fromCatalog = false;

	/**
	 * Handle of parent item
	 */
	protected String parentHandle;

	// the date which is used to calculate prices in IPC
	/**
	 * Pricing date
	 */
	protected Date pricingDate;

	/**
	 * 
	 */
	// sales item status
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_SHIPPING_STATUS)
	protected ShippingStatus shipStatus;

	/**
	 * 
	 */
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_BILLING_ITEM_STATUS)
	protected BillingStatus billStatus;

	/**
	 * 
	 */
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_OVERALL_STATUS_ORDER)
	protected OverallStatus procStatus;
	/**
	 * 
	 */
	protected ProcessingStatus processingStatus;

	/**
	 * 
	 */
	protected UserStatusList userStatusList;




	/**
	 * Rejection code
	 */
	protected String rejectionCode;
	/**
	 * Is gift card?
	 */
	protected boolean giftCard;
	/**
	 * Item text
	 */
	protected Text individualText;


	@Override
	public ConfigModel getProductConfiguration()
	{
		return productConfiguration;
	}

	ConfigModel productConfiguration;
	private boolean productConfigurationDirty = true;
	private Configuration externalConfiguration;


	/**
	 * Default constructor for the Item
	 */
	public ItemSalesDoc()
	{
		super();
		init();
	}

	@Override
	public void init()
	{
		//		procStatus = (OverallStatusOrder) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_OVERALL_STATUS_ORDER);
		//		procStatus.init(EStatus.UNDEFINED);
		//		billStatus = (BillingStatus) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BILLING_ITEM_STATUS);
		//		billStatus.init(EStatus.UNDEFINED);
		//		shipStatus = (ShippingStatus) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SHIPPING_STATUS);
		//		shipStatus.init(EStatus.UNDEFINED);
	}

	/**
	 * Performs a deep-copy rather than a shallow copy. For sake of performance you should avoid haevy usage of this
	 * method, as creating a deep copy is quite expensive.<br>
	 * The <code>TransferItem</code> will not be cloned, only the reference is copied.
	 * 
	 * @return deep-copy of this object
	 */
	@Override
	public Object clone()
	{

		final ItemSalesDoc myClone = (ItemSalesDoc) super.clone();

		// clone mutable objects
		if (alternativProductList != null)
		{
			myClone.alternativProductList = (AlternativeProductListImpl) alternativProductList.clone();
		}

		if (billStatus != null)
		{
			myClone.billStatus = (BillingStatus) billStatus.clone();
		}

		if (pricingDate != null)
		{
			myClone.pricingDate = (Date) pricingDate.clone();
		}
		if (procStatus != null)
		{
			myClone.procStatus = (OverallStatus) procStatus.clone();
		}
		if (shipToLine != null)
		{
			myClone.shipToLine = shipToLine.clone();
		}

		// do not clone transfer item

		if (userStatusList != null)
		{
			myClone.userStatusList = (UserStatusList) userStatusList.clone();
		}

		return myClone;
	}

	/**
	 * Creates a new AlternativProductList.
	 * 
	 * @return AlternativProductListData
	 */
	@Override
	public AlternativeProductList createAlternativProductList()
	{
		return (AlternativeProductList) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ALTERNATIVE_PRODUCT_LIST);
	}

	@Override
	public boolean equals(final Object object)
	{

		if (!(object instanceof Item))
		{
			return false;
		}

		final Item item = (Item) object;

		// Case the TechKey is null for both objects, we rely on the hashcode
		if (TechKey.isEmpty(getTechKey()) && TechKey.isEmpty(item.getTechKey()))
		{
			return this == item;
			// return toString().equals(item.toString());
		}
		else if ((getTechKey() == null) || (item.getTechKey() == null))
		{
			return false;
		}
		else
		{
			return getTechKey().equals(item.getTechKey());
		}

	}

	/**
	 * Returns the alternativProductList.
	 * 
	 * @return AlternativProductList
	 */
	@Override
	public AlternativeProductList getAlternativProductList()
	{
		return alternativProductList;
	}

	@Override
	public OverallStatus getOverallStatus()
	{
		return procStatus;
	}

	/**
	 * Returns the parentHandle, that is the handle of the parent, if the position is a subposition
	 * 
	 * @return String the parentHandle
	 */
	@Override
	public String getParentHandle()
	{
		return parentHandle;
	}

	/**
	 * Get the payment terms.
	 * 
	 * @return String the payment terms
	 */
	@Override
	public String getPaymentTerms()
	{
		return paymentTerms;
	}

	@Override
	public Date getPricingDate()
	{
		return pricingDate;
	}

	@Override
	public ShippingStatus getShippingStatus()
	{
		return shipStatus;
	}

	/**
     *
     */
	@Override
	public ShipTo getShipTo()
	{
		return shipToLine;
	}

	/**
	 * Returns the substitutionReasonId.
	 * 
	 * @return String
	 */
	@Override
	public String getSubstitutionReasonId()
	{
		return substitutionReasonId;
	}

	/**
	 * Returns the systemProductId.
	 * 
	 * @return String
	 */
	@Override
	public String getSystemProductId()
	{
		return systemProductId;
	}

	// implemented to prevent JLin / Finbug, because of equals implemented, but
	// not hashCode
	@Override
	public int hashCode()
	{
		// super is sufficient. It calls hasCode() on the techKey
		return super.hashCode();
	}

	/**
	 * This method returns a flag, that indicates if the item is copied from another item, e.g. when an order is created
	 * from an order template If so, this flag might be used, to suppress things like campaign determination, etc. for
	 * the copied item.
	 * 
	 * @return true if this item is copied from another item false else
	 */
	@Override
	public boolean isCopiedFromOtherItem()
	{
		return copiedFromOtherItem;
	}

	/**
	 * @return true if the item can be changed
	 */
	@Override
	public boolean isChangeAllowed()
	{
		final boolean isBOMProduct = ItemUsage.BOM == itemUsage;
		// completed or BOM product
		final boolean isChangeAllowed = !(getOverallStatus().isProcessed() || isBOMProduct);
		return isChangeAllowed;

	}

	@Override
	public boolean isFreeGood()
	{
		return ItemUsage.FREE_GOOD_EXCL.equals(getItemUsage()) || "FREE".equals(getItmTypeUsage());
	}

	@Override
	public boolean isFromCatalog()
	{
		return fromCatalog;
	}

	/**
	 * Overwrites the super implementation. Merge is not allowed if a gift card or a configurable product is involved.
	 * Sub items are also not subject to merge. <br>
	 * 
	 * @return true if the items allowed to be merged
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item#isMergeSupported(de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item)
	 */
	@Override
	public boolean isMergeSupported(final Item toMergeWith)
	{

		// for configurable products merge is not supported
		if (toMergeWith.isConfigurable() || isConfigurable())
		{
			return false;
		}

		// merge is not supported for subitems
		if (!toMergeWith.getParentId().isInitial() || !getParentId().isInitial())
		{
			return false;
		}

		return true;

	}

	/**
	 * Sets the alternativProductList.
	 */
	@Override
	public void setAlternativProductList(final AlternativeProductList alternativProductList)
	{
		this.alternativProductList = alternativProductList;
	}

	@Override
	public void setBillingStatus(final BillingStatus billStatus)
	{
		this.billStatus = billStatus;
	}

	/**
	 * This method sets a flag, that indicates if the item is copied from another item, e.g. when an order is created
	 * from an order template If so, this flag might be used, to suppress things like campaign determination, etc. for
	 * the copied item.
	 * 
	 * @param isCopiedFromOtherItem
	 *           true when item is copied
	 */
	@Override
	public void setCopiedFromOtherItem(final boolean isCopiedFromOtherItem)
	{
		this.copiedFromOtherItem = isCopiedFromOtherItem;
	}

	@Override
	public void setFromCatalog(final boolean fromCatalog)
	{
		this.fromCatalog = fromCatalog;
	}

	@Override
	public void setOverallStatus(final OverallStatus procStatus)
	{
		this.procStatus = procStatus;
	}

	/**
	 * Sets the parentHandle, that is the handle of the parent, if the position is a subposition
	 * 
	 * @param parentHandle
	 *           the new value for the parentHandle
	 */
	@Override
	public void setParentHandle(final String parentHandle)
	{
		this.parentHandle = parentHandle;
	}

	/**
	 * Set the payment terms.
	 * 
	 * @param paymentTerms
	 *           the payment terms to be set.
	 */
	@Override
	public void setPaymentTerms(final String paymentTerms)
	{
		this.paymentTerms = paymentTerms;
	}

	@Override
	public void setPricingDate(final Date pricingDate)
	{

		this.pricingDate = pricingDate;

	}

	@Override
	public void setShippingStatus(final ShippingStatus shipStatus)
	{
		this.shipStatus = shipStatus;
	}

	/**
     *
     */
	@Override
	public void setShipTo(final ShipTo shipToLine)
	{
		this.shipToLine = shipToLine;
	}

	/**
	 * Sets the substitutionReasonId.
	 * 
	 * @param substitutionReasonId
	 *           The substitutionReasonId to set
	 */
	@Override
	public void setSubstitutionReasonId(final String substitutionReasonId)
	{
		this.substitutionReasonId = substitutionReasonId;
	}

	/**
	 * Sets the systemProductId.
	 * 
	 * @param systemProductId
	 *           The systemProductId to set
	 */
	@Override
	public void setSystemProductId(final String systemProductId)
	{
		this.systemProductId = systemProductId;
	}

	/**
	 * Sets list of user status
	 * 
	 * @param userStatusList
	 */
	public void setUserStatusList(final UserStatusList userStatusList)
	{
		this.userStatusList = userStatusList;
	}

	@Override
	public String toString()
	{
		final PrettyPrinter pp = new PrettyPrinter(super.toString() + "ItemSalesDoc[");
		pp.add(shipToLine, "shipToLine");
		pp.add(getQuantity(), "quantity");
		return pp.toString() + "]";
	}

	@Override
	public String getRejectionCode()
	{
		return rejectionCode;
	}

	@Override
	public void setRejectionCode(final String rejection)
	{
		rejectionCode = rejection;

	}

	/**
	 * Indicates whether item is a gift card
	 * 
	 * @param isGiftCard
	 */
	public void setGiftCard(final boolean isGiftCard)
	{
		giftCard = isGiftCard;
	}

	/**
	 * @return Is it a gift card?
	 */
	public boolean isGiftCard()
	{
		return giftCard;
	}

	@Override
	public BigDecimal getQuantityToPay()
	{
		BigDecimal toPay = this.getQuantity();
		final BigDecimal free = this.getFreeQuantity();

		if ((toPay != null) && (free != null))
		{
			toPay = toPay.subtract(free);
		}

		return toPay;
	}

	@Override
	public ProcessingStatus getProcessingStatus()
	{
		return processingStatus;
	}

	@Override
	public void setProcessingStatus(final ProcessingStatus processingStatus)
	{
		this.processingStatus = processingStatus;
	}


	@Override
	public void applyAlternativeProduct(final TechKey productGUID, final String productID) throws CommunicationException
	{
		setProductGuid(productGUID);
		setProductId(productID);
		setProductChanged(false);
	}

	@Override
	public boolean isSubItem()
	{
		final TechKey techKey = getParentId();

		return !TechKey.isEmpty(techKey);
	}

	@Override
	public BillingStatus getBillingStatus()
	{
		return billStatus;
	}


	@Override
	public void setProductConfiguration(final ConfigModel configModel)
	{
		this.productConfiguration = configModel;

	}

	@Override
	public boolean isProductConfigurationDirty()
	{
		return productConfigurationDirty;
	}


	@Override
	public void setProductConfigurationDirty(final boolean productConfigurationDirty)
	{
		this.productConfigurationDirty = productConfigurationDirty;
	}


	@Override
	public void setProductConfigurationExternal(final Configuration externalConfiguration)
	{
		this.externalConfiguration = externalConfiguration;

	}


	@Override
	public Configuration getProductConfigurationExternal()
	{
		return externalConfiguration;
	}



}
