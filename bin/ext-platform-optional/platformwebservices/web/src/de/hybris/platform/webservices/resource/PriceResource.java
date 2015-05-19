/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.resource;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.ServiceLocator;
import de.hybris.platform.webservices.dto.price.PriceDTO;
import de.hybris.platform.webservices.price.Price;
import de.hybris.platform.webservices.price.Prices;
import de.hybris.platform.webservices.price.impl.PricesImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * artificial Price resource to access pricing data within product
 */
public class PriceResource extends AbstractResource<PriceDTO>
{

	private PricingStrategy strategy;

	/**
	 * retruns filled in {@link PriceDTO} with specific for current {@link #strategy} values
	 */
	@Override
	protected PriceDTO readResource(final String resourceId) throws Exception
	{
		final ProductModel model = (ProductModel) getParentResource().getResourceValue();
		final ServiceLocator service = getParentResource().getServiceLocator();
		return strategy.calculatePrice(new PricesImpl(service, model));

	}


	public void setPricingStrategy(final PricingStrategy strategy)
	{
		this.strategy = strategy;
	}

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public Response getPrice()
	{
		final PriceDTO price = getResourceValue();
		getResponse().entity(price);
		return getResponse().build();
	}

	interface PricingStrategy
	{
		PriceDTO calculatePrice(Prices prices);
	}

	public class DefaultPricingStrategy implements PricingStrategy
	{

		@Override
		public PriceDTO calculatePrice(final Prices prices)
		{
			final Price price = prices.getDefaultPricing();
			if (price.isEmptyPrice())
			{
				return new PriceDTO();
			}
			final PriceDTO priceDto = new PriceDTO();
			final double _value = Math.round(price.getPriceValue() * 100) / 100;

			priceDto.setValue(Double.valueOf(_value));
			priceDto.setCurrency(price.getCurrency().getIsocode());
			priceDto.setSymbol(StringEscapeUtils.escapeXml(price.getCurrency().getSymbol()));
			return priceDto;
		}

	}

	public class LowestQuantityPricingStrategy implements PricingStrategy
	{

		@Override
		public PriceDTO calculatePrice(final Prices prices)
		{
			final Price price = prices.getLowestQuantityPrice();
			if (price.isEmptyPrice())
			{
				return new PriceDTO();
			}
			final PriceDTO priceDto = new PriceDTO();
			final double _value = Math.round(price.getPriceValue() * 100) / 100;

			priceDto.setValue(Double.valueOf(_value));
			priceDto.setCurrency(price.getCurrency().getIsocode());
			priceDto.setSymbol(StringEscapeUtils.escapeXml(price.getCurrency().getSymbol()));
			return priceDto;
		}

	}

	public class BestValuePricingStrategy implements PricingStrategy
	{

		@Override
		public PriceDTO calculatePrice(final Prices prices)
		{
			final Price price = prices.getBestValuePrice();
			if (price.isEmptyPrice())
			{
				return new PriceDTO();
			}
			final PriceDTO priceDto = new PriceDTO();
			final double _value = Math.round(price.getPriceValue() * 100) / 100;

			priceDto.setValue(Double.valueOf(_value));
			priceDto.setCurrency(price.getCurrency().getIsocode());
			priceDto.setSymbol(StringEscapeUtils.escapeXml(price.getCurrency().getSymbol()));
			return priceDto;
		}

	}

}
