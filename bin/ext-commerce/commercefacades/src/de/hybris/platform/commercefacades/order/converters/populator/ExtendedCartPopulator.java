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

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;


/**
 * Converter for {@link de.hybris.platform.commercefacades.order.data.CartData}, that populates additionally :<br>
 * 
 * <ul>
 * <li>delivery address</li>
 * <li>payment info</li>
 * <li>delivery mode</li>
 * </ul>
 * 
 * @author KKW
 */
public class ExtendedCartPopulator extends CartPopulator
{

	@Override
	public void populate(final CartModel source, final CartData target)
	{
		super.populate(source, target);
		addDeliveryAddress(source, target);
		addPaymentInformation(source, target);
		addDeliveryMethod(source, target);
	}
}
