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
package de.hybris.platform.yb2bacceleratorfacades.product.populators;

import de.hybris.platform.acceleratorfacades.order.data.PriceRangeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator for product price range.
 */
public class ProductPriceRangePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{

	private PriceService priceService;
	private PriceDataFactory priceDataFactory;

	protected PriceService getPriceService()
	{
		return priceService;
	}

	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
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

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		if (productModel != null && productData != null)
		{
			// make sure you have the baseProduct because variantProducts won't have other variants
			final ProductModel baseProduct;
			if (productModel instanceof VariantProductModel)
			{
				final VariantProductModel variant = (VariantProductModel) productModel;
				baseProduct = variant.getBaseProduct();
			}
			else
			{
				baseProduct = productModel;
			}

			final Collection<VariantProductModel> variants = baseProduct.getVariants();
			final List<PriceInformation> allPricesInfos = new ArrayList<PriceInformation>();

			// collect all price infos
			for (final VariantProductModel variant : variants)
			{
				allPricesInfos.addAll(getPriceService().getPriceInformationsForProduct(variant));
			}

			// sort the list
			Collections.sort(allPricesInfos, PriceRangeComparator.INSTANCE);


			final PriceRangeData priceRange = new PriceRangeData();
			// get the min and max
			if (!allPricesInfos.isEmpty())
			{
				priceRange.setMinPrice(createPriceData(PriceDataType.FROM, allPricesInfos.get(0)));
				priceRange.setMaxPrice(createPriceData(PriceDataType.FROM, allPricesInfos.get(allPricesInfos.size() - 1)));
			}

			productData.setPriceRange(priceRange);

		}

	}

	protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo)
	{
		return getPriceDataFactory().create(priceType, BigDecimal.valueOf(priceInfo.getPriceValue().getValue()),
				priceInfo.getPriceValue().getCurrencyIso());
	}

	public static class PriceRangeComparator extends AbstractComparator<PriceInformation>
	{
		public static final PriceRangeComparator INSTANCE = new PriceRangeComparator();

		@Override
		protected int compareInstances(final PriceInformation price1, final PriceInformation price2)
		{
			if (price1 == null || BigDecimal.valueOf(price1.getPriceValue().getValue()) == null)
			{
				return BEFORE;
			}
			if (price2 == null || BigDecimal.valueOf(price2.getPriceValue().getValue()) == null)
			{
				return AFTER;
			}

			return compareValues(price1.getPriceValue().getValue(), price2.getPriceValue().getValue());
		}
	}
}