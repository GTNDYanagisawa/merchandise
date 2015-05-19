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
package de.hybris.platform.addonsupport.renderer.impl;

import de.hybris.platform.acceleratorcms.component.renderer.impl.DefaultCMSComponentRendererRegistry;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Extends the storefront CMS Component Registry so AddOns can easily declare additional CMS Component Renderer Mappings
 * by registering existing beans of type {@link AddOnCMSComponentRendererMapping}
 * 
 * <bean id="informationBannerComponentRendererMapping" parent="addonCmsComponentRendererMapping" > <property
 * name="typeCode" value="InformationBannerComponent" /> <property name="renderer"
 * ref="informationBannerCMSComponentRenderer" /> </bean>
 * 
 */
public class AddOnCMSComponentRendererRegistry extends DefaultCMSComponentRendererRegistry implements InitializingBean,
		ApplicationContextAware
{

	protected ApplicationContext applicationContext;
	private boolean failOnInvalidRendererMappings = false;

	private static final Logger LOG = Logger.getLogger(AddOnCMSComponentRendererRegistry.class);

	@Override
	public void afterPropertiesSet() throws Exception
	{
		final boolean validate = Registry.getCurrentTenantNoFallback().getJaloConnection().isSystemInitialized();
		if (!validate)
		{
			LOG.warn("not validating renderer mappings as System is not Initialised");
		}

		final Map<String, AddOnCMSComponentRendererMapping> mappings = this.applicationContext
				.getBeansOfType(AddOnCMSComponentRendererMapping.class);
		if (mappings != null && mappings.size() > 0)
		{
			for (final Entry<String, AddOnCMSComponentRendererMapping> entry : mappings.entrySet())
			{
				final AddOnCMSComponentRendererMapping mapping = entry.getValue();
				if (validate)
				{
					validateTypeCode(mapping.getTypeCode());
				}
				super.getRenderers().put(mapping.getTypeCode(), mapping.getRenderer());

			}
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	protected void validateTypeCode(final String code)
	{

		try
		{
			final ComposedTypeModel type = getTypeService().getComposedTypeForCode(code);
			if (!(AbstractCMSComponentModel.class.isAssignableFrom(getTypeService().getModelClass(type))))
			{
				if (isFailOnInvalidRendererMappings())
				{
					throw new IllegalStateException("[" + code + "] is not a subclass of AbstractCMSComponentModel");
				}
				else
				{
					LOG.warn("[" + code + "] is not a subclass of AbstractCMSComponentModel");
				}
			}
		}
		catch (final UnknownIdentifierException e)
		{
			if (isFailOnInvalidRendererMappings())
			{
				throw e;
			}
			else
			{
				LOG.warn("[" + code + "] is not a known CMS Component type. Perhaps you need to perform a System Update?");
			}
		}

	}

	public boolean isFailOnInvalidRendererMappings()
	{
		return failOnInvalidRendererMappings;
	}

	public void setFailOnInvalidRendererMappings(final boolean failOnInvalidRendererMappings)
	{
		this.failOnInvalidRendererMappings = failOnInvalidRendererMappings;
	}

}
