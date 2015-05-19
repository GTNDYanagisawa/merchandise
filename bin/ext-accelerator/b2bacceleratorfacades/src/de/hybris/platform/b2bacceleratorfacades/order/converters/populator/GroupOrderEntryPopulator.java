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
package de.hybris.platform.b2bacceleratorfacades.order.converters.populator;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorfacades.order.data.PriceRangeData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Groups multiple {@link OrderEntryData} as one entry in a {@link AbstractOrderData) based on the multidimensional
 * variants that share the same base product. All non multidimensional product entries will be leaved unmodified as a
 * single entry.
 */
public class GroupOrderEntryPopulator implements Populator<AbstractOrderModel, AbstractOrderData>
{

	public static final String VARIANT_TYPE = "GenericVariantProduct";
	public static final int INVALID_ENTRY_NUMBER = -1;
	public static final long ZERO_QUANTITY = 0L;

	private ProductService productService;
	private PriceDataFactory priceDataFactory;

	@Override
	public void populate(AbstractOrderModel source, AbstractOrderData target) throws ConversionException
	{

		target.setEntries(groupEntries(target.getEntries()));

	}

    protected ArrayList groupEntries(final List<OrderEntryData> entries)
    {
        Map<String, OrderEntryData> group = new HashMap<>();
        boolean anyGroup = false;

        for (final OrderEntryData entry : entries)
        {
            final ProductData product = entry.getProduct();

            if (isGroupable(product))
            {
                anyGroup = true;
                if (!group.containsKey(product.getBaseProduct()))
                {
                    group.put(product.getBaseProduct(), createGroupedOrderEntry(entry));
                }

                group.get(product.getBaseProduct()).getEntries().add(entry);

            }
            else
            {
                group.put(product.getCode(), entry);
            }

        }

        if (anyGroup)
        {
            consolidateGroupedOrderEntry(group);
        }

        return new ArrayList(group.values());
    }

    // This should be replaced by product.mutidimentional but at this stage is not yet populated
    // Only works for multidimentional products
    protected boolean isGroupable(final ProductData product) {
        return product.getBaseProduct() != null && CollectionUtils.isNotEmpty(product.getBaseOptions())
                && VARIANT_TYPE.equalsIgnoreCase(product.getBaseOptions().get(0).getVariantType());
    }

	protected void consolidateGroupedOrderEntry(final Map<String, OrderEntryData> group)
	{

		for (String productCode : group.keySet())
		{
			final OrderEntryData parentEntry = group.get(productCode);
			if (parentEntry.getEntries() != null)
			{
				PriceData firstEntryTotalPrice = parentEntry.getEntries().get(0).getTotalPrice();
				PriceRangeData priceRange = parentEntry.getProduct().getPriceRange();

                if (firstEntryTotalPrice != null){
                    priceRange.setMaxPrice(getMaxPrice(parentEntry, firstEntryTotalPrice));
                    priceRange.setMinPrice(getMinPrice(parentEntry, firstEntryTotalPrice));
                    parentEntry.setTotalPrice(getTotalPrice(parentEntry, firstEntryTotalPrice));
                }

				parentEntry.setQuantity(getTotalQuantity(parentEntry));
			}

		}
	}

	protected PriceData getMaxPrice(OrderEntryData parentEntry, PriceData samplePrice)
	{
		long newMaxPrice = 0;

		for (OrderEntryData childEntry : parentEntry.getEntries())
		{
			if (isNotEmptyPrice(childEntry.getBasePrice()))
			{
				final long basePriceValue = childEntry.getBasePrice().getValue().longValue();
				if (basePriceValue > newMaxPrice)
				{
					newMaxPrice = basePriceValue;
				}
			}
		}

		return buildPrice(samplePrice, newMaxPrice);
	}

	protected PriceData getMinPrice(OrderEntryData parentEntry, PriceData samplePrice)
	{
		long newMinPrice = Long.MAX_VALUE;

		for (OrderEntryData childEntry : parentEntry.getEntries())
		{
			if (isNotEmptyPrice(childEntry.getBasePrice()))
			{
				final long basePriceValue = childEntry.getBasePrice().getValue().longValue();
				if (basePriceValue < newMinPrice)
				{
					newMinPrice = basePriceValue;
				}
			}
		}

		return buildPrice(samplePrice, newMinPrice);
	}

	private boolean isNotEmptyPrice(PriceData price)
	{
		return price != null && price.getValue() != null;
	}


	protected PriceData getTotalPrice(OrderEntryData parentEntry, PriceData samplePrice)
	{
		long newTotalPrice = 0;

		for (OrderEntryData childEntry : parentEntry.getEntries())
		{
			if (isNotEmptyPrice(childEntry.getBasePrice()))
			{
				newTotalPrice += childEntry.getTotalPrice().getValue().longValue();
			}
		}

		return buildPrice(samplePrice, newTotalPrice);
	}

	protected long getTotalQuantity(OrderEntryData parentEntry)
	{

		long totalQuantity = 0;
		for (OrderEntryData childEntry : parentEntry.getEntries())
		{
			totalQuantity += (childEntry.getQuantity() != null ? childEntry.getQuantity() : 0);
		}


		return totalQuantity;
	}




	protected OrderEntryData createGroupedOrderEntry(final OrderEntryData firstEntry)
	{

		final OrderEntryData groupedEntry = new OrderEntryData();
		groupedEntry.setEntries(new ArrayList<OrderEntryData>());
		groupedEntry.setEntryNumber(INVALID_ENTRY_NUMBER);

		groupedEntry.setQuantity(ZERO_QUANTITY);

		final ProductData baseProduct = createBaseProduct(firstEntry.getProduct());
		groupedEntry.setProduct(baseProduct);

		groupedEntry.setUpdateable(firstEntry.isUpdateable());
		groupedEntry.setBasePrice(firstEntry.getBasePrice());

		return groupedEntry;
	}

	protected ProductData createBaseProduct(final ProductData variant)
	{

		final ProductData productData = new ProductData();

		productData.setUrl(variant.getUrl());
		productData.setPurchasable(variant.getPurchasable());
		productData.setMultidimensional(true);
		productData.setImages(variant.getImages());

		final ProductModel productModel = productService.getProductForCode(variant.getBaseProduct());
		productData.setCode(productModel.getCode());
		productData.setName(productModel.getName());
		productData.setDescription(productModel.getDescription());

		productData.setPriceRange(new PriceRangeData());


		return productData;

	}

	protected PriceData buildPrice(final PriceData base, final long amount)
	{
		return getPriceDataFactory().create(base.getPriceType(), BigDecimal.valueOf(amount), base.getCurrencyIso());
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

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}
}
