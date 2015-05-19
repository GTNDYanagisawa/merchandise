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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.populator;

import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.ProductConfigurationService;


/**
 * Populates SAP specific attributes we need for the back end downtime scenario
 *
 */
public class DefaultStandardOrderEntryPopulator extends OrderEntryPopulator
{
	private ProductConfigurationService productConfigurationService;

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		super.populate(source, target);
		populateCFGAttributes(source, target);
	}

	/**
	 * Populates configurable attribute and handle from the hybris persistence key
	 *
	 * @param source
	 *           Model
	 * @param target
	 *           DAO for cart entry
	 */
	void populateCFGAttributes(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		final String externalConfiguration = source.getExternalConfiguration();
		if (externalConfiguration != null)
		{
			final boolean configurable = !externalConfiguration.isEmpty();
			target.setConfigurable(configurable);
			if (configurable && (!isConfigurationSessionAvailable(source.getPk().toString())))
			{
				final ConfigModel configModel = productConfigurationService.getConfigModel(source.getProduct().getCode(),
						source.getExternalConfiguration());
				productConfigurationService.setIntoSession(source.getPk().toString(), configModel.getId());
			}
		}
		final PK pk = source.getPk();
		if (pk != null)
		{
			target.setHandle(pk.toString());
		}
	}


	/**
	 * @param configurationContainer
	 */
	public void setProductConfigurationService(final ProductConfigurationService configurationContainer)
	{
		this.productConfigurationService = configurationContainer;

	}

	/**
	 * @return Product Configuration Service
	 */
	public Object getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * Returns true if a item with the itemKey has is found in the session
	 *
	 * @param itemKey
	 * @return true if the itemKey is found in the session
	 */
	protected boolean isConfigurationSessionAvailable(final String itemKey)
	{
		return productConfigurationService.isInSession(itemKey);
	}

}
