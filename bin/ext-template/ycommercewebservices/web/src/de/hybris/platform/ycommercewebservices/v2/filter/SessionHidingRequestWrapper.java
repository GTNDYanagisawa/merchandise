/**
 * 
 */
package de.hybris.platform.ycommercewebservices.v2.filter;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;


@SuppressWarnings("deprecation")
public class SessionHidingRequestWrapper extends HttpServletRequestWrapper
{
	HttpServletRequest wrappedRequest;

	public SessionHidingRequestWrapper(final HttpServletRequest request)
	{
		super(request);
		wrappedRequest = request;
	}

	@Override
	public HttpSession getSession(final boolean create)
	{
		if (create)
		{
			return getDummySession();
		}
		else
		{
			return null;
		}
	}

	@Override
	public HttpSession getSession()
	{
		return getDummySession();
	}

	private HttpSession getDummySession()
	{
		final HttpSession hs = new HttpSession()
		{
			@Override
			public void setMaxInactiveInterval(final int paramInt)
			{
				return;
			}

			@Override
			public void setAttribute(final String paramString, final Object paramObject)
			{
				return;
			}

			@Override
			public void removeValue(final String paramString)
			{
				return;
			}

			@Override
			public void removeAttribute(final String paramString)
			{
				return;
			}

			@Override
			public void putValue(final String paramString, final Object paramObject)
			{
				return;
			}

			@Override
			public boolean isNew()
			{
				return false;
			}

			@Override
			public void invalidate()
			{
				return;
			}

			@Override
			public String[] getValueNames()
			{
				return null;
			}

			@Override
			public Object getValue(final String paramString)
			{
				return null;
			}

			@Override
			public HttpSessionContext getSessionContext()
			{
				return null;
			}

			@Override
			public ServletContext getServletContext()
			{
				return wrappedRequest.getServletContext();
			}

			@Override
			public int getMaxInactiveInterval()
			{
				return 0;
			}

			@Override
			public long getLastAccessedTime()
			{
				return 0;
			}

			@Override
			public String getId()
			{
				return null;
			}

			@Override
			public long getCreationTime()
			{
				return 0;
			}

			@Override
			public Enumeration<String> getAttributeNames()
			{
				return null;
			}

			@Override
			public Object getAttribute(final String paramString)
			{
				return null;
			}
		};
		return hs;
	}
}
