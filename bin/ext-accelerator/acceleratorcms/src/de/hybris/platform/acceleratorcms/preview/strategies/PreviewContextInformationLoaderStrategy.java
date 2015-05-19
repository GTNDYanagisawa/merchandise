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
package de.hybris.platform.acceleratorcms.preview.strategies;

import de.hybris.platform.cms2.model.preview.PreviewDataModel;



public interface PreviewContextInformationLoaderStrategy
{
	void initContextFromPreview(PreviewDataModel preview);
}
