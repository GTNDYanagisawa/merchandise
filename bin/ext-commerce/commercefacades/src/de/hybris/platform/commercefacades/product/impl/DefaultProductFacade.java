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
package de.hybris.platform.commercefacades.product.impl;


import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commerceservices.product.CommerceProductReferenceService;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link ProductFacade}
 */
public class DefaultProductFacade<REF_TARGET> implements ProductFacade
{
	private ProductService productService;
	private CustomerReviewService customerReviewService;
	private UserService userService;
	private ModelService modelService;
	private CommonI18NService commonI18NService;

	private Converter<ProductModel, ProductData> productConverter;
	private Converter<CustomerReviewModel, ReviewData> customerReviewConverter;

	private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

	private CommerceProductReferenceService<ProductReferenceTypeEnum, REF_TARGET> commerceProductReferenceService;
	private Converter<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>, ProductReferenceData> referenceDataProductReferenceConverter;
	private ConfigurablePopulator<REF_TARGET, ProductData, ProductOption> referenceProductConfiguredPopulator;

	@Override
	public ReviewData postReview(final String productCode, final ReviewData reviewData)
	{
		Assert.notNull(reviewData, "Parameter reviewData cannot be null.");
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final UserModel userModel = getUserService().getCurrentUser();
		final CustomerReviewModel customerReviewModel = getCustomerReviewService().createCustomerReview(reviewData.getRating(),
				reviewData.getHeadline(), reviewData.getComment(), userModel, productModel);
		customerReviewModel.setLanguage(getCommonI18NService().getCurrentLanguage());
		customerReviewModel.setAlias(reviewData.getAlias());
		getModelService().save(customerReviewModel);
		return getCustomerReviewConverter().convert(customerReviewModel);
	}

	@Override
	public List<ReviewData> getReviews(final String productCode)
	{
		return getReviews(productCode, null);
	}

	@Override
	public List<ReviewData> getReviews(final String productCode, final Integer numberOfReviews)
	{
		final ProductModel product = getProductService().getProductForCode(productCode);
		final List<CustomerReviewModel> reviews = getCustomerReviewService().getReviewsForProductAndLanguage(product,
				getCommonI18NService().getCurrentLanguage());

		if (numberOfReviews == null)
		{
			return Converters.convertAll(reviews, getCustomerReviewConverter());
		}
		else if (numberOfReviews.intValue() < 0)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			return Converters.convertAll(
					reviews.subList(0, Math.min(numberOfReviews.intValue(), reviews.size())),
					getCustomerReviewConverter());
		}
	}

	@Override
	public ProductData getProductForOptions(final ProductModel productModel, final Collection<ProductOption> options)
	{
		final ProductData productData = getProductConverter().convert(productModel);

		if (options != null)
		{
			getProductConfiguredPopulator().populate(productModel, productData, options);
		}

		return productData;
	}

	@Override
	public ProductData getProductForCodeAndOptions(final String code, final Collection<ProductOption> options)
	{
		final ProductModel productModel = getProductService().getProductForCode(code);
		return getProductForOptions(productModel, options);
	}

	@Override
	public List<ProductReferenceData> getProductReferencesForCode(final String code,
			final List<ProductReferenceTypeEnum> referenceTypes, final List<ProductOption> options, final Integer limit)
	{
		final List<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>> references = getCommerceProductReferenceService()
				.getProductReferencesForCode(code, referenceTypes, limit);

		final List<ProductReferenceData> result = new ArrayList<ProductReferenceData>();

		for (final ReferenceData<ProductReferenceTypeEnum, REF_TARGET> reference : references)
		{
			final ProductReferenceData productReferenceData = getReferenceDataProductReferenceConverter().convert(reference);
			getReferenceProductConfiguredPopulator().populate(reference.getTarget(), productReferenceData.getTarget(), options);
			result.add(productReferenceData);
		}

		return result;
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public List<ProductReferenceData> getProductReferencesForCode(final String code, final ProductReferenceTypeEnum referenceType,
			final List<ProductOption> options, final Integer limit)
	{
		final List<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>> references = getCommerceProductReferenceService()
				.getProductReferencesForCode(code, referenceType, limit);

		final List<ProductReferenceData> result = new ArrayList<ProductReferenceData>();

		for (final ReferenceData<ProductReferenceTypeEnum, REF_TARGET> reference : references)
		{
			final ProductReferenceData productReferenceData = getReferenceDataProductReferenceConverter().convert(reference);
			getReferenceProductConfiguredPopulator().populate(reference.getTarget(), productReferenceData.getTarget(), options);
			result.add(productReferenceData);
		}

		return result;
	}

	/**
	 * Get an attribute value from a product. If the attribute value is null and the product is a variant then the same
	 * attribute will be requested from the base product.
	 * 
	 * @param productModel
	 *           the product
	 * @param attribute
	 *           the name of the attribute to lookup
	 * @return the value of the attribute
	 */
	protected Object getProductAttribute(final ProductModel productModel, final String attribute)
	{
		final Object value = getModelService().getAttributeValue(productModel, attribute);
		if (productModel instanceof VariantProductModel
				&& (value == null || (value instanceof Collection && ((Collection) value).isEmpty())))
		{
			final ProductModel baseProduct = ((VariantProductModel) productModel).getBaseProduct();
			if (baseProduct != null)
			{
				return getProductAttribute(baseProduct, attribute);
			}
		}

		return value;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected CustomerReviewService getCustomerReviewService()
	{
		return customerReviewService;
	}

	@Required
	public void setCustomerReviewService(final CustomerReviewService customerReviewService)
	{
		this.customerReviewService = customerReviewService;
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

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
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

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
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

	protected ConfigurablePopulator<ProductModel, ProductData, ProductOption> getProductConfiguredPopulator()
	{
		return productConfiguredPopulator;
	}

	@Required
	public void setProductConfiguredPopulator(
			final ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator)
	{
		this.productConfiguredPopulator = productConfiguredPopulator;
	}

	protected CommerceProductReferenceService<ProductReferenceTypeEnum, REF_TARGET> getCommerceProductReferenceService()
	{
		return commerceProductReferenceService;
	}

	@Required
	public void setCommerceProductReferenceService(
			final CommerceProductReferenceService<ProductReferenceTypeEnum, REF_TARGET> commerceProductReferenceService)
	{
		this.commerceProductReferenceService = commerceProductReferenceService;
	}

	protected Converter<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>, ProductReferenceData> getReferenceDataProductReferenceConverter()
	{
		return referenceDataProductReferenceConverter;
	}

	@Required
	public void setReferenceDataProductReferenceConverter(
			final Converter<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>, ProductReferenceData> referenceDataProductReferenceConverter)
	{
		this.referenceDataProductReferenceConverter = referenceDataProductReferenceConverter;
	}

	protected ConfigurablePopulator<REF_TARGET, ProductData, ProductOption> getReferenceProductConfiguredPopulator()
	{
		return referenceProductConfiguredPopulator;
	}

	@Required
	public void setReferenceProductConfiguredPopulator(
			final ConfigurablePopulator<REF_TARGET, ProductData, ProductOption> referenceProductConfiguredPopulator)
	{
		this.referenceProductConfiguredPopulator = referenceProductConfiguredPopulator;
	}
}
