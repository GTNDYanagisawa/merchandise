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

package com.hybris.backoffice.jalo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Test;


public class JaloPropertyNotVisibleInModelAccessorExtenderTest
{

	private final String ATTR_IN_BOTH = "attrInBoth";
	private final String ATTR_IN_INITIAL = "attrInInitial";
	private final String ATTR_IN_EXTENSION = "attrInExtension";

	@Test
	public void mergingTest()
	{
		final JaloPropertyNotVisibleInModelAccessor accessor = new JaloPropertyNotVisibleInModelAccessor();
		accessor.setSupportedJaloAttributes(createInitialMap());

		final JaloPropertyNotVisibleInModelAccessorExtender extender = new JaloPropertyNotVisibleInModelAccessorExtender();
		extender.setAccessor(accessor);
		extender.setAdditionalJaloAttributes(createMapForExtension());
		extender.addJaloAttributesFromExtension();

		final Map<String, Set<Class>> supportedJaloAttributes = accessor.getSupportedJaloAttributes();
		Assertions.assertThat(supportedJaloAttributes).isNotEmpty();
		Assertions.assertThat(supportedJaloAttributes).hasSize(3);
		Assertions.assertThat(supportedJaloAttributes.get(ATTR_IN_BOTH)).hasSize(4)
				.containsOnly(String.class, Character.class, StringBuffer.class, StringBuilder.class);
		Assertions.assertThat(supportedJaloAttributes.get(ATTR_IN_INITIAL)).hasSize(2).containsOnly(Integer.class, Long.class);
		Assertions.assertThat(supportedJaloAttributes.get(ATTR_IN_EXTENSION)).hasSize(2).containsOnly(Double.class, Float.class);

		extender.removeJaloAttributesFromExtension();
		Assertions.assertThat(accessor.getSupportedJaloAttributes()).isEqualTo(createInitialMap());
	}

    @Test
    public void testNPE()
    {
        final JaloPropertyNotVisibleInModelAccessor accessor = new JaloPropertyNotVisibleInModelAccessor();
        final JaloPropertyNotVisibleInModelAccessorExtender extender = new JaloPropertyNotVisibleInModelAccessorExtender();
        extender.setAccessor(accessor);
        extender.addJaloAttributesFromExtension();
        Assertions.assertThat(accessor.getSupportedJaloAttributes()).isEmpty();
        extender.removeJaloAttributesFromExtension();
        Assertions.assertThat(accessor.getSupportedJaloAttributes()).isEmpty();
    }

	private Map<String, Set<Class>> createInitialMap()
	{
		final Map<String, Set<Class>> map = new HashMap<>();
		map.put(ATTR_IN_BOTH, new HashSet<Class>(Arrays.asList(String.class, Character.class)));
		map.put(ATTR_IN_INITIAL, new HashSet<Class>(Arrays.asList(Integer.class, Long.class)));
		return map;
	}

	private Map<String, Set<Class>> createMapForExtension()
	{
		final Map<String, Set<Class>> map = new HashMap<>();
		map.put(ATTR_IN_BOTH, new HashSet<Class>(Arrays.asList(StringBuffer.class, StringBuilder.class)));
		map.put(ATTR_IN_EXTENSION, new HashSet<Class>(Arrays.asList(Double.class, Float.class)));
		return map;

	}
}
