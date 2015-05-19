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
package de.hybris.platform.yb2bacceleratorfacades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Email context (velocity) for order replenishment.
 */
public class OrderReplenishmentEmailContext extends AbstractEmailContext<ReplenishmentProcessModel>
{
	private Converter<CartToOrderCronJobModel, ScheduledCartData> scheduledCartConverter;
	private ScheduledCartData scheduledCartData;

	@Override
	public void init(final ReplenishmentProcessModel replenishmentProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(replenishmentProcessModel, emailPageModel);
		scheduledCartData = getScheduledCartConverter().convert(replenishmentProcessModel.getCartToOrderCronJob());
	}

	@Override
	protected BaseSiteModel getSite(final ReplenishmentProcessModel replenishmentProcessModel)
	{
		return replenishmentProcessModel.getCartToOrderCronJob().getCart().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final ReplenishmentProcessModel replenishmentProcessModel)
	{
		return (CustomerModel) replenishmentProcessModel.getCartToOrderCronJob().getCart().getUser();
	}

	public ScheduledCartData getScheduledCartData()
	{
		return scheduledCartData;
	}

	protected Converter<CartToOrderCronJobModel, ScheduledCartData> getScheduledCartConverter()
	{
		return scheduledCartConverter;
	}

	@Required
	public void setScheduledCartConverter(final Converter<CartToOrderCronJobModel, ScheduledCartData> scheduledCartConverter)
	{
		this.scheduledCartConverter = scheduledCartConverter;
	}

	@Override
	protected LanguageModel getEmailLanguage(final ReplenishmentProcessModel replenishmentProcessModel)
	{
		return replenishmentProcessModel.getCartToOrderCronJob().getCart().getUser().getSessionLanguage();
	}
}
