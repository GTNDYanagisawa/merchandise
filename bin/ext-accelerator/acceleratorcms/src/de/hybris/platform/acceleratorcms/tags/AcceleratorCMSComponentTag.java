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
package de.hybris.platform.acceleratorcms.tags;

import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2lib.cmstags.CMSComponentTag;

import javax.servlet.http.HttpServletRequest;


/**
 * This CMSComponentTag does not regenerate the RestrictionData for each CMSComponent on the page, instead relies on
 * {@link AcceleratorCMSBodyTag} to populate it into the "restrictionData" request attribute.
 *
 * @deprecated The AcceleratorCMSComponentTag is deprecated. Instead of using this tag use both the
 * {@link de.hybris.platform.acceleratorcms.tags2.CMSContentSlotTag} and
 * {@link de.hybris.platform.acceleratorcms.tags2.CMSComponentTag} tags.
 */
@Deprecated
public class AcceleratorCMSComponentTag extends CMSComponentTag
{
	@Override
	protected RestrictionData populate(final HttpServletRequest request, final CMSDataFactory cmsDataFactory)
	{
		return (RestrictionData) request.getAttribute("restrictionData");
	}

	@Override
	protected void initLiveEditJS(final HttpServletRequest request)
	{
		// Do nothing as this is now done in the body tag
	}
}
