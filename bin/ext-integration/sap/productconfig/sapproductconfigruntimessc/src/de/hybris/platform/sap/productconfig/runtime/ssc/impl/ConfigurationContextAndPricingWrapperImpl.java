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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;


import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationContextAndPricingWrapper;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDocument;
import com.sap.custdev.projects.fbs.slc.cfg.client.IItemInfo;
import com.sap.custdev.projects.fbs.slc.cfg.client.ItemInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.DocumentData;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingIntegration;
import com.sap.custdev.projects.fbs.slc.helper.ConfigSessionManager;
import com.sap.custdev.projects.fbs.slc.pricing.ip.api.InteractivePricingMgr;
import com.sap.custdev.projects.fbs.slc.pricing.slc.api.ISLCItem;
import com.sap.spe.conversion.ICurrencyValue;
import com.sap.spe.pricing.transactiondata.IPricingItem;
import com.sap.sxe.sys.SAPDate;
import com.sap.sxe.sys.SAPTimestamp;


public class ConfigurationContextAndPricingWrapperImpl implements ConfigurationContextAndPricingWrapper
{
	private final static Logger LOG = Logger.getLogger(ConfigurationContextAndPricingWrapperImpl.class);

	private ConfigModelFactory configModelFactory;

	@Autowired
	private CommonI18NService i18NService;

	@Autowired(required = false)
	private PricingConfigurationParameter pricingConfigurationParameter;

	@Autowired(required = false)
	private ConfigurationParameterB2B configurationParameterB2B;

	@Autowired(required = false)
	private ProductService productService;

	@Override
	public void preparePricingContext(final IConfigSession session, final String configId, final KBKey kbKey)
			throws InteractivePricingException
	{
		if (pricingConfigurationParameter != null && pricingConfigurationParameter.isPricingSupported())
		{
			final ConfigSessionManager configSessionManager = session.getConfigSessionManager();

			configSessionManager.setPricingContext(configId, getDocumentPricingContext(), getItemPricingContext(kbKey),
					kbKey.getKbLogsys());
			configSessionManager.setInteractivePricingEnabled(configId, true);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Pricing is active for config [CONFIG_ID='" + configId + "']");
			}
		}
		else
		{
			LOG.debug("Pricing is disabled/not supported");
		}
	}

	protected IDocument getDocumentPricingContext()
	{

		final IDocument documentPricingContext = new DocumentData();

		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_VKORG,
				pricingConfigurationParameter.getSalesOrganization());
		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_VTWEG,
				pricingConfigurationParameter.getDistributionChannelForConditions());
		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_SPART,
				pricingConfigurationParameter.getDivisionForConditions());


		if (configurationParameterB2B != null && configurationParameterB2B.isSupported())
		{
			addAttributeToDocumentPricingContext(documentPricingContext,
					SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR, configurationParameterB2B.getCustomerNumber());
			addAttributeToDocumentPricingContext(documentPricingContext,
					SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_LAND1, configurationParameterB2B.getCountrySapCode());
			addAttributeToDocumentPricingContext(documentPricingContext,
					SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KONDA, configurationParameterB2B.getCustomerPriceGroup());
		}

		documentPricingContext.setPricingProcedure(pricingConfigurationParameter.getPricingProcedure());

		final CurrencyModel currencyModel = i18NService.getCurrentCurrency();
		final String currency = pricingConfigurationParameter.retrieveCurrencySapCode(currencyModel);

		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KONWA,
				currency);
		documentPricingContext.setDocumentCurrencyUnit(currency);
		documentPricingContext.setLocalCurrencyUnit(currency);

		documentPricingContext.setApplication(SapproductconfigruntimesscConstants.APPLICATION_V);
		documentPricingContext.setUsage(SapproductconfigruntimesscConstants.USAGE_A);

		if (LOG.isTraceEnabled())
		{
			documentPricingContext.setPerformPricingTrace(true);
		}

		return documentPricingContext;
	}

	protected IItemInfo getItemPricingContext(final KBKey kbKey)
	{
		final IItemInfo itemPricingContext = new ItemInfoData();

		itemPricingContext.addAttribute(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PMATN, kbKey.getProductCode());
		itemPricingContext.setProductId(kbKey.getProductCode());
		itemPricingContext.addAttribute(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PRSFD, "X");

		final SAPTimestamp timeStamp = new SAPTimestamp(new SAPDate(new Date()));
		itemPricingContext
				.addTimestamp(SapproductconfigruntimesscConstants.DET_DEFAULT_TIMESTAMP, timeStamp.formatyyyyMMddHHmmss());

		itemPricingContext.setQuantity(BigDecimal.ONE);
		final ProductModel product = productService.getProductForCode(kbKey.getProductCode());
		final UnitModel unitModel = product.getUnit();
		final String sapUOM = pricingConfigurationParameter.retrieveUnitSapCode(unitModel);
		itemPricingContext.setQuantityUnit(sapUOM);

		itemPricingContext.setPricingRelevant(true);
		return itemPricingContext;
	}


	@Override
	public void processPrice(final IConfigSession session, final String configId, final ConfigModel configModel)
			throws InteractivePricingException
	{
		if (pricingConfigurationParameter != null && pricingConfigurationParameter.isPricingSupported())
		{
			final ConfigSessionManager configSessionManager = session.getConfigSessionManager();

			final InteractivePricingIntegration interactivePricing = configSessionManager.getInteractivePricingIntegration(configId);
			final InteractivePricingMgr pricingManager = interactivePricing.getInteractivePricingManager();
			final ISLCItem rootItem = pricingManager.getRootItem();
			final IPricingItem pricingItem = rootItem.getPricingItem();

			pricingItem.pricing(true);

			final ICurrencyValue netValue = pricingItem.getNetValueWithoutFreight();

			final PriceModel currentTotalPriceModel = configModelFactory.createInstanceOfPriceModel();
			currentTotalPriceModel.setPriceValue(netValue.getValue());
			currentTotalPriceModel.setCurrency(netValue.getUnitName());
			configModel.setCurrentTotalPrice(currentTotalPriceModel);

			final Map<String, ICurrencyValue> condFuncValuesMap = pricingItem.getAccumulatedValuesForConditionsWithPurpose();
			final ICurrencyValue basePrice = condFuncValuesMap.get(pricingConfigurationParameter.getTargetForBasePrice());
			final ICurrencyValue optionPrice = condFuncValuesMap.get(pricingConfigurationParameter.getTargetForSelectedOptions());

			final PriceModel basePriceModel = configModelFactory.createInstanceOfPriceModel();
			basePriceModel.setPriceValue(basePrice.getValue());
			basePriceModel.setCurrency(basePrice.getUnitName());
			configModel.setBasePrice(basePriceModel);

			final PriceModel selectedOptionsPriceModel = configModelFactory.createInstanceOfPriceModel();
			selectedOptionsPriceModel.setPriceValue(optionPrice.getValue());
			selectedOptionsPriceModel.setCurrency(optionPrice.getUnitName());
			configModel.setSelectedOptionsPrice(selectedOptionsPriceModel);
		}
	}


	@Override
	public Hashtable<String, String> retrieveConfigurationContext(final KBKey kbKey)
	{
		final Hashtable<String, String> configContext = new Hashtable<String, String>();

		if (pricingConfigurationParameter != null)
		{
			addCustomerNumberToContext(configContext);
			addCountrySapCodeToContext(configContext);
			addSalesOrganisationToContext(configContext);
			addDistributionChannelToContext(configContext);
			addDivisionsForConditionsToContext(configContext);
		}

		final String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_ERDAT, date);
		configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_KWMENG, "1");
		configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_MATNR, kbKey.getProductCode());

		return configContext;
	}

	private void addDivisionsForConditionsToContext(final Map<String, String> configContext)
	{
		final String division = pricingConfigurationParameter.getDivisionForConditions();
		if (division != null && !division.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_SPART, division);
		}
	}

	private void addDistributionChannelToContext(final Map<String, String> configContext)
	{
		final String distributionChannel = pricingConfigurationParameter.getDistributionChannelForConditions();
		if (distributionChannel != null && !distributionChannel.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_VTWEG, distributionChannel);
		}
	}

	private void addSalesOrganisationToContext(final Map<String, String> configContext)
	{
		final String salesOrganization = pricingConfigurationParameter.getSalesOrganization();
		if (salesOrganization != null && !salesOrganization.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_VKORG, salesOrganization);
		}
	}

	private void addCountrySapCodeToContext(final Map<String, String> configContext)
	{
		String country = null;
		if (configurationParameterB2B != null && configurationParameterB2B.isSupported())
		{
			country = configurationParameterB2B.getCountrySapCode();
		}
		if (country != null && !country.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_AG_LAND1, country);
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_RG_LAND1, country);
		}
	}

	private void addCustomerNumberToContext(final Map<String, String> configContext)
	{
		String customerNumber = null;
		if (configurationParameterB2B != null && configurationParameterB2B.isSupported())
		{
			customerNumber = configurationParameterB2B.getCustomerNumber();
		}
		if (customerNumber != null && !customerNumber.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_KUNNR, customerNumber);
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_AG_KUNNR, customerNumber);
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_RG_KUNNR, customerNumber);
		}
	}

	protected void addAttributeToDocumentPricingContext(final IDocument documentPricingContext, final String attributeName,
			final String attributeValue)
	{
		if (attributeValue != null && !attributeValue.isEmpty())
		{
			documentPricingContext.addAttribute(attributeName, attributeValue);
		}
	}

	public void setI18NService(final CommonI18NService i18nService)
	{
		i18NService = i18nService;
	}

	public void setPricingConfigurationParameter(final PricingConfigurationParameter pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}

	public void setConfigurationParameterB2B(final ConfigurationParameterB2B configurationParameterB2B)
	{
		this.configurationParameterB2B = configurationParameterB2B;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}

	protected PricingConfigurationParameter getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	protected ConfigurationParameterB2B getConfigurationParameterB2B()
	{
		return configurationParameterB2B;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return configModelFactory;
	}
}
