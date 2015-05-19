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
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;



@UnitTest
public class ProductReferencePopulatorTest
{
	private static final String DESCRIPTION = "descText";
	private static final Integer QUANTITY = Integer.valueOf(10);

	private AbstractPopulatingConverter<ProductReferenceModel, ProductReferenceData> productReferenceConverter =
				new ConverterFactory<ProductReferenceModel, ProductReferenceData, ProductReferencePopulator>().create(ProductReferenceData.class, new ProductReferencePopulator());

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testConvert()
	{
		final ProductReferenceModel source = mock(ProductReferenceModel.class);

		given(source.getQuantity()).willReturn(QUANTITY);
		given(source.getDescription()).willReturn(DESCRIPTION);
		given(source.getReferenceType()).willReturn(ProductReferenceTypeEnum.ACCESSORIES);

		final ProductReferenceData result = productReferenceConverter.convert(source);

		Assert.assertEquals(QUANTITY, result.getQuantity());
		Assert.assertEquals(DESCRIPTION, result.getDescription());
		Assert.assertEquals(ProductReferenceTypeEnum.ACCESSORIES, result.getReferenceType());
	}
}
