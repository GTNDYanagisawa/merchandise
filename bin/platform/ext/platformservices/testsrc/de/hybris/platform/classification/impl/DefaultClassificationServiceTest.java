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
package de.hybris.platform.classification.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.ClassificationClassesResolverStrategy;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.classification.strategy.LoadStoreFeaturesStrategy;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultClassificationServiceTest
{
	@InjectMocks
	private final ClassificationService service = new DefaultClassificationService();
	@Mock
	private LoadStoreFeaturesStrategy loadStoreFeaturesStrategy;
	@Mock
	private ClassificationClassesResolverStrategy classificationClassesResolverStrategy;
	@Mock
	private ProductModel product;
	@Mock
	private ClassAttributeAssignmentModel assignment;
	@Mock
	private Feature feature1, feature2, feature3;
	private List<Feature> features;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		features = new ArrayList<Feature>();
		features.add(feature1);
		features.add(feature2);
		features.add(feature3);
	}


	@Test
	public void shouldGetFeaturesForGivenProduct()
	{
		// given
		given(classificationClassesResolverStrategy.resolve(product)).willReturn(Collections.EMPTY_SET);
		given(classificationClassesResolverStrategy.getAllClassAttributeAssignments(Collections.EMPTY_SET)).willReturn(
				Collections.EMPTY_LIST);
		given(loadStoreFeaturesStrategy.loadFeatures(Collections.EMPTY_LIST, product)).willReturn(features);

		// when
		final FeatureList featureList = service.getFeatures(product);

		// then
		assertThat(featureList).isNotNull();
		assertThat(featureList.getFeatures()).isEqualTo(features);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenGettingFeaturesForNullProduct()
	{
		// given
		final ProductModel product = null;

		try
		{
			// when
			service.getFeatures(product);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e.getMessage()).isEqualTo("product can't be null");
		}
	}

	@Test
	public void shouldGetFeatureForGivenProductAndAssignment()
	{
		// given
		given(classificationClassesResolverStrategy.resolve(product)).willReturn(Collections.EMPTY_SET);
		given(classificationClassesResolverStrategy.getAllClassAttributeAssignments(Collections.EMPTY_SET)).willReturn(
				Collections.EMPTY_LIST);
		given(loadStoreFeaturesStrategy.loadFeatures(Collections.EMPTY_LIST, product)).willReturn(features);
		given(feature3.getClassAttributeAssignment()).willReturn(assignment);

		// when
		final Feature feature = service.getFeature(product, assignment);

		// then
		assertThat(feature).isNotNull();
		assertThat(feature).isEqualTo(feature3);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenGettingFeatureForNullAssignment()
	{
		// given
		final ClassAttributeAssignmentModel assignment = null;

		try
		{
			// when
			service.getFeature(product, assignment);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e.getMessage()).isEqualTo("assignment can't be null");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenGettingFeatureForNullProduct()
	{
		// given
		final ProductModel product = null;

		try
		{
			// when
			service.getFeature(product, assignment);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e.getMessage()).isEqualTo("product can't be null");
		}
	}

	@Test
	public void shouldSetFeatureForProduct()
	{
		// when
		service.setFeature(product, feature1);

		// then
		verify(loadStoreFeaturesStrategy, times(1)).storeFeatures(product, Collections.singletonList(feature1));
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenSettingFeatureWithNullFeature()
	{
		// given
		final UnlocalizedFeature feature = null;

		try
		{
			// when
			service.setFeature(product, feature);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e.getMessage()).isEqualTo("feature can't be null");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenSettingFeatureWithNullProduct()
	{
		// given
		final ProductModel product = null;

		try
		{
			// when
			service.setFeature(product, feature1);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e.getMessage()).isEqualTo("product can't be null");
		}
	}

	@Test
	public void shouldSetFeaturesForProduct() // NOPMD
	{
		// given
		final FeatureList featureList = new FeatureList(features);

		// when
		service.setFeatures(product, featureList);

		// then
		verify(loadStoreFeaturesStrategy, times(1)).storeFeatures(product, featureList.getFeatures());
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenSetFeaturesWithNullFeatureList()
	{
		// given
		final FeatureList featureList = null;

		try
		{
			// when
			service.setFeatures(product, featureList);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then

			assertThat(e.getMessage()).isEqualTo("feature list can't be null");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenSetFeaturesWithNullProduct()
	{
		// given
		final ProductModel product = null;
		final FeatureList featureList = new FeatureList(features);

		try
		{
			// when
			service.setFeatures(product, featureList);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then

			assertThat(e.getMessage()).isEqualTo("product can't be null");
		}
	}

	@Test
	public void shouldReplaceFeaturesForProduct() // NOPMD
	{
		// given
		given(classificationClassesResolverStrategy.resolve(product)).willReturn(Collections.EMPTY_SET);
		given(classificationClassesResolverStrategy.getAllClassAttributeAssignments(Collections.EMPTY_SET)).willReturn(
				Collections.EMPTY_LIST);

		final FeatureList featureList = new FeatureList(features);

		// when
		service.replaceFeatures(product, featureList);

		// then
		verify(loadStoreFeaturesStrategy, times(1)).replaceFeatures(Collections.EMPTY_LIST, product, featureList.getFeatures());
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenReplaceFeaturesWithNullFeatureList()
	{
		// given
		final FeatureList featureList = null;

		try
		{
			// when
			service.replaceFeatures(product, featureList);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then

			assertThat(e.getMessage()).isEqualTo("feature list can't be null");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenReplaceFeaturesWithNullProduct()
	{
		// given
		final ProductModel product = null;
		final FeatureList featureList = new FeatureList(features);

		try
		{
			// when
			service.replaceFeatures(product, featureList);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then

			assertThat(e.getMessage()).isEqualTo("product can't be null");
		}
	}
}
