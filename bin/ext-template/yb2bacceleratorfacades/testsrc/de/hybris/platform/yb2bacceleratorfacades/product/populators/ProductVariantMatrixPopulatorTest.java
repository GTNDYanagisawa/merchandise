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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductVariantMatrixPopulatorTest
{


	@Mock
	Comparator<VariantValueCategoryModel> valueCategoryComparator;

	@Mock
	PriceInformation priceInformation;


	@InjectMocks
	ProductVariantMatrixPopulator<ProductModel, ProductData> populator = new ProductVariantMatrixPopulator<>();

	@Before
	public void setup()
	{
		populator.setVariantMatrixElementPopulator(new VariantMatrixElementPopulator());

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
			System.out.println("Processing Variant:" + variant.getCode() + " - Dimension: " + dimension);

			productData = new ProductData();
			populator.populate(variant, productData);

			validateVariants(productData.getVariantMatrix(), 0, variant);
		}


		assertFalse(CollectionUtils.isEmpty(productData.getVariantMatrix()));
		assertTrue(productData.getMultidimensional());


	}

	protected ValidationStatus validateVariants(final List<VariantMatrixElementData> variantMatrix, final int level,
			final ProductModel selectedVariant)
	{
		final ValidationStatus status = new ValidationStatus();

		final Set<String> differentCodes = new HashSet<String>();
		boolean isCodePresentInChildren = false;
		int totalVariantsDataCreated = 0;
		for (int valueIndex = 0; valueIndex < variantMatrix.size(); valueIndex++)
		{
			final VariantMatrixElementData element = variantMatrix.get(valueIndex);


			final VariantOptionData variantOption = element.getVariantOption();

			// if it has no elements then is must be a leaf
			assertEquals(CollectionUtils.isEmpty(element.getElements()), element.getIsLeaf().booleanValue());


			differentCodes.add(element.getVariantOption().getCode());

			//if the element has the same code as the variant
			status.setCodePresent(element.getVariantOption().getCode().equals(selectedVariant.getCode()));

			if (status.isCodePresent())
			{
				// only one element can have the same code as the selectedVariant
				assertFalse(isCodePresentInChildren);
				isCodePresentInChildren = true;
			}


			if (!element.getIsLeaf())
			{
				assertNotNull(variantOption.getCode());
				final ValidationStatus childrenStatus = validateVariants(element.getElements(), level + 1, selectedVariant);

				//if the parent has the code then one of the children must have it too, and viceversa.
				assertThat(status.isCodePresent(), is(childrenStatus.isCodePresent()));
			}


			final String valueCategoryName = categoryNamesMap.get(dimensions[level]).get(valueIndex);
			assertEquals(valueCategoryName, element.getVariantValueCategory().getName());

			totalVariantsDataCreated++;
		}

		// all codes must be different in the list of elements
		assertThat(differentCodes.size(), is(variantMatrix.size()));

		status.setCodePresent(isCodePresentInChildren);
		status.setTotalVariantsDataCreated(totalVariantsDataCreated);
		return status;

	}


	private class ValidationStatus
	{
		private int totalVariantsDataCreated = 0;
		private boolean isCodePresent = false;

		public int getTotalVariantsDataCreated()
		{
			return totalVariantsDataCreated;
		}

		public void setTotalVariantsDataCreated(final int totalVariantsDataCreated)
		{
			this.totalVariantsDataCreated = totalVariantsDataCreated;
		}

		public boolean isCodePresent()
		{
			return isCodePresent;
		}

		public void setCodePresent(final boolean isCodePresent)
		{
			this.isCodePresent = isCodePresent;
		}
	}
}
