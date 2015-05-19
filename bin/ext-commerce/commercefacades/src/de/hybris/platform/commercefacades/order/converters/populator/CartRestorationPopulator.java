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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class CartRestorationPopulator implements Populator<CommerceCartRestoration, CartRestorationData>
{
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;

	protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}

	@Required
	public void setCartModificationConverter(
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartModificationConverter = cartModificationConverter;
	}

	@Override
	public void populate(final CommerceCartRestoration source, final CartRestorationData target)
	{
		if (source != null)
		{
			final List<CartModificationData> modifications = new ArrayList<CartModificationData>();
			for (final CommerceCartModification modification : source.getModifications())
			{
				modifications.add(getCartModificationConverter().convert(modification));
			}
			target.setModifications(modifications);
		}
	}
}
