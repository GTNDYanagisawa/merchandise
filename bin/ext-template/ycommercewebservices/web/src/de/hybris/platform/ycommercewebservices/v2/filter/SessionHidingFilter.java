/**
 * 
 */
package de.hybris.platform.ycommercewebservices.v2.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class SessionHidingFilter implements Filter
{
	@Override
	public void destroy()
	{
		//
	}

	@Override
	public void doFilter(final ServletRequest paramServletRequest, final ServletResponse paramServletResponse,
			final FilterChain paramFilterChain) throws IOException, ServletException
	{
		final HttpServletRequest req = (HttpServletRequest) paramServletRequest;
		paramFilterChain.doFilter(new SessionHidingRequestWrapper(req), paramServletResponse);
	}

	@Override
	public void init(final FilterConfig paramFilterConfig) throws ServletException
	{
		//
	}

}
