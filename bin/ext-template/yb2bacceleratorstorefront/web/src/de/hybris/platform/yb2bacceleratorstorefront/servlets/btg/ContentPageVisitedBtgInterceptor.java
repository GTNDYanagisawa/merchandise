/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.yb2bacceleratorstorefront.servlets.btg;

import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractPageController;
import de.hybris.platform.btg.events.ContentPageVisitedBTGRuleDataEvent;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.servicelayer.event.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Interceptor to create BTG events for visited content pages. Using a filter is not appropriate since pages are
 * retrieved by controllers and populated in the model.
 */
public class ContentPageVisitedBtgInterceptor extends HandlerInterceptorAdapter
{
	private static final Logger LOG = Logger.getLogger(ContentPageVisitedBtgInterceptor.class);
	private EventService eventService;

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView)
	{
		if (modelAndView != null)
		{
			final AbstractPageModel page = (AbstractPageModel) modelAndView.getModel().get(AbstractPageController.CMS_PAGE_MODEL);
			if (page != null && page.getPk() != null)
			{
				try
				{
					eventService.publishEvent(new ContentPageVisitedBTGRuleDataEvent(page.getPk().getLongValueAsString()));
				}
				catch (final Exception e)
				{
					LOG.error("Could not publish event", e);
				}
			}
		}
	}
}
