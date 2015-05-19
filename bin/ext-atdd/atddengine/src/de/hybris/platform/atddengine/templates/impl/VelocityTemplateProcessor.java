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

import java.io.Writer;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;


public class VelocityTemplateProcessor implements TemplateProcessor
{
	private final VelocityEngine velocityEngine;

	public VelocityTemplateProcessor()
	{
		velocityEngine = new VelocityEngine();
		initializeVelocityEninge();
	}

	private void initializeVelocityEninge()
	{
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	private VelocityContext initializeVelocityContext(final Map<String, Object> binding)
	{
		final VelocityContext velocytyContext = new VelocityContext();

		for (final Map.Entry<String, Object> entry : binding.entrySet())
		{
			velocytyContext.put(entry.getKey(), entry.getValue());
		}

		return velocytyContext;
	}

	@Override
	public void processTemplate(final Writer writer, final String templatPath, final Map<String, Object> binding)
	{
		final VelocityContext velocityContext = initializeVelocityContext(binding);

		final Template template = velocityEngine.getTemplate(templatPath);
		template.merge(velocityContext, writer);
	}
}
