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
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.user.UserModel} as source and
 * {@link de.hybris.platform.commercefacades.user.data.CustomerData} as target type.
 */
public class CustomerPopulator implements Populator<CustomerModel, CustomerData>
{
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	private Converter<LanguageModel, LanguageData> languageConverter;
	private CustomerNameStrategy customerNameStrategy;

	protected Converter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}

	protected Converter<LanguageModel, LanguageData> getLanguageConverter()
	{
		return languageConverter;
	}

	@Required
	public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter)
	{
		this.languageConverter = languageConverter;
	}

	protected CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	@Override
	public void populate(final CustomerModel source, final CustomerData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getSessionCurrency() != null)
		{
			target.setCurrency(getCurrencyConverter().convert(source.getSessionCurrency()));
		}
		if (source.getSessionLanguage() != null)
		{
			target.setLanguage(getLanguageConverter().convert(source.getSessionLanguage()));
		}

		final String[] names = getCustomerNameStrategy().splitName(source.getName());
		if (names != null)
		{
			target.setFirstName(names[0]);
			target.setLastName(names[1]);
		}

		final TitleModel title = source.getTitle();
		if (title != null)
		{
			target.setTitleCode(title.getCode());
		}

		target.setName(source.getName());
		setUid(source, target);
	}

	protected void setUid(final UserModel source, final CustomerData target)
	{
		target.setUid(source.getUid());
		if (source instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) source;
			if (isOriginalUidAvailable(customer))
			{
				target.setDisplayUid(customer.getOriginalUid());
			}
		}
	}

	protected boolean isOriginalUidAvailable(final CustomerModel source)
	{
		return source.getOriginalUid() != null;
	}
}
