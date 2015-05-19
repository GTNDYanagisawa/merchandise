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

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link VariantOptionData} based on {@link VariantProductModel}
 */
public class VariantOptionDataPopulator implements Populator<VariantProductModel, VariantOptionData>
{
	private VariantsService variantsService;
	private CommercePriceService commercePriceService;
	private PriceDataFactory priceDataFactory;
	private UrlResolver<ProductModel> productModelUrlResolver;
	private Converter<MediaModel, ImageData> imageConverter;
	private Converter<ProductModel, StockData> stockConverter;

	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

	protected VariantsService getVariantsService()
	{
		return variantsService;
	}

	@Required
	public void setVariantsService(final VariantsService variantsService)
	{
		this.variantsService = variantsService;
	}

	protected UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	@Required
	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

	protected CommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	@Required
	public void setCommercePriceService(final CommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected Converter<ProductModel, StockData> getStockConverter()
	{
		return stockConverter;
	}

	@Required
	public void setStockConverter(final Converter<ProductModel, StockData> stockConverter)
	{
		this.stockConverter = stockConverter;
	}

	@Override
	public void populate(final VariantProductModel source, final VariantOptionData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getBaseProduct() != null)
		{
			final List<VariantAttributeDescriptorModel> descriptorModels = getVariantsService().getVariantAttributesForVariantType(
					source.getBaseProduct().getVariantType());

			final Collection<VariantOptionQualifierData> variantOptionQualifiers = new ArrayList<VariantOptionQualifierData>();
			for (final VariantAttributeDescriptorModel descriptorModel : descriptorModels)
			{
				// Create the variant qualifier
				final VariantOptionQualifierData variantOptionQualifier = new VariantOptionQualifierData();
				final String qualifier = descriptorModel.getQualifier();
				variantOptionQualifier.setQualifier(qualifier);
				variantOptionQualifier.setName(descriptorModel.getName());
				// Lookup the value
				final Object variantAttributeValue = lookupVariantAttributeName(source, qualifier);
				variantOptionQualifier.setValue(variantAttributeValue == null ? "" : variantAttributeValue.toString());

				// Add to list of variants
				variantOptionQualifiers.add(variantOptionQualifier);
			}
			target.setVariantOptionQualifiers(variantOptionQualifiers);
			target.setCode(source.getCode());
			target.setUrl(getProductModelUrlResolver().resolve(source));
			target.setStock(getStockConverter().convert(source));

			final PriceDataType priceType;
			final PriceInformation info;
			if (CollectionUtils.isEmpty(source.getVariants()))
			{
				priceType = PriceDataType.BUY;
				info = getCommercePriceService().getWebPriceForProduct(source);
			}
			else
			{
				priceType = PriceDataType.FROM;
				info = getCommercePriceService().getFromPriceForProduct(source);
			}

			if (info != null)
			{
				final PriceData priceData = getPriceDataFactory().create(priceType,
						BigDecimal.valueOf(info.getPriceValue().getValue()), info.getPriceValue().getCurrencyIso());
				target.setPriceData(priceData);
			}
		}
	}

	protected Object lookupVariantAttributeName(final VariantProductModel productModel, final String attribute)
	{
		final Object value = getVariantsService().getVariantAttributeValue(productModel, attribute);
		if (value == null)
		{
			final ProductModel baseProduct = productModel.getBaseProduct();
			if (baseProduct instanceof VariantProductModel)
			{
				return lookupVariantAttributeName((VariantProductModel) baseProduct, attribute);
			}
		}
		return value;
	}
}
