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
package de.hybris.platform.yb2bacceleratorcore.actions.replenishment;

import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.tx.Transaction;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action for placing orders.
 */
public class PlaceOrderAction extends AbstractProceduralAction<ReplenishmentProcessModel>
{
	private CommerceCheckoutService commerceCheckoutService;
	private ImpersonationService impersonationService;


	@Override
	public void executeAction(final ReplenishmentProcessModel process) throws Exception
	{
		final BusinessProcessParameterModel clonedCartParameter = processParameterHelper.getProcessParameterByName(process, "cart");
		final CartModel cart = (CartModel) clonedCartParameter.getValue();
		this.modelService.refresh(cart);

		final ImpersonationContext context = new ImpersonationContext();
		context.setOrder(cart);
		context.setLanguage(cart.getUser().getSessionLanguage());
		final OrderModel orderModel = getImpersonationService().executeInContext(context,
				new ImpersonationService.Executor<OrderModel, ImpersonationService.Nothing>()
				{
					@Override
					public OrderModel execute()
					{
						final OrderModel orderModel;
						try
						{
							Transaction.current().enableDelayedStore(false);
							orderModel = getCommerceCheckoutService().placeOrder(cart);
						}
						catch (final InvalidCartException e)
						{
							throw new RuntimeException(e.getMessage(), e);
						}
						orderModel.setSchedulingCronJob(process.getCartToOrderCronJob());
						return orderModel;
					}
				});
		getModelService().save(orderModel);
		getProcessParameterHelper().setProcessParameter(process, "order", orderModel);
	}

	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}
}
