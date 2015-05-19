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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action for authorizing payments.
 */
public class AuthorizePaymentAction extends AbstractSimpleDecisionAction<ReplenishmentProcessModel>
{
	private CommerceCheckoutService commerceCheckoutService;
	private ImpersonationService impersonationService;

	@Override
	public Transition executeAction(final ReplenishmentProcessModel process) throws Exception
	{
		final BusinessProcessParameterModel clonedCartParameter = processParameterHelper.getProcessParameterByName(process, "cart");
		final CartModel clonedCart = (CartModel) clonedCartParameter.getValue();
		getModelService().refresh(clonedCart);

		final ImpersonationContext context = new ImpersonationContext();
		context.setOrder(clonedCart);
		return getImpersonationService().executeInContext(context,
				new ImpersonationService.Executor<Transition, ImpersonationService.Nothing>()
				{
					@Override
					public Transition execute()
					{
						if (clonedCart.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
						{
							final PaymentTransactionEntryModel paymentTransactionEntryModel = getCommerceCheckoutService()
									.authorizePayment(clonedCart, null, getCommerceCheckoutService().getPaymentProvider());
							if (paymentTransactionEntryModel == null
									|| !TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus()))
							{
								clonedCart.setStatus(OrderStatus.B2B_PROCESSING_ERROR);
								modelService.save(clonedCart);
								return Transition.NOK;
							}
						}
						return Transition.OK;
					}
				});
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
