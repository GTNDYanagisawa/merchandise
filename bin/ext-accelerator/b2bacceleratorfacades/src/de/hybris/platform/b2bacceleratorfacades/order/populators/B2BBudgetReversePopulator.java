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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.StandardDateRange;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link B2BBudgetData} with {@link B2BBudgetModel}.
 */
public class B2BBudgetReversePopulator implements Populator<B2BBudgetData, B2BBudgetModel>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private CommonI18NService commonI18NService;

	@Override
	public void populate(final B2BBudgetData source, final B2BBudgetModel target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setBudget(source.getBudget());
		final B2BUnitModel b2BUnitModel = getB2BUnitService().getUnitForUid(source.getUnit().getUid());
		if (b2BUnitModel != null)
		{
			target.setUnit(b2BUnitModel);
		}
		final CurrencyModel currencyModel = getCommonI18NService().getCurrency(source.getCurrency().getIsocode());
		target.setCurrency(currencyModel);
		target.setDateRange(new StandardDateRange(source.getStartDate(), source.getEndDate()));
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		b2BUnitService = b2bUnitService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
