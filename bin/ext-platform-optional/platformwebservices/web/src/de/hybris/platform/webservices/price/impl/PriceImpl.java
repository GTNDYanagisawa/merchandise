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
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.price.DiscountInformation;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.StandardDateRange;
import de.hybris.platform.webservices.ServiceLocator;
import de.hybris.platform.webservices.price.Price;
import de.hybris.platform.webservices.price.PriceLeverage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * pojo responsible for representing one price row entry for specified product which also respects
 * taxes/discount/voucher which mighth affect this object
 */
public class PriceImpl implements Price
{
	private static final Logger log = Logger.getLogger(PriceImpl.class);

	public static Price EMPTY_PRICE = new PriceImpl();

	private int amount;
	private Price oldPricing = null;
	private double finalPrice = -1;
	private Date startDate = null;
	private Date endDate = null;
	private UnitModel unit = null;
	private CurrencyModel currency = null;
	private boolean net = false;

	private List<PriceLeverage> taxes = null;
	private List<PriceLeverage> discounts = null;

	private ServiceLocator serviceLocator;

	public PriceImpl()
	{
		// DOCTODO Document reason, why this block is empty
	}

	private void initService(final ServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}


	public PriceImpl(final ServiceLocator serviceLocator, final PriceInformation info, final CurrencyModel currencyModel,
			final List<DiscountInformation> discounts, final List<TaxInformation> taxInfos)
	{
		initService(serviceLocator);
		initData(info, currencyModel, discounts, taxInfos);
	}

	@SuppressWarnings("deprecation")
	private void initData(final PriceInformation info, final CurrencyModel currencyModel,
			final List<DiscountInformation> discounts, final List<TaxInformation> taxInfos)
	{
		//detect amount
		final Number _amount = (Number) info.getQualifierValue(PriceRow.MIN_QUANTITY);
		this.amount = (_amount != null) ? _amount.intValue() : 1;

		//detect unit
		final Unit _unit = (Unit) info.getQualifierValue(PriceRow.UNIT);
		if (_unit != null)
		{
			this.unit = serviceLocator.getModelService().get(_unit);
		}

		//set currency
		this.currency = currencyModel;

		//set final price
		this.finalPrice = info.getPriceValue().getValue();

		//set discounts (and adjust final price)
		this.discounts = new ArrayList<PriceLeverage>();
		this.finalPrice = this.applyDiscounts(discounts, this.amount,
				(Currency) serviceLocator.getModelService().getSource(currencyModel));

		//set dates
		final StandardDateRange _dateRange = (StandardDateRange) info.getQualifierValue(PriceRow.DATE_RANGE);
		if (_dateRange != null)
		{
			this.startDate = _dateRange.getStart();
			this.endDate = _dateRange.getEnd();
		}

		//Collect taxes
		if (taxInfos != null && !taxInfos.isEmpty())
		{
			this.taxes = new ArrayList<PriceLeverage>();
			for (final TaxInformation taxInfo : taxInfos)
			{
				if (taxInfo.getTaxValue().isAbsolute())
				{
					log.error("Absolute taxvalues aren't supported", new UnsupportedOperationException(
							"Absolute taxvalues aren't supported"));
				}
				final PriceLeverage tax = new PriceLeverageImpl(serviceLocator, taxInfo.getTaxValue());

				this.taxes.add(tax);
			}
		}
		// XXX: IGNORES gross/net settings
	}

	@Override
	public boolean isGross()
	{
		return !this.net;
	}

	public void setGross(final boolean isGross)
	{
		this.net = !isGross;
	}

	@Override
	public boolean isNet()
	{
		return this.net;
	}

	public void setNet(final boolean isNet)
	{
		this.net = isNet;
	}

	@Override
	public int getAmount()
	{
		return this.amount;
	}

	public void setAmount(final int amount)
	{
		this.amount = amount;
	}

	@Override
	public Price getOldPricing()
	{
		return this.oldPricing;
	}

	public void setOldPricing(final Price oldPrice)
	{
		this.oldPricing = oldPrice;
	}

	@Override
	public double getPriceValue()
	{
		return this.finalPrice;
	}

	public void setPriceValue(final double value)
	{
		this.finalPrice = value;
	}

	@Override
	public UnitModel getUnit()
	{
		return this.unit;
	}

	public void setUnit(final UnitModel unit)
	{
		this.unit = unit;
	}

	@Override
	public Date getValidFromDate()
	{
		return this.startDate;
	}

	public void setValidFromDate(final Date startDate)
	{
		this.startDate = startDate;
	}

	@Override
	public Date getValidToDate()
	{
		return this.endDate;
	}

	public void setValidToDate(final Date validTo)
	{
		this.endDate = validTo;
	}

	@Override
	public List<PriceLeverage> getTaxes()
	{
		return this.taxes;
	}

	@Override
	public List<PriceLeverage> getDiscounts()
	{
		return this.discounts;
	}

	@Override
	public boolean isAvailable()
	{
		return this.finalPrice >= 0;
	}

	@Override
	public CurrencyModel getCurrency()
	{
		return this.currency;
	}

	private double applyDiscounts(final List<DiscountInformation> discounts, final int amount, final Currency curr)
	{
		//Note:
		//DiscountValues are used for for tow different tasks (why no inheritance?)
		//1) act as template; provide neccessary meta information like value, absolute/relative etc
		//2) act as concrete discount for a given price (appliedValue, appliedCurrency)

		double result = this.finalPrice;
		for (final DiscountInformation dInfo : discounts)
		{
			//get unapplied DiscountValue (template instance)
			final DiscountValue unApplied = dInfo.getDiscountValue();

			//create applied DiscountValue (concrete instance)
			try
			{
				final DiscountValue applied = unApplied.apply(amount, result, curr.getDigits().intValue(), curr.getIsoCode());
				//wrap into a more general PriceLeverage
				final PriceLeverage discount = new PriceLeverageImpl(serviceLocator, applied);
				this.discounts.add(discount);

				//adjust final price
				result -= applied.getAppliedValue();
			}
			catch (final IllegalArgumentException ilae)
			{
				log.error(ilae.getMessage());
				if (log.isDebugEnabled())
				{
					log.debug(ilae);
				}
				throw new IncompatibleDiscountException(ilae.getMessage());
			}
		}
		return result;
	}


	@Override
	public String toString()
	{
		final String cur = this.currency != null ? this.currency.getIsocode() : "";
		return super.toString() + this.finalPrice + " " + cur;
	}

	@Override
	public boolean isEmptyPrice()
	{
		return this == EMPTY_PRICE;
	}





}
