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
package de.hybris.platform.yb2bacceleratorfacades.variant.populators;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;


public class VariantOptionDataPricePopulator<SOURCE extends VariantProductModel, TARGET extends VariantOptionData> implements
		Populator<SOURCE, TARGET>
{



	private PriceDataFactory priceDataFactory;

	private CommercePriceService commercePriceService;


	@Override
	public void populate(final VariantProductModel variantProductModel, final VariantOptionData variantOptionData)
			throws ConversionException
	{

		final PriceInformation priceInformation = getCommercePriceService().getWebPriceForProduct(variantProductModel);

		PriceData priceData;
		if (priceInformation != null && priceInformation.getPriceValue() != null)
		{
			priceData = getPriceDataFactory().create(PriceDataType.FROM,
					new BigDecimal(priceInformation.getPriceValue().getValue()), priceInformation.getPriceValue().getCurrencyIso());
		}
		else
		{
			priceData = new PriceData();
			priceData.setValue(BigDecimal.ZERO);
			priceData.setFormattedValue("0");
		}
		variantOptionData.setPriceData(priceData);

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

}