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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populate the product data reviews for the product
 */
public class ProductReviewsPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET>
{
	private CustomerReviewService customerReviewService;
	private Converter<CustomerReviewModel, ReviewData> customerReviewConverter;
	private CommonI18NService commonI18NService;

	protected CustomerReviewService getCustomerReviewService()
	{
		return customerReviewService;
	}

	@Required
	public void setCustomerReviewService(final CustomerReviewService customerReviewService)
	{
		this.customerReviewService = customerReviewService;
	}

	protected Converter<CustomerReviewModel, ReviewData> getCustomerReviewConverter()
	{
		return customerReviewConverter;
	}

	@Required
	public void setCustomerReviewConverter(final Converter<CustomerReviewModel, ReviewData> customerReviewConverter)
	{
		this.customerReviewConverter = customerReviewConverter;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		final List<CustomerReviewModel> reviews = getCustomerReviewService().getReviewsForProductAndLanguage(productModel, getCommonI18NService().getCurrentLanguage());
		productData.setReviews(Converters.convertAll(reviews, getCustomerReviewConverter()));
		productData.setNumberOfReviews(Integer.valueOf(reviews.size()));
	}
}
