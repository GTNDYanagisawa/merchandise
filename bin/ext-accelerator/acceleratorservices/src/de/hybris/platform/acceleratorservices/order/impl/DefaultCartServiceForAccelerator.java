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
package de.hybris.platform.acceleratorservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.order.AbstractOrderService;
import de.hybris.platform.order.impl.DefaultCartService;

import org.apache.log4j.Logger;


/**
 * Override addNewEntry method from {@link AbstractOrderService} for adding new entry/update entries to cart.
 */
public class DefaultCartServiceForAccelerator extends DefaultCartService
{
	private static final long serialVersionUID = 5949847361480305375L;

	private static final Logger LOG = Logger.getLogger(DefaultCartServiceForAccelerator.class);
	private static final int APPEND_AS_LAST = -1;

	@Override
	public AbstractOrderEntryModel addNewEntry(final ComposedTypeModel entryType, final CartModel order,
			final ProductModel product, final long qty, final UnitModel unit, final int number, final boolean addToPresent)
	{
		validateParameterNotNullStandardMessage("entryType", entryType);
		validateParameterNotNullStandardMessage("product", product);
		validateParameterNotNullStandardMessage("order", order);
		if (qty <= 0)
		{
			throw new IllegalArgumentException("Quantity must be a positive non-zero value");
		}
		if (number < APPEND_AS_LAST)
		{
			throw new IllegalArgumentException("Number must be greater or equal -1");
		}
		UnitModel usedUnit = unit;
		if (usedUnit == null)
		{
			LOG.debug("No unit passed, trying to get product unit");
			usedUnit = product.getUnit();
			validateParameterNotNullStandardMessage("usedUnit", usedUnit);
		}

		AbstractOrderEntryModel ret = null;
		// search for present entries for this product if needed
		if (addToPresent)
		{
			for (final CartEntryModel e : getEntriesForProduct(order, product))
			{
				/*
				 * Check if the entrymodel has Point of service and if "Yes" then compare the entry number as we might have multiple POS for
				 * same product and update should happen for right entry model with right POS. Else if the POS is null and since we always
				 * pass -1 from DefaultCommerceCartService, we pass -1 only for addnew for POS, which means it's a shipping
				 * mode entry.
				 */
				if (((e.getDeliveryPointOfService() == null && number == APPEND_AS_LAST)) || e.getDeliveryPointOfService() != null
						&& number == e.getEntryNumber().intValue())
				{
					//Ensure that order entry is not a 'give away', and has same units
					if (Boolean.FALSE.equals(e.getGiveAway()) && usedUnit.equals(e.getUnit()))
					{
						e.setQuantity(Long.valueOf(e.getQuantity().longValue() + qty));
						ret = e;
						break;
					}
				}
			}
		}

		if (ret == null)
		{
			ret = getAbstractOrderEntryService().createEntry(entryType, order);
			ret.setQuantity(Long.valueOf(qty));
			ret.setProduct(product);
			ret.setUnit(usedUnit);
			addEntryAtPosition(order, ret, number);
		}
		order.setCalculated(Boolean.FALSE);
		return ret;
	}


}
