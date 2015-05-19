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
package de.hybris.platform.acceleratorcms.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 */
public class DefaultCMSPageContextService implements CMSPageContextService
{
	private static final Logger LOG = Logger.getLogger(DefaultCMSPageContextService.class); //NOPMD

	private CMSPreviewService cmsPreviewService;
	private SessionService sessionService;
	private CMSPageService cmsPageService;
	private UserService userService;

	protected CMSPreviewService getCmsPreviewService()
	{
		return cmsPreviewService;
	}

	@Required
	public void setCmsPreviewService(final CMSPreviewService cmsPreviewService)
	{
		this.cmsPreviewService = cmsPreviewService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	@Required
	public void setCmsPageService(final CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
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

	// -------------

	protected CmsPageRequestContextData lookupCmsPageRequestContextData(final ServletRequest request)
	{
		return SpringHelper.getSpringBean(request, "cmsPageRequestContextData", CmsPageRequestContextData.class, true);
	}

	// -------------

	@Override
	public CmsPageRequestContextData initialiseCmsPageContextForRequest(final ServletRequest request)
	{
		validateParameterNotNull(request, "Parameter request must not be null");

		final CmsPageRequestContextData contextData = lookupCmsPageRequestContextData(request);

		final PreviewDataModel previewDataModel = getPreviewDataModel(request);
		contextData.setPreviewData(previewDataModel);
		contextData.setPreview(previewDataModel != null);
		contextData.setLiveEdit(previewDataModel != null && Boolean.TRUE.equals(previewDataModel.getLiveEdit()));

		contextData.setSessionId(getSessionService().getCurrentSession().getSessionId());

		return contextData;
	}

	@Override
	public CmsPageRequestContextData updateCmsPageContextForPage(final ServletRequest request, final AbstractPageModel page,
			final RestrictionData restrictionData)
	{
		validateParameterNotNull(request, "Parameter request must not be null");
		validateParameterNotNull(restrictionData, "Parameter restrictionData must not be null");

		final CmsPageRequestContextData contextData = lookupCmsPageRequestContextData(request);

		contextData.setPage(page);
		contextData.setRestrictionData(restrictionData);

		if (page != null)
		{
			contextData.setPositionToSlot((Map) getPositionsToContentSlots(page));
		}

		contextData.setUser(getUserService().getCurrentUser());

		return contextData;
	}

	@Override
	public CmsPageRequestContextData getCmsPageRequestContextData(final ServletRequest request)
	{
		validateParameterNotNull(request, "Parameter request must not be null");
		return lookupCmsPageRequestContextData(request);
	}


	protected PreviewDataModel getPreviewDataModel(final ServletRequest request)
	{
		final String previewTicketId = getPreviewTicketId(request);
		if (StringUtils.isNotBlank(previewTicketId))
		{
			final CMSPreviewTicketModel previewTicket = getCmsPreviewService().getPreviewTicket(previewTicketId);
			if (previewTicket != null)
			{
				return previewTicket.getPreviewData();
			}
		}
		return null;
	}

	/**
	 * Retrieves {@link CMSFilter#PREVIEW_TICKET_ID_PARAM} from current request
	 * 
	 * @param request
	 *           current request
	 * @return current ticket id
	 */
	protected String getPreviewTicketId(final ServletRequest request)
	{
		String id = request.getParameter(CMSFilter.PREVIEW_TICKET_ID_PARAM);
		if (StringUtils.isBlank(id))
		{
			id = getSessionService().getAttribute(CMSFilter.PREVIEW_TICKET_ID_PARAM);
		}
		return id;
	}

	/**
	 * Retrieve all content slots for the page and return them in a map
	 * 
	 * @param page
	 *           the page
	 * @return map with content slots assigned to positions
	 */
	protected Map<String, ContentSlotData> getPositionsToContentSlots(final AbstractPageModel page)
	{
		validateParameterNotNull(page, "Parameter page must not be null");

		final Collection<ContentSlotData> slotModels = getCmsPageService().getContentSlotsForPage(page);

		final Map<String, ContentSlotData> slots = new HashMap<String, ContentSlotData>(slotModels.size());
		for (final ContentSlotData contentSlot : slotModels)
		{
			slots.put(contentSlot.getPosition(), contentSlot);
		}

		return slots;
	}
}
