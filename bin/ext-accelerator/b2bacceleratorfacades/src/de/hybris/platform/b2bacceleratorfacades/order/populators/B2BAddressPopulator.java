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


import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link AddressData} with {@link AddressModel}.
 */
public class B2BAddressPopulator implements Populator<AddressModel, AddressData>
{
	private CartService cartService;

	@Override
	public void populate(final AddressModel addressModel, final AddressData addressData) throws ConversionException
	{
		addressData.setEditable(true);

		if (getCartService().hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			for (final AbstractOrderEntryModel entry : cartModel.getEntries())
			{
				if (entry.getCostCenter() != null)
				{
					// if cost center has been assigned to the cart mark address is not editable.
					addressData.setEditable(false);
					break;
				}
			}
		}
	}


	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}
