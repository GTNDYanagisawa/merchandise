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
package de.hybris.platform.ycommercewebservices.xstream;

import java.io.Writer;

import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonWriter;


public class JsonXStreamMarshallerFactory extends XmlXStreamMarshallerFactory
{
	private XStreamMarshaller jsonMarshallerInstance;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		jsonMarshallerInstance = getObjectInternal();
	}

	@Override
	public Object getObject() throws Exception
	{
		return jsonMarshallerInstance;
	}

	/**
	 * creates a custom json writer which swallows top most root nodes
	 */
	@Override
	protected XStreamMarshaller createMarshaller()
	{
		final XStreamMarshaller marshaller = super.createMarshaller();
		marshaller.setStreamDriver(new com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver()
		{
			@Override
			public HierarchicalStreamWriter createWriter(final Writer writer)
			{
				return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
			}
		});
		return marshaller;
	}
}
