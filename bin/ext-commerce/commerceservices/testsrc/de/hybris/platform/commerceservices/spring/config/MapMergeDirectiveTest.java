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
package de.hybris.platform.commerceservices.spring.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/commerceservices/mergedirective-test-spring.xml")
@UnitTest
public class MapMergeDirectiveTest
{
	@Resource
	private Map<String, String> addToMapMergeBean;

	@Resource
	private MultipleMapMergeBean multipleMapMergeBean;

	@Test
	public void testAddToMap()
	{
		assertEquals(4, addToMapMergeBean.size());
		assertTrue(addToMapMergeBean.containsKey("black"));
		assertEquals("Was expecting \"white\", but got " + addToMapMergeBean.get("black"), "white", addToMapMergeBean.get("black"));
	}

	@Test
	public void testAddToPropertyMap()
	{
		assertEquals(4, multipleMapMergeBean.getPropertyMap().size());
		assertTrue(multipleMapMergeBean.getPropertyMap().containsKey("quark"));
		assertEquals("Was expecting \"antiquark\", but got " + multipleMapMergeBean.getPropertyMap().get("quark"), "antiquark",
				multipleMapMergeBean.getPropertyMap().get("quark"));
	}

	@Test
	public void testAddToFieldMap() throws NoSuchFieldException, IllegalAccessException
	{
		final Field fieldMap = MultipleMapMergeBean.class.getDeclaredField("fieldMap");
		fieldMap.setAccessible(true);
		final Map<String, String> map = (Map<String, String>) fieldMap.get(multipleMapMergeBean);

		System.out.println("map:" + map.size());
		System.out.println("m: " + map);

		assertEquals(4, map.size());
		assertTrue(map.containsKey("seven"));
		assertEquals("Was expecting \"fourteen\", but got " + map.get("seven"), "fourteen", map.get("seven"));
	}
}