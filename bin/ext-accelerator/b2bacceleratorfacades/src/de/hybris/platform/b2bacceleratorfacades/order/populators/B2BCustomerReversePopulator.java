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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.b2bacceleratorservices.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class B2BCustomerReversePopulator implements Populator<CustomerData, B2BCustomerModel>
{
	private CompanyB2BCommerceService companyB2BCommerceService;
	private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;
	private CustomerNameStrategy customerNameStrategy;
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;
	private UserService userService;
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	@Override
	public void populate(final CustomerData source, final B2BCustomerModel target) throws ConversionException
	{
		target.setEmail(source.getEmail());
		target.setName(getCustomerNameStrategy().getName(source.getFirstName(), source.getLastName()));
		final B2BUnitModel defaultUnit = getCompanyB2BCommerceService().getUnitForUid(source.getUnit().getUid());
		final B2BUnitModel oldDefaultUnit = getB2bUnitService().getParent(target);
		target.setDefaultB2BUnit(defaultUnit);

		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(target.getGroups());
		if (oldDefaultUnit != null && groups.contains(oldDefaultUnit))
		{
			groups.remove(oldDefaultUnit);
		}
		groups.add(defaultUnit);
		target.setGroups(groups);
		getB2BCommerceB2BUserGroupService().updateUserGroups(getUserGroups(), source.getRoles(), target);
		if (StringUtils.isNotBlank(source.getTitleCode()))
		{
			target.setTitle(getUserService().getTitleForCode(source.getTitleCode()));
		}
		else
		{
			target.setTitle(null);
		}
		setUid(source, target);
	}

	protected void setUid(final CustomerData source, final B2BCustomerModel target)
	{
		if (source.getDisplayUid() != null && !source.getDisplayUid().isEmpty())
		{
			target.setOriginalUid(source.getDisplayUid());
			target.setUid(source.getDisplayUid().toLowerCase());
		}
		else if (source.getEmail() != null)
		{
			target.setOriginalUid(source.getEmail());
			target.setUid(source.getEmail().toLowerCase());
		}
	}

	public List<String> getUserGroups()
	{
		return getB2BUserGroupsLookUpStrategy().getUserGroups();
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

	protected <T extends CompanyB2BCommerceService> T getCompanyB2BCommerceService()
	{
		return (T) companyB2BCommerceService;
	}

	@Required
	public void setCompanyB2BCommerceService(final CompanyB2BCommerceService companyB2BCommerceService)
	{
		this.companyB2BCommerceService = companyB2BCommerceService;
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

	public B2BCommerceB2BUserGroupService getB2BCommerceB2BUserGroupService()
	{
		return b2BCommerceB2BUserGroupService;
	}

	@Required
	public void setB2BCommerceB2BUserGroupService(final B2BCommerceB2BUserGroupService b2bCommerceB2BUserGroupService)
	{
		b2BCommerceB2BUserGroupService = b2bCommerceB2BUserGroupService;
	}

	protected B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
