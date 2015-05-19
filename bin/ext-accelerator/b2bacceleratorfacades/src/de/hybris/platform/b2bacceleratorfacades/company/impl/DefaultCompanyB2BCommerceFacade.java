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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bacceleratorfacades.company.CompanyB2BCommerceFacade;
import de.hybris.platform.b2bacceleratorfacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bacceleratorfacades.company.data.UserData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUserGroupData;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BBudgetReversePopulator;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BCostCenterReversePopulator;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BCustomerReversePopulator;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BPermissionsReversePopulator;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BUnitReversePopulator;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceBudgetService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceCostCenterService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommercePermissionService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUserService;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.b2bacceleratorservices.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2bacceleratorservices.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * A facade for organization management.
 */
public class DefaultCompanyB2BCommerceFacade implements CompanyB2BCommerceFacade
{
	private CompanyB2BCommerceService companyB2BCommerceService;
	private UserService userService;
	private Converter<B2BCustomerModel, UserData> b2BUserConverter;
	private Converter<B2BUnitModel, B2BUnitNodeData> unitTreeConverter;
	private Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter;
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;
	private Converter<B2BBudgetModel, B2BBudgetData> b2BBudgetConverter;
	private Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter;
	private Converter<B2BUserGroupModel, B2BUserGroupData> b2BUserGroupConverter;
	private Populator<AddressData, AddressModel> addressReversePopulator;
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;
	private B2BBudgetReversePopulator b2BBudgetReversePopulator;
	private B2BCostCenterReversePopulator b2BCostCenterReversePopulator;
	private Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2BPermissionTypeDataConverter;
	private B2BPermissionsReversePopulator b2BPermissionReversePopulator;
	private EnumerationService enumerationService;
	private B2BCommerceUserService b2BCommerceUserService;
	private B2BCommerceUnitService b2BCommerceUnitService;
	private B2BCommercePermissionService b2BCommercePermissionService;
	private B2BCommerceBudgetService b2BCommerceBudgetService;
	private B2BCommerceCostCenterService b2BCommerceCostCenterService;
	private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;
	private B2BCustomerReversePopulator b2BCustomerReversePopulator;
	private B2BUnitReversePopulator b2BUnitReversePopulator;
	private BaseStoreService baseStoreService;

	private ModelService modelService;

	@Override
	public List<B2BUnitNodeData> getBranchNodes()
	{
		return Converters.convertAll(getB2BCommerceUnitService().getBranch(), getUnitTreeConverter());
	}

	@Override
	public B2BUnitData getParentUnit()
	{
		final B2BUnitModel parentUnit = getB2BCommerceUnitService().getParentUnit();
		if (parentUnit != null)
		{
			return this.getB2BUnitConverter().convert(parentUnit);
		}
		else
		{
			return null;
		}
	}

	@Override
	public B2BUnitData getUnitForUid(final String uid)
	{
		final B2BUnitModel unitForUid = getCompanyB2BCommerceService().getUnitForUid(uid);
		return (unitForUid != null ? this.getB2BUnitConverter().convert(unitForUid) : null);
	}

	@Override
	public List<String> getAllCurrencies()
	{
		final List<CurrencyModel> currencies = (List<CurrencyModel>) getCompanyB2BCommerceService().getAllCurrencies();
		final List<String> currencyIsoCodeList = new ArrayList<String>(currencies.size());
		for (final CurrencyModel currencyModel : currencies)
		{
			currencyIsoCodeList.add(currencyModel.getIsocode());
		}
		return currencyIsoCodeList;
	}

	@Override
	public CustomerData getCustomerDataForUid(final String uid)
	{
		Assert.hasText(uid, "The field [uid] cannot be empty");
		final B2BCustomerModel customerModel = getCompanyB2BCommerceService().getCustomerForUid(uid);
		validateParameterNotNull(customerModel, String.format("Customer for uid %s not found", uid));
		return getB2BCustomerConverter().convert(customerModel);
	}

	@Override
	public Map<String, String> getBusinessProcesses()
	{
		return this.getCompanyB2BCommerceService().getBusinessProcesses();
	}

	@Override
	public List<String> getUserGroups()
	{
		return getB2BUserGroupsLookUpStrategy().getUserGroups();
	}


	@Override
	public SearchPageData<B2BBudgetData> getPagedBudgets(final PageableData pageableData)
	{
		final SearchPageData<B2BBudgetModel> b2BBudgets = getB2BCommerceBudgetService().findPagedBudgets(pageableData);
		return convertPageData(b2BBudgets, getB2BBudgetConverter());
	}

	@Override
	public B2BBudgetData getBudgetDataForCode(final String budgetCode)
	{
		B2BBudgetData b2BBudgetData = null;
		final B2BBudgetModel budgetModel = getB2BCommerceBudgetService().getBudgetModelForCode(budgetCode);
		if (budgetModel != null)
		{
			b2BBudgetData = getB2BBudgetConverter().convert(budgetModel);
			if (CollectionUtils.isNotEmpty(budgetModel.getCostCenters()))
			{
				b2BBudgetData.setCostCenters(Converters.convertAll(budgetModel.getCostCenters(), getB2BCostCenterConverter()));
			}
		}
		return b2BBudgetData;
	}


	@Override
	public String getCurrentStore()
	{
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		return baseStore.getName();
	}

	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	protected B2BSelectionData createB2BSelectionData(final String code, final boolean selected, final boolean active)
	{
		final B2BSelectionData b2BSelectionData = new B2BSelectionData();
		b2BSelectionData.setId(code);
		b2BSelectionData.setNormalizedCode(code == null ? null : code.replaceAll("\\W", "_"));
		b2BSelectionData.setSelected(selected);
		b2BSelectionData.setActive(active);
		return b2BSelectionData;
	}

	protected B2BSelectionData populateRolesForCustomer(final B2BCustomerModel customerModel,
			final B2BSelectionData b2BSelectionData)
	{
		final List<String> roles = new ArrayList<String>();
		final Set<PrincipalGroupModel> roleModels = new HashSet<PrincipalGroupModel>(customerModel.getGroups());
		CollectionUtils.filter(roleModels, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUnitModel.class)));
		CollectionUtils
				.filter(roleModels, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUserGroupModel.class)));

		for (final PrincipalGroupModel role : roleModels)
		{
			roles.add(role.getUid());
		}
		b2BSelectionData.setRoles(roles);

		return b2BSelectionData;
	}


	protected <T extends CompanyB2BCommerceService> T getCompanyB2BCommerceService()
	{
		return (T) companyB2BCommerceService;
	}

	@Required
	public void setCompanyB2BCommerceService(final CompanyB2BCommerceService companyB2BCommerceService)
	{
		this.companyB2BCommerceService = companyB2BCommerceService;
	}

	protected Converter<B2BCustomerModel, UserData> getB2BUserConverter()
	{
		return b2BUserConverter;
	}

	@Required
	public void setB2BUserConverter(final Converter<B2BCustomerModel, UserData> b2BUserConverter)
	{
		this.b2BUserConverter = b2BUserConverter;
	}

	protected Converter<B2BUnitModel, B2BUnitNodeData> getUnitTreeConverter()
	{
		return unitTreeConverter;
	}

	@Required
	public void setUnitTreeConverter(final Converter<B2BUnitModel, B2BUnitNodeData> unitTreeConverter)
	{
		this.unitTreeConverter = unitTreeConverter;
	}

	protected Converter<B2BUnitModel, B2BUnitData> getB2BUnitConverter()
	{
		return b2BUnitConverter;
	}

	@Required
	public void setB2BUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter)
	{
		this.b2BUnitConverter = b2BUnitConverter;
	}

	protected Converter<B2BCostCenterModel, B2BCostCenterData> getB2BCostCenterConverter()
	{
		return b2BCostCenterConverter;
	}

	@Required
	public void setB2BCostCenterConverter(final Converter<B2BCostCenterModel, B2BCostCenterData> b2bCostCenterConverter)
	{
		b2BCostCenterConverter = b2bCostCenterConverter;
	}

	protected Converter<B2BCustomerModel, CustomerData> getB2BCustomerConverter()
	{
		return b2BCustomerConverter;
	}

	@Required
	public void setB2BCustomerConverter(final Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter)
	{
		b2BCustomerConverter = b2bCustomerConverter;
	}

	protected Converter<B2BBudgetModel, B2BBudgetData> getB2BBudgetConverter()
	{
		return b2BBudgetConverter;
	}

	@Required
	public void setB2BBudgetConverter(final Converter<B2BBudgetModel, B2BBudgetData> b2bBudgetConverter)
	{
		b2BBudgetConverter = b2bBudgetConverter;
	}

	protected Populator<AddressData, AddressModel> getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final Populator<AddressData, AddressModel> addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	protected Converter<B2BPermissionModel, B2BPermissionData> getB2BPermissionConverter()
	{
		return b2BPermissionConverter;
	}

	@Required
	public void setB2BPermissionConverter(final Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter)
	{
		this.b2BPermissionConverter = b2BPermissionConverter;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected B2BUserGroupsLookUpStrategy getB2BUserGroupsLookUpStrategy()
	{
		return b2BUserGroupsLookUpStrategy;
	}

	@Required
	public void setB2BUserGroupsLookUpStrategy(final B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy)
	{
		this.b2BUserGroupsLookUpStrategy = b2BUserGroupsLookUpStrategy;
	}

	protected B2BBudgetReversePopulator getB2BBudgetReversePopulator()
	{
		return b2BBudgetReversePopulator;
	}

	@Required
	public void setB2BBudgetReversePopulator(final B2BBudgetReversePopulator b2bBudgetReversePopulator)
	{
		b2BBudgetReversePopulator = b2bBudgetReversePopulator;
	}

	protected B2BCostCenterReversePopulator getB2BCostCenterReversePopulator()
	{
		return b2BCostCenterReversePopulator;
	}

	@Required
	public void setB2BCostCenterReversePopulator(final B2BCostCenterReversePopulator b2bCostCenterReversePopulator)
	{
		b2BCostCenterReversePopulator = b2bCostCenterReversePopulator;
	}

	protected Converter<B2BUserGroupModel, B2BUserGroupData> getB2BUserGroupConverter()
	{
		return b2BUserGroupConverter;
	}

	@Required
	public void setB2BUserGroupConverter(final Converter<B2BUserGroupModel, B2BUserGroupData> b2BUserGroupConverter)
	{
		this.b2BUserGroupConverter = b2BUserGroupConverter;
	}

	protected Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> getB2BPermissionTypeDataConverter()
	{
		return b2BPermissionTypeDataConverter;
	}

	@Required
	public void setB2BPermissionTypeDataConverter(
			final Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2bPermissionTypeDataConverter)
	{
		b2BPermissionTypeDataConverter = b2bPermissionTypeDataConverter;
	}

	/**
	 * @return the b2bCustomerReversePopulator
	 */
	protected B2BCustomerReversePopulator getB2BCustomerReversePopulator()
	{
		return b2BCustomerReversePopulator;
	}

	/**
	 * @param b2BCustomerReversePopulator
	 *           the b2bCustomerReversePopulator to set
	 */
	@Required
	public void setB2BCustomerReversePopulator(final B2BCustomerReversePopulator b2BCustomerReversePopulator)
	{
		this.b2BCustomerReversePopulator = b2BCustomerReversePopulator;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected B2BPermissionsReversePopulator getB2BPermissionReversePopulator()
	{
		return b2BPermissionReversePopulator;
	}

	@Required
	public void setB2BPermissionReversePopulator(final B2BPermissionsReversePopulator b2bPermissionReversePopulator)
	{
		b2BPermissionReversePopulator = b2bPermissionReversePopulator;
	}

	protected B2BCommerceUserService getB2BCommerceUserService()
	{
		return b2BCommerceUserService;
	}

	@Required
	public void setB2BCommerceUserService(final B2BCommerceUserService b2BCommerceUserService)
	{
		this.b2BCommerceUserService = b2BCommerceUserService;
	}

	protected B2BCommerceUnitService getB2BCommerceUnitService()
	{
		return b2BCommerceUnitService;
	}

	@Required
	public void setB2BCommerceUnitService(final B2BCommerceUnitService b2BCommerceUnitService)
	{
		this.b2BCommerceUnitService = b2BCommerceUnitService;
	}

	protected B2BCommercePermissionService getB2BCommercePermissionService()
	{
		return b2BCommercePermissionService;
	}

	@Required
	public void setB2BCommercePermissionService(final B2BCommercePermissionService b2BCommercePermissionService)
	{
		this.b2BCommercePermissionService = b2BCommercePermissionService;
	}

	protected B2BCommerceBudgetService getB2BCommerceBudgetService()
	{
		return b2BCommerceBudgetService;
	}

	@Required
	public void setB2BCommerceBudgetService(final B2BCommerceBudgetService b2BCommerceBudgetService)
	{
		this.b2BCommerceBudgetService = b2BCommerceBudgetService;
	}

	protected B2BCommerceCostCenterService getB2BCommerceCostCenterService()
	{
		return b2BCommerceCostCenterService;
	}

	@Required
	public void setB2BCommerceCostCenterService(final B2BCommerceCostCenterService b2BCommerceCostCenterService)
	{
		this.b2BCommerceCostCenterService = b2BCommerceCostCenterService;
	}

	protected B2BCommerceB2BUserGroupService getB2BCommerceB2BUserGroupService()
	{
		return b2BCommerceB2BUserGroupService;
	}

	@Required
	public void setB2BCommerceB2BUserGroupService(final B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService)
	{
		this.b2BCommerceB2BUserGroupService = b2BCommerceB2BUserGroupService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the b2BUnitReversePopulator
	 */
	protected B2BUnitReversePopulator getB2BUnitReversePopulator()
	{
		return b2BUnitReversePopulator;
	}

	/**
	 * @param b2bUnitReversePopulator
	 *           the b2BUnitReversePopulator to set
	 */
	@Required
	public void setB2BUnitReversePopulator(final B2BUnitReversePopulator b2bUnitReversePopulator)
	{
		b2BUnitReversePopulator = b2bUnitReversePopulator;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}
