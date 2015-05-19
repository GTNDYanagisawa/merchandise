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
package de.hybris.platform.acceleratorcms.context;

import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import javax.servlet.http.HttpServletRequest;


/**
 * Interface for context information loader
 */
public interface ContextInformationLoader
{
	CMSSiteModel initializeSiteFromRequest(final String absoluteURL);

	void initializePreviewRequest(final PreviewDataModel previewDataModel);

	void setCatalogVersions();

	void loadFakeContextInformation(final HttpServletRequest httpRequest, final PreviewDataModel previewData);

	void storePreviewData(final PreviewDataModel previewData);

}
