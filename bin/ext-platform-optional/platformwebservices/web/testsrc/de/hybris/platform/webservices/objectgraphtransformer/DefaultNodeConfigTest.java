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
package de.hybris.platform.webservices.objectgraphtransformer;

import static org.apache.commons.lang.WordUtils.uncapitalize;
import static org.fest.assertions.Assertions.assertThat;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultNodeConfig;

import org.junit.Test;


public class DefaultNodeConfigTest
{
	public static final String FIRST_QUALIFIER = "FirstQualifier";
	public static final String SECOND_QUALIFIER = "SecondQualifier";

	@Test
	public void shouldObtainValidPropertyNameForAttributeWithQualifierStartingFromUppercaseCharacter()
	{
		final DefaultNodeConfig nodeConfig = new DefaultNodeConfig(TestDTO.class);

		final String[] names = nodeConfig.getUidPropertyNames();

		assertThat(names).isNotNull().hasSize(2).contains(uncapitalize(FIRST_QUALIFIER)).contains(uncapitalize(SECOND_QUALIFIER));
	}

	@GraphNode(target = TestDTO.class, factory = GenericNodeFactory.class, uidProperties = FIRST_QUALIFIER + ","
			+ SECOND_QUALIFIER)
	public static class TestDTO
	{
		// empty DTO
	}
}
