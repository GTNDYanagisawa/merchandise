/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cockpit.zk.mock;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.SessionCache;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;


/**
 * @author Jacek
 */
public class DummyWebApp implements WebApp, WebAppCtrl
{

	private DummyServletContext servletCtx;
	private final String springConstant = "org.springframework.web.context.WebApplicationContext.ROOT";
	private final ApplicationContext applicationContext;

	/**
	 * @param applicationContext
	 */
	public DummyWebApp(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	@Override
	public String getAppName()
	{
		return null;
	}

	@Override
	public Object getAttribute(final String name)
	{
		return servletCtx.getAttribute(name);
	}

	@Override
	public Map getAttributes()
	{
		return null;
	}

	@Override
	public String getBuild()
	{
		return null;
	}

	@Override
	public Configuration getConfiguration()
	{
		return null;
	}

	@Override
	public String getInitParameter(final String name)
	{
		return null;
	}

	@Override
	public Iterator getInitParameterNames()
	{
		return null;
	}

	@Override
	public String getMimeType(final String file)
	{
		return null;
	}

	@Override
	public Object getNativeContext()
	{
		servletCtx = new DummyServletContext();
		servletCtx.setAttribute(springConstant, new DummyWebApplicationContext(applicationContext, servletCtx));
		return servletCtx;
	}

	@Override
	public String getRealPath(final String path)
	{
		return null;
	}

	@Override
	public URL getResource(final String path)
	{
		return null;
	}

	@Override
	public InputStream getResourceAsStream(final String path)
	{
		return null;
	}

	@Override
	public Set getResourcePaths(final String path)
	{
		return null;
	}

	@Override
	public int getSubversion(final int portion)
	{
		return 0;
	}

	@Override
	public String getUpdateURI()
	{
		return null;
	}

	@Override
	public String getVersion()
	{
		return null;
	}

	@Override
	public WebApp getWebApp(final String uripath)
	{
		return null;
	}

	@Override
	public void removeAttribute(final String name)
	{//
	}

	@Override
	public void setAppName(final String name)
	{//
	}

	@Override
	public void setAttribute(final String name, final Object value)
	{//
	}

	@Override
	public String getDirectory()
	{//
		return null;
	}

	@Override
	public void init(final Object context, final Configuration config)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void destroy()
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public UiEngine getUiEngine()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUiEngine(final UiEngine engine)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public DesktopCache getDesktopCache(final Session sess)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public DesktopCacheProvider getDesktopCacheProvider()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDesktopCacheProvider(final DesktopCacheProvider provider)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public UiFactory getUiFactory()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUiFactory(final UiFactory factory)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public FailoverManager getFailoverManager()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFailoverManager(final FailoverManager manager)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public IdGenerator getIdGenerator()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdGenerator(final IdGenerator generator)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public SessionCache getSessionCache()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSessionCache(final SessionCache cache)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void sessionWillPassivate(final Session sess)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void sessionDidActivate(final Session sess)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(final Session sess)
	{
		// YTODO Auto-generated method stub

	}

}
