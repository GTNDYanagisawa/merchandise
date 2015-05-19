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
package de.hybris.platform.acceleratorfacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator for product volume prices.
 */
public class ProductVolumePricesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
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
		if (productData != null)
		{
			final List<PriceInformation> pricesInfos = getPriceService().getPriceInformationsForProduct(productModel);
			if (pricesInfos == null || pricesInfos.size() < 2)
			{
				productData.setVolumePrices(Collections.<PriceData> emptyList());
			}
			else
			{
				final List<PriceData> volPrices = new ArrayList<PriceData>();

				final PriceDataType priceType = getPriceType(productModel);//not necessary

				for (final PriceInformation priceInfo : pricesInfos)
				{
					final Long minQuantity = getMinQuantity(priceInfo);
					if (minQuantity != null)
					{
						final PriceData volPrice = createPriceData(priceType, priceInfo);
						if (volPrice != null)
						{
							volPrice.setMinQuantity(minQuantity);
							volPrices.add(volPrice);
						}
					}
				}

				// Sort the list into quantity order
				Collections.sort(volPrices, VolumePriceComparator.INSTANCE);

				// Set the max quantities
				for (int i = 0; i < volPrices.size() - 1; i++)
				{
					volPrices.get(i).setMaxQuantity(Long.valueOf(volPrices.get(i + 1).getMinQuantity().longValue() - 1));
				}

				productData.setVolumePrices(volPrices);
			}
		}
	}

	protected PriceDataType getPriceType(final ProductModel productModel)
	{
		if (CollectionUtils.isEmpty(productModel.getVariants()))
		{
			return PriceDataType.BUY;
		}
		else
		{
			return PriceDataType.FROM;
		}
	}

	protected Long getMinQuantity(final PriceInformation priceInfo)
	{
		final Map qualifiers = priceInfo.getQualifiers();
		final Object minQtdObj = qualifiers.get(PriceRow.MINQTD);
		if (minQtdObj instanceof Long)
		{
			return (Long) minQtdObj;
		}
		return null;
	}

	protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo)
	{
		return getPriceDataFactory().create(priceType, BigDecimal.valueOf(priceInfo.getPriceValue().getValue()),
				priceInfo.getPriceValue().getCurrencyIso());
	}

	public static class VolumePriceComparator extends AbstractComparator<PriceData>
	{
		public static final VolumePriceComparator INSTANCE = new VolumePriceComparator();

		@Override
		protected int compareInstances(final PriceData price1, final PriceData price2)
		{
			if (price1 == null || price1.getMinQuantity() == null)
			{
				return BEFORE;
			}
			if (price2 == null || price2.getMinQuantity() == null)
			{
				return AFTER;
			}

			return compareValues(price1.getMinQuantity().longValue(), price2.getMinQuantity().longValue());
		}
	}
}
