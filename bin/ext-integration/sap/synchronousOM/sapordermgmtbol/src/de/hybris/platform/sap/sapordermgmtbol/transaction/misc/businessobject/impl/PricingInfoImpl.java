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

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.PricingInfo;

import java.util.Map;


/**
 * The <code>PricingInfo</code> handles the pricing infos, which are e.g. necessary to handle the Basket within the IPC.
 * <p>
 * To read the pricing info from the backend you can use the <code>shopData</code> object.<br>
 * <i>For further details see the interface <code>PricingInfoData</code></i>
 * 
 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.PricingInfo
 */
public class PricingInfoImpl implements PricingInfo
{

	private String procedureName;
	private String fGProcedureName;
	private String documentCurrencyUnit;
	private String localCurrencyUnit;
	private String salesOrganisation;
	private String salesOrganisationCrm;
	private String distributionChannel;
	private String distributionChannelOriginal;
	private Map<String, String> headerAttributes;
	private Map<String, Map<String, Map<String, String>>> itemAttributes;

	@Override
	public String getProcedureName()
	{
		return procedureName;
	}

	@Override
	public void setProcedureName(final String procedureName)
	{
		this.procedureName = procedureName;
	}

	@Override
	public String getDocumentCurrencyUnit()
	{
		return documentCurrencyUnit;
	}

	@Override
	public void setDocumentCurrencyUnit(final String documentCurrencyUnit)
	{
		this.documentCurrencyUnit = documentCurrencyUnit;
	}

	@Override
	public String getLocalCurrencyUnit()
	{
		return localCurrencyUnit;
	}

	@Override
	public void setLocalCurrencyUnit(final String localCurrencyUnit)
	{
		this.localCurrencyUnit = localCurrencyUnit;
	}

	@Override
	public String getSalesOrganisation()
	{
		return salesOrganisation;
	}

	@Override
	public void setSalesOrganisation(final String salesOrganisation)
	{
		this.salesOrganisation = salesOrganisation;
	}

	@Override
	public String getSalesOrganisationCrm()
	{
		return salesOrganisationCrm;
	}

	@Override
	public void setSalesOrganisationCrm(final String salesOrganisationCrm)
	{
		this.salesOrganisationCrm = salesOrganisationCrm;
	}

	@Override
	public String getDistributionChannel()
	{
		return distributionChannel;
	}

	@Override
	public void setDistributionChannel(final String distributionChannel)
	{
		this.distributionChannel = distributionChannel;
	}

	@Override
	public String getDistributionChannelOriginal()
	{
		return distributionChannelOriginal;
	}

	@Override
	public void setDistributionChannelOriginal(final String distributionChannelOriginal)
	{
		this.distributionChannelOriginal = distributionChannelOriginal;
	}

	@Override
	public Map<String, String> getHeaderAttributes()
	{
		return headerAttributes;
	}

	@Override
	public void setHeaderAttributes(final Map<String, String> headerAttributes)
	{
		this.headerAttributes = headerAttributes;
	}

	@Override
	public void setItemAttributes(final Map<String, Map<String, Map<String, String>>> itemAttributes)
	{
		this.itemAttributes = itemAttributes;
	}

	@Override
	public Map<String, Map<String, Map<String, String>>> getItemAttributes()
	{
		return itemAttributes;
	}

	@Override
	public void setFGProcedureName(final String fGProcedureName)
	{
		this.fGProcedureName = fGProcedureName;
	}

	@Override
	public String getFGProcedureName()
	{
		return fGProcedureName;
	}
}
