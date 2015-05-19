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

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.PrettyPrinter;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocumentItem;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Item of a sales document. Each sales document consists of a header and a number of items. This items are represented
 * by this class.
 * 
 * @version 1.0
 */
public class ItemBaseImpl extends SimpleItemImpl implements ItemBase
{

	@SuppressWarnings("unused")
	private final static Log4JWrapper sapLogger = Log4JWrapper.getInstance(ItemBaseImpl.class.getName());

	/**
	 * Created date
	 */
	protected Date createdAt = null;

	/**
	 * Currency
	 */
	protected String currency;

	/**
	 * Item is configurable?
	 */
	protected boolean configurable;
	/**
	 * Item is product variant?
	 */
	protected boolean variant;
	/**
	 * Configuration type
	 */
	protected String configType;
	/**
	 * Already delivered quantity
	 */
	protected BigDecimal deliveredQuantity;
	/**
	 * Delivered quantity unit
	 */
	protected String deliveredQuantityUnit;
	/**
	 * Item type usage
	 */
	protected String itmTypeUsage;
	/**
	 * Item usage
	 */
	protected ItemUsage itemUsage = ItemUsage.NONE;
	/**
	 * Business object type
	 */
	protected String businessObjectType;

	/**
	 * Latest delivery date
	 */
	protected Date latestDeliveryDate;

	/**
	 * Free quantity (related to free goods)
	 */
	protected BigDecimal freeQuantity;

	/**
	 * Previous quantity
	 */
	protected BigDecimal oldQuantity; // for business event modify document

	/**
	 * Item text
	 */
	protected Text text;

	/**
	 * Net price unit
	 */
	protected String netPriceUnit;

	/**
	 * Quantity for net price
	 */
	protected BigDecimal netQuantPriceUnit = BigDecimal.ZERO;
	/**
	 * Tax value
	 */
	protected BigDecimal taxValue;
	/**
	 * Net value
	 */
	protected BigDecimal netValue;
	/**
	 * Net value without freight
	 */
	protected BigDecimal netValueWOFreight;
	/**
	 * Freight value
	 */
	protected BigDecimal freightValue;
	/**
	 * Gross value
	 */
	protected BigDecimal grossValue;
	/**
	 * Gross value without freight
	 */
	protected BigDecimal grossValueWOFreight;
	/**
	 * Net price
	 */
	protected BigDecimal netPrice;
	/**
	 * Total discount
	 */
	protected BigDecimal totalDiscount;


	/**
	 * Total value
	 */
	protected BigDecimal totalValue;

	/**
	 * Required delivery date
	 */
	protected Date reqDeliveryDate;
	/**
	 * Confirmed delivery date
	 */
	protected Date confirmedDeliveryDate;
	/**
	 * Confirmed quantity
	 */
	protected BigDecimal confirmedQuantity;
	/**
	 * List of possible units
	 */
	protected List<String> possibleUnits = new ArrayList<String>();
	/**
	 * Quantity to deliver
	 */
	protected BigDecimal quantityToDeliver;
	/**
	 * List of schedule lines
	 */
	protected List<Schedline> scheduleLines = new ArrayList<Schedline>();
	/**
	 * Partner list
	 */
	protected PartnerList partnerList;

	/**
	 * Delivery priority
	 */
	protected String deliveryPriority;

	/**
	 * Item is deletable
	 */
	protected boolean deletable;
	/**
	 * Item can be cancelled
	 */
	protected boolean cancelable;

	/**
	 * Item is price relevant
	 */
	protected boolean priceRelevant = true;

	/**
	 * Doc Flow for the document items since doc flow harmonization for CRM 5.2
	 */
	protected ArrayList<ConnectedDocumentItem> predecessorList = new ArrayList<ConnectedDocumentItem>();

	/**
	 * List of successors
	 */
	protected ArrayList<ConnectedDocumentItem> successorList = new ArrayList<ConnectedDocumentItem>();

	/**
	 * Invalid from an business logic point of view
	 */
	protected boolean erroneous = false;

	/**
	 * Returns true if the product of the item exists in the backend
	 */
	protected boolean productExists = true;

	private boolean configurationDirty = false;

	/**
	 * Item category
	 */
	protected String itemCategory = "";

	/**
	 * Statistical
	 */
	protected boolean statistical = false;

	/**
	 * Default constructor for the Item
	 */
	public ItemBaseImpl()
	{
		super();
		createUniqueHandle();
	}

	@Override
	public void init()
	{
		partnerList = (PartnerList) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST);
	}

	@Override
	public void addPredecessor(final ConnectedDocumentItem predecessor)
	{
		if (predecessor != null)
		{
			predecessorList.add(predecessor);
		}
	}

	@Override
	public void addSuccessor(final ConnectedDocumentItem successor)
	{
		if (successor != null)
		{
			successorList.add(successor);
		}
	}

	/**
	 * Performs a deep-copy rather than a shallow copy. For sake of performance you should avoid haevy usage of this
	 * method, as creating a deep copy is quite expensive.<br>
	 * The <code>externalObject</code> will not be cloned, only the reference is copied.
	 * 
	 * @return deep-copy of this object
	 */
	@Override
	public Object clone()
	{
		ItemBaseImpl myClone;
		try
		{
			myClone = (ItemBaseImpl) super.clone();
		}
		catch (final CloneNotSupportedException ex)
		{
			// should not happen, because we are clone able
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", ex);
		}

		// clone mutable objects

		// we can't clone the external item explicitly, because we do not know
		// the concrete type

		if (partnerList != null)
		{
			myClone.partnerList = partnerList.clone();
		}



		if (text != null)
		{
			myClone.text = text.clone();
		}

		// duplicate all lists, clone method of the ArrayList
		// is not suitable for this task, because it creates only a shallow
		// (not-deep) copy

		if (predecessorList != null)
		{
			myClone.predecessorList = new ArrayList<ConnectedDocumentItem>(predecessorList.size());
			for (final ConnectedDocumentItem conDocItm : predecessorList)
			{
				myClone.predecessorList.add((ConnectedDocumentItem) conDocItm.clone());
			}
		}

		if (scheduleLines != null)
		{
			myClone.scheduleLines = new ArrayList<Schedline>(scheduleLines.size());
			for (final Schedline line : scheduleLines)
			{
				myClone.scheduleLines.add((Schedline) line.clone());
			}
		}

		if (successorList != null)
		{
			myClone.successorList = new ArrayList<ConnectedDocumentItem>(successorList.size());
			for (final ConnectedDocumentItem conDocItm : successorList)
			{
				myClone.successorList.add((ConnectedDocumentItem) conDocItm.clone());
			}
		}

		return myClone;
	}

	@Override
	public ConnectedDocumentItem createConnectedDocumentItemData()
	{
		// get the help object
		final ConnectedDocumentItem connectedDocumentItem = (ConnectedDocumentItem) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_CONNECTED_DOCUMENT_ITEM);
		return connectedDocumentItem;
	}

	@Override
	public Schedline createScheduleLine()
	{
		return (Schedline) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SCHEDLINE);
	}

	@Override
	public Text createText()
	{
		return (Text) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_TEXT);
	}

	/**
	 * Returns configType flag value, if the item is configurable
	 * 
	 * @return A,B,X,G, etc, if the item is configurable
	 */
	@Override
	public String getConfigType()
	{
		return configType;
	}

	@Override
	public Date getConfirmedDeliveryDate()
	{
		return confirmedDeliveryDate;
	}

	@Override
	public BigDecimal getConfirmedQuantity()
	{
		BigDecimal sum;
		if (confirmedQuantity == null)
		{
			// no confirmed quant set, extract it from schedlines
			sum = BigDecimal.ZERO;
			for (final Schedline sLine : getScheduleLines())
			{
				sum = sum.add(sLine.getCommittedQuantity());
			}
			// do not buffer computed Confirmed Quantity, as we would not know
			// when to recompute
		}
		else
		{
			sum = confirmedQuantity;
		}

		return sum;

	}

	@Override
	public Date getCreatedAt()
	{
		return createdAt;
	}

	@Override
	public void setCreatedAt(final Date createdAt)
	{
		this.createdAt = createdAt;
	}

	@Override
	public String getCurrency()
	{
		return currency;
	}

	@Override
	public BigDecimal getDeliveredQuantity()
	{
		return deliveredQuantity;
	}

	@Override
	public String getDeliveredQuantityUnit()
	{
		return deliveredQuantityUnit;
	}

	@Override
	public String getDeliveryPriority()
	{
		return deliveryPriority;
	}

	@Override
	public BigDecimal getFreeQuantity()
	{
		return freeQuantity;
	}

	@Override
	public BigDecimal getFreightValue()
	{
		return freightValue;
	}

	@Override
	public BigDecimal getGrossValue()
	{
		return grossValue;
	}

	@Override
	public String getItmTypeUsage()
	{
		return itmTypeUsage;
	}

	@Override
	public ItemUsage getItemUsage()
	{
		return itemUsage;
	}

	@Override
	public BigDecimal getNetPrice()
	{
		return netPrice;
	}

	@Override
	public String getNetPriceUnit()
	{
		return netPriceUnit;
	}

	@Override
	public BigDecimal getNetQuantPriceUnit()
	{
		return netQuantPriceUnit;
	}

	@Override
	public BigDecimal getNetValue()
	{
		return netValue;
	}

	@Override
	public BigDecimal getNetValueWOFreight()
	{
		return netValueWOFreight;
	}

	@Override
	public BigDecimal getOldQuantity()
	{
		return oldQuantity;
	}

	@Override
	public PartnerList getPartnerListData()
	{
		return partnerList;
	}

	@Override
	public List<String> getPossibleUnits()
	{
		return possibleUnits;
	}



	@Override
	public List<ConnectedDocumentItem> getPredecessorList()
	{
		return predecessorList;
	}

	@Override
	public BigDecimal getQuantityToDeliver()
	{
		return quantityToDeliver;
	}

	@Override
	public List<Schedline> getScheduleLines()
	{
		return scheduleLines;
	}

	@Override
	public List<ConnectedDocumentItem> getSuccessorList()
	{
		return successorList;
	}



	@Override
	public BigDecimal getTaxValue()
	{
		return taxValue;
	}

	@Override
	public Text getText()
	{
		return text;
	}

	@Override
	public BigDecimal getTotalDiscount()
	{
		return totalDiscount;
	}

	@Override
	public boolean isCancelable()
	{
		return cancelable;
	}

	@Override
	public boolean isConfigurable()
	{
		return configurable;
	}


	@Override
	public boolean isDeletable()
	{
		return deletable;
	}

	@Override
	public boolean isErroneous()
	{
		return erroneous;
	}

	/**
	 * returns true, if the item is price relevant
	 * 
	 * @return true, if the item is price relevant
	 */
	public boolean isPriceRelevant()
	{
		return priceRelevant;
	}

	@Override
	public void setBusinessObjectType(final String busType)
	{
		businessObjectType = busType;
	}

	@Override
	public void setCancelable(final boolean cancelable)
	{
		this.cancelable = cancelable;
	}

	/**
	 * Sets the configType flag, if the item is configurable
	 * 
	 * @param configType
	 *           contains the value <value> A,B,X, G, etc..</value>
	 */
	@Override
	public void setConfigType(final String configType)
	{
		this.configType = configType;
	}

	/**
	 * Marks an item as configurable
	 * 
	 * @param configurable
	 *           <code>true</code>, only if the item should be considered configurable
	 */
	@Override
	public void setConfigurable(final boolean configurable)
	{
		this.configurable = configurable;
	}




	@Override
	public void setConfirmedDeliveryDate(final Date confirmedDeliveryDate)
	{
		this.confirmedDeliveryDate = confirmedDeliveryDate;
	}

	@Override
	public void setConfirmedQuantity(final BigDecimal confirmedQuantity)
	{
		this.confirmedQuantity = confirmedQuantity;
	}

	@Override
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	@Override
	public void setDeletable(final boolean deletable)
	{
		this.deletable = deletable;
	}

	@Override
	public void setDeliverdQuantity(final BigDecimal deliveredQuantity)
	{
		this.deliveredQuantity = deliveredQuantity;
	}

	@Override
	public void setDeliverdQuantityUnit(final String unit)
	{
		deliveredQuantityUnit = unit;
	}

	@Override
	public void setDeliveryPriority(final String deliveryPriority)
	{
		this.deliveryPriority = deliveryPriority;
	}

	@Override
	public void setErroneous(final boolean erroneous)
	{
		this.erroneous = erroneous;
	}

	@Override
	public void setFreeQuantity(final BigDecimal arg)
	{
		freeQuantity = arg;
	}

	@Override
	public void setFreightValue(final BigDecimal freightValue)
	{
		this.freightValue = freightValue;
	}

	@Override
	public void setGrossValue(final BigDecimal grossValue)
	{
		this.grossValue = grossValue;
	}

	@Override
	public void setItmTypeUsage(final String itmTypeUsage)
	{
		this.itmTypeUsage = itmTypeUsage;
	}

	@Override
	public void setItemUsage(final ItemUsage itemUsage)
	{
		this.itemUsage = itemUsage;
	}

	@Override
	public void setNetPrice(final BigDecimal netPrice)
	{
		this.netPrice = netPrice;
	}

	@Override
	public void setNetPriceUnit(final String netPriceUnit)
	{
		this.netPriceUnit = netPriceUnit;
	}

	@Override
	public void setNetQuantPriceUnit(final BigDecimal netQuantPriceUnit)
	{
		this.netQuantPriceUnit = netQuantPriceUnit;
	}

	@Override
	public void setNetValue(final BigDecimal netValue)
	{
		this.netValue = netValue;
	}

	@Override
	public void setNetValueWOFreight(final BigDecimal netValueWOFreight)
	{
		this.netValueWOFreight = netValueWOFreight;
	}

	@Override
	public void setOldQuantity(final BigDecimal oldQuantity)
	{
		this.oldQuantity = oldQuantity;
	}

	/**
	 * Set the business partner list
	 * 
	 * @param partnerList
	 *           new list of business partners
	 */
	public void setPartnerList(final PartnerList partnerList)
	{
		this.partnerList = partnerList;
	}

	@Override
	public void setPartnerListData(final PartnerList list)
	{
		partnerList = list;

	}

	@Override
	public void setPossibleUnits(final List<String> possibleUnits)
	{
		this.possibleUnits = possibleUnits;
	}



	@Override
	public void setPriceRelevant(final boolean isPriceRelevant)
	{
		this.priceRelevant = isPriceRelevant;
	}

	@Override
	public void setQuantityToDeliver(final BigDecimal qty)
	{
		quantityToDeliver = qty;
	}

	@Override
	public void setReqDeliveryDate(final Date date)
	{
		reqDeliveryDate = date;
	}

	@Override
	public void setScheduleLines(final List<Schedline> scheduleLines)
	{
		this.scheduleLines = scheduleLines;
	}


	@Override
	public void setTaxValue(final BigDecimal taxValue)
	{
		this.taxValue = taxValue;
	}

	@Override
	public void setText(final Text text)
	{
		this.text = text;
	}

	@Override
	public void setTotalDiscount(final BigDecimal totalDiscount)
	{

		this.totalDiscount = totalDiscount;

	}


	/**
	 * Returns a String representation of the item, which can be used for debugging purpose. This is not suitable for
	 * display on the user interface.
	 */
	@Override
	public String toString()
	{
		final PrettyPrinter pp = new PrettyPrinter("ItemBaseImpl [");
		pp.add(techKey, "techKey");
		pp.add(getQuantity(), "quantity");
		pp.add(netValue, "netValue");
		pp.add(currency, "currency");
		pp.add(getDescription(), "description");
		pp.add(Boolean.valueOf(configurable), "configurable");
		pp.add(itmTypeUsage, "itmTypeUsage");
		pp.add(businessObjectType, "businessObjectType");
		pp.add(Integer.valueOf(getNumberInt()), "numberInt");
		pp.add(oldQuantity, "oldQuantity");
		pp.add(text, "text");
		pp.add(getUnit(), "unit");
		pp.add(taxValue, "taxValue");
		pp.add(netValueWOFreight, "netValueWOFreight");
		pp.add(freightValue, "freightValue");
		pp.add(grossValue, "grossValue");
		pp.add(reqDeliveryDate, "reqDeliveryDate");
		pp.add(confirmedDeliveryDate, "confirmedDeliveryDate");
		pp.add(confirmedQuantity, "confirmedQuantity");
		pp.add(partnerList, "partnerList");
		pp.add(getMessageList(), "messages");
		pp.add(Boolean.valueOf(priceRelevant), "priceRelevant");
		return pp.toString() + "]\n";
	}

	@Override
	public boolean isProductExists()
	{
		return productExists;
	}

	@Override
	public void setProductExists(final boolean productExists)
	{
		this.productExists = productExists;

	}

	@Override
	public Date getReqDeliveryDate()
	{
		return reqDeliveryDate;
	}

	@Override
	public boolean isProductEmpty()
	{
		return (getProductId() == null) || "".equals(getProductId());
	}

	@Override
	public BigDecimal getGrossValueWOFreight()
	{
		return grossValueWOFreight;
	}

	@Override
	public void setGrossValueWOFreight(final BigDecimal grossValueWOFreight)
	{
		this.grossValueWOFreight = grossValueWOFreight;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemBase#setLatestDeliveryDate(java.util.Date)
	 */
	@Override
	public void setLatestDeliveryDate(final Date latestDeliveryDate)
	{
		this.latestDeliveryDate = latestDeliveryDate;
	}

	@Override
	public void setQuantity(final BigDecimal quantity)
	{
		super.setQuantity(quantity);
		if (oldQuantity == null)
		{
			oldQuantity = quantity;
		}

	}

	@Override
	public boolean isConfigurationDirty()
	{

		return this.configurationDirty;
	}

	@Override
	public void setConfigurableDirty(final boolean isDirty)
	{
		configurationDirty = isDirty;

	}

	@Override
	public boolean isVariant()
	{
		return variant;
	}

	@Override
	public void setVariant(final boolean variant)
	{
		this.variant = variant;
	}

	@Override
	public String getItemCategory()
	{
		return itemCategory;
	}

	@Override
	public void setItemCategory(final String itemCategory)
	{
		this.itemCategory = itemCategory;
	}

	@Override
	public boolean isStatistical()
	{
		return statistical;
	}

	@Override
	public void setStatistical(final boolean statistical)
	{
		this.statistical = statistical;
	}

}
