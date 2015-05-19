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
package de.hybris.platform.acceleratorservices.order.strategies.impl;

import de.hybris.platform.acceleratorservices.order.strategies.UncollectedConsignmentsStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Moves given consignment to the customer service to perform further actions.
 */
public class CustomerServiceUncollectedConsignmentStrategy implements UncollectedConsignmentsStrategy
{
	private static final Logger LOG = Logger.getLogger(CustomerServiceUncollectedConsignmentStrategy.class);

	private Integer timeThreshold;
	private BusinessProcessService businessProcessService;
	private ModelService modelService;

	@Override
	public boolean processConsignment(final ConsignmentModel consignmentModel)
	{
		if (consignmentModel != null)
		{
			final Date timeLimit = DateUtils.addHours(new Date(), 0 - getTimeThreshold().intValue());
			if (timeLimit.after(consignmentModel.getShippingDate()))
			{
				if (getBusinessProcessService().getProcess("moveConsignmentToCustomerServicesProcess-" + consignmentModel.getCode()) == null)
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("Process moveConsignmentToCustomerServicesProcess-" + consignmentModel.getCode() + " created.");
					}
					final ConsignmentProcessModel consignmentProcessModel = getBusinessProcessService().createProcess(
							"moveConsignmentToCustomerServicesProcess-" + consignmentModel.getCode(),
							"moveConsignmentToCustomerServicesProcess");
					if (consignmentProcessModel != null)
					{
						consignmentProcessModel.setConsignment(consignmentModel);
						getModelService().save(consignmentProcessModel);
						getBusinessProcessService().startProcess(consignmentProcessModel);
						return true;
					}
				}
			}
		}

		return false;
	}

	public Integer getTimeThreshold()
	{
		return timeThreshold;
	}

	@Required
	public void setTimeThreshold(final Integer timeThreshold)
	{
		this.timeThreshold = timeThreshold;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
