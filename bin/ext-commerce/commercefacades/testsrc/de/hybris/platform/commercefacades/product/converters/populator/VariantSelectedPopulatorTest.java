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
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link VariantSelectedPopulator}
 */
@UnitTest
public class VariantSelectedPopulatorTest
{
	@Mock
	private Converter<VariantProductModel, BaseOptionData> baseOptionDataConverter;

	private VariantSelectedPopulator variantSelectedPopulator;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		variantSelectedPopulator = new VariantSelectedPopulator();
		variantSelectedPopulator.setBaseOptionDataConverter(baseOptionDataConverter);
	}

	@Test
	public void testPopulate()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);
		final VariantProductModel variantProductModel = mock(VariantProductModel.class);
		final BaseOptionData baseOptionData = mock(BaseOptionData.class);

		given(source.getBaseProduct()).willReturn(baseProduct);
		given(baseProduct.getVariants()).willReturn(Collections.singleton(source));
		given(baseOptionDataConverter.convert(source)).willReturn(baseOptionData);
		given(source.getVariantType()).willReturn(variantTypeModel);
		given(source.getVariants()).willReturn(Collections.singleton(variantProductModel));

		final ProductData result = new ProductData();
		variantSelectedPopulator.populate(source, result);

		Assert.assertEquals(1, result.getBaseOptions().size());
		Assert.assertTrue(result.getBaseOptions().contains(baseOptionData));
	}
}
