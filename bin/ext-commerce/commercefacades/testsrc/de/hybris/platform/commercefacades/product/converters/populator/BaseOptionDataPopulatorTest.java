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
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class BaseOptionDataPopulatorTest
{
	private static final String VARIANT_TYPE_CODE = "vType";

	private final BaseOptionDataPopulator baseOptionDataPopulator = new BaseOptionDataPopulator();
	private AbstractPopulatingConverter<VariantProductModel, BaseOptionData> baseOptionDataConverter;
	@Mock
	private AbstractPopulatingConverter<VariantProductModel, VariantOptionData> variantOptionDataConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		baseOptionDataPopulator.setVariantOptionDataConverter(variantOptionDataConverter);

		baseOptionDataConverter = new ConverterFactory<VariantProductModel, BaseOptionData, BaseOptionDataPopulator>().create(
				BaseOptionData.class, baseOptionDataPopulator);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenSourceIsNull()
	{
		baseOptionDataConverter.convert(null, mock(BaseOptionData.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenPrototypeIsNull()
	{
		baseOptionDataConverter.convert(mock(VariantProductModel.class), null);
	}


	@Test
	public void testConvert()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);
		final VariantOptionData variantOptionData = mock(VariantOptionData.class);


		given(source.getBaseProduct()).willReturn(baseProduct);
		given(variantTypeModel.getCode()).willReturn(VARIANT_TYPE_CODE);
		given(baseProduct.getVariantType()).willReturn(variantTypeModel);
		given(variantOptionDataConverter.convert(source)).willReturn(variantOptionData);

		final BaseOptionData result = baseOptionDataConverter.convert(source);

		Assert.assertEquals(variantOptionData, result.getSelected());
		Assert.assertEquals(VARIANT_TYPE_CODE, result.getVariantType());
	}


	@Test
	public void testConvertWithPopulator()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);
		final VariantOptionData variantOptionData = mock(VariantOptionData.class);

		given(source.getBaseProduct()).willReturn(baseProduct);
		given(variantTypeModel.getCode()).willReturn(VARIANT_TYPE_CODE);
		given(baseProduct.getVariantType()).willReturn(variantTypeModel);
		given(variantOptionDataConverter.convert(source)).willReturn(variantOptionData);

		final BaseOptionData result = new BaseOptionData();
		baseOptionDataConverter.populate(source, result);
		Assert.assertEquals(variantOptionData, result.getSelected());
		Assert.assertEquals(VARIANT_TYPE_CODE, result.getVariantType());
	}

}
