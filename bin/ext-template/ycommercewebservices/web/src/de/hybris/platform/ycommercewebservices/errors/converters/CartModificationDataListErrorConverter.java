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
 */

package de.hybris.platform.ycommercewebservices.errors.converters;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.converters.AbstractErrorConverter;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converts {@link CartModificationDataList} to a list of {@link de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO}.
 */
public class CartModificationDataListErrorConverter extends AbstractErrorConverter
{
	private CartModificationDataErrorConverter cartModificationDataErrorConverter;

	@Override
	public boolean supports(final Class clazz)
	{
		return CartModificationDataList.class.isAssignableFrom(clazz);
	}

	@Override
	public void populate(final Object o, final List<ErrorWsDTO> webserviceErrorList)
	{
		final CartModificationDataList cartModificationList = (CartModificationDataList) o;
		for (final CartModificationData modificationData : cartModificationList.getCartModificationList())
		{
			getCartModificationDataErrorConverter().populate(modificationData, webserviceErrorList);
		}
	}

	public CartModificationDataErrorConverter getCartModificationDataErrorConverter()
	{
		return cartModificationDataErrorConverter;
	}

	@Required
	public void setCartModificationDataErrorConverter(final CartModificationDataErrorConverter cartModificationDataErrorConverter)
	{
		this.cartModificationDataErrorConverter = cartModificationDataErrorConverter;
	}
}
