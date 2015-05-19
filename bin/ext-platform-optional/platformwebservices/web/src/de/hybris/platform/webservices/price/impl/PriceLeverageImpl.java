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
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.order.price.Discount;
import de.hybris.platform.jalo.order.price.Tax;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.SearchTools;
import de.hybris.platform.util.TaxValue;
import de.hybris.platform.webservices.ServiceLocator;
import de.hybris.platform.webservices.price.PriceLeverage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Provides a general {@link PriceLeverage} implementation for either {@link DiscountValue} or {@link TaxValue}.
 */
public class PriceLeverageImpl extends AbstractPriceLeverage
{
	private ServiceLocator serviceLocator;

	public PriceLeverageImpl(final ServiceLocator serviceLocator, final DiscountValue discountValue)
	{
		super();
		initService(serviceLocator);
		initDiscount(discountValue);

	}

	public PriceLeverageImpl(final ServiceLocator serviceLocator, final TaxValue taxValue)
	{
		super();
		initService(serviceLocator);
		initTax(taxValue);
	}

	private void initTax(final TaxValue taxValue)
	{
		final String taxCode = taxValue.getCode() != null ? taxValue.getCode() : "";
		if (taxCode == null)
		{
			throw new IllegalArgumentException("No Tax specified");
		}
		//just take the unlocalized first		
		String taxName = taxValue.getCode();

		// now get the Tax object (if any) to get a nice localized name etc.
		final Collection<TaxModel> taxes = this.getTaxes(taxCode);
		if (taxes.size() > 1)
		{
			throw new IllegalStateException("Multiple Taxes found for code " + taxCode);
		}
		if (taxes.size() == 1)
		{
			//final Tax tax = taxes.iterator().next();
			final TaxModel tax = taxes.iterator().next();

			taxName = tax.getName() != null ? tax.getName() : tax.getCode();
		}

		//and the currency
		final String _currency = taxValue.getCurrencyIsoCode();
		final CurrencyModel appliedCurrency = _currency != null ? getCurrencyModel(taxValue.getCurrencyIsoCode()) : null;

		super.setId(taxCode);
		super.setValue(taxValue.getValue());
		super.setAbsolute(taxValue.isAbsolute());
		super.setName(taxName);
		super.setAppliedValue(taxValue.getAppliedValue());
		super.setAppliedCurrency(appliedCurrency);
		super.setType(PriceLeverage.TYPE_TAX);
	}

	private void initDiscount(final DiscountValue discountValue)
	{
		final String discountCode = discountValue.getCode();

		if (discountCode == null)
		{
			throw new IllegalArgumentException("No Discount specified");
		}

		final Collection<DiscountModel> discounts = this.getDiscounts(discountCode);

		if (discounts.isEmpty())
		{
			throw new IllegalArgumentException("No Discounts found for code " + discountCode);
		}

		if (discounts.size() > 1)
		{
			throw new IllegalArgumentException("Multiple Discounts found for code " + discountCode);
		}

		final DiscountModel discount = discounts.iterator().next();

		final CurrencyModel appliedCurrency = getCurrencyModel(discountValue.getCurrencyIsoCode());
		final String discountName = discount.getName() != null ? discount.getName() : discount.getCode();

		super.setId(discountCode);
		super.setValue(discountValue.getValue());
		super.setAbsolute(discountValue.isAbsolute());
		super.setName(discountName);
		super.setAppliedValue(discountValue.getAppliedValue());
		super.setAppliedCurrency(appliedCurrency);
		super.setType(PriceLeverage.TYPE_DISCOUNT);

	}



	private void initService(final ServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}


	private CurrencyModel getCurrencyModel(final String isocode)
	{
		final CurrencyModel result = /* ((I18NService) Registry.getApplicationContext().getBean("i18nService")) */
		serviceLocator.getI18nService().getCurrency(isocode);
		return result;
	}

	/**
	 * gets discounts by searchString
	 */
	private List<DiscountModel> getDiscounts(final String searchString)
	{		
		final String discountTypeCode = serviceLocator.getTypeService().getComposedType(DiscountModel.class).getCode();

		final de.hybris.platform.servicelayer.search.SearchResult<DiscountModel> result = serviceLocator.getFlexibleSearchService()
				.search(
						"SELECT {" + Item.PK + "} FROM {" + discountTypeCode + "} " + "WHERE {" + Discount.CODE + "} "
								+ (SearchTools.isLIKEPattern(searchString) ? " LIKE " : " = ") + " ?" + Discount.CODE + " "
								+ "ORDER BY {" + Discount.CODE + "} ASC, {" + Item.CREATION_TIME + "} DESC",
						Collections.singletonMap(Discount.CODE, searchString));
		return result.getResult();
	}

	/**
	 * 
	 * gets the taxes models by searchString
	 */
	private List<TaxModel> getTaxes(final String searchString)
	{
		
		final String taxTypeCode = serviceLocator.getTypeService().getComposedType(TaxModel.class).getCode();

		final de.hybris.platform.servicelayer.search.SearchResult<TaxModel> result = serviceLocator.getFlexibleSearchService()
				.search(
						"SELECT {" + Item.PK + "} FROM {" + taxTypeCode + "} WHERE {" + Tax.CODE + "} "
								+ (SearchTools.isLIKEPattern(searchString) ? " LIKE " : " = ") + " ?" + Tax.CODE,
						Collections.singletonMap(Tax.CODE, searchString));

		return result.getResult();
	}


}
