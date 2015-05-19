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
package de.hybris.platform.b2bacceleratorfacades.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2bacceleratorfacades.company.B2BCommerceCostCenterFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;


public class DefaultB2BCommerceCostCenterFacade extends DefaultCompanyB2BCommerceFacade implements B2BCommerceCostCenterFacade
{
	@Override
	public SearchPageData<B2BCostCenterData> getPagedCostCenters(final PageableData pageableData)
	{
		final SearchPageData<B2BCostCenterModel> costCenters = getB2BCommerceCostCenterService().getPagedCostCenters(pageableData);
		final SearchPageData<B2BCostCenterData> costCentersPageData = convertPageData(costCenters, getB2BCostCenterConverter());

		return costCentersPageData;
	}

	@Override
	public void updateCostCenterDetails(final B2BCostCenterData b2BCostCenterData) throws DuplicateUidException
	{
		final B2BCostCenterModel b2BCostCenterModel = getB2BCommerceCostCenterService().getCostCenterForCode(
				b2BCostCenterData.getOriginalCode());
		if (b2BCostCenterModel != null)
		{
			getB2BCostCenterReversePopulator().populate(b2BCostCenterData, b2BCostCenterModel);
			getCompanyB2BCommerceService().saveModel(b2BCostCenterModel);
		}
	}

	@Override
	public B2BCostCenterData getCostCenterDataForCode(final String costCenterCode)
	{
		validateParameterNotNullStandardMessage("costCenterCode", costCenterCode);
		final B2BCostCenterModel b2bCostCenterModel = getB2BCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		return getB2BCostCenterConverter().convert(b2bCostCenterModel);
	}

	@Override
	public void addCostCenter(final B2BCostCenterData b2BCostCenterData) throws DuplicateUidException
	{
		final B2BCostCenterModel b2BCostCenterModel = this.getModelService().create(B2BCostCenterModel.class);
		getB2BCostCenterReversePopulator().populate(b2BCostCenterData, b2BCostCenterModel);
		getCompanyB2BCommerceService().saveModel(b2BCostCenterModel);

	}

	@Override
	public SearchPageData<B2BBudgetData> getPagedBudgetsForCostCenters(final PageableData pageableData, final String costCenterCode)
	{
		final SearchPageData<B2BBudgetData> searchPageData = this.getPagedBudgets(pageableData);
		final B2BCostCenterData costCenter = this.getCostCenterDataForCode(costCenterCode);
		for (final B2BBudgetData budgetData : searchPageData.getResults())
		{
			budgetData.setSelected(CollectionUtils.find(costCenter.getB2bBudgetData(), new BeanPropertyValueEqualsPredicate(
					B2BBudgetModel.CODE, budgetData.getCode())) != null);
		}

		return searchPageData;
	}


	@Override
	public void enableDisableCostCenter(final String costCenterCode, final boolean active) throws DuplicateUidException
	{
		final B2BCostCenterModel b2BCostCenterModel = getB2BCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		if (b2BCostCenterModel != null && b2BCostCenterModel.getUnit() != null
				&& Boolean.TRUE.equals(b2BCostCenterModel.getUnit().getActive()))
		{
			b2BCostCenterModel.setActive(Boolean.valueOf(active));
			getCompanyB2BCommerceService().saveModel(b2BCostCenterModel);
		}
	}


	@Override
	public B2BSelectionData selectBudgetForCostCenter(final String costCenterCode, final String budgetCode)
			throws DuplicateUidException
	{
		final B2BCostCenterModel b2BCostCenterModel = getB2BCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		final Set<B2BBudgetModel> budgetModelSet = new HashSet<B2BBudgetModel>(b2BCostCenterModel.getBudgets());
		final B2BBudgetModel b2BBudgetModel = getB2BCommerceBudgetService().getBudgetModelForCode(budgetCode);
		budgetModelSet.add(b2BBudgetModel);
		b2BCostCenterModel.setBudgets(budgetModelSet);
		getCompanyB2BCommerceService().saveModel(b2BCostCenterModel);

		return createB2BSelectionData(b2BBudgetModel.getCode(), true, b2BBudgetModel.getActive().booleanValue());

	}

	@Override
	public B2BSelectionData deSelectBudgetForCostCenter(final String costCenterCode, final String budgetCode)
			throws DuplicateUidException
	{
		final B2BCostCenterModel b2BCostCenterModel = getB2BCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		final Set<B2BBudgetModel> budgetModelSet = new HashSet<B2BBudgetModel>(b2BCostCenterModel.getBudgets());
		final B2BBudgetModel b2BBudgetModel = getB2BCommerceBudgetService().getBudgetModelForCode(budgetCode);
		if (b2BBudgetModel != null)
		{
			budgetModelSet.remove(b2BBudgetModel);
		}

		b2BCostCenterModel.setBudgets(budgetModelSet);
		getCompanyB2BCommerceService().saveModel(b2BCostCenterModel);

		if (b2BBudgetModel != null)
		{
			return createB2BSelectionData(b2BBudgetModel.getCode(), false, b2BBudgetModel.getActive().booleanValue());
		}

		throw new IllegalStateException("The b2BBudgetModel must be found in the system");
	}
}
