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
package de.hybris.platform.acceleratorcms.component.slot;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;

import java.util.List;

/**
 * Service to support resolving Content Slots and Components in the context of the current page data.
 * Also extends the CMSComponentRenderer interface to support rendering of any AbstractCMSComponentModel.
 */
public interface CMSPageSlotComponentService extends CMSComponentRenderer<AbstractCMSComponentModel>
{
	/**
	 * Lookup a Content Slot given its ID.
	 *
	 * @param id the ID of the content slot
	 * @return the content slot
	 */
	ContentSlotModel getContentSlotForId(String id);

	/**
	 * Lookup a Component given its ID.
	 *
	 * @param id the ID of the component
	 * @return the component
	 */
	AbstractCMSComponentModel getComponentForId(String id);

	/**
	 * Lookup the Content Slot data on the current page in the specified position.
	 * The current page is looked up in the CmsPageRequestContextData.
	 *
	 * @param cmsPageRequestContextData The CMS context data for the current request
	 * @param position The position in the page
	 * @return the content slot data
	 */
	ContentSlotData getContentSlotForPosition(CmsPageRequestContextData cmsPageRequestContextData, String position);

	/**
	 * Get the CMS components in a Content Slot.
	 *
	 * @param cmsPageRequestContextData The CMS context data for the current request
	 * @param contentSlot The Content Slot
	 * @param evaluateRestrictions Flag set to <tt>true</tt> to indicate that restrictions should be evaluated on the components
	 * @param limit A limit on the number of components to return, set to <tt>-1</tt> for unlimited
	 * @return the list of components in the slot
	 */
	List<AbstractCMSComponentModel> getCMSComponentsForContentSlot(CmsPageRequestContextData cmsPageRequestContextData, ContentSlotModel contentSlot, boolean evaluateRestrictions, int limit);

	/**
	 * Get the CMS components for a CMS component.
	 * Some CMS components are actually containers of other components. Depending on the specific component the container
	 * may be replaced with some of the child components.
	 *
	 * @param cmsPageRequestContextData The CMS context data for the current request
	 * @param component The component
	 * @param evaluateRestrictions Flag set to <tt>true</tt> to indicate that restrictions should be evaluated on the components
	 * @param limit A limit on the number of components to return, set to <tt>-1</tt> for unlimited
	 * @return the list of components
	 */
	List<AbstractCMSComponentModel> getCMSComponentsForComponent(CmsPageRequestContextData cmsPageRequestContextData, AbstractCMSComponentModel component, boolean evaluateRestrictions, int limit);

	/**
	 * Test if the specified Component is visible in the context of the current page.
	 * Evaluates any restrictions applied to the CMS Component.
	 * The restrictions are evaluated in the context of the CmsPageRequestContextData.
	 *
	 * @param cmsPageRequestContextData The CMS context data for the current request
	 * @param component The component
	 * @param evaluateRestrictions Flag set to <tt>true</tt> to indicate that restrictions should be evaluated on the component
	 * @return <tt>true</tt> if the Component should be shown for this request
	 */
	boolean isComponentVisible(CmsPageRequestContextData cmsPageRequestContextData, AbstractCMSComponentModel component, boolean evaluateRestrictions);
}
