package de.hybris.platform.ycommercewebservices.v2.filter;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


/**
 * Test suite for {@link UserMatchingFilter}
 */
public class UserMatchingFilterTest
{
	static final String DEFAULT_REGEXP = "^/[^/]+/users/([^/]+)";
	static final String ANONYMOUS_UID = "anonymous";
	static final String CUSTOMER_UID = "customerUID";
	static final String CUSTOMER_MANAGER_UID = "customerManagerUID";
	static final String ROLE_UNKNOWN = "ROLE_UNKNOWN";
	private UserMatchingFilter userMatchingFilter;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;
	@Mock
	private FilterChain filterChain;
	@Mock
	private UserService userService;
	@Mock
	private CustomerModel principalUserModel;
	@Mock
	private CustomerModel customerUserModel;
	@Mock
	private CustomerModel anonymousUserModel;
	@Mock
	private TestingAuthenticationToken authentication;
	@Mock
	private GrantedAuthority grantedAuthority;
	private Collection<GrantedAuthority> authorities;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		userMatchingFilter = new UserMatchingFilter()
		{
			@Override
			protected Authentication getAuth()
			{
				return authentication;
			}
		};
		userMatchingFilter.setRegexp(DEFAULT_REGEXP);
		userMatchingFilter.setUserService(userService);
		authorities = new ArrayList<>();
		given(userService.getAnonymousUser()).willReturn(anonymousUserModel);
	}

	public void createAuthority(final String role, final String principal)
	{
		given(grantedAuthority.getAuthority()).willReturn(role);
		authorities.add(grantedAuthority);
		given(authentication.getAuthorities()).willReturn(authorities);
		given(authentication.getPrincipal()).willReturn(principal);
		given(userService.getUserForUID(principal)).willReturn(principalUserModel);
	}

	public void testNullPathInfo(final String role, final String principal) throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn(null);
		createAuthority(role, principal);

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(userService, times(1)).setCurrentUser(principalUserModel);
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testNullPathInfoOnAnonymous() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn(null);
		given(grantedAuthority.getAuthority()).willReturn(UserMatchingFilter.ROLE_ANONYMOUS);
		authorities.add(grantedAuthority);
		given(authentication.getAuthorities()).willReturn(authorities);
		given(authentication.getPrincipal()).willReturn(ANONYMOUS_UID);
		given(userService.getUserForUID(ANONYMOUS_UID)).willReturn(anonymousUserModel);

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(userService, times(1)).setCurrentUser(anonymousUserModel);
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testNullPathInfoOnCustomer() throws ServletException, IOException
	{
		testNullPathInfo(UserMatchingFilter.ROLE_CUSTOMERGROUP, CUSTOMER_UID);
	}

	@Test
	public void testNullPathInfoOnCustomerManager() throws ServletException, IOException
	{
		testNullPathInfo(UserMatchingFilter.ROLE_CUSTOMERMANAGERGROUP, CUSTOMER_MANAGER_UID);
	}

	@Test
	public void testNotMatchingPathForTrustedClient() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/test/some/longer/path");
		createAuthority(UserMatchingFilter.ROLE_TRUSTED_CLIENT, "trusted_client");

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(userService, times(1)).setCurrentUser(anonymousUserModel);
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = AccessDeniedException.class)
	public void testMatchingPathForUnknownRole() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/wsTest/users/admin");
		createAuthority(ROLE_UNKNOWN, "unknown");

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	public void testMatchingPathForCustomerManagingUser(final String role, final String principal) throws IOException,
			ServletException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/wsTest/users/" + CUSTOMER_UID + "/and/more");
		createAuthority(role, principal);
		given(userService.getUserForUID(CUSTOMER_UID)).willReturn(customerUserModel);

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(userService, times(1)).setCurrentUser(customerUserModel);
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testMatchingPathForTrustedClient() throws ServletException, IOException
	{
		testMatchingPathForCustomerManagingUser(UserMatchingFilter.ROLE_TRUSTED_CLIENT, "trusted_client");
	}

	@Test
	public void testMatchingPathForCustomerManager() throws ServletException, IOException
	{
		testMatchingPathForCustomerManagingUser(UserMatchingFilter.ROLE_CUSTOMERMANAGERGROUP, "customermanager");
	}

	@Test
	public void testMatchingPathForAuthenticatedCustomer() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/wsTest/users/" + CUSTOMER_UID + "/and/more");
		createAuthority(UserMatchingFilter.ROLE_CUSTOMERGROUP, CUSTOMER_UID);

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(userService, times(1)).setCurrentUser(principalUserModel);
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = AccessDeniedException.class)
	public void testFailMatchingPathForUnauthenticatedCustomer() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/wsTest/users/admin/and/more");
		createAuthority(UserMatchingFilter.ROLE_CUSTOMERGROUP, CUSTOMER_UID);

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testMatchingFilterForAnonymousUser() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/wsTest/users/" + ANONYMOUS_UID + "/and/more");

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(userService, times(1)).setCurrentUser(anonymousUserModel);
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testMatchingPathForCurrentCustomer() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/wsTest/users/current/and/more");
		createAuthority(UserMatchingFilter.ROLE_CUSTOMERGROUP, CUSTOMER_UID);

		userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(userService, times(1)).setCurrentUser(principalUserModel);
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}

}
