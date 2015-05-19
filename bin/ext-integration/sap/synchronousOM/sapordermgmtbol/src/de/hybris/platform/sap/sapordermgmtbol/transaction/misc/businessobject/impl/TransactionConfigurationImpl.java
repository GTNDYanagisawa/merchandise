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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectHelper;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.interf.TransactionConfigurationBackend;

import java.util.Locale;
import java.util.Map;

import sap.hybris.integration.models.constants.SapmodelConstants;


/**
 * The Shop object contains all kind of settings for the application.
 * <p>
 * The shop object use <code>ShopBackend</code> to comunicate with the backend.<br>
 * <i>For further details see the interface <code>ShopData</code></i>
 */
@BackendInterface(TransactionConfigurationBackend.class)
public class TransactionConfigurationImpl extends BusinessObjectBase implements TransactionConfiguration
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(TransactionConfigurationImpl.class.getName());

	protected static final String DEFAULT_TEXTID_HEADER = "1000";
	protected static final String DEFAULT_TEXTID_ITEM = "1000";

	// ////////////////////////////
	// Attributes directly maintained by WCB
	// ////////////////////////////
	private String headerTextId;
	private String itemTextId;



	// Properties for ECO ERP
	private String customerPurchOrderType;
	private String deliveryBlock;



	private boolean mergeIdenticalProducts;




	@Override
	public String getCustomerPurchOrderType()
	{
		return customerPurchOrderType;
	}

	@Override
	public void setCustomerPurchOrderType(final String customerPurchOrderType)
	{
		this.customerPurchOrderType = customerPurchOrderType;
	}

	@Override
	public String getDeliveryBlock()
	{
		return deliveryBlock;
	}

	@Override
	public void setDeliveryBlock(final String deliveryBlock)
	{
		this.deliveryBlock = deliveryBlock;
	}

	/**
	 * @return the text ID under which header texts are stored in the backend
	 */
	@Override
	public String getHeaderTextID()
	{
		if ((null == headerTextId) || headerTextId.isEmpty())
		{
			headerTextId = DEFAULT_TEXTID_HEADER;
		}

		return headerTextId;
	}

	/**
	 * @return the text ID under which item texts are stored in the backend
	 */
	@Override
	public String getItemTextID()
	{

		if ((null == itemTextId) || itemTextId.isEmpty())
		{
			itemTextId = DEFAULT_TEXTID_ITEM;
		}

		return itemTextId;
	}

	/**
	 * @param arg
	 *           the text ID under which header texts are stored in the backend
	 */
	@Override
	public void setHeaderTextID(final String arg)
	{
		headerTextId = arg;
	}

	@Override
	public void setItemTextID(final String arg)
	{
		itemTextId = arg;
	}



	@Override
	@Deprecated
	public String getProcessType()
	{
		return (String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE);
	}

	protected TransactionConfigurationBackend getBackendService() throws BackendException
	{
		if (backendObject == null)
		{
			backendObject = getBackendBusinessObject();
		}
		return (TransactionConfigurationBackend) backendObject;
	}

	@Override
	public String getLanguageIso()
	{
		// unit test enabling
		if (LocaleUtil.getLocale() != null)
		{
			return LocaleUtil.getLocale().getLanguage();
		}
		else
		{
			return Locale.US.getLanguage();
		}
	}

	@Override
	public boolean isMergeIdenticalProducts()
	{
		return mergeIdenticalProducts;
	}

	@Override
	public void setMergeIdenticalProducts(final boolean mergeIdenticalProducts)
	{
		this.mergeIdenticalProducts = mergeIdenticalProducts;
	}


	@Override
	public String getSourceForNetValueWithoutFreight()
	{
		return (String) moduleConfigurationAccess
				.getProperty(SapordermgmtbolConstants.CONFIGURATION_PROPERTY_SOURCE_NET_WO_FREIGHT);
	}

	@Override
	public String getSourceForFreightItem()
	{
		return (String) moduleConfigurationAccess.getProperty(SapordermgmtbolConstants.CONFIGURATION_PROPERTY_SOURCE_FREIGHT);

	}




	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.wec.app.ecom.module.checkout.businessobject.interf.
	 * CheckoutConfiguration#getAllowedDeliveryTypes(boolean)
	 */
	public Map<String, String> getAllowedDeliveryTypes(final boolean consideringWECCustomizing) throws CommunicationException
	{

		sapLogger.entering("getAllowedDeliveryTypes()");
		Map<String, String> allowedDeliveryTypesBackend = null;

		try
		{

			allowedDeliveryTypesBackend = ((TransactionConfigurationBackend) getBackendBusinessObject()).getAllowedDeliveryTypes();

		}
		catch (final BackendException bex)
		{
			BusinessObjectHelper.splitException(bex);
		}
		finally
		{
			sapLogger.exiting();
		}

		return allowedDeliveryTypesBackend;
	}
}
