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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.zkoss.idom.Document;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.xel.Evaluator;


/**
 * @author Jacek
 */
public class DummyExecution implements Execution, ExecutionCtrl
{//

	private final ApplicationContext applicationContext;

	/**
	 * @param applicationContext
	 */
	public DummyExecution(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	@Override
	public void addAuResponse(final String key, final AuResponse resposne)
	{//
	}

	@Override
	public void addResponseHeader(final String name, final String value)
	{//
	}

	@Override
	public boolean containsResponseHeader(final String name)
	{//
		return false;
	}

	@Override
	public Component[] createComponents(final PageDefinition pagedef, final Map arg)
	{//
		return null;
	}

	@Override
	public Component[] createComponents(final String uri, final Map arg)
	{//
		return null;
	}

	@Override
	public Component createComponents(final PageDefinition pagedef, final Component parent, final Map arg)
	{//
		return null;
	}

	@Override
	public Component createComponents(final String uri, final Component parent, final Map arg)
	{//
		return null;
	}

	@Override
	public Component[] createComponentsDirectly(final String content, final String extension, final Map arg)
	{//
		return null;
	}

	@Override
	public Component[] createComponentsDirectly(final Document content, final String extension, final Map arg)
	{//
		return null;
	}

	@Override
	public Component[] createComponentsDirectly(final Reader reader, final String extension, final Map arg) throws IOException
	{//
		return null;
	}

	@Override
	public Component createComponentsDirectly(final String content, final String extension, final Component parent, final Map arg)
	{//
		return null;
	}

	@Override
	public Component createComponentsDirectly(final Document content, final String extension, final Component parent, final Map arg)
	{//
		return null;
	}

	@Override
	public Component createComponentsDirectly(final Reader reader, final String extension, final Component parent, final Map arg)
			throws IOException
	{//
		return null;
	}

	@Override
	public String encodeURL(final String uri)
	{//
		return null;
	}

	@Override
	public Object evaluate(final Component comp, final String expr, final Class expectedType)
	{//
		return null;
	}

	@Override
	public Object evaluate(final Page page, final String expr, final Class expectedType)
	{//
		return null;
	}

	@Override
	public void forward(final String page) throws IOException
	{//
	}

	@Override
	public void forward(final Writer writer, final String page, final Map params, final int mode) throws IOException
	{//
	}

	@Override
	public Map getArg()
	{//
		return null;
	}

	@Override
	public Object getAttribute(final String name)
	{//
		return null;
	}

	@Override
	public Map getAttributes()
	{//
		return null;
	}

	@Override
	public String getContextPath()
	{//
		return null;
	}

	@Override
	public Desktop getDesktop()
	{//
		return new DummyDesktop(applicationContext);
	}

	@Override
	public Evaluator getEvaluator(final Page page, final Class expfcls)
	{//
		return null;
	}

	@Override
	public Evaluator getEvaluator(final Component comp, final Class expfcls)
	{//
		return null;
	}

	@Override
	public String getHeader(final String name)
	{//
		return null;
	}

	@Override
	public Iterator getHeaderNames()
	{//
		return null;
	}

	@Override
	public Iterator getHeaders(final String name)
	{//
		return null;
	}

	@Override
	public String getLocalAddr()
	{//
		return null;
	}

	@Override
	public String getLocalName()
	{//
		return null;
	}

	@Override
	public int getLocalPort()
	{//
		return 0;
	}

	@Override
	public Object getNativeRequest()
	{//
		return null;
	}

	@Override
	public Object getNativeResponse()
	{//
		return null;
	}

	@Override
	public PageDefinition getPageDefinition(final String uri)
	{//
		return null;
	}

	@Override
	public PageDefinition getPageDefinitionDirectly(final String content, final String extension)
	{//
		return null;
	}

	@Override
	public PageDefinition getPageDefinitionDirectly(final Document content, final String extension)
	{//
		return null;
	}

	@Override
	public PageDefinition getPageDefinitionDirectly(final Reader reader, final String extension) throws IOException
	{//
		return null;
	}

	@Override
	public String getParameter(final String name)
	{//
		return null;
	}

	@Override
	public Map getParameterMap()
	{//
		return null;
	}

	@Override
	public String[] getParameterValues(final String name)
	{//
		return null;
	}

	@Override
	public String getRemoteAddr()
	{//
		return null;
	}

	@Override
	public String getRemoteHost()
	{//
		return null;
	}

	@Override
	public String getRemoteName()
	{//
		return null;
	}

	@Override
	public String getRemoteUser()
	{//
		return null;
	}

	@Override
	public String getScheme()
	{//
		return null;
	}

	@Override
	public String getServerName()
	{//
		return null;
	}

	@Override
	public int getServerPort()
	{//
		return 0;
	}

	@Override
	public String getUserAgent()
	{//
		return null;
	}

	@Override
	public Principal getUserPrincipal()
	{//
		return null;
	}

	@Override
	public VariableResolver getVariableResolver()
	{//
		return null;
	}

	@Override
	public void include(final String page) throws IOException
	{//
	}

	@Override
	public void include(final Writer writer, final String page, final Map params, final int mode) throws IOException
	{//
	}

	@Override
	public boolean isAsyncUpdate(final Page page)
	{//
		return false;
	}

	@Override
	public boolean isBrowser()
	{//
		return false;
	}

	@Override
	public boolean isBrowser(final String type)
	{//
		return false;
	}

	@Override
	public boolean isExplorer()
	{//
		return false;
	}

	@Override
	public boolean isExplorer7()
	{//
		return false;
	}

	@Override
	public boolean isForwarded()
	{//
		return false;
	}

	@Override
	public boolean isGecko()
	{//
		return false;
	}

	@Override
	public boolean isGecko3()
	{//
		return false;
	}

	@Override
	public boolean isHilDevice()
	{//
		return false;
	}

	@Override
	public boolean isIncluded()
	{//
		return false;
	}

	@Override
	public boolean isMilDevice()
	{//
		return false;
	}

	@Override
	public boolean isOpera()
	{//
		return false;
	}

	@Override
	public boolean isRobot()
	{//
		return false;
	}

	@Override
	public boolean isSafari()
	{//
		return false;
	}

	@Override
	public boolean isUserInRole(final String role)
	{//
		return false;
	}

	@Override
	public boolean isVoided()
	{//
		return false;
	}

	@Override
	public void popArg()
	{//
	}

	@Override
	public void postEvent(final Event evt)
	{//
	}

	@Override
	public void postEvent(final int priority, final Event evt)
	{//
	}

	@Override
	public void pushArg(final Map arg)
	{//
	}

	@Override
	public void removeAttribute(final String name)
	{//
	}

	@Override
	public void sendRedirect(final String uri)
	{//
	}

	@Override
	public void sendRedirect(final String uri, final String target)
	{//
	}

	@Override
	public void setAttribute(final String name, final Object value)
	{//
	}

	@Override
	public void setResponseHeader(final String name, final String value)
	{//
	}

	@Override
	public void setVoided(final boolean voided)
	{//
	}

	@Override
	public String toAbsoluteURI(final String uri, final boolean skipInclude)
	{//
		return null;
	}

	@Override
	public Page getCurrentPage()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentPage(final Page page)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public PageDefinition getCurrentPageDefinition()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentPageDefinition(final PageDefinition pgdef)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public Event getNextEvent()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActivated()
	{
		// YTODO Auto-generated method stub
		return false;
	}

	@Override
	public void onActivate()
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void onDeactivate()
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public boolean isRecovering()
	{
		// YTODO Auto-generated method stub
		return false;
	}

	@Override
	public Visualizer getVisualizer()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHeader(final String name, final String value)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(final String name, final long value)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void addHeader(final String name, final String value)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void addDateHeader(final String name, final long value)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public Object getRequestAttribute(final String name)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRequestAttribute(final String name, final Object value)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void setDesktop(final Desktop desktop)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void setRequestId(final String reqId)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public String getRequestId()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public String locate(final String arg0)
	{
		// YTODO Auto-generated method stub
		return null;
	}

}
