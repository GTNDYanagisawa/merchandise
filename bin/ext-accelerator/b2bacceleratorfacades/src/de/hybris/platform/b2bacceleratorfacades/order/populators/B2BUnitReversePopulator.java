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

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


public class B2BUnitReversePopulator implements Populator<B2BUnitData, B2BUnitModel>
{
	private CompanyB2BCommerceService companyB2BCommerceService;

	@Override
	public void populate(final B2BUnitData source, final B2BUnitModel target) throws ConversionException
	{
		target.setName(source.getName());
		target.setLocName(source.getName());
		target.setUid(source.getUid());
		target.setActive(Boolean.TRUE);
		target.setApprovalProcessCode(source.getApprovalProcessCode());
		if (source.getUnit() != null)
		{
			final B2BUnitModel parentUnit = this.getCompanyB2BCommerceService().getUnitForUid(source.getUnit().getUid());
			if (getCompanyB2BCommerceService() instanceof B2BCommerceUnitService)
			{
				((B2BCommerceUnitService) getCompanyB2BCommerceService()).setParentUnit(target, parentUnit);
			}
		}

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
}
