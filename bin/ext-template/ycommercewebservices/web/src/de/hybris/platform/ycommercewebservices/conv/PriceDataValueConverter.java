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
 */
package de.hybris.platform.ycommercewebservices.conv;

import de.hybris.platform.commercefacades.product.data.PriceData;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonWriter;


/**
 * Converter for PriceData which return 'value' field without quotes around it.
 */
public class PriceDataValueConverter implements Converter
{
	@Override
	public boolean canConvert(final Class type)
	{
		return type == PriceData.class;
	}

	@Override
	public void marshal(final Object object, final HierarchicalStreamWriter writer, final MarshallingContext context)
	{
		final PriceData priceData = (PriceData) object;

		addNode("currencyIso", String.class, priceData.getCurrencyIso(), writer, context);
		addNode("priceType", String.class, priceData.getPriceType(), writer, context);

		if (priceData.getValue() != null)
		{
			writer.startNode("value");
			writer.setValue(priceData.getValue().toString());
			writer.endNode();
		}

		addNode("formattedValue", String.class, priceData.getFormattedValue(), writer, context);
		addNode("maxQuantity", Long.class, priceData.getMaxQuantity(), writer, context);
		addNode("minQuantity", Long.class, priceData.getMinQuantity(), writer, context);
	}

	protected void addNode(final String nodeName, final Class clazz, final Object object, final HierarchicalStreamWriter writer,
			final MarshallingContext context)
	{
		if (object != null)
		{
			if (writer instanceof JsonWriter)
			{
				((JsonWriter) writer).startNode(nodeName, clazz);
				context.convertAnother(object);
				((JsonWriter) writer).endNode();
			}
			else
			{
				writer.startNode(nodeName);
				context.convertAnother(object);
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context)
	{
		return null;
	}
}
