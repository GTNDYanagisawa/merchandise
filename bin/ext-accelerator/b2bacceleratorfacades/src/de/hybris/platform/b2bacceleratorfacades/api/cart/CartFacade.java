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
package de.hybris.platform.b2bacceleratorfacades.api.cart;

import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.List;


public interface CartFacade {
    /**
     * Method for adding a product to cart.
     *
     * @param cartEntry the cart entry with the new product to add..
     * @return the cart modification data that includes a statusCode and the actual quantity added to the cart
     * @throws de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException if the validation fails.
     */
    public CartModificationData addOrderEntry(final OrderEntryData cartEntry) throws EntityValidationException;

    /**
     * Method for updating the number of products.
     *
     * @param cartEntry the cart entry with the new value of quantity for product.
     * @return the cart modification data that includes a statusCode and the actual quantity that the entry was updated
     * to
     * @throws de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException if the validation fails.
     */
    public CartModificationData updateOrderEntry(final OrderEntryData cartEntry) throws EntityValidationException;


    /**
     * Method for adding a product to cart.
     *
     * @param cartEntries the cart entries with the new products to add.
     * @return the cart modification data that includes a statusCode and the actual quantity added to the cart
     */
    public List<CartModificationData> addOrderEntryList(final List<OrderEntryData> cartEntries);


    /**
     * Method for updating a list of products in the cart.
     *
     * @param cartEntries the cart entries with the new products to add.
     * @return the cart modification data that includes a statusCode and the actual quantity added to the cart
     */
    public List<CartModificationData> updateOrderEntryList(final List<OrderEntryData> cartEntries);


}
