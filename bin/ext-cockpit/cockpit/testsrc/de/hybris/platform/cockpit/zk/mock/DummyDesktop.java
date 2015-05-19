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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.zkoss.util.media.Media;
import org.zkoss.zk.device.Device;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.EventProcessingThread;
import org.zkoss.zk.ui.sys.RequestQueue;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.util.EventInterceptor;


/**
 * @author Jacek
 */
public class DummyDesktop implements Desktop, DesktopCtrl
{
	private final ApplicationContext applicationContext;

	/**
	 * @param applicationContext
	 */
	public DummyDesktop(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	@Override
	public void addEventInterceptor(final EventInterceptor eventInterceptor)
	{
		//
	}

	@Override
	public void addListener(final Object listener)
	{
		//
	}

	@Override
	public boolean enableServerPush(final boolean enable)
	{
		return false;
	}

	@Override
	public Object getAttribute(final String name)
	{
		return null;
	}

	@Override
	public Map getAttributes()
	{
		return null;
	}

	@Override
	public String getBookmark()
	{
		return null;
	}

	@Override
	public Component getComponentByUuid(final String uuid)
	{
		return null;
	}

	@Override
	public Component getComponentByUuidIfAny(final String uuid)
	{
		return null;
	}

	@Override
	public Collection getComponents()
	{
		return null;
	}

	@Override
	public String getCurrentDirectory()
	{
		return null;
	}

	@Override
	public Device getDevice()
	{
		return null;
	}

	@Override
	public String getDeviceType()
	{
		return null;
	}

	@Override
	public String getDownloadMediaURI(final Media media, final String pathInfo)
	{
		return null;
	}

	@Override
	public String getDynamicMediaURI(final Component comp, final String pathInfo)
	{
		return null;
	}

	@Override
	public Execution getExecution()
	{
		return null;
	}

	@Override
	public String getId()
	{
		return null;
	}

	@Override
	public Page getPage(final String pageId) throws ComponentNotFoundException
	{
		return null;
	}

	@Override
	public Page getPageIfAny(final String pageId)
	{
		return null;
	}

	@Override
	public Collection getPages()
	{
		return null;
	}

	@Override
	public String getRequestPath()
	{
		return null;
	}

	@Override
	public Session getSession()
	{
		return null;
	}

	@Override
	public String getUpdateURI(final String pathInfo)
	{
		return null;
	}

	@Override
	public WebApp getWebApp()
	{
		return new DummyWebApp(applicationContext);
	}

	@Override
	public boolean hasPage(final String pageId)
	{
		return false;
	}

	@Override
	public void invalidate()
	{
		//
	}

	@Override
	public boolean isAlive()
	{
		return false;
	}

	@Override
	public boolean isServerPushEnabled()
	{
		return false;
	}

	@Override
	public Object removeAttribute(final String name)
	{
		return null;
	}

	@Override
	public boolean removeEventInterceptor(final EventInterceptor eventInterceptor)
	{
		return false;
	}

	@Override
	public boolean removeListener(final Object listener)
	{
		return false;
	}

	@Override
	public Object setAttribute(final String name, final Object value)
	{
		return null;
	}

	@Override
	public void setBookmark(final String name)
	{
		//
	}

	@Override
	public void setCurrentDirectory(final String dir)
	{
		//
	}

	@Override
	public void setDeviceType(final String deviceType)
	{
		//
	}

	@Override
	public void setServerPushDelay(final int min, final int max, final int factor)
	{
		//
	}

	@Override
	public boolean activateServerPush(final long timeout) throws InterruptedException
	{
		return false;
	}

	@Override
	public void addComponent(final Component comp)
	{//
	}

	@Override
	public void addPage(final Page page)
	{//
	}

	@Override
	public void afterComponentAttached(final Component comp, final Page page)
	{//
	}

	@Override
	public void afterComponentDetached(final Component comp, final Page prevpage)
	{//
	}

	@Override
	public void afterComponentMoved(final Component parent, final Component child, final Component prevparent)
	{//
	}

	@Override
	public void afterProcessEvent(final Event event)
	{//
	}

	@Override
	public Event beforePostEvent(final Event event)
	{//
		return null;
	}

	@Override
	public Event beforeProcessEvent(final Event event)
	{//
		return null;
	}

	@Override
	public Event beforeSendEvent(final Event event)
	{//
		return null;
	}

	@Override
	public boolean ceaseSuspendedThread(final EventProcessingThread evtthd, final String cause)
	{//
		return false;
	}

	@Override
	public void deactivateServerPush()
	{//
	}

	@Override
	public void destroy()
	{//
	}

	@Override
	public boolean enableServerPush(final ServerPush serverpush)
	{//
		return false;
	}

	@Override
	public Object getActivationLock()
	{//
		return null;
	}

	@Override
	public Media getDownloadMedia(final String medId, final boolean reserved)
	{//
		return null;
	}

	@Override
	public Object getLastResponse(final String channel, final String reqId)
	{//
		return null;
	}

	@Override
	public int getNextKey()
	{//
		return 0;
	}

	@Override
	public String getNextUuid()
	{//
		return null;
	}

	@Override
	public RequestQueue getRequestQueue()
	{//
		return null;
	}

	@Override
	public int getResponseId(final boolean advance)
	{//
		return 0;
	}

	@Override
	public ServerPush getServerPush()
	{//
		return null;
	}

	@Override
	public Collection getSuspendedThreads()
	{//
		return null;
	}

	@Override
	public Visualizer getVisualizer()
	{//
		return null;
	}

	@Override
	public void invokeDesktopCleanups()
	{//
	}

	@Override
	public void invokeExecutionCleanups(final Execution exec, final Execution parent, final List errs)
	{//
	}

	@Override
	public void invokeExecutionInits(final Execution exec, final Execution parent) throws UiException
	{//
	}

	@Override
	public void onPiggyback()
	{//
	}

	@Override
	public void onPiggybackListened(final Component comp, final boolean listen)
	{//
	}

	@Override
	public void recoverDidFail(final Throwable exception)
	{//
	}

	@Override
	public void removeComponent(final Component comp)
	{//
	}

	@Override
	public void removePage(final Page page)
	{//
	}

	@Override
	public void responseSent(final String channel, final String reqId, final Object resInfo)
	{//
	}

	@Override
	public void sessionDidActivate(final Session sess)
	{//
	}

	@Override
	public void sessionWillPassivate(final Session sess)
	{//
	}

	@Override
	public void setBookmarkByClient(final String name)
	{//
	}

	@Override
	public void setExecution(final Execution exec)
	{//
	}

	@Override
	public void setId(final String id)
	{//
	}

	@Override
	public void setResponseId(final int resId)
	{//
	}

	@Override
	public void setVisualizer(final Visualizer visualizer)
	{//
	}

	@Override
	public void setBookmark(final String arg0, final boolean arg1)
	{
		// YTODO Auto-generated method stub

	}

}
