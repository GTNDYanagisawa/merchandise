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
package de.hybris.platform.atddengine.templates;

import java.io.Writer;
import java.util.Map;


public interface TemplateProcessor
{
	void processTemplate(final Writer writer, final String templatePath, final Map<String, Object> binding);
}
