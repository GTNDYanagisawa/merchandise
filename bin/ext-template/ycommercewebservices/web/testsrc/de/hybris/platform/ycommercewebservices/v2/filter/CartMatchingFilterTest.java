/**
 * 
 */
package de.hybris.platform.ycommercewebservices.v2.filter;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;


/**
 * Test suite for {@link CartMatchingFilter}
 * 
 */
@UnitTest
public class CartMatchingFilterTest
{
	static final String DEFAULT_REGEXP = "^/[^/]+/users/[^/]+/carts/([^/]+)";
	static final String CURRENT_CART_ID = "current";
	static final String CART_GUID = "6d868385adf11f729b6e30acd2c44195ccd6e882";
	static final String CART_CODE = "00000001";
	private CartMatchingFilter cartMatchingFilter;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;
	@Mock
	private FilterChain filterChain;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private CartService cartService;
	@Mock
	private BaseSiteModel currentBaseSiteModel;
	@Mock
	private BaseSiteModel otherBaseSiteModel;
	@Mock
	private CustomerModel customerUserModel;
	@Mock
	private CustomerModel anonymousUserModel;
	@Mock
	private CartModel cartModel;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private CurrencyModel otherCurrencyModel;
	@Mock
	private ModelService modelService;
	@Mock
	private UserModel userModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cartMatchingFilter = new CartMatchingFilter();
		cartMatchingFilter.setRegexp(DEFAULT_REGEXP);
		cartMatchingFilter.setBaseSiteService(baseSiteService);
		cartMatchingFilter.setUserService(userService);
		cartMatchingFilter.setCommerceCartService(commerceCartService);
		cartMatchingFilter.setCartService(cartService);
		cartMatchingFilter.setCommerceCommonI18NService(commerceCommonI18NService);
		cartMatchingFilter.setModelService(modelService);

		given(commerceCommonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		given(cartModel.getCurrency()).willReturn(currencyModel);
	}

	@Test
	public void testEmptyPathInfo() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn(null);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(commerceCartService, never()).restoreCart(any(CartModel.class));
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = IllegalStateException.class)
	public void testNoUserInSession() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);
		given(userService.getCurrentUser()).willReturn(null);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = IllegalStateException.class)
	public void testNoBaseSiteInSession() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(null);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = AccessDeniedException.class)
	public void testFailWhenNonCustomerUserAccessingCart() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/current/carts/" + CART_GUID);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(userService.getCurrentUser()).willReturn(userModel);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = AccessDeniedException.class)
	public void testAnonymousUserCurrentCart() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CURRENT_CART_ID);
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(anonymousUserModel))).willReturn(Boolean.TRUE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testAnonymousUser() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(anonymousUserModel))).willReturn(Boolean.TRUE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		//		given(commerceCartService.getCartForGuidAndSiteAndUser(CART_GUID, currentBaseSiteModel, anonymousUserModel)).willReturn(
		//				cartModel);
		given(commerceCartService.getCartForGuidAndSite(CART_GUID, currentBaseSiteModel)).willReturn(cartModel);
		given(cartModel.getUser()).willReturn(anonymousUserModel);
		given(cartModel.getGuid()).willReturn(CART_GUID);
		given(cartService.getSessionCart()).willReturn(cartModel);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(commerceCartService, times(1)).restoreCart(cartModel);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = CartException.class)
	public void testAnonymousUserRestorationFailed() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(anonymousUserModel))).willReturn(Boolean.TRUE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSiteAndUser(CART_GUID, currentBaseSiteModel, anonymousUserModel)).willReturn(
				cartModel);
		given(commerceCartService.restoreCart(cartModel)).willThrow(
				new CommerceCartRestorationException("Couldn't restore cart: " + CART_GUID));

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(commerceCartService, times(1)).restoreCart(cartModel);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testValidateBaseSiteFromLoadedCart() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(anonymousUserModel))).willReturn(Boolean.TRUE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSite(CART_GUID, currentBaseSiteModel)).willReturn(cartModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getSite()).willReturn(currentBaseSiteModel);
		given(cartModel.getUser()).willReturn(anonymousUserModel);
		given(cartModel.getGuid()).willReturn(CART_GUID);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(commerceCartService, times(1)).restoreCart(cartModel);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = BaseSiteMismatchException.class)
	public void testValidateBaseSiteFromLoadedCartMismatch() throws ServletException, IOException,
			CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(anonymousUserModel))).willReturn(Boolean.TRUE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSite(CART_GUID, currentBaseSiteModel)).willReturn(cartModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getSite()).willReturn(otherBaseSiteModel);
		given(cartModel.getUser()).willReturn(anonymousUserModel);
		given(cartModel.getGuid()).willReturn(CART_GUID);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = CartException.class)
	public void testAnonymousUserCartNotFound() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(anonymousUserModel))).willReturn(Boolean.TRUE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSiteAndUser(CART_GUID, currentBaseSiteModel, anonymousUserModel)).willReturn(
				null);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testCustomerUserCurrentCart() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/customerUser/carts/" + CURRENT_CART_ID);
		given(userService.getCurrentUser()).willReturn(customerUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(customerUserModel))).willReturn(Boolean.FALSE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSiteAndUser(null, currentBaseSiteModel, customerUserModel))
				.willReturn(cartModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getCode()).willReturn(CART_CODE);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(commerceCartService, times(1)).restoreCart(cartModel);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = CartException.class)
	public void testCustomerUserCurrentCartRestorationFailed() throws ServletException, IOException,
			CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/customerUser/carts/" + CURRENT_CART_ID);
		given(userService.getCurrentUser()).willReturn(customerUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(customerUserModel))).willReturn(Boolean.FALSE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSiteAndUser(null, currentBaseSiteModel, customerUserModel))
				.willReturn(cartModel);

		given(commerceCartService.restoreCart(cartModel)).willThrow(
				new CommerceCartRestorationException("Couldn't restore cart: " + CURRENT_CART_ID));

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = CartException.class)
	public void testCustomerUserCurrentCartNotFound() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/customerUser/carts/" + CURRENT_CART_ID);
		given(userService.getCurrentUser()).willReturn(customerUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(customerUserModel))).willReturn(Boolean.FALSE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSiteAndUser(null, currentBaseSiteModel, customerUserModel)).willReturn(null);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testCustomerUser() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/customerUser/carts/" + CART_CODE);
		given(userService.getCurrentUser()).willReturn(customerUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(customerUserModel))).willReturn(Boolean.FALSE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForCodeAndUser(CART_CODE, customerUserModel)).willReturn(cartModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getCode()).willReturn(CART_CODE);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		verify(commerceCartService, times(1)).restoreCart(cartModel);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = CartException.class)
	public void testCustomerUserRestorationFailed() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/customerUser/carts/" + CART_CODE);
		given(userService.getCurrentUser()).willReturn(customerUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(customerUserModel))).willReturn(Boolean.FALSE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForCodeAndUser(CART_CODE, customerUserModel)).willReturn(cartModel);
		given(commerceCartService.restoreCart(cartModel)).willThrow(
				new CommerceCartRestorationException("Couldn't restore cart: " + CART_CODE));

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = CartException.class)
	public void testCustomerUserCartNotFound() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/customerUser/carts/" + CART_CODE);
		given(userService.getCurrentUser()).willReturn(customerUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(customerUserModel))).willReturn(Boolean.FALSE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForCodeAndUser(CART_CODE, customerUserModel)).willReturn(null);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = CartException.class)
	public void testFailWithErrorWhenAnonymousCartExpired() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID + "/and/more");
		given(userService.getCurrentUser()).willReturn(anonymousUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(anonymousUserModel))).willReturn(Boolean.TRUE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForGuidAndSiteAndUser(CART_GUID, currentBaseSiteModel, anonymousUserModel)).willReturn(
				cartModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getGuid()).willReturn("Different_guid");

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test(expected = CartException.class)
	public void testFailWithErrorWhenCartExpired() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_CODE + "/and/more");
		given(userService.getCurrentUser()).willReturn(customerUserModel);
		given(Boolean.valueOf(userService.isAnonymousUser(customerUserModel))).willReturn(Boolean.FALSE);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		given(commerceCartService.getCartForCodeAndUser(CART_CODE, customerUserModel)).willReturn(cartModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getGuid()).willReturn("Different_code");

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testApplyCurrencyToCartAndRecalculate() throws CalculationException
	{
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getCurrency()).willReturn(otherCurrencyModel);

		cartMatchingFilter.applyCurrencyToCartAndRecalculateIfNeeded();
		verify(commerceCartService, times(1)).recalculateCart(cartModel);
	}

	@Test(expected = CartException.class)
	public void testApplyCurrencyToCartAndRecalculateWithException() throws CalculationException
	{
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getCurrency()).willReturn(otherCurrencyModel);
		doThrow(new CalculationException("Some calculation exception")).when(commerceCartService).recalculateCart(cartModel);

		try
		{
			cartMatchingFilter.applyCurrencyToCartAndRecalculateIfNeeded();
		}
		catch (Exception e)
		{
			verify(commerceCartService, times(1)).recalculateCart(cartModel);
			throw e;
		}
	}

	@Test
	public void testApplyCurrencyToCartAndNoRecalculate() throws CalculationException
	{
		given(cartService.getSessionCart()).willReturn(cartModel);

		cartMatchingFilter.applyCurrencyToCartAndRecalculateIfNeeded();
		verify(commerceCartService, times(0)).recalculateCart(cartModel);
	}
}
