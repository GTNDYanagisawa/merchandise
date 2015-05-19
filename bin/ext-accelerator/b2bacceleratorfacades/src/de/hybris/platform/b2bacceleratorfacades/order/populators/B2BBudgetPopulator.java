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
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates implementation for {@link B2BBudgetModel} as source and {@link B2BBudgetData} as target type.
 */
public class B2BBudgetPopulator implements Populator<B2BBudgetModel, B2BBudgetData>
{
	private Converter<CurrencyModel, CurrencyData> currencyConverter;

	@Override
	public void populate(final B2BBudgetModel source, final B2BBudgetData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setActive(source.getActive().booleanValue());
		target.setStartDate(source.getDateRange().getStart());
		target.setEndDate(source.getDateRange().getEnd());
		target.setBudget(source.getBudget());
		if (source.getCurrency() != null)
		{
			target.setCurrency(this.getCurrencyConverter().convert(source.getCurrency()));
		}
		final B2BUnitData b2BUnitData = new B2BUnitData();
		b2BUnitData.setUid(source.getUnit().getUid());
		b2BUnitData.setName(source.getUnit().getLocName());
		b2BUnitData.setActive(Boolean.TRUE.equals(source.getUnit().getActive()));

		target.setUnit(b2BUnitData);
		populateCostCenterListForBudget(source, target);
	}


	protected void populateCostCenterListForBudget(final B2BBudgetModel source, final B2BBudgetData target)
	{
		final Set<B2BCostCenterModel> costCenterModelSet = source.getCostCenters();
		final List<String> costCenterList = new ArrayList<String>(costCenterModelSet.size());
		for (final B2BCostCenterModel costCenterModel : costCenterModelSet)
		{
			costCenterList.add(costCenterModel.getName());
		}
		target.setCostCenterNames(costCenterList);
	}

	public Converter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}
}
