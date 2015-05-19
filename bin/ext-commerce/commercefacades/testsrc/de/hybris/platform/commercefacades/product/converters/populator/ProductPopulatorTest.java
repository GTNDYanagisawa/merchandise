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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;



@UnitTest
public class ProductPopulatorTest
{
	private ProductPopulator productPopulator = new ProductPopulator();
	private AbstractPopulatingConverter<ProductModel, ProductData> productConverter;

	@Mock
	private Populator<ProductModel, ProductData> productBasicPopulator;
	@Mock
	private Populator<ProductModel, ProductData> variantSelectedPopulator;
	@Mock
	private Populator<ProductModel, ProductData> productPrimaryImagePopulator;
	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productPopulator.setProductBasicPopulator(productBasicPopulator);
		productPopulator.setProductPrimaryImagePopulator(productPrimaryImagePopulator);
		productPopulator.setVariantSelectedPopulator(variantSelectedPopulator);
		productPopulator.setProductModelUrlResolver(productModelUrlResolver);

		productConverter = new ConverterFactory<ProductModel, ProductData, ProductPopulator>().create(ProductData.class, productPopulator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenSourceIsNull()
	{
		productConverter.convert(null, mock(ProductData.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenPrototypeIsNull()
	{
		productConverter.convert(mock(ProductModel.class), null);
	}


	@Test
	public void testConvert()
	{
		final ProductModel source = mock(ProductModel.class);

		final ProductData result = productConverter.convert(source);

		Assert.assertNotNull(result);
		verify(productBasicPopulator).populate(source, result);
		verify(productPrimaryImagePopulator).populate(source, result);
		verify(variantSelectedPopulator).populate(source, result);
	}
}
