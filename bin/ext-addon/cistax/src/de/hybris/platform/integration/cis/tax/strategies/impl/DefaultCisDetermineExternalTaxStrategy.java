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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import de.hybris.platform.commerceservices.externaltax.DecideExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.commons.strategies.OndemandDeliveryAddressStrategy;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * An implementation of {@link DecideExternalTaxesStrategy} to determine whether or not a recalculation of the taxes is
 * required.
 */
public class DefaultCisDetermineExternalTaxStrategy implements DecideExternalTaxesStrategy
{

	private OndemandDeliveryAddressStrategy ondemandDeliveryAddressStrategy;

	@Override
	public boolean shouldCalculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		Assert.notNull(abstractOrder, "Order is null. Cannot apply external tax to it.");

		return (Boolean.TRUE.equals(abstractOrder.getNet())
				&& Boolean.TRUE.equals(abstractOrder.getStore().getExternalTaxEnabled()) && abstractOrder.getDeliveryMode() != null && getOndemandDeliveryAddressStrategy()
				.getDeliveryAddressForOrder(abstractOrder) != null);
	}

	protected OndemandDeliveryAddressStrategy getOndemandDeliveryAddressStrategy()
	{
		return ondemandDeliveryAddressStrategy;
	}

	@Required
	public void setOndemandDeliveryAddressStrategy(final OndemandDeliveryAddressStrategy ondemandDeliveryAddressStrategy)
	{
		this.ondemandDeliveryAddressStrategy = ondemandDeliveryAddressStrategy;
	}
}
