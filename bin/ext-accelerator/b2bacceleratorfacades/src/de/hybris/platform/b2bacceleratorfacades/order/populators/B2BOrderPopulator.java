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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionResultData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link de.hybris.platform.commercefacades.order.data.OrderData} with {@link OrderModel}.
 */
public class B2BOrderPopulator implements Populator<OrderModel, OrderData>
{
	private static final Logger LOG = Logger.getLogger(B2BOrderPopulator.class);

	private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;
	private Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeConverter;
	private Converter<B2BCommentModel, B2BCommentData> b2BCommentConverter;
	private Converter<B2BPermissionResultModel, B2BPermissionResultData> b2BPermissionResultConverter;
	private Converter<UserModel, CustomerData> b2bCustomerConverter;
	private Converter<TriggerModel, TriggerData> triggerConverter;

	@Override
	public void populate(final OrderModel orderModel, final OrderData orderData) throws ConversionException
	{

		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if (entry.getCostCenter() != null)
			{
				orderData.setCostCenter(b2BCostCenterConverter.convert(entry.getCostCenter()));
				break;
			}
		}

		orderData.setPurchaseOrderNumber(orderModel.getPurchaseOrderNumber());
		orderData.setPaymentType(getB2bPaymentTypeConverter().convert(orderModel.getPaymentType()));
		if (CollectionUtils.isNotEmpty(orderModel.getB2bcomments()))
		{
			//Always get the latest comment
			orderData.setB2BComment(getB2BCommentConverter().convert(filterOutLastComment(orderModel)));
		}

		populatePermissionResults(orderModel, orderData);

		orderData.setB2bCustomerData(b2bCustomerConverter.convert(orderModel.getUser()));

		if (orderModel.getSchedulingCronJob() != null)
		{
			orderData.setJobCode(orderModel.getSchedulingCronJob().getCode());
			if (CollectionUtils.isNotEmpty(orderModel.getSchedulingCronJob().getTriggers()))
			{
				orderData.setTriggerData(getTriggerConverter().convert(
						orderModel.getSchedulingCronJob().getTriggers().iterator().next()));
			}
		}
		orderData.setQuoteExpirationDate(orderModel.getQuoteExpirationDate());
		orderData.setB2bCommentData(Converters.convertAll(orderModel.getB2bcomments(), getB2BCommentConverter()));
	}

	protected void populatePermissionResults(final OrderModel orderModel, final OrderData b2BOrderData)
	{
		try
		{
			final Collection<B2BPermissionResultModel> permissionResults = orderModel.getPermissionResults();
			b2BOrderData.setB2bPermissionResult(Converters.convertAll(permissionResults, getB2BPermissionResultConverter()));
		}
		catch (final Exception e)
		{
			// with certain raise conditions permission results can become stale causing an exception [object no longer valid]
			LOG.warn(String.format("Failed to look up permission results for order %s, error was: %s", orderModel.getCode(),
					e.getMessage()));
		}
	}

	protected B2BCommentModel filterOutLastComment(final OrderModel processedOrder)
	{
		final List<B2BCommentModel> b2bComments = new ArrayList<B2BCommentModel>(processedOrder.getB2bcomments());

		Collections.sort(b2bComments, new Comparator<B2BCommentModel>()
		{
			@Override
			public int compare(final B2BCommentModel comment1, final B2BCommentModel comment2)
			{
				return (comment1.getCreationtime().compareTo(comment2.getCreationtime()));
			}
		});

		final B2BCommentModel b2bCommentModel = b2bComments.get(b2bComments.size() - 1);
		return b2bCommentModel;
	}

	protected Converter<B2BCostCenterModel, B2BCostCenterData> getB2BCostCenterConverter()
	{
		return b2BCostCenterConverter;
	}

	@Required
	public void setB2BCostCenterConverter(final Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter)
	{
		this.b2BCostCenterConverter = b2BCostCenterConverter;
	}

	/**
	 * @return the b2bPaymentTypeConverter
	 */
	protected Converter<CheckoutPaymentType, B2BPaymentTypeData> getB2bPaymentTypeConverter()
	{
		return b2bPaymentTypeConverter;
	}

	/**
	 * @param b2bPaymentTypeConverter
	 *           the b2bPaymentTypeConverter to set
	 */
	@Required
	public void setB2bPaymentTypeConverter(final Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeConverter)
	{
		this.b2bPaymentTypeConverter = b2bPaymentTypeConverter;
	}


	protected Converter<B2BCommentModel, B2BCommentData> getB2BCommentConverter()
	{
		return b2BCommentConverter;
	}

	@Required
	public void setB2BCommentConverter(final Converter<B2BCommentModel, B2BCommentData> b2BCommentConverter)
	{
		this.b2BCommentConverter = b2BCommentConverter;
	}

	/**
	 * @return the b2BPermissionResultConverter
	 */
	protected Converter<B2BPermissionResultModel, B2BPermissionResultData> getB2BPermissionResultConverter()
	{
		return b2BPermissionResultConverter;
	}

	/**
	 * @param b2bPermissionResultConverter
	 *           the b2BPermissionResultConverter to set
	 */
	public void setB2BPermissionResultConverter(
			final Converter<B2BPermissionResultModel, B2BPermissionResultData> b2bPermissionResultConverter)
	{
		b2BPermissionResultConverter = b2bPermissionResultConverter;
	}

	protected Converter<UserModel, CustomerData> getB2bCustomerConverter()
	{
		return b2bCustomerConverter;
	}

	@Required
	public void setB2bCustomerConverter(final Converter<UserModel, CustomerData> b2bCustomerConverter)
	{
		this.b2bCustomerConverter = b2bCustomerConverter;
	}

	protected Converter<TriggerModel, TriggerData> getTriggerConverter()
	{
		return triggerConverter;
	}

	@Required
	public void setTriggerConverter(final Converter<TriggerModel, TriggerData> triggerConverter)
	{
		this.triggerConverter = triggerConverter;
	}
}
