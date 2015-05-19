package de.hybris.platform.ycommercewebservices.v2.filter;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ycommercewebservices.exceptions.BaseSiteMismatchException;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;


/**
 * Filter that puts cart from the requested url into the session.
 */
public class CartMatchingFilter extends AbstractUrlMatchingFilter
{
	private static final String CURRENT_CART = "current";
	private String regexp;
	private UserService userService;
	private CartService cartService;
	private CommerceCartService commerceCartService;
	private BaseSiteService baseSiteService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private ModelService modelService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (matchesUrl(request, regexp))
		{
			loadCart(request);
		}

		filterChain.doFilter(request, response);
	}

	protected void loadCart(final HttpServletRequest request) throws BaseSiteMismatchException
	{
		final UserModel currentUser = userService.getCurrentUser();

		if (currentUser == null)
		{
			throw new IllegalStateException(
					"Current user could not be retrieved from the request. Check filter order in your configuration.");
		}
		else if (!CustomerModel.class.isAssignableFrom(currentUser.getClass()))
		{
			// only customers can own carts
			throw new AccessDeniedException("Access is denied");
		}

		if (getBaseSiteService().getCurrentBaseSite() == null)
		{
			throw new IllegalStateException(
					"BaseSite could not be retrieved from the request. Check filter order in your configuration.");
		}

		final String cartID = getValue(request, regexp);

		if (!userService.isAnonymousUser(currentUser))
		{
			loadUserCart(cartID);
		}
		else
		{
			loadAnonymousCart(cartID);
		}
	}

	/**
	 * Loads customer's cart
	 * 
	 * @param cartID
	 */
	protected void loadUserCart(final String cartID) throws BaseSiteMismatchException
	{
		String requestedCartID = cartID;
		if (requestedCartID.equals(CURRENT_CART))
		{
			// current means last modified cart
			try
			{
				final CartModel cart = commerceCartService.getCartForGuidAndSiteAndUser(null, baseSiteService.getCurrentBaseSite(),
						userService.getCurrentUser());
				if (cart == null)
				{
					throw new CartException("No cart created yet.", CartException.NOT_FOUND);
				}
				validateBaseSiteFromCart(cart);
				requestedCartID = cart.getCode();
				commerceCartService.restoreCart(cart);
				applyCurrencyToCartAndRecalculateIfNeeded();
			}
			catch (final CommerceCartRestorationException e)
			{
				throw new CartException("Couldn't restore cart: " + e.getMessage(), CartException.INVALID, requestedCartID, e);
			}
		}
		else
		{
			try
			{
				final CartModel cart = commerceCartService.getCartForCodeAndUser(requestedCartID, userService.getCurrentUser());
				if (cart == null)
				{
					throw new CartException("Cart not found.", CartException.NOT_FOUND, requestedCartID);
				}
				validateBaseSiteFromCart(cart);
				commerceCartService.restoreCart(cart);
				applyCurrencyToCartAndRecalculateIfNeeded();
			}
			catch (final CommerceCartRestorationException e)
			{
				throw new CartException("Couldn't restore cart: " + e.getMessage(), CartException.INVALID, requestedCartID, e);
			}
		}

		// code might be now different because of cart expiration
		checkCartExpiration(requestedCartID, cartService.getSessionCart().getCode());
	}

	/**
	 * Loads anonymous or guest cart
	 * 
	 * @param cartID
	 */
	protected void loadAnonymousCart(final String cartID) throws BaseSiteMismatchException
	{
		if (cartID.equals(CURRENT_CART))
		{
			throw new AccessDeniedException("Access is denied");
		}
		else
		{
			try
			{
				final CartModel cart = commerceCartService.getCartForGuidAndSite(cartID, baseSiteService.getCurrentBaseSite());
				if (cart != null && CustomerModel.class.isAssignableFrom(cart.getUser().getClass()))
				{
					final CustomerModel cartOwner = (CustomerModel) cart.getUser();
					if (userService.isAnonymousUser(cartOwner) || CustomerType.GUEST.equals(cartOwner.getType()))
					{
						validateBaseSiteFromCart(cart);
						commerceCartService.restoreCart(cart);
						applyCurrencyToCartAndRecalculateIfNeeded();
					}
					else
					{
						// 'access denied' presented as 'not found' for security reasons
						throw new CartException("Cart not found.", CartException.NOT_FOUND, cartID);
					}
				}
				else
				{
					throw new CartException("Cart not found.", CartException.NOT_FOUND, cartID);
				}
			}
			catch (final CommerceCartRestorationException e)
			{
				throw new CartException("Couldn't restore cart: " + e.getMessage(), CartException.INVALID, cartID, e);
			}

			// guid might be now different because of cart expiration
			checkCartExpiration(cartID, cartService.getSessionCart().getGuid());
		}
	}

	/**
	 * Checks if base site set in the cart is the same as one set in baseSiteService. It prevents mixing requests for
	 * multiple sites in one session
	 */
	protected void validateBaseSiteFromCart(final CartModel cart) throws BaseSiteMismatchException
	{
		if (cart != null)
		{
			final BaseSiteModel baseSiteFromCart = cart.getSite();
			final BaseSiteModel baseSiteFromService = getBaseSiteService().getCurrentBaseSite();

			if (baseSiteFromCart != null && baseSiteFromService != null && !baseSiteFromCart.equals(baseSiteFromService))
			{
				throw new BaseSiteMismatchException(baseSiteFromService.getUid(), baseSiteFromCart.getUid());
			}
		}
	}

	/**
	 * Checks currently set currency and compares it with one set in cart. If not equal, sets new currency in cart and
	 * recalculates. This is similar logic to SessionContext.checkSpecialAttributes. Calling this is needed if
	 * checkSpecialAttributes was called before (when there was no cart in session)
	 */
	protected void applyCurrencyToCartAndRecalculateIfNeeded()
	{
		final CartModel cart = cartService.getSessionCart();
		final CurrencyModel currentCurrency = commerceCommonI18NService.getCurrentCurrency();
		if (!cart.getCurrency().equals(currentCurrency))
		{
			cart.setCurrency(currentCurrency);
			modelService.save(cart);
			try
			{
				commerceCartService.recalculateCart(cart);
			}
			catch (final CalculationException e)
			{
				throw new CartException("Couldn't recalculate cart" + e.getMessage(), CartException.CANNOT_RECALCULATE, e);
			}
		}
	}

	/**
	 * Checks whether cart expired and informs user about the change
	 * 
	 * @param requestedCartID
	 */
	protected void checkCartExpiration(final String requestedCartID, final String restoredCartID)
	{
		if (!requestedCartID.equals(restoredCartID))
		{
			throw new CartException("Cart [guid=" + requestedCartID + "] has expired. A new cart has been created [guid="
					+ restoredCartID + "]", CartException.EXPIRED, restoredCartID);
		}
	}

	protected String getRegexp()
	{
		return regexp;
	}

	@Required
	public void setRegexp(final String regexp)
	{
		this.regexp = regexp;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
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

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
