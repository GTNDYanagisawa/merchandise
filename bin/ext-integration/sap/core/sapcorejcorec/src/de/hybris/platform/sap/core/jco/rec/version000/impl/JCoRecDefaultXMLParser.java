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
package de.hybris.platform.sap.core.jco.rec.version000.impl;

import de.hybris.platform.sap.core.common.DocumentBuilderFactoryUtil;
import de.hybris.platform.sap.core.jco.rec.version000.JCoRecXMLParser;
import de.hybris.platform.sap.core.jco.rec.version000.JCoRecXMLParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordMetaData;


/**
 * This implementation of {@link JCoRecXMLParser} is the default parser used in JCoRec.<br>
 * Parsing of single functions is not supported.
 */
class JCoRecDefaultXMLParser implements JCoRecXMLParser
{

	@Override
	public void parse(final JCoRecRepository repo, final String functionKey, final File f) throws JCoRecXMLParserException
	{
		//Parsing of single functions is not supported. Use {@link #parse(JCoRecRepository, File)} instead.
		throw new JCoRecXMLParserException("Not Supported");
	}

	/**
	 * Parses a xml file to {@code Document doc} and further parses the {@link JCoRecordMetaData},
	 * {@link JCoFunctionTemplate}, {@link JCoFunction} and {@link JCoRecord} represented in {@code doc} to the given
	 * {@link JCoRecRepository}.
	 * 
	 * @param repo
	 *           the {@link JCoRecRepository} where the parsed Objects are appended
	 * @param file
	 *           the {@link File} to parse
	 * @throws JCoRecXMLParserException
	 *            in case of an error while parsing the file or if the file does not exist
	 * 
	 * @see de.hybris.platform.sap.core.jco.rec.version000.JCoRecXMLParser#parse(JCoRecRepository, File) java.io.File)
	 */
	@Override
	public void parse(final JCoRecRepository repo, final File file) throws JCoRecXMLParserException
	{
		if (file.exists() == false)
		{
			throw new JCoRecXMLParserException("File not found during parsing: " + file.getAbsolutePath());
		}

		try
		{

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			DocumentBuilderFactoryUtil.setSecurityFeatures(factory);

			final DocumentBuilder builder = factory.newDocumentBuilder();

			String xml = readFileAsString(file);
			xml = xml.replaceAll("(>\\s*)(\\n|\\r|\\s)+<", "><");


			final Document doc = builder.parse(new InputSource(new StringReader(xml)));

			if (doc == null)
			{
				throw new JCoRecXMLParserException("Could not create xml document for parsing. File path is "
						+ file.getAbsolutePath() + ".");
			}

			new JCoRecMetaDataParser().parseMetaDataRepository(doc, repo.getMetaDataRepository());
			new JCoRecFunctionParser().parseRepository(doc, repo);

		}
		catch (final SAXException | IOException | ParserConfigurationException e)
		{
			throw new JCoRecXMLParserException(e);
		}
	}

	/**
	 * Reads the contend of a {@link File} as a {@link String}.
	 * 
	 * @param file
	 *           the {@link File} to be read
	 * @return the content as a {@link String}
	 * @throws java.io.IOException
	 *            if an error occurs while reading the file
	 */
	protected String readFileAsString(final File file) throws java.io.IOException
	{
		final byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = null;
		try
		{
			f = new BufferedInputStream(new FileInputStream(file));
			f.read(buffer);
		}
		finally
		{
			if (f != null)
			{
				f.close();
			}
		}
		return new String(buffer);
	}


}
