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

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link B2BUnitModel} to {@link B2BUnitData}.
 */
public class B2BUnitPopulator implements Populator<B2BUnitModel, B2BUnitData>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private UserService userService;
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;
	private Converter<B2BBudgetModel, B2BBudgetData> b2BBudgetConverter;
	private Converter<PrincipalModel, PrincipalData> principalConverter;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;
	private CompanyB2BCommerceService companyB2BCommerceService;

	@Override
	public void populate(final B2BUnitModel source, final B2BUnitData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		populateUnit(source, target);
		populateUnitRelations(source, target);

		final B2BUnitModel parent = getB2BUnitService().getParent(source);
		if (parent != null)
		{
			target.setUnit(populateUnit(parent, new B2BUnitData()));
		}

		populateChildrenUnits(source, target);
	}

	protected B2BUnitData populateChildrenUnits(final B2BUnitModel source, final B2BUnitData target)
	{
		final List<B2BUnitData> childUnits = new ArrayList<B2BUnitData>();
		final Set<PrincipalModel> members = source.getMembers();
		final Collection<B2BUnitModel> childrenUnits = CollectionUtils.select(members,
				PredicateUtils.instanceofPredicate(B2BUnitModel.class));
		for (final B2BUnitModel unit : childrenUnits)
		{
			childUnits.add(this.populateUnit(unit, new B2BUnitData()));
		}
		target.setChildren(childUnits);
		return target;
	}

	protected B2BUnitData populateUnit(final B2BUnitModel source, final B2BUnitData target)
	{
		target.setName(source.getLocName());
		target.setUid(source.getUid());
		target.setActive(Boolean.TRUE.equals(source.getActive()));
		target.setApprovalProcessCode(source.getApprovalProcessCode());
		final Map<String, String> businessProcesses = companyB2BCommerceService.getBusinessProcesses();
		target.setApprovalProcessName((businessProcesses != null) ? businessProcesses.get(source.getApprovalProcessCode()) : "");
		return target;
	}

	protected B2BUnitData populateUnitRelations(final B2BUnitModel source, final B2BUnitData target)
	{
		// unit's budgets
		if (CollectionUtils.isNotEmpty(source.getBudgets()))
		{
			target.setBudgets(Converters.convertAll(source.getBudgets(), getB2BBudgetConverter()));
		}

		// unit's cost centers
		if (CollectionUtils.isNotEmpty(source.getCostCenters()))
		{
			target.setCostCenters(Converters.convertAll(CollectionUtils.select(source.getCostCenters(),
					new BeanPropertyValueEqualsPredicate(B2BCostCenterModel.ACTIVE, Boolean.TRUE)), getB2BCostCenterConverter()));
		}

		// unit approvers
		if (CollectionUtils.isNotEmpty(source.getApprovers()))
		{
			final UserGroupModel approverGroup = userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);
			target.setApprovers(Converters.convertAll(CollectionUtils.select(source.getApprovers(), new Predicate()
			{
				@Override
				public boolean evaluate(final Object object)
				{
					final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) object;
					return userService.isMemberOfGroup(b2bCustomerModel, approverGroup);
				}
			}), getB2BCustomerConverter()));
		}

		// unit addresses
		if (CollectionUtils.isNotEmpty(source.getAddresses()))
		{
			target.setAddresses(Converters.convertAll(source.getAddresses(), getAddressConverter()));
		}

		// unit's  customers
		final Collection<B2BCustomerModel> b2BCustomers = getB2BUnitService().getUsersOfUserGroup(source,
				B2BConstants.B2BCUSTOMERGROUP, false);
		if (CollectionUtils.isNotEmpty(b2BCustomers))
		{
			target.setCustomers(Converters.convertAll(b2BCustomers, getB2BCustomerConverter()));
		}

		// unit's  managers
		final Collection<B2BCustomerModel> managers = getB2BUnitService().getUsersOfUserGroup(source, B2BConstants.B2BMANAGERGROUP,
				false);
		if (CollectionUtils.isNotEmpty(managers))
		{
			target.setManagers(Converters.convertAll(managers, getB2BCustomerConverter()));
		}

		// unit's administrators
		final Collection<B2BCustomerModel> administrators = getB2BUnitService().getUsersOfUserGroup(source,
				B2BConstants.B2BADMINGROUP, false);
		if (CollectionUtils.isNotEmpty(administrators))
		{
			target.setAdministrators(Converters.convertAll(administrators, getB2BCustomerConverter()));
		}

		final Collection<PrincipalModel> accountManagers = new HashSet<PrincipalModel>();
		if (source.getAccountManager() != null)
		{
			accountManagers.add(source.getAccountManager());
		}
		if (CollectionUtils.isNotEmpty(source.getAccountManagerGroups()))
		{
			for (final UserGroupModel userGroupModel : source.getAccountManagerGroups())
			{
				accountManagers.addAll(userGroupModel.getMembers());
			}
		}
		if (CollectionUtils.isNotEmpty(accountManagers))
		{
			target.setAccountManagers(Converters.convertAll(accountManagers, getPrincipalConverter()));
		}

		return target;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
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

	protected Converter<B2BCostCenterModel, B2BCostCenterData> getB2BCostCenterConverter()
	{
		return b2BCostCenterConverter;
	}

	@Required
	public void setB2BCostCenterConverter(final Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter)
	{
		this.b2BCostCenterConverter = b2BCostCenterConverter;
	}

	protected Converter<B2BBudgetModel, B2BBudgetData> getB2BBudgetConverter()
	{
		return b2BBudgetConverter;
	}

	@Required
	public void setB2BBudgetConverter(final Converter<B2BBudgetModel, B2BBudgetData> b2BBudgetConverter)
	{
		this.b2BBudgetConverter = b2BBudgetConverter;
	}

	protected Converter<PrincipalModel, PrincipalData> getPrincipalConverter()
	{
		return principalConverter;
	}

	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}


	protected Converter<B2BCustomerModel, CustomerData> getB2BCustomerConverter()
	{
		return b2BCustomerConverter;
	}

	@Required
	public void setB2BCustomerConverter(final Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter)
	{
		this.b2BCustomerConverter = b2BCustomerConverter;
	}

	protected CompanyB2BCommerceService getCompanyB2BCommerceService()
	{
		return companyB2BCommerceService;
	}

	@Required
	public void setCompanyB2BCommerceService(final CompanyB2BCommerceService companyB2BCommerceService)
	{
		this.companyB2BCommerceService = companyB2BCommerceService;
	}
}
