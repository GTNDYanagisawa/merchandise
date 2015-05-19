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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action for cloning carts.
 */
public class CloneCartAction extends AbstractProceduralAction<ReplenishmentProcessModel>
{
	private CartService cartService;
	private TypeService typeService;
	private UserService userService;
	private KeyGenerator guidKeyGenerator;

	@Override
	public void executeAction(final ReplenishmentProcessModel process) throws Exception
	{
		final CartToOrderCronJobModel cartToOrderCronJob = process.getCartToOrderCronJob();
		final CartModel cronJobCart = cartToOrderCronJob.getCart();
		getUserService().setCurrentUser(cronJobCart.getUser());
		final CartModel clone = getCartService().clone(getTypeService().getComposedTypeForClass(CartModel.class),
				getTypeService().getComposedTypeForClass(CartEntryModel.class), cronJobCart,
				getGuidKeyGenerator().generate().toString());
		clone.setPaymentAddress(cartToOrderCronJob.getPaymentAddress());
		clone.setDeliveryAddress(cartToOrderCronJob.getDeliveryAddress());
		clone.setPaymentInfo(cartToOrderCronJob.getPaymentInfo());
		clone.setStatus(OrderStatus.CREATED);
		clone.setAllPromotionResults(Collections.EMPTY_SET);
		clone.setPaymentTransactions(Collections.EMPTY_LIST);
		clone.setPermissionResults(Collections.EMPTY_LIST);
		clone.setGuid(getGuidKeyGenerator().generate().toString());
		this.modelService.save(clone);
		processParameterHelper.setProcessParameter(process, "cart", clone);
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected KeyGenerator getGuidKeyGenerator()
	{
		return guidKeyGenerator;
	}

	@Required
	public void setGuidKeyGenerator(final KeyGenerator guidKeyGenerator)
	{
		this.guidKeyGenerator = guidKeyGenerator;
	}
}
