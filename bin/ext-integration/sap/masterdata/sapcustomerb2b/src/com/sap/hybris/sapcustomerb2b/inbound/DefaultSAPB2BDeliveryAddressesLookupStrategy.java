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
package com.sap.hybris.sapcustomerb2b.inbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.strategies.impl.DefaultB2BDeliveryAddressesLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Enhancement of B2BUnit lookup strategy to determine the sales area dependent addresses
 * 
 */
public class DefaultSAPB2BDeliveryAddressesLookupStrategy extends DefaultB2BDeliveryAddressesLookupStrategy
{

	@Override
	public List<AddressModel> getDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder,
			final boolean visibleAddressesOnly)
	{

		List<AddressModel> deliveryAddresses = new ArrayList<AddressModel>();

		// retrieve default delivery addresses for order
		deliveryAddresses = super.getDeliveryAddressesForOrder(abstractOrder, visibleAddressesOnly);

		// retrieve B2B customer of order
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) abstractOrder.getUser();

		// retrieve B2B unit of B2B customer
		final B2BUnitModel b2bUnit = super.getB2bUnitService().getParent(b2bCustomer);

		// retrieve delivery addresses for B2B unit
		final List<AddressModel> deliveryAddressesForB2BUnit = getDeliveryAddressesForB2BUnit(b2bUnit, visibleAddressesOnly);

		// merge delivery addresses for order and for B2B unit
		if (deliveryAddresses != null && !deliveryAddresses.isEmpty())
		{
			if (deliveryAddressesForB2BUnit != null && !deliveryAddressesForB2BUnit.isEmpty())
			{
				deliveryAddresses.addAll(deliveryAddressesForB2BUnit);
			}
		}
		else
		{
			if (deliveryAddressesForB2BUnit != null && !deliveryAddressesForB2BUnit.isEmpty())
			{
				return deliveryAddressesForB2BUnit;

			}
			else
			{
				return Collections.emptyList();
			}
		}

		return deliveryAddresses;
	}

	protected List<AddressModel> getDeliveryAddressesForB2BUnit(final B2BUnitModel b2bUnit, final boolean visibleAddressesOnly)
	{
		if (visibleAddressesOnly)
		{
			return getVisibleDeliveryAddressesForB2BUnit(b2bUnit);
		}
		return getAllDeliveryAddressesForB2BUnit(b2bUnit);
	}

	protected List<AddressModel> getVisibleDeliveryAddressesForB2BUnit(final B2BUnitModel b2bUnit)
	{
		ServicesUtil.validateParameterNotNull(b2bUnit, "B2BUnit cannot be null");
		final List<AddressModel> addresses = new ArrayList<AddressModel>();
		for (final AddressModel address : b2bUnit.getAddresses())
		{
			// check whether B2B unit address is a shipping address and visible
			// in address book
			if (Boolean.TRUE.equals(address.getShippingAddress()) && Boolean.TRUE.equals(address.getVisibleInAddressBook()))
			{
				addresses.add(address);
			}
		}
		return addresses;
	}

	protected List<AddressModel> getAllDeliveryAddressesForB2BUnit(final B2BUnitModel b2bUnit)
	{

		ServicesUtil.validateParameterNotNull(b2bUnit, "B2BUnit cannot be null");
		final List<AddressModel> addresses = new ArrayList<AddressModel>();
		for (final AddressModel address : b2bUnit.getAddresses())
		{
			// check whether B2B unit address is a shipping address
			if (Boolean.TRUE.equals(address.getShippingAddress()))
			{
				addresses.add(address);
			}
		}
		return addresses;
	}
}
