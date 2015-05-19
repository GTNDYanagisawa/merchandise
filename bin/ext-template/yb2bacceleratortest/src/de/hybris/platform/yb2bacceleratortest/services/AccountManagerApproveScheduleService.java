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
package de.hybris.platform.yb2bacceleratortest.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.orderscheduling.model.OrderScheduleCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.yb2bacceleratortest.constants.YB2BAcceleratorTestConstants.POWERTOOLS_SITE;


public class AccountManagerApproveScheduleService
{
	protected static final Logger LOG = Logger.getLogger(AccountManagerApproveScheduleService.class);

	private ModelService modelService;

	private String accountManagerTestDataJobId;
	private CustomerAccountService customerAccountService;
	private BaseStoreSelectorStrategy baseStoreSelectorStrategy;
	private UserService userService;
	private CMSAdminSiteService cmsAdminSiteService;
	private ImpersonationService impersonationService;
	private CronJobService cronJobService;



	public OrderScheduleCronJobModel scheduleAccountManagerJobToApproveReject(final String orderCode, final String customerUid)
	{
		LOG.debug("Scheduling a job to approve or reject action for order " + orderCode);

		final B2BCustomerModel user = userService.getUserForUID(customerUid.toLowerCase(), B2BCustomerModel.class);
		final OrderModel orderModel = customerAccountService.getOrderForCode(user, orderCode, getCurrentBaseStore(user));

		final OrderScheduleCronJobModel orderScheduleCronJobModel = getModelService().create(OrderScheduleCronJobModel.class);
		orderScheduleCronJobModel.setOrder(orderModel);
		orderScheduleCronJobModel.setJob(getCronJobService().getJob(getAccountManagerTestDataJobId()));

		final TriggerModel triggerModel = getModelService().create(TriggerModel.class);
		triggerModel.setSecond(null);
		triggerModel.setMinute(null);
		triggerModel.setHour(null);
		triggerModel.setDay(null);
		triggerModel.setMonth(null);
		triggerModel.setYear(null);
		triggerModel.setDaysOfWeek(null);

		triggerModel.setActivationTime(DateUtils.addSeconds(new Date(), 30));
		triggerModel.setActive(Boolean.TRUE);
		triggerModel.setCronJob(orderScheduleCronJobModel);

		getModelService().save(triggerModel);
		orderScheduleCronJobModel.setTriggers(Collections.singletonList(triggerModel));

		this.getModelService().save(orderScheduleCronJobModel);
		LOG.debug("Created job " + orderScheduleCronJobModel.getCode());
		return orderScheduleCronJobModel;
	}


	protected BaseStoreModel getCurrentBaseStore(final UserModel user)
	{
		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(getCmsAdminSiteService().getSiteForId(POWERTOOLS_SITE));
		ctx.setUser(user);


		return (BaseStoreModel) getImpersonationService().executeInContext(ctx,
				new ImpersonationService.Executor<Object, ImpersonationService.Nothing>()
				{
					@Override
					public Object execute() throws ImpersonationService.Nothing
					{
						return getBaseStoreSelectorStrategy().getCurrentBaseStore();
					}
				});
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CronJobService getCronJobService()
	{
		return cronJobService;
	}

	@Required
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	public String getAccountManagerTestDataJobId()
	{
		return accountManagerTestDataJobId;
	}

	@Required
	public void setAccountManagerTestDataJobId(final String accountManagerTestDataJobId)
	{
		this.accountManagerTestDataJobId = accountManagerTestDataJobId;
	}

	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public BaseStoreSelectorStrategy getBaseStoreSelectorStrategy()
	{
		return baseStoreSelectorStrategy;
	}

	@Required
	public void setBaseStoreSelectorStrategy(final BaseStoreSelectorStrategy baseStoreSelectorStrategy)
	{
		this.baseStoreSelectorStrategy = baseStoreSelectorStrategy;
	}


	public CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}


	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}


	public ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}


	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}


}
