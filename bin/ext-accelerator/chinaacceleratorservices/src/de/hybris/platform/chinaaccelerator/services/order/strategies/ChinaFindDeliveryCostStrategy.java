/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-$year hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.chinaaccelerator.services.order.strategies;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.chinaaccelerator.services.order.daos.ChinaZoneDeliveryModeValueDao;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.order.strategies.calculation.impl.DefaultFindDeliveryCostStrategy;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.PriceValue;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ChinaFindDeliveryCostStrategy extends DefaultFindDeliveryCostStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultFindDeliveryCostStrategy.class);
	private ChinaZoneDeliveryModeValueDao chinaZoneDeliveryModeValueDao;

	@Override
	public PriceValue getDeliveryCost(final AbstractOrderModel order)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);
		
		// Fallback to super class in order to fix JUnit test from core accelerator
		if(null == order.getStore()){
			return super.getDeliveryCost(order);
		}
		
		try
		{
			getModelService().save(order);
			final AddressModel address = order.getDeliveryAddress();
			validateParameterNotNull(address, "Delivery address is null for order " + order.getCode());
			final DistrictModel district = address.getCityDistrict();
			validateParameterNotNull(district, "Delivery district is null for order " + order.getCode());
			final DeliveryModeModel dMode = order.getDeliveryMode();
			final Collection<ZoneDeliveryModeValueModel> values = getChinaZoneDeliveryModeValueDao().findDeliveryValues(dMode,
					district);

			if (values.isEmpty())
			{
				throw new JaloDeliveryModeException("no delivery price defined for mode " + dMode + " and district " + district, 0);
			}

			final ZoneDeliveryModeValueModel bestMatch = values.iterator().next();
			return new PriceValue(order.getCurrency().getIsocode(), bestMatch.getValue(), order.getNet());
		}
		catch (final Exception e)
		{
			LOG.warn("Could not find deliveryCost for order [" + order.getCode() + "] due to : " + e.getMessage() + "... skipping!");
			return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet());
		}
	}


	public ChinaZoneDeliveryModeValueDao getChinaZoneDeliveryModeValueDao()
	{
		return chinaZoneDeliveryModeValueDao;
	}

	@Required
	public void setChinaZoneDeliveryModeValueDao(final ChinaZoneDeliveryModeValueDao chinaZoneDeliveryModeValueDao)
	{
		this.chinaZoneDeliveryModeValueDao = chinaZoneDeliveryModeValueDao;
	}
}
