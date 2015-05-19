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
package de.hybris.platform.acceleratorservices.cronjob;

import de.hybris.platform.acceleratorservices.model.CartRemovalCronJobModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.CommerceCartDao;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;


/**
 * A Cron Job to clean up carts.
 */
public class CartRemovalJob extends AbstractJobPerformable<CartRemovalCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(CartRemovalJob.class);

	private CommerceCartDao commerceCartDao;
	private TimeService timeService;
	private UserService userService;

	private static final int DEFAULT_CART_MAX_AGE = 2419200;
	private static final int DEFAULT_ANONYMOUS_CART_MAX_AGE = 1209600;

	@Override
	public PerformResult perform(final CartRemovalCronJobModel job)
	{
		try
		{
			for (final BaseSiteModel site : job.getSites())
			{
				int age = DEFAULT_CART_MAX_AGE;

				if (site.getCartRemovalAge() != null)
				{
					age = site.getCartRemovalAge().intValue();
				}
				for (final CartModel oldCart : getCommerceCartDao().getCartsForRemovalForSiteAndUser(
						new DateTime(getTimeService().getCurrentTime()).minusSeconds(age).toDate(), site, null))
				{
					getModelService().remove(oldCart);
				}

				age = DEFAULT_ANONYMOUS_CART_MAX_AGE;

				if (site.getAnonymousCartRemovalAge() != null)
				{
					age = site.getAnonymousCartRemovalAge().intValue();
				}

				for (final CartModel oldCart : getCommerceCartDao().getCartsForRemovalForSiteAndUser(
						new DateTime(getTimeService().getCurrentTime()).minusSeconds(age).toDate(), site,
						getUserService().getAnonymousUser()))
				{
					getModelService().remove(oldCart);
				}
			}

			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during cart cleanup", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	protected CommerceCartDao getCommerceCartDao()
	{
		return commerceCartDao;
	}

	@Required
	public void setCommerceCartDao(final CommerceCartDao commerceCartDao)
	{
		this.commerceCartDao = commerceCartDao;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
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
}
