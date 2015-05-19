/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.atddengine.templates.impl;

import de.hybris.platform.atddengine.templates.TemplateProcessor;
import de.hybris.platform.atddengine.templates.TemplateProcessorFactory;


public class VelocityTemplateProcessorFactory implements TemplateProcessorFactory
{
	@Override
	public TemplateProcessor createTemplateProcessor()
	{
		return new VelocityTemplateProcessor();
	}
}
