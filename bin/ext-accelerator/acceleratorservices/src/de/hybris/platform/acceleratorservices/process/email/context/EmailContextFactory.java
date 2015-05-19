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
package de.hybris.platform.acceleratorservices.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Interface used to create the velocity context for rendering emails.
 */
public interface EmailContextFactory<T extends BusinessProcessModel>
{
	/**
	 * Create the velocity context for rendering an email. A {@link RendererTemplateModel} is passed in that may contain
	 * the reference to one or more properties files for generating context data for the subject and body sections of the
	 * email.
	 * 
	 * @param businessProcessModel
	 *           the process model
	 * @param emailPageModel
	 *           the CMS email page
	 * @param renderTemplate
	 *           the renderer template
	 * @return the velocity context
	 */
	AbstractEmailContext<T> create(T businessProcessModel, EmailPageModel emailPageModel, RendererTemplateModel renderTemplate);
}
