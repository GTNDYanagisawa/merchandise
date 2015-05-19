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
package de.hybris.platform.acceleratorcms.component.container.impl;

import de.hybris.platform.acceleratorcms.component.container.CMSComponentContainerStrategy;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

/**
 */
public class DefaultCMSComponentContainerStrategyRegistry implements CMSComponentContainerStrategy
{
	private TypeService typeService;
	private Map<String, CMSComponentContainerStrategy> strategies;
	private CMSComponentContainerStrategy defaultCMSComponentContainerStrategy;

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected Map<String, CMSComponentContainerStrategy> getStrategies()
	{
		return strategies;
	}

	@Required
	public void setStrategies(final Map<String, CMSComponentContainerStrategy> strategies)
	{
		this.strategies = strategies;
	}

	protected CMSComponentContainerStrategy getDefaultCMSComponentContainerStrategy()
	{
		return defaultCMSComponentContainerStrategy;
	}

	@Required
	public void setDefaultCMSComponentContainerStrategy(final CMSComponentContainerStrategy defaultCMSComponentContainerStrategy)
	{
		this.defaultCMSComponentContainerStrategy = defaultCMSComponentContainerStrategy;
	}

	@Override
	public List<AbstractCMSComponentModel> getDisplayComponentsForContainer(final AbstractCMSComponentContainerModel container)
	{
		final String typeCode = getTypeService().getComposedTypeForClass(container.getClass()).getCode();
		final Map<String, CMSComponentContainerStrategy> map = getStrategies();
		if (map != null && map.containsKey(typeCode))
		{
			return map.get(typeCode).getDisplayComponentsForContainer(container);
		}

		return getDefaultCMSComponentContainerStrategy().getDisplayComponentsForContainer(container);
	}
}
