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

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bacceleratorfacades.company.B2BCommercePermissionFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.B2BPermissionTypeEnum;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;


public class DefaultB2BCommercePermissionFacade extends DefaultCompanyB2BCommerceFacade implements B2BCommercePermissionFacade
{
	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissions(final PageableData pageableData)
	{
		final SearchPageData<B2BPermissionModel> permissions = getB2BCommercePermissionService().getPagedPermissions(pageableData);
		final SearchPageData<B2BPermissionData> searchPageData = convertPageData(permissions, getB2BPermissionConverter());
		return searchPageData;
	}

	@Override
	public List<B2BPermissionTypeData> getB2BPermissionTypes()
	{
		final List<B2BPermissionTypeEnum> permissionTypes = getEnumerationService().getEnumerationValues(
				B2BPermissionTypeEnum._TYPECODE);
		return Converters.convertAll(permissionTypes, getB2BPermissionTypeDataConverter());
	}

	@Override
	public B2BPermissionTypeData getB2BPermissionTypeDataForPermission(final B2BPermissionTypeEnum b2BPermissionTypeEnum)
	{
		return getB2BPermissionTypeDataConverter().convert(b2BPermissionTypeEnum);
	}

	@Override
	public void enableDisablePermission(final String permissionCode, final boolean active) throws DuplicateUidException
	{
		final B2BPermissionModel b2BPermissionModel = getCompanyB2BCommerceService().getPermissionForCode(permissionCode);
		if (b2BPermissionModel != null)
		{
			b2BPermissionModel.setActive(Boolean.valueOf(active));
		}
		getCompanyB2BCommerceService().saveModel(b2BPermissionModel);
	}

	@Override
	public void updatePermissionDetails(final B2BPermissionData b2BPermissionData) throws DuplicateUidException
	{
		final B2BPermissionModel b2BPermissionModel = getCompanyB2BCommerceService().getPermissionForCode(
				b2BPermissionData.getOriginalCode());
		if (b2BPermissionModel != null)
		{
			getB2BPermissionReversePopulator().populate(b2BPermissionData, b2BPermissionModel);
			getCompanyB2BCommerceService().saveModel(b2BPermissionModel);
		}
	}

	@Override
	public void addPermission(final B2BPermissionData b2BPermissionData) throws DuplicateUidException
	{
		final B2BPermissionTypeData b2BPermissionType = b2BPermissionData.getB2BPermissionTypeData();

		final B2BPermissionModel b2BPermissionModel = this.getModelService().create(
				B2BPermissionTypeEnum.valueOf(b2BPermissionType.getCode()).toString());

		if (b2BPermissionModel != null)
		{
			getB2BPermissionReversePopulator().populate(b2BPermissionData, b2BPermissionModel);
			getCompanyB2BCommerceService().saveModel(b2BPermissionModel);
		}
	}

	@Override
	public B2BPermissionData getPermissionDetails(final String uid)
	{
		final B2BPermissionModel b2BPermissionModel = getCompanyB2BCommerceService().getPermissionForCode(uid);
		final B2BPermissionData permissionData = getB2BPermissionConverter().convert(b2BPermissionModel);

		return permissionData;
	}
}
