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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.schedline.data.ScheduleLineData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * 
 */
public class DefaultAbstractOrderEntryPopulator implements Populator<Item, OrderEntryData>
{
	private static final Logger LOG = Logger.getLogger(DefaultAbstractOrderEntryPopulator.class);


	private PriceDataFactory priceFactory;
	private Converter<Schedline, ScheduleLineData> scheduleLinesConverter;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Item item, final OrderEntryData target) throws ConversionException
	{
		target.setBasePrice(priceFactory.create(PriceDataType.BUY, item.getNetValueWOFreight(), item.getCurrency()));
		target.setTotalPrice(priceFactory.create(PriceDataType.BUY, item.getGrossValue(), item.getCurrency()));

		target.setQuantity(Long.valueOf(item.getQuantity().longValue()));
		target.setEntryNumber(Integer.valueOf(item.getNumberInt()));
		final ProductData productData = createProductFromItem(item);
		productData.setPurchasable(Boolean.TRUE);
		target.setProduct(productData);
		target.setUpdateable(true);
		target.setConfigurable(item.isConfigurable());
		target.setHandle(item.getHandle());

		if (LOG.isDebugEnabled())
		{
			LOG.debug("creating schedule lines for: " + item.getNumberInt());
		}

		target.setScheduleLines(convertScheduleLines(item.getScheduleLines()));

	}

	ProductData createProductFromItem(final Item item)
	{
		final ProductData productData = new ProductData();
		final TechKey productGuid = item.getProductGuid();
		if (productGuid != null && (!TechKey.isEmpty(productGuid)))
		{
			//item exists in ERP			
			productData.setCode(formatProductIdForHybris(item.getProductGuid().getIdAsString()));
		}
		else
		{
			productData.setCode(formatProductIdForHybris(item.getProductId()));
		}
		productData.setName(item.getProductId());
		return productData;
	}

	/**
	 * @param SapScheduleLines
	 * @return List<ScheduleLineData>
	 */
	private List<ScheduleLineData> convertScheduleLines(final List<Schedline> SapScheduleLines)
	{
		final List<ScheduleLineData> scheduleLines = new ArrayList<ScheduleLineData>();

		for (final Schedline sapScheduleLine : SapScheduleLines)
		{
			final ScheduleLineData hybrisScheduleLine = scheduleLinesConverter.convert(sapScheduleLine);
			scheduleLines.add(hybrisScheduleLine);
		}
		return scheduleLines;
	}



	/**
	 * @return the priceFactory
	 */
	public PriceDataFactory getPriceFactory()
	{
		return priceFactory;
	}

	/**
	 * @param priceFactory
	 *           the priceFactory to set
	 */
	public void setPriceFactory(final PriceDataFactory priceFactory)
	{
		this.priceFactory = priceFactory;
	}

	/**
	 * @return the scheduleLinesConverter
	 */
	public Converter<Schedline, ScheduleLineData> getScheduleLinesConverter()
	{
		return scheduleLinesConverter;
	}

	/**
	 * @param scheduleLinesConverter
	 *           the scheduleLinesConverter to set
	 */
	public void setScheduleLinesConverter(final Converter<Schedline, ScheduleLineData> scheduleLinesConverter)
	{
		this.scheduleLinesConverter = scheduleLinesConverter;
	}

	/**
	 * Formats product ID taken from backend catalog and prepares it for Hybris. Removes leading zeros.l
	 * 
	 * @param input
	 * @return Formatted product ID
	 */
	protected String formatProductIdForHybris(final String input)
	{
		try
		{
			final BigDecimal inputAsBigDecimal = new BigDecimal(input);
			return inputAsBigDecimal.toString();
		}
		catch (final NumberFormatException e)
		{
			return input;
		}
	}

}
