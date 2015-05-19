/**
 * 
 */
package de.hybris.platform.ycommercewebservices.cart.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.core.model.order.CartModel;


public class CommerceWebServicesCartFacade extends DefaultCartFacade
{
	@Override
	public CartData getSessionCart()
	{
		final CartData cartData;
		final CartModel cart = getCartService().getSessionCart();
		cartData = getCartConverter().convert(cart);
		return cartData;
	}

    public boolean isAnonymousUserCart(final String cartGuid){
        final CartModel cart = getCommerceCartService().getCartForGuidAndSiteAndUser(cartGuid,
                getBaseSiteService().getCurrentBaseSite(), getUserService().getAnonymousUser());
        return cart != null;
    }

    public boolean isCurrentUserCart(final String cartGuid) {
        final CartModel cart = getCommerceCartService().getCartForGuidAndSiteAndUser(cartGuid,
                getBaseSiteService().getCurrentBaseSite(), getUserService().getCurrentUser());
        return cart != null;
    }
}
