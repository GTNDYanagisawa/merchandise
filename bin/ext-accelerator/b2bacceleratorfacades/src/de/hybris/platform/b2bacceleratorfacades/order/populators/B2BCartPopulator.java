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
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link CartData} with {@link CartModel}.
 */
public class B2BCartPopulator<T extends CartData> extends AbstractOrderPopulator<CartModel, T>
{
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;
	private Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeConverter;
	private Converter<B2BCommentModel, B2BCommentData> b2BCommentConverter;
	private Converter<UserModel, CustomerData> b2bCustomerConverter;
	private B2BOrderService b2bOrderService;

	@Override
	public void populate(final CartModel source, final T target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		addDeliveryAddress(source, target);
		addDeliveryMethod(source, target);

		for (final AbstractOrderEntryModel entry : source.getEntries())
		{
			if (entry.getCostCenter() != null)
			{
				target.setCostCenter(getB2BCostCenterConverter().convert(entry.getCostCenter()));
				break;
			}
		}

		target.setPurchaseOrderNumber(source.getPurchaseOrderNumber());
		final CheckoutPaymentType paymentType = (source.getPaymentType() != null ? source.getPaymentType()
				: CheckoutPaymentType.ACCOUNT);

		target.setPaymentType(getB2bPaymentTypeConverter().convert(paymentType));

		if(!CheckoutPaymentType.ACCOUNT.equals(paymentType))
		{
			addPaymentInformation(source, target);
		}

		if (CollectionUtils.isNotEmpty(source.getB2bcomments()))
		{
			target.setB2BComment(getB2BCommentConverter().convert(source.getB2bcomments().iterator().next()));
		}
		target.setB2bCustomerData(getB2bCustomerConverter().convert(source.getUser()));
		target.setQuoteAllowed(Boolean.valueOf(getB2bOrderService().isQuoteAllowed(source)));
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

	protected Converter<CheckoutPaymentType, B2BPaymentTypeData> getB2bPaymentTypeConverter()
	{
		return b2bPaymentTypeConverter;
	}

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

	protected Converter<UserModel, CustomerData> getB2bCustomerConverter()
	{
		return b2bCustomerConverter;
	}

	@Required
	public void setB2bCustomerConverter(final Converter<UserModel, CustomerData> b2bCustomerConverter)
	{
		this.b2bCustomerConverter = b2bCustomerConverter;
	}

	protected B2BOrderService getB2bOrderService()
	{
		return b2bOrderService;
	}

	@Required
	public void setB2bOrderService(final B2BOrderService b2bOrderService)
	{
		this.b2bOrderService = b2bOrderService;
	}
}
