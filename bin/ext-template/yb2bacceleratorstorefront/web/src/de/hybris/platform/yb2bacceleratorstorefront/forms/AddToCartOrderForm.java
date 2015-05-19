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
package de.hybris.platform.yb2bacceleratorstorefront.forms;

import de.hybris.platform.b2bacceleratorfacades.product.data.CartEntryData;
import de.hybris.platform.b2bacceleratorfacades.product.data.ProductQuantityData;

import java.util.List;




public class AddToCartOrderForm
{

    private List<CartEntryData> cartEntries;

    /**
     * @return Return the cartEntries.
     */
    public List<CartEntryData> getCartEntries()
    {
        return cartEntries;
    }

    /**
     * @param cartEntries
     *           The cartEntries to set.
     */
    public void setCartEntries(final List<CartEntryData> cartEntries)
    {
        this.cartEntries = cartEntries;
    }


}
