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
package de.hybris.platform.b2bacceleratorservices.orderscheduling.impl;

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.orderscheduling.impl.DefaultScheduleOrderServiceImpl;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.cronjob.TriggerService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class B2BAcceleratorScheduleOrderService extends DefaultScheduleOrderServiceImpl
{
	private String cartToOrderJobBeanId;
	private CartService cartService;
	private TypeService typeService;
	private KeyGenerator keyGenerator;
	private TriggerService triggerService;
	private I18NService i18NService;

	/*
	 * Override in order to inject a custom job.
	 */
	@Override
	public CartToOrderCronJobModel createOrderFromCartCronJob(final CartModel cart, final AddressModel deliveryAddress,
			final AddressModel paymentAddress, final PaymentInfoModel paymentInfo, final List<TriggerModel> triggers)
	{
		final CartToOrderCronJobModel cartToOrderCronJob = this.getModelService().create(CartToOrderCronJobModel.class);
		cartToOrderCronJob.setCart(cloneCart(cart,deliveryAddress,paymentAddress,paymentInfo));
		cartToOrderCronJob.setDeliveryAddress(deliveryAddress);
		cartToOrderCronJob.setPaymentAddress(paymentAddress);
		cartToOrderCronJob.setPaymentInfo(paymentInfo);
		cartToOrderCronJob.setJob(this.getCronJobService().getJob(getCartToOrderJobBeanId()));
		setCronJobToTrigger(cartToOrderCronJob, triggers);
		this.getModelService().save(cartToOrderCronJob);
		return cartToOrderCronJob;
	}

	protected CartModel cloneCart(final CartModel cart, final AddressModel deliveryAddress, final AddressModel paymentAddress,
			final PaymentInfoModel paymentInfo)
	{
		final CartModel clone = getCartService().clone(getTypeService().getComposedTypeForClass(CartModel.class),
				getTypeService().getComposedTypeForClass(CartEntryModel.class), cart, getKeyGenerator().generate().toString());
		clone.setPaymentAddress(paymentAddress);
		clone.setDeliveryAddress(deliveryAddress);
		clone.setPaymentInfo(paymentInfo);
		clone.setUser(cart.getUser());
		return clone;
	}

	protected void setCronJobToTrigger(final CronJobModel cronJob, final List<TriggerModel> triggers)
	{
		for (final TriggerModel trigger : triggers)
		{
			trigger.setCronJob(cronJob);
		}
		cronJob.setTriggers(triggers);
	}

	protected String getCartToOrderJobBeanId()
	{
		return cartToOrderJobBeanId;
	}

	@Required
	public void setCartToOrderJobBeanId(final String cartToOrderJobBeanId)
	{
		this.cartToOrderJobBeanId = cartToOrderJobBeanId;
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

	protected KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	protected TriggerService getTriggerService()
	{
		return triggerService;
	}

	@Required
	public void setTriggerService(final TriggerService triggerService)
	{
		this.triggerService = triggerService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}
}
