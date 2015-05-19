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
package de.hybris.platform.webservices.price.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.jalo.order.price.DiscountInformation;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.ProductPriceInformations;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.webservices.ServiceLocator;
import de.hybris.platform.webservices.price.Price;
import de.hybris.platform.webservices.price.Prices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * pojo covering all prices information from pricefactory system for a specified product provided by product model
 */
public class PricesImpl implements Prices
{
	private static final Logger log = Logger.getLogger(PricesImpl.class);

	private List<Price> pricingList = null;
	private Price defaultPricing = PriceImpl.EMPTY_PRICE;
	private Set<UnitModel> orderableUnits = null;

	private Price bestValue = PriceImpl.EMPTY_PRICE;
	private Price minimumAmount = PriceImpl.EMPTY_PRICE;

	private ServiceLocator serviceLocator;


	public PricesImpl(final ServiceLocator serviceLocator, final ProductModel product)
	{

		initService(serviceLocator);
		initData(product);
	}

	private void initService(final ServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}


	private void initData(final ProductModel product)
	{
		initJaloProduct((Product) serviceLocator.getModelService().getSource(product));
	}

	private void initJaloProduct(final Product product)
	{
		final CurrencyModel cur = serviceLocator.getI18nService().getCurrentCurrency();

		//net or gross (globally)
		//XXX default net/gross hardcoded as always net, access to cmservice needed
		final boolean _isNet = true;

		//get price informations (price rows)
		final Date forDate = Calendar.getInstance(serviceLocator.getI18nService().getCurrentTimeZone(),
				serviceLocator.getI18nService().getCurrentLocale()).getTime();
		ProductPriceInformations pInfos = null;
		try
		{
			pInfos = product.getAllPriceInformations(forDate, _isNet);
		}
		catch (final Exception e)
		{
			log.error(e);
		}

		this.defaultPricing = new PriceImpl();
		//are pricerows available?
		final Collection<PriceInformation> prices = pInfos.getPrices();

		final List<TaxInformation> taxes = new ArrayList(pInfos.getTaxes());

		final List<DiscountInformation> discounts = new ArrayList(pInfos.getDiscounts());

		this.pricingList = Collections.EMPTY_LIST;
		this.orderableUnits = new HashSet<UnitModel>();
		if (prices != null && !prices.isEmpty())
		{
			//... create PricingList
			this.pricingList = new ArrayList<Price>();
			//... for each price row ...
			for (final PriceInformation pinfo : prices)
			{
				//...add a Price to PricingList
				final Price price = new PriceImpl(serviceLocator, pinfo, cur, discounts, taxes);

				this.pricingList.add(price);

				if (price.getUnit() != null)
				{
					this.orderableUnits.add(price.getUnit());
				}
				//price for minimum amount
				if (this.minimumAmount.getAmount() >= price.getAmount())
				{
					this.minimumAmount = price;
				}
				//price for best value
				if (!this.bestValue.isAvailable() || price.getPriceValue() < this.bestValue.getPriceValue())
				{
					this.bestValue = price;
				}
			}
		}

		this.defaultPricing = this.bestValue;
		if (log.isDebugEnabled())
		{
			log.debug("Default Pricing for product " + product.getCode() + ": " + this.getDefaultPricing().toString());
		}

	}

	@Override
	public List<Price> getPricingList()
	{
		return this.pricingList;
	}

	public void setPricingList(final List<Price> pricingList)
	{
		this.pricingList = pricingList;
	}

	@Override
	public Price getDefaultPricing()
	{
		return this.defaultPricing;
	}

	public void setDefaultPricing(final Price defaultPricing)
	{
		this.defaultPricing = defaultPricing;
	}

	@Override
	public Set<UnitModel> getOrderableUnits()
	{
		return this.orderableUnits;
	}

	@Override
	public boolean isAvailable()
	{
		return !this.pricingList.isEmpty();
	}

	@Override
	public Price getBestValuePrice()
	{
		return this.bestValue;
	}

	@Override
	public Price getLowestQuantityPrice()
	{
		return this.minimumAmount;
	}

	@Override
	public String toString()
	{
		String result = super.toString();
		if (this.pricingList != null)
		{
			for (final Price price : this.pricingList)
			{
				result = result.concat(price.toString());
			}
		}
		return result;
	}


}
