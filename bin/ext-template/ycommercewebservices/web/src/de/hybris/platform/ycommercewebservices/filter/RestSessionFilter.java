package de.hybris.platform.ycommercewebservices.filter;

import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.GenericFilterBean;


public class RestSessionFilter extends GenericFilterBean
{
	private SessionService sessionService;

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException
	{
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		Session session = null;

		try
		{
			session = sessionService.createNewSession();
			filterChain.doFilter(request, response);
		}
		finally
		{
			if (session != null)
			{
				sessionService.closeSession(session);
			}

			final HttpSession httpSession = httpRequest.getSession(false);
			if (httpSession != null)
			{
				httpSession.invalidate();
			}
		}
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
