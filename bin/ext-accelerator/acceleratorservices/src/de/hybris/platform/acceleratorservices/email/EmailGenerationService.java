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
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Service for generating an email.
 */
public interface EmailGenerationService
{
	/**
	 * Generates EmailMessage give business process and cms email page.
	 * 
	 * @param businessProcessModel
	 *           Business process object
	 * @param emailPageModel
	 *           Email page
	 * @return EmailMessage
	 */
	EmailMessageModel generate(BusinessProcessModel businessProcessModel, EmailPageModel emailPageModel);
}
