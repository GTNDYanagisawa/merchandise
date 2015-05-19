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
package de.hybris.platform.b2bpunchoutaddon.converter;

import de.hybris.platform.b2b.punchout.PunchOutUtils;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;

import org.cxml.CXML;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.ClassUtils;


/**
 * Implementation of Message Converter to set header elements.
 */
public class CXMLJaxb2MessageConverter extends Jaxb2RootElementHttpMessageConverter
{

	@Override
	protected void writeToResult(final Object o, final HttpHeaders headers, final Result result) throws IOException
	{
		if (o instanceof CXML)
		{
			try
			{
				final Class clazz = ClassUtils.getUserClass(o);
				final Marshaller marshaller = createMarshaller(clazz);
				PunchOutUtils.removeStandalone(marshaller);
				PunchOutUtils.setHeader(marshaller);
				marshaller.marshal(o, result);
			}
			catch (final MarshalException ex)
			{
				throw new HttpMessageNotWritableException("Could not marshal [" + o + "]: " + ex.getMessage(), ex);
			}
			catch (final JAXBException ex)
			{
				throw new HttpMessageConversionException("Could not instantiate JAXBContext: " + ex.getMessage(), ex);
			}

		}
		else
		{
			super.writeToResult(o, headers, result);
		}
	}

}
