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
package de.hybris.platform.integration.cis.tax.service.impl;


import de.hybris.platform.commerceservices.externaltax.CalculateExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;
import de.hybris.platform.integration.cis.tax.service.CisTaxCalculationService;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisOrder;
import com.hybris.cis.api.tax.model.CisTaxDoc;
import com.hybris.cis.client.rest.tax.TaxClient;
import com.hybris.commons.client.RestResponse;


public class DefaultCisTaxCalculationService implements CisTaxCalculationService
{
	private static final Logger LOG = Logger.getLogger(DefaultCisTaxCalculationService.class);
	private Converter<AbstractOrderModel, CisOrder> cisOrderConverter;
	private TaxClient taxClient;
	private ConfigurationService configurationService;
	private Converter<CisTaxDocOrder, ExternalTaxDocument> externalTaxDocumentConverter;
	private OndemandHystrixCommandConfiguration hystrixCommandConfig;
	private CalculateExternalTaxesStrategy calculateExternalTaxesFallbackStrategy;
	private Converter<AddressModel, CisAddress> cisAddressConverter;
	private SessionService sessionService;
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;

	@Override
	public ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		final CisOrder cisOrder = getCisOrderConverter().convert(abstractOrder);

		return getExternalTaxDocument(abstractOrder, cisOrder);
	}

	protected ExternalTaxDocument getExternalTaxDocument(final AbstractOrderModel abstractOrder, final CisOrder cisOrder)
	{
		return getOndemandHystrixCommandFactory().newCommand(getHystrixCommandConfig(),
				new HystrixExecutable<ExternalTaxDocument>()
				{
					@Override
					public ExternalTaxDocument runEvent()
					{
						final RestResponse<CisTaxDoc> cisTaxDocRestResponse;

						final String cisClientRef = abstractOrder.getGuid();
						if (abstractOrder instanceof CartModel)
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug(String.format("Getting taxes from external tax service for cart: %s %s",
										abstractOrder.getCode(),
										ReflectionToStringBuilder.toString(cisOrder, ToStringStyle.SHORT_PREFIX_STYLE)));
							}

							cisTaxDocRestResponse = getTaxClient().quote(cisClientRef, cisOrder);
						}
						else
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug(String.format("Getting taxes from external tax service for order: %s %s",
										abstractOrder.getCode(),
										ReflectionToStringBuilder.toString(cisOrder, ToStringStyle.SHORT_PREFIX_STYLE)));
							}

							cisTaxDocRestResponse = getTaxClient().post(cisClientRef, cisOrder);
						}

						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("External Tax Service returned Tax Document for order %s %s", abstractOrder
									.getCode(), ReflectionToStringBuilder.toString(cisTaxDocRestResponse.getResult(),
									ToStringStyle.SHORT_PREFIX_STYLE)));
						}

						return getExternalTaxDocumentConverter().convert(
								new CisTaxDocOrder(cisTaxDocRestResponse.getResult(), abstractOrder));
					}

					@Override
					public ExternalTaxDocument fallbackEvent()
					{
						return getCalculateExternalTaxesFallbackStrategy().calculateExternalTaxes(abstractOrder);
					}

					@Override
					public ExternalTaxDocument defaultEvent()
					{
						return getCalculateExternalTaxesFallbackStrategy().calculateExternalTaxes(abstractOrder);
					}
				}).execute();
	}

	public TaxClient getTaxClient()
	{
		return taxClient;
	}

	public void setTaxClient(final TaxClient taxClient)
	{
		this.taxClient = taxClient;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public OndemandHystrixCommandConfiguration getHystrixCommandConfig()
	{
		return hystrixCommandConfig;
	}

	public void setHystrixCommandConfig(final OndemandHystrixCommandConfiguration hystrixCommandConfig)
	{
		this.hystrixCommandConfig = hystrixCommandConfig;
	}

	public Converter<CisTaxDocOrder, ExternalTaxDocument> getExternalTaxDocumentConverter()
	{
		return externalTaxDocumentConverter;
	}

	public void setExternalTaxDocumentConverter(final Converter<CisTaxDocOrder, ExternalTaxDocument> externalTaxDocumentConverter)
	{
		this.externalTaxDocumentConverter = externalTaxDocumentConverter;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public Converter<AddressModel, CisAddress> getCisAddressConverter()
	{
		return cisAddressConverter;
	}

	public void setCisAddressConverter(final Converter<AddressModel, CisAddress> cisAddressConverter)
	{
		this.cisAddressConverter = cisAddressConverter;
	}

	public CalculateExternalTaxesStrategy getCalculateExternalTaxesFallbackStrategy()
	{
		return calculateExternalTaxesFallbackStrategy;
	}

	public void setCalculateExternalTaxesFallbackStrategy(
			final CalculateExternalTaxesStrategy calculateExternalTaxesFallbackStrategy)
	{
		this.calculateExternalTaxesFallbackStrategy = calculateExternalTaxesFallbackStrategy;
	}

	public Converter<AbstractOrderModel, CisOrder> getCisOrderConverter()
	{
		return cisOrderConverter;
	}

	public void setCisOrderConverter(final Converter<AbstractOrderModel, CisOrder> cisOrderConverter)
	{
		this.cisOrderConverter = cisOrderConverter;
	}

	protected OndemandHystrixCommandFactory getOndemandHystrixCommandFactory()
	{
		return ondemandHystrixCommandFactory;
	}

	@Required
	public void setOndemandHystrixCommandFactory(final OndemandHystrixCommandFactory ondemandHystrixCommandFactory)
	{
		this.ondemandHystrixCommandFactory = ondemandHystrixCommandFactory;
	}
}
