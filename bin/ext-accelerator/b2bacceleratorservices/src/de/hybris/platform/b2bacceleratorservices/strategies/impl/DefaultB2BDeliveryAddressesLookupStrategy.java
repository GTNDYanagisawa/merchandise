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
package de.hybris.platform.b2bacceleratorservices.strategies.impl;

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.strategies.DeliveryAddressesLookupStrategy;
import de.hybris.platform.commerceservices.util.ItemComparator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

public class DefaultB2BDeliveryAddressesLookupStrategy implements DeliveryAddressesLookupStrategy
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private DeliveryAddressesLookupStrategy fallbackDeliveryAddressesLookupStrategy;

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	protected DeliveryAddressesLookupStrategy getFallbackDeliveryAddressesLookupStrategy()
	{
		return fallbackDeliveryAddressesLookupStrategy;
	}

	@Required
	public void setFallbackDeliveryAddressesLookupStrategy(final DeliveryAddressesLookupStrategy fallbackDeliveryAddressesLookupStrategy)
	{
		this.fallbackDeliveryAddressesLookupStrategy = fallbackDeliveryAddressesLookupStrategy;
	}

	@Override
	public List<AddressModel> getDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder, final boolean visibleAddressesOnly)
	{
		if (CheckoutPaymentType.ACCOUNT.equals(abstractOrder.getPaymentType()))
		{
			// Lookup the 
			final B2BCostCenterModel costCenter = getCostCenterForOrder(abstractOrder);
			if (costCenter != null)
			{
				final Set<AddressModel> addresses = collectAddressesForCostCenter(costCenter);
				if (addresses != null && !addresses.isEmpty())
				{
					return sortAddresses(addresses);
				}
			}

			// Can't find any pay on account addresses yet - maybe the cost centre is not set yet?
			return Collections.emptyList();
		}
		else
		{
			// Use fallback
			return getFallbackDeliveryAddressesLookupStrategy().getDeliveryAddressesForOrder(abstractOrder, visibleAddressesOnly);
		}
	}

	protected Set<AddressModel> collectAddressesForCostCenter(final B2BCostCenterModel costCenter)
	{
		final Set<B2BUnitModel> branch = getB2bUnitService().getBranch(costCenter.getUnit());
		final Set<AddressModel> addresses = new HashSet<AddressModel>();
		for (final B2BUnitModel unit : branch)
		{
			if (CollectionUtils.isNotEmpty(unit.getAddresses()))
			{
				addresses.addAll(unit.getAddresses());
			}
		}
		return addresses;
	}

	protected List<AddressModel> sortAddresses(final Collection<AddressModel> addresses)
	{
		final ArrayList<AddressModel> result = new ArrayList<AddressModel>(addresses);
		Collections.sort(result, ItemComparator.INSTANCE);
		return result;
	}

	protected B2BCostCenterModel getCostCenterForOrder(final AbstractOrderModel abstractOrderModel)
	{
		for (final AbstractOrderEntryModel abstractOrderEntry : abstractOrderModel.getEntries())
		{
			final B2BCostCenterModel costCenter = abstractOrderEntry.getCostCenter();
			if (costCenter != null)
			{
				return costCenter;
			}
		}
		return null;
	}
}
