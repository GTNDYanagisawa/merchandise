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
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.tx.Transaction;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action for cart calculation
 */
public class CalculateCartAction extends AbstractProceduralAction<ReplenishmentProcessModel>
{
	private CommerceCartService commerceCartService;
	private ImpersonationService impersonationService;

	@Override
	public void executeAction(final ReplenishmentProcessModel process) throws Exception
	{
		final BusinessProcessParameterModel clonedCartParameter = processParameterHelper.getProcessParameterByName(process, "cart");
		final CartModel cart = (CartModel) clonedCartParameter.getValue();
		getModelService().refresh(cart);

		final ImpersonationContext context = new ImpersonationContext();
		context.setOrder(cart);
		getModelService().save(
				getImpersonationService().executeInContext(context,
						new ImpersonationService.Executor<CartModel, ImpersonationService.Nothing>()
						{
							@Override
							public CartModel execute()
							{
								Transaction.current().enableDelayedStore(false);
								getCommerceCartService().calculateCart(cart);
								return cart;
							}
						}));
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
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
