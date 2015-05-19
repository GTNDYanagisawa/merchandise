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
 */
package de.hybris.platform.acceleratorwebservicesaddon.controllers.v2;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceDataList;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceListWsDTO;
import de.hybris.platform.commercewebservicescommons.mapping.DataMapper;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSetLevelHelper;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@ApiVersion("v2")
public class ExtendedCartsController
{
	private final static Logger LOG = Logger.getLogger(ExtendedCartsController.class);
	@Resource
	private AcceleratorCheckoutFacade acceleratorCheckoutFacade;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;


	/**
	 * Web service handler for getting consolidated pickup options<br>
	 * Request Method = <code>GET</code>
	 * 
	 * @return {@link PointOfServiceListWsDTO} as response body
	 */
	@RequestMapping(value = "/{cartId}/consolidate", method = RequestMethod.GET)
	@ResponseBody
	public PointOfServiceListWsDTO getConsolidatedPickupOptions(
			@RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final PointOfServiceDataList pointOfServices = new PointOfServiceDataList();
		pointOfServices.setPointOfServices(acceleratorCheckoutFacade.getConsolidatedPickupOptions());
		return dataMapper.map(pointOfServices, PointOfServiceListWsDTO.class, fields);
	}

	/**
	 * Web service handler for consolidating pickup locations<br>
	 * Request Method = <code>POST</code>
	 * 
	 * @param storeName
	 *           - name of store where items will be picked
	 * @return {@link CartModificationListWsDTO} as response body
	 */
	@RequestMapping(value = "/{cartId}/consolidate", method = RequestMethod.POST)
	@ResponseBody
	public CartModificationListWsDTO consolidatePickupLocations(@RequestParam(required = true) final String storeName,
			@RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
			throws CommerceCartModificationException
	{
		final CartModificationDataList modifications = new CartModificationDataList();
		modifications.setCartModificationList(acceleratorCheckoutFacade.consolidateCheckoutCart(storeName));
		final CartModificationListWsDTO result = dataMapper.map(modifications, CartModificationListWsDTO.class, fields);
		return result;
	}
}
