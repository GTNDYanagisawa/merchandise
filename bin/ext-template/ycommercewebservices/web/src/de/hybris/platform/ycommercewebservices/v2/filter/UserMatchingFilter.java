package de.hybris.platform.ycommercewebservices.v2.filter;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Filter that puts user from the requested url into the session.
 */
public class UserMatchingFilter extends AbstractUrlMatchingFilter
{
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	public static final String ROLE_CUSTOMERGROUP = "ROLE_CUSTOMERGROUP";
	public static final String ROLE_CUSTOMERMANAGERGROUP = "ROLE_CUSTOMERMANAGERGROUP";
	public static final String ROLE_TRUSTED_CLIENT = "ROLE_TRUSTED_CLIENT";
	private static final String CURRENT_USER = "current";
	private static final String ANONYMOUS_USER = "anonymous";
	private String regexp;
	private UserService userService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		final Authentication auth = getAuth();

		final String userID = getValue(request, regexp);

		if (userID == null)
		{
			if (hasRole(ROLE_CUSTOMERGROUP, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth))
			{
				setCurrentUser((String) auth.getPrincipal());
			}
			else
			{
				// fallback to anonymous
				setCurrentUser(userService.getAnonymousUser());
			}
		}
		else if (userID.equals(ANONYMOUS_USER))
		{
			setCurrentUser(userService.getAnonymousUser());
		}
		else if (hasRole(ROLE_TRUSTED_CLIENT, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth))
		{
			setCurrentUser(userID);
		}
		else if (hasRole(ROLE_CUSTOMERGROUP, auth))
		{
			if (userID.equals(CURRENT_USER) || userID.equals(auth.getPrincipal()))
			{
				setCurrentUser((String) auth.getPrincipal());
			}
			else
			{
				throw new AccessDeniedException("Access is denied");
			}
		}
		else
		{
			// could not match any authorized role
			throw new AccessDeniedException("Access is denied");
		}

		filterChain.doFilter(request, response);
	}

	protected Authentication getAuth()
	{
		return SecurityContextHolder.getContext().getAuthentication();
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

	protected boolean hasRole(final String role, final Authentication auth)
	{
		for (final GrantedAuthority ga : auth.getAuthorities())
		{
			if (ga.getAuthority().equals(role))
			{
				return true;
			}
		}
		return false;
	}

	protected void setCurrentUser(final String uid)
	{
		final UserModel userModel = userService.getUserForUID(uid);
		userService.setCurrentUser(userModel);
	}

	protected void setCurrentUser(final UserModel user)
	{
		userService.setCurrentUser(user);
	}
}
