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
package de.hybris.platform.b2bacceleratorservices.customer.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.customer.B2BCustomerAccountService;
import de.hybris.platform.b2bacceleratorservices.dao.B2BAcceleratorCartToOrderCronJobModelDao;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BCustomerAccountService extends DefaultCustomerAccountService implements B2BCustomerAccountService
{
	private B2BAcceleratorCartToOrderCronJobModelDao b2bAcceleratorCartToOrderCronJobModelDao;

	@Override
	public CartToOrderCronJobModel getCartToOrderCronJobForCode(final String code, final UserModel user)
	{
		return this.getB2bAcceleratorCartToOrderCronJobModelDao().findCartToOrderCronJobByCode(code, user);
	}

	@Override
	public List<? extends CartToOrderCronJobModel> getCartToOrderCronJobsForUser(final UserModel user)
	{
		return getB2bAcceleratorCartToOrderCronJobModelDao().findCartToOrderCronJobsByUser(user);
	}

	@Override
	public SearchPageData<CartToOrderCronJobModel> getPagedCartToOrderCronJobsForUser(final UserModel user,
			final PageableData pageableData)
	{
		return this.getB2bAcceleratorCartToOrderCronJobModelDao().findPagedCartToOrderCronJobsByUser(user, pageableData);
	}

	@Override
	public SearchPageData<OrderModel> getOrdersForJob(final String jobCode, final PageableData pageableData)
	{
		return this.getB2bAcceleratorCartToOrderCronJobModelDao().findOrderByJob(jobCode, pageableData);
	}

	@Override
	public void updateProfile(final CustomerModel customerModel, final String titleCode, final String name, final String login)
			throws DuplicateUidException
	{
		if (customerModel instanceof B2BCustomerModel)
		{
			((B2BCustomerModel) customerModel).setEmail(login);
		}
		super.updateProfile(customerModel, titleCode, name, login);
	}


	@Required
	public void setB2bAcceleratorCartToOrderCronJobModelDao(
			final B2BAcceleratorCartToOrderCronJobModelDao b2bAcceleratorCartToOrderCronJobModelDao)
	{
		this.b2bAcceleratorCartToOrderCronJobModelDao = b2bAcceleratorCartToOrderCronJobModelDao;
	}

	protected B2BAcceleratorCartToOrderCronJobModelDao getB2bAcceleratorCartToOrderCronJobModelDao()
	{
		return b2bAcceleratorCartToOrderCronJobModelDao;
	}

	@Override
	protected void adjustPassword(final UserModel currentUser, final String newUid, final String currentPassword)
			throws PasswordMismatchException
	{
		if (currentUser instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) currentUser;
			b2bCustomer.setEmail(b2bCustomer.getOriginalUid());
		}
		super.adjustPassword(currentUser, newUid, currentPassword);
	}


}
