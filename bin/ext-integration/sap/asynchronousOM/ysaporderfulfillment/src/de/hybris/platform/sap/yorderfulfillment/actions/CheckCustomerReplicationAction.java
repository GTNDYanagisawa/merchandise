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
package de.hybris.platform.sap.yorderfulfillment.actions;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/**
 * 
 */
public class CheckCustomerReplicationAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{

	private FlexibleSearchService flexibleSearchService;
	private SAPGlobalConfigurationService sAPGlobalConfigurationService;
	private B2CCustomerHelper b2CCustomerHelper;

	@SuppressWarnings("javadoc")
	public B2CCustomerHelper getB2CCustomerHelper()
	{
		return b2CCustomerHelper;
	}

	@SuppressWarnings("javadoc")
	public void setB2CCustomerHelper(final B2CCustomerHelper b2cCustomerHelper)
	{
		b2CCustomerHelper = b2cCustomerHelper;
	}

	@SuppressWarnings("javadoc")
	public SAPGlobalConfigurationService getsAPGlobalConfigurationService()
	{
		return sAPGlobalConfigurationService;
	}

	@SuppressWarnings("javadoc")
	public void setsAPGlobalConfigurationService(final SAPGlobalConfigurationService sAPGlobalConfigurationService)
	{
		this.sAPGlobalConfigurationService = sAPGlobalConfigurationService;
	}

	@SuppressWarnings("javadoc")
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@SuppressWarnings("javadoc")
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();


		if (sAPGlobalConfigurationService.getProperty("replicateregistereduser").equals(Boolean.TRUE))
		{
			final CustomerModel customerModel = ((CustomerModel) order.getUser());
			final boolean isCustomerExported = customerModel.getSapIsReplicated().booleanValue();
			final boolean isGuestUser = isGuestUser(customerModel);
			final boolean isB2B = isB2BCase(order);

			if (isCustomerExported || isGuestUser || isB2B)
			{
				return Transition.OK;
			}
			else
			{
				return Transition.NOK;
			}
		}
		return Transition.OK;
	}

	protected boolean isB2BCase(final OrderModel orderModel)
	{
		if (orderModel.getSite() != null)
		{
			return SiteChannel.B2B.equals(orderModel.getSite().getChannel());
		}
		else
		{
			return false;
		}
	}

	protected boolean isGuestUser(final CustomerModel customerModel)
	{
		return CustomerType.GUEST.equals(customerModel.getType());
	}

}
