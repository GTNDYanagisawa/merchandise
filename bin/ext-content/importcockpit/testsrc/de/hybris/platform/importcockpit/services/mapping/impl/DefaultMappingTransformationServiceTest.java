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
package de.hybris.platform.importcockpit.services.mapping.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultMappingTransformationServiceTest
{
	private static final String COLOR_QUALIFIER = "color";

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private DefaultMappingTransformationService transformationService;

	@Mock
	private ComposedTypeModel composedType;

	@Mock
	private VariantTypeModel variantType;

	@Mock
	private TypeService typeService;

	@Mock
	private VariantsService variantsService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test()
	{
		final AttributeDescriptorModel dummyDescriptor = Mockito.mock(AttributeDescriptorModel.class);
		final VariantAttributeDescriptorModel dummyVariantDescriptor = Mockito.mock(VariantAttributeDescriptorModel.class);
		when(dummyVariantDescriptor.getQualifier()).thenReturn(COLOR_QUALIFIER);

		when(transformationService.getTypeService()).thenReturn(typeService);
		when(transformationService.getVariantsService()).thenReturn(variantsService);
		when(typeService.getAttributeDescriptor(composedType, COLOR_QUALIFIER)).thenReturn(dummyDescriptor);
		assertThat(transformationService.getAttributeDescriptor(COLOR_QUALIFIER, composedType)).isEqualTo(dummyDescriptor);
		verify(variantsService, Mockito.times(0)).getVariantAttributesForVariantType(Matchers.<VariantTypeModel> any());

		when(variantsService.getVariantAttributesForVariantType(variantType)).thenReturn(
				Collections.<VariantAttributeDescriptorModel> singletonList(dummyVariantDescriptor));
		assertThat(transformationService.getAttributeDescriptor(COLOR_QUALIFIER, variantType)).isEqualTo(dummyVariantDescriptor);
		verify(typeService, Mockito.times(0)).getAttributeDescriptor(Matchers.anyString(), Matchers.anyString());
		verify(variantsService, Mockito.times(1)).getVariantAttributesForVariantType(Matchers.<VariantTypeModel> any());
	}
}
