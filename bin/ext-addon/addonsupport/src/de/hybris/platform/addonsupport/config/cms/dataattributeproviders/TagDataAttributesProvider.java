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
package de.hybris.platform.addonsupport.config.cms.dataattributeproviders;

import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.util.Map;



/**
 * @author mgolubovic
 * 
 */
public interface TagDataAttributesProvider
{
	Map<String, String> getAttributes(AbstractCMSComponentModel comContainer, AbstractCMSComponentModel currentComponent,
			CmsPageRequestContextData currentCmsPageRequestContextData);
}
