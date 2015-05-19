/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.util.objectgraphtransformer.impl;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;

import de.hybris.platform.catalog.dto.KeywordDTO;


public class JaxbTest
{
	@Test
	public void testJaxb()
	{

		JAXBContext ctx = null;
		try
		{
			ctx = JAXBContext.newInstance(KeywordDTO.class);

			final String xml = //
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
					+ "<keyword pk=\"8796093088346\">" //
					+ "<categories>" //
					+ "<category code=\"topseller\">" //
					+ "<catalogVersion version=\"Online \">" //
					+ "<catalog id=\"clothescatalog\"/>" //
					+ "</catalogVersion>" //
					+ "</category>" //
					+ "</categories>" //
					+ "</keyword>";
			final KeywordDTO dto = (KeywordDTO) ctx.createUnmarshaller().unmarshal(new StringReader(xml));
			dto.hashCode();

			Assert.assertNotNull(dto.getCategories());
			Assert.assertNotNull(dto.getCategories().iterator().next());
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			Assert.fail();
		}
	}

	public static void main(final String[] argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		//		Logger log = Logger.getLogger(ObjTree.class);
		//		log.setLevel(Level.DEBUG);
		//
		//		log = Logger.getLogger(DefaultObjTreeNodeCopier.class);
		//		log.setLevel(Level.DEBUG);

		Logger.getRootLogger().setLevel(Level.DEBUG);
		final JaxbTest test = new JaxbTest();
		test.testJaxb();
	}


}
