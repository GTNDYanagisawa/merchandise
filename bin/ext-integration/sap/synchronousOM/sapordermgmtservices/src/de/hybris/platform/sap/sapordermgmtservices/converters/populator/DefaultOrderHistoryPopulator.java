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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Default implementation for populating order history from a BOL search result.
 */
public class DefaultOrderHistoryPopulator implements Populator<SearchResult, OrderHistoryData>
{


	@Override
	public void populate(final SearchResult source, final OrderHistoryData target) throws ConversionException
	{
		target.setPlaced(source.getCreationDate());
		target.setCode(source.getKey().getIdAsString());
		target.setPurchaseOrderNumber(source.getPurchaseOrderNumber());

		populateOverallstatus(source, target);
		populateCondensedstatus(source, target);
	}

	/**
	 * Maps overall BOL status into hybris overall status, see {@link OrderHistoryData#setStatus(OrderStatus)}
	 * 
	 * @param source
	 *           BOL search result
	 * @param target
	 *           Order history data
	 */
	protected void populateOverallstatus(final SearchResult source, final OrderHistoryData target)
	{
		final String overallStatus = source.getOverallStatus();

		if (overallStatus != null && overallStatus.equals("C"))
		{
			target.setStatus(OrderStatus.COMPLETED);
		}
		else
		{
			target.setStatus(OrderStatus.CREATED);
			return;
		}

	}

	/**
	 * Determines the shipping status from the BOL representation of an order search result.
	 * 
	 * @param source
	 *           BOL search result
	 * @return Delivery status in hybris representation (which is shipping status in SAP terminology)
	 */
	protected DeliveryStatus getShippingstatus(final SearchResult source)
	{
		final String shippingStatus = source.getShippingStatus();

		if (shippingStatus == null)
		{
			return DeliveryStatus.NOTSHIPPED;
		}

		switch (shippingStatus)
		{
			case "C":
				return DeliveryStatus.SHIPPED;
			case "B":
				return DeliveryStatus.PARTSHIPPED;
			default:
				return DeliveryStatus.NOTSHIPPED;
		}
	}

	/**
	 * Compiles a condensed status from overall and delivery status, see
	 * {@link OrderHistoryData#setCondensedStatus(String)}. <br>
	 * In case the overall status is completed, condensed status will also be set to completed, otherwise condensed
	 * status will be equal to delivery status.
	 * 
	 * @param source
	 *           BOL search result
	 * @param target
	 *           Order history data
	 */
	protected void populateCondensedstatus(final SearchResult source, final OrderHistoryData target)
	{
		if (target.getStatus() == OrderStatus.CREATED)
		{
			final DeliveryStatus status = getShippingstatus(source);

			target.setCondensedStatus(status.getCode().toLowerCase());
			return;
		}
		else if (target.getStatus() == OrderStatus.COMPLETED)
		{
			target.setCondensedStatus(target.getStatus().getCode().toLowerCase());
			return;
		}
	}

}
