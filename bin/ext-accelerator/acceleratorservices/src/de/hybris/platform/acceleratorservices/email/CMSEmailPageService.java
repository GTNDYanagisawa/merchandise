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
package de.hybris.platform.acceleratorservices.email;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;


/**
 * Service for looking up the CMS email page for a template name.
 */
public interface CMSEmailPageService
{
	/**
	 * Retrieves EmailPage given its frontend template name.
	 * 
	 * @param frontendTemplateName
	 *           Frontend template name
	 * @param catalogVersion
	 *           Catalog version
	 * @return EmailPage object if found, null otherwise
	 */
	EmailPageModel getEmailPageForFrontendTemplate(String frontendTemplateName, CatalogVersionModel catalogVersion);
}
