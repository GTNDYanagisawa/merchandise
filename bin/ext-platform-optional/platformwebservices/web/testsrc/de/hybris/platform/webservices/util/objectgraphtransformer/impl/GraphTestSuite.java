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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.hybris.platform.core.dto.user.AddressDTO;


@RunWith(Suite.class)
@SuiteClasses(
{ GraphGeneralTest.class, //
		GraphInitializerTest.class, //
		GraphInitializerTest2.class, //
		NodeContextTest.class, //		
		PropertyConverterTest.class, //
		ConvertNodeToPropertyTest.class, //
		VirtualPropertyTest.class, //
		IdentAndEqualNodesTest.class, //
		GraphNodeFactoryTest.class, //
		GraphPropertyFilterTest.class, //
		CustomNodeConfigsTest.class, //
		BidiGraphTest.class //
})
public class GraphTestSuite
{

	public static void main(final String[] argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		new BidiGraphTransformer(AddressDTO.class);
	}

}
