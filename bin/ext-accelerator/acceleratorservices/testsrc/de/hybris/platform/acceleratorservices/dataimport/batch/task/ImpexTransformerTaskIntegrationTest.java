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
package de.hybris.platform.acceleratorservices.dataimport.batch.task;

import static junit.framework.Assert.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test if ImpexTransformerTask collects and creates proper map converters
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/acceleratorservices/integration/hot-folder-spring-test.xml")
@UnitTest
public class ImpexTransformerTaskIntegrationTest
{
	@Resource(name = "batchTransformerTask")
	private ImpexTransformerTask task;

	/**
	 * Tests if ImpexTransformerTask collects all converters mappings and creates proper map of converters
	 */
	@Test
	public void testInitConvertersMap()
	{
		final Map<String, List<ImpexConverter>> converterMap = task.getConverterMap();
		assertNotNull("Map of converters should not be empty here", converterMap);
		assertThat(converterMap).isNotEmpty();
		assertEquals("Expected 7 converter mappings in 6 entries", 6, converterMap.size());
		final List<ImpexConverter> baseProductConverters = converterMap.get("base_product");
		assertNotNull("Converters for base_product should be included in map", baseProductConverters);
		assertThat(converterMap).isNotEmpty();
		Assert.assertEquals(2, baseProductConverters.size());
		assertNotNull("Expected one converter for tax", converterMap.get("tax"));
		assertEquals("Expected one converter for tax", 1, converterMap.get("tax").size());
		assertNotNull("Expected one converter for price", converterMap.get("price"));
		assertEquals("Expected one converter for price", 1, converterMap.get("price").size());
		assertNotNull("Expected one converter for stock", converterMap.get("stock"));
		assertEquals("Expected one converter for stock", 1, converterMap.get("stock").size());
		assertNotNull("Expected one converter for merchandise", converterMap.get("merchandise"));
		assertEquals("Expected one converter for merchandise", 1, converterMap.get("merchandise").size());
		assertNotNull("Expected one converter for stock", converterMap.get("stock"));
		assertEquals("Expected one converter for stock", 1, converterMap.get("stock").size());
	}
}
