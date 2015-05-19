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

package com.hybris.cockpitng.editor.defaultenum;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleStatus;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.TestUtils;

import java.util.Arrays;
import java.util.List;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

@UnitTest
public class PlatformEnumValueResolverTest
{

	private final PlatformEnumValueResolver platformEnumValueResolver = new PlatformEnumValueResolver();
	private final List allArticleStatuses = (Arrays.asList(ArticleStatus.OLD_ARTICLE, ArticleStatus.NEW_ARTICLE));

	@Before
	public void before()
	{
		final ClassificationSystemService classificationSystemService = Mockito.mock(ClassificationSystemService.class);
		final EnumerationService enumerationService = Mockito.mock(EnumerationService.class);
		Mockito.when(enumerationService.getEnumerationValues(ArticleStatus.class)).thenReturn(allArticleStatuses);
		Mockito.when(enumerationService.getEnumerationValues("ArticleStatus")).thenReturn(allArticleStatuses);
		final ModelService modelService = Mockito.mock(ModelService.class);
		platformEnumValueResolver.setClassificationSystemService(classificationSystemService);
		platformEnumValueResolver.setEnumerationService(enumerationService);
		platformEnumValueResolver.setModelService(modelService);
	}

	@Test
	public void testValueIsHybrisEnumValue()
	{
		final Object value = ArticleStatus.NEW_ARTICLE;
		final List<Object> returnedArticleStatuses = platformEnumValueResolver.getAllValues(null, value);
		Assertions.assertThat(returnedArticleStatuses).isEqualTo(allArticleStatuses);
	}


	@Test
	public void testValueTypeIsHybrisEnumValue()
	{
		final String valueType = "java.lang.Enum(de.hybris.platform.catalog.enums.ArticleStatus)";
		final List<Object> returnedArticleStatuses = platformEnumValueResolver.getAllValues(valueType, null);
		Assertions.assertThat(returnedArticleStatuses).isEqualTo(this.allArticleStatuses);
	}

	@Test
	public void testValueTypeIsHybrisEnumValueShortNotation()
	{
		final String valueType = "java.lang.Enum(ArticleStatus)";
		final List<Object> returnedArticleStatuses = platformEnumValueResolver.getAllValues(valueType, null);
		Assertions.assertThat(returnedArticleStatuses).isEqualTo(this.allArticleStatuses);
	}

	@Test
	public void testShouldReturnEmptyList()
	{
		final String valueType = "java.lang.Enum";
		final List<Object> returnedValues = platformEnumValueResolver.getAllValues(valueType, null);
		Assertions.assertThat(returnedValues).isEmpty();
	}


	@Test
	public void testValueTypeIsSimpleEnum()
	{
		final String valueType = "java.lang.Enum(com.hybris.cockpitng.editor.defaultenum.MyTestingEnum)";
		final List<Object> allMyEnumValues = platformEnumValueResolver.getAllValues(valueType, null);
		Assertions.assertThat(allMyEnumValues).isEqualTo(Arrays.asList(MyTestingEnum.values()));
	}

	@Test
	public void testValueIsSimpleEnum()
	{
		final List<Object> allMyEnumValues = platformEnumValueResolver.getAllValues(null, MyTestingEnum.VALUE_2);
		Assertions.assertThat(allMyEnumValues).isEqualTo(Arrays.asList(MyTestingEnum.values()));
	}

	@Test
	public void testNPEResistance()
	{
		TestUtils.disableFileAnalyzer("Exception should be thrown");
		try
		{
			platformEnumValueResolver.getAllValues(null, null);
			Assert.fail("Exception should be thrown");
		}
		catch (final IllegalArgumentException ex)
		{
			// ok
		}
		TestUtils.enableFileAnalyzer();
	}

	@Test
	public void test()
	{
		TestUtils.disableFileAnalyzer("Exception should be thrown");
		try
		{
			platformEnumValueResolver.getAllValues(null, new Object());
			Assert.fail("Exception should be thrown");
		}
		catch (final IllegalArgumentException ex)
		{
			//ok
		}
		TestUtils.enableFileAnalyzer();

	}

}

enum MyTestingEnum
{
	VALUE_1, VALUE_2, VALUE_3
}





