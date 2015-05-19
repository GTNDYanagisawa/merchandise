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
package de.hybris.platform.b2b.punchout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.cxml.CXML;


public class PunchOutUtils
{

	private static final String XML_WITHOUT_STANDALONE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static final String DOCTYPE = "<!DOCTYPE cXML SYSTEM \"http://xml.cXML.org/schemas/cXML/1.2.024/cXML.dtd\">";

	public static CXML unmarshallCXMLFromFile(final String relativeFilePath) throws FileNotFoundException
	{
		final InputStream fileInputStream = PunchOutUtils.class.getClassLoader().getResourceAsStream(relativeFilePath);

		if (fileInputStream == null)
		{
			throw new FileNotFoundException("Could not find file [" + relativeFilePath + "]");
		}

		try
		{
			final JAXBContext jaxbContext = JAXBContext.newInstance(CXML.class);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (CXML) unmarshaller.unmarshal(fileInputStream);
		}
		catch (final JAXBException e)
		{
			throw new PunchOutException(e.getErrorCode(), e.getMessage());
		}
	}

	public static String marshallFromBeanTree(final CXML cxml)
	{
		final StringWriter writer = new StringWriter();

		try
		{
			final JAXBContext context = JAXBContext.newInstance(CXML.class);
			final Marshaller m = context.createMarshaller();
			removeStandalone(m);
			setHeader(m);
			m.marshal(cxml, writer);
		}
		catch (final JAXBException e)
		{
			throw new PunchOutException(e.getErrorCode(), e.getMessage());
		}

		// FIXME - just testing - do it properly
		String xml = writer.toString();
		xml = XML_WITHOUT_STANDALONE + DOCTYPE + xml;

		return xml;
	}

	public static void removeStandalone(final Marshaller marshaller) throws PropertyException
	{
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
	}

	public static void setHeader(final Marshaller m) throws PropertyException
	{
		m.setProperty("com.sun.xml.internal.bind.xmlHeaders", XML_WITHOUT_STANDALONE + DOCTYPE);
	}


	/**
	 * Transforms a CXML into a Base64 String.
	 *
	 * @param cxml
	 *           the cxml object.
	 * @return Base64 String
	 */
	public static String transformCXMLToBase64(final CXML cxml)
	{
		final String cXML = marshallFromBeanTree(cxml);
		final String cXMLEncoded = Base64.encodeBase64String(cXML.getBytes());

		return cXMLEncoded;
	}

}
