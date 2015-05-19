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
package de.hybris.platform.secureportaladdon.interceptors;

import static de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractPageController.CMS_PAGE_MODEL;
import static de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractPageController.CMS_PAGE_TITLE;
import static de.hybris.platform.yb2bacceleratorstorefront.interceptors.beforeview.CmsPageBeforeViewHandler.SLOTS_MODEL;

import de.hybris.platform.acceleratorservices.storefront.data.MetaElementData;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.secureportaladdon.constants.SecureportaladdonWebConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;


/**
 * 
 * Implementation of BeforeViewHandlerAdaptee.
 * 
 */
public class SecurePortalBeforeViewHandler implements BeforeViewHandlerAdaptee
{

	private static final String SIDE_CONTENT = "SideContent";
	private static final String ENABLE_REGISTRATION = "enableRegistration";
	private CMSSiteService cmsSiteService;
	private CMSPageService cmsPageService;
	private PageTitleResolver pageTitleResolver;
	private Map<String, Map<String, String>> spViewMap;

	public static final String VIEW_NAME_MAP_KEY = "viewName";
	public static final String CMS_PAGE_ID_MAP_KEY = "cmsPageId";

	@Required
	public void setSpViewMap(final Map<String, Map<String, String>> spViewMap)
	{
		this.spViewMap = spViewMap;
	}

	@Required
	public void setPageTitleResolver(final PageTitleResolver pageTitleResolver)
	{
		this.pageTitleResolver = pageTitleResolver;
	}

	@Required
	public void setCmsPageService(final CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	private boolean isSiteSecured()
	{
		final CMSSiteModel site = cmsSiteService.getCurrentSite();
		return site.isRequiresAuthentication();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hybris.addon.common.interceptors.BeforeViewHandlerAdaptee#beforeView(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, org.springframework.ui.ModelMap, java.lang.String)
	 */
	/** Adds model flag to switch registration link in the Login View */
	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			String viewName) throws Exception
	{

		model.addAttribute(ENABLE_REGISTRATION, cmsSiteService.getCurrentSite().isEnableRegistration());

		if (isSiteSecured() && spViewMap.containsKey(viewName))
		{

			model.remove(CMS_PAGE_MODEL);

			final ContentPageModel scpPageModel = cmsPageService.getPageForLabelOrId(spViewMap.get(viewName)
					.get(CMS_PAGE_ID_MAP_KEY));
			model.addAttribute(CMS_PAGE_MODEL, scpPageModel);
			model.addAttribute(CMS_PAGE_TITLE, pageTitleResolver.resolveContentPageTitle(scpPageModel.getTitle()));
			replaceModelMetaData(model, scpPageModel.getKeywords(), scpPageModel.getDescription());
			replaceSideContentSlotComponents(scpPageModel, model);

			viewName = SecureportaladdonWebConstants.VIEW_PAGE_PREFIX + spViewMap.get(viewName).get(VIEW_NAME_MAP_KEY);
		}

		return viewName;
	}

	/**
	 * @param scpPageModel
	 */
	private void replaceSideContentSlotComponents(final ContentPageModel contentPageModel, final ModelMap model)
	{
		final HashMap<String, ContentSlotModel> slots = (HashMap<String, ContentSlotModel>) model.get(SLOTS_MODEL);
		if (slots != null && slots.get(SIDE_CONTENT) != null)
		{
			slots.get(SIDE_CONTENT).setCmsComponents(
					getContentSlotsForPageAsMap(contentPageModel).get(SIDE_CONTENT).getCmsComponents());
		}

	}

	private Map<String, ContentSlotModel> getContentSlotsForPageAsMap(final AbstractPageModel page)
	{
		if (page == null)
		{
			return Collections.emptyMap();
		}

		final Collection<ContentSlotData> slotModels = cmsPageService.getContentSlotsForPage(page);

		final HashMap<String, ContentSlotModel> slots = new HashMap<String, ContentSlotModel>(slotModels.size());
		for (final ContentSlotData contentSlot : slotModels)
		{
			slots.put(contentSlot.getPosition(), contentSlot.getContentSlot());
		}

		return slots;
	}

	private void replaceModelMetaData(final ModelMap model, final String metaKeywords, final String metaDescription)
	{
		final List<MetaElementData> metadata = new LinkedList<MetaElementData>();
		metadata.add(createMetaElement("keywords", metaKeywords));
		metadata.add(createMetaElement("description", metaDescription));
		model.remove("metatags");
		model.addAttribute("metatags", metadata);
	}

	private MetaElementData createMetaElement(final String name, final String content)
	{
		final MetaElementData element = new MetaElementData();
		element.setName(name);
		element.setContent(content);
		return element;
	}
}
