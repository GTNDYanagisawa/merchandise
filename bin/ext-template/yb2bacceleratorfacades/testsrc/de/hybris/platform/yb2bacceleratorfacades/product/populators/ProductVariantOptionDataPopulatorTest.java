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
package de.hybris.platform.yb2bacceleratorfacades.product.populators;

import static de.hybris.platform.yb2bacceleratorfacades.product.populators.VariantMatrixFactoryUtil.categoryNamesMap;
import static de.hybris.platform.yb2bacceleratorfacades.product.populators.VariantMatrixFactoryUtil.dimensions;
import static de.hybris.platform.yb2bacceleratorfacades.product.populators.VariantMatrixFactoryUtil.mockNewVariantProductModelWithVariantsAndValueCategories;
import static de.hybris.platform.yb2bacceleratorfacades.product.populators.VariantMatrixFactoryUtil.mockProductDataWithTree;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.yb2bacceleratorfacades.product.ProductVariantOption;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductVariantOptionDataPopulatorTest
{

	private static final String INDENT_CHARACTER = "***";

	@Mock
	ConfigurablePopulator<VariantProductModel, VariantOptionData, ProductVariantOption> variantOptionDataPopulator;

	@InjectMocks
	ProductVariantOptionDataPopulator<ProductModel, ProductData> populator = new ProductVariantOptionDataPopulator<>();

	@Before
	public void setup()
	{
		doAnswer(new Answer()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final String code = ((VariantProductModel) args[0]).getCode();
				final VariantOptionData optionData = (VariantOptionData) args[1];
				optionData.setCode(code);
				optionData.setUrl("url" + code);
				optionData.setVariantOptionQualifiers(new ArrayList<VariantOptionQualifierData>());
				return null;
			}
		}).when(variantOptionDataPopulator).populate(any(VariantProductModel.class), any(VariantOptionData.class), any(List.class));

	}

	@Test
	public void shouldPopulateNDimension()
	{

		ProductData productData = null;

		//Should work for all dimensions
		for (int dimension = 0; dimension < dimensions.length; dimension++)
		{

			//Should work for any of the variants regardless of their position in the list
			final ProductModel variant = mockNewVariantProductModelWithVariantsAndValueCategories(dimension);

			productData = mockProductDataWithTree(variant);

			populator.populate(variant, productData);
			validateVariants(productData.getVariantMatrix(), 0);
		}


		assertFalse(CollectionUtils.isEmpty(productData.getVariantMatrix()));
		assertTrue(productData.getMultidimensional());


	}


	protected void validateVariants(final List<VariantMatrixElementData> variantMatrix, final int level)
	{

		for (int valueIndex = 0; valueIndex < variantMatrix.size(); valueIndex++)
		{
			final VariantMatrixElementData element = variantMatrix.get(valueIndex);

			final VariantOptionData parentVariantOption = element.getVariantOption();
			final VariantOptionData variantOption = parentVariantOption;

			if (!element.getIsLeaf())
			{
				assertNotNull(variantOption.getCode());

				//the parent has the same code and properties as one and just one of its children.
				int count = 0;
				for (final VariantMatrixElementData child : element.getElements())
				{
					if (child.getVariantOption().getCode().equals(parentVariantOption.getCode())
							&& child.getVariantOption().getVariantOptionQualifiers()
									.equals(parentVariantOption.getVariantOptionQualifiers())
							&& child.getVariantOption().getUrl().equals(parentVariantOption.getUrl()))
					{
						count++;
					}
				}
				assertThat(count, is(1));



				validateVariants(element.getElements(), level + 1);
			}


			final String valueCategoryName = categoryNamesMap.get(dimensions[level]).get(valueIndex);
			assertEquals(valueCategoryName, element.getVariantValueCategory().getName());


		}


	}

	protected String getIndent(final int level)
	{
		String indent = INDENT_CHARACTER;
		for (int i = 0; i < level; i++)
		{
			indent += INDENT_CHARACTER;

		}
		return indent;
	}


}
