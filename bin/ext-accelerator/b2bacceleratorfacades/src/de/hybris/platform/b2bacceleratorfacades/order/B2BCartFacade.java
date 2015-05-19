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
package de.hybris.platform.b2bacceleratorfacades.order;

import java.util.Comparator;

import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;

@Deprecated
public interface B2BCartFacade extends CartFacade
{

	/**
	 * Group multi-dimensional items in a cart.
	 * 
	 * @param variantSortStrategy
	 *           The strategy used to sort the variants.
	 */
	<T extends AbstractOrderData> void groupMultiDimensionalProducts(final T orderData,
			final Comparator<VariantOptionData> variantSortStrategy);

}
