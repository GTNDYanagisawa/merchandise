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
package de.hybris.platform.cockpit.coverage.strategies.impl;

import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.impl.AttributeProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SimpleCoverageCalculationStrategyTest
{
	private SimpleCoverageCalculationStrategy simpleStrategy;

	@Mock
	private TypeService typeService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ProductModel mockedProductModel;
	@Mock
	private AttributeProvider mockedAttributeProvider;
	@Mock
	private ItemModelContextImpl itemModelContext;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		simpleStrategy = new SimpleCoverageCalculationStrategy();
		simpleStrategy.setTypeService(typeService);
		simpleStrategy.setCommonI18nService(commonI18NService);
		simpleStrategy.setModelService(modelService);

		final AttributeDescriptorModel mockedCodeAttr = Mockito.mock(AttributeDescriptorModel.class);
		Mockito.when(mockedCodeAttr.getQualifier()).thenReturn("code");
		Mockito.when(typeService.getAttributeDescriptor("product", "code")).thenReturn(mockedCodeAttr);
		Mockito.when(mockedProductModel.getItemModelContext()).thenReturn(itemModelContext);


		((ItemModelContextImpl) ModelContextUtils.getItemModelContext(mockedProductModel))
				.setAttributeProvider(mockedAttributeProvider);
		Mockito.when(mockedProductModel.getItemtype()).thenReturn("product");

		Mockito.when(mockedAttributeProvider.getAttribute("code")).thenReturn(null);
	}

	@Test
	public void testCoverageCalculationSingleAttribute()
	{
		final Set<String> attributeQualifiers = new HashSet<String>();
		attributeQualifiers.add("code");
		simpleStrategy.setAttributeQualifiers(attributeQualifiers);

		double calculateCoverageIndex = simpleStrategy.calculate(mockedProductModel).getCoverageIndex();

		assertTrue("Expected 0% coverage but got " + calculateCoverageIndex, calculateCoverageIndex < 0.01);

		Mockito.when(modelService.getAttributeValue(mockedProductModel, "code")).thenReturn("somestring");

		calculateCoverageIndex = simpleStrategy.calculate(mockedProductModel).getCoverageIndex();
		assertTrue("Expected 100% coverage but got " + calculateCoverageIndex, calculateCoverageIndex > 0.99);
	}

	@Test
	public void testCoverageCalculationWOAttributes()
	{
		final double calculateCoverageIndex = simpleStrategy.calculate(mockedProductModel).getCoverageIndex();
		assertTrue("Expected 100% coverage but got " + calculateCoverageIndex, calculateCoverageIndex > 0.99);
	}
}
