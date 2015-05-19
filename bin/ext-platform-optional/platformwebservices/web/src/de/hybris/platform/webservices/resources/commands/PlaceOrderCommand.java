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
package de.hybris.platform.webservices.resources.commands;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservices.AbstractCommand;


/**
 * the Command for placing new order
 */
public class PlaceOrderCommand extends AbstractCommand<CartModel>
{
	private static final Logger LOG = Logger.getLogger(PlaceOrderCommand.class);

	@Override
	public OrderModel execute(final CartModel resrcEntity, final Object reqEntity) throws Exception
	{
		final AddressModel deliveryAddressModel = resrcEntity.getDeliveryAddress();
		final AddressModel paymentAddressModel = resrcEntity.getPaymentAddress();
		final PaymentInfoModel paymentInfoModel = resrcEntity.getPaymentInfo();

		final OrderService orderService = getServiceLocator().getOrderService();
		final ModelService modelService = getServiceLocator().getModelService();

		final OrderModel order = orderService
				.placeOrder(resrcEntity, deliveryAddressModel, paymentAddressModel, paymentInfoModel);
		getResponse().status(Response.Status.CREATED);
		modelService.remove(resrcEntity);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Order (" + order.getPk() + ") successfully created");
		}

		return order;
	}

}
