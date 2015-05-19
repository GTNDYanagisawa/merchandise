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
package de.hybris.platform.sap.core.common;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;


/**
 * The class contains utility methods for the DocumentBuilderFactory.
 *
 */
public class DocumentBuilderFactoryUtil
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(DocumentBuilderFactoryUtil.class.getName());



	/**
	 * An XML parser should be configured securely so that it does not allow external entities as part of an incoming XML
	 * document. To avoid XML External Entities attacks (XXE injections) the following properties should be set for an
	 * XML factory, parser or reader:
	 *
	 * Xerces 1/Xerces 2 - "http://xml.org/sax/features/external-general-entities" -
	 * "http://xml.org/sax/features/external-parameter-entities"
	 *
	 * Xerces 2 - "http://apache.org/xml/features/disallow-doctype-decl"
	 *
	 * The method tries to set the feature for the given DocumentBuilderFactory instance. A log entry will be created if
	 * the feature could not be set.
	 *
	 * @param dbf
	 *           the DocumentBuilderFactory instance for which the feature shall be set.
	 *
	 */
	public static void setSecurityFeatures(final DocumentBuilderFactory dbf)
	{

		try
		{
			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

		}
		catch (final ParserConfigurationException e)
		{
			log.error("The feature 'http://xml.org/sax/features/external-general-entities' could not be set for the current document builder factory.");
		}

		try
		{
			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		}
		catch (final ParserConfigurationException e)
		{
			log.error("The feature 'http://xml.org/sax/features/external-parameter-entities' could not be set for the current document builder factory.");
		}

		try
		{
			// Xerces 2 only - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);

		}
		catch (final ParserConfigurationException e)
		{
			log.error("The feature 'http://apache.org/xml/features/disallow-doctype-decl' could not be set for the current document builder factory.");
		}

		// Enable XML validation
		//dbf.setValidating(true);
	}

}
