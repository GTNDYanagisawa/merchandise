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

import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.*;

import de.hybris.platform.yb2bacceleratorfacades.product.ProductVariantOption;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.yb2bacceleratorfacades.product.populators.VariantMatrixFactoryUtil.getIndent;


/**
 * 
 * 
 * Populate the multi-dimensional variant tree for a given product.
 */
public class ProductVariantMatrixPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{
	private Populator<VariantValueCategoryModel, VariantMatrixElementData> variantMatrixElementPopulator;
    private Comparator<VariantValueCategoryModel> valueCategoryComparator;

    /**
     * Populates the productData with the categories information modeled as a tree.
     *
     * The resulting tree, modeled as a group of nested lists, where the outer list will have some
     * {@link VariantMatrixElementData} representing the categories with priority_1, and each of those contains a
     * list of {@link VariantMatrixElementData} representing the categories with priority_2, and so on.<br>
     * e.g.: A product with dimensions color/fit/size would return a list organized like this:
     * <BROWN:<normal:<7;8;9>; wide:<8;9;10>>; BLACK<normal:<7;8>>>
     *
     * @param productModel the product to take the data from.
     * @param productData the data to put the data in.
     * @throws ConversionException
     */
    @Override
	public void populate(final ProductModel productModel, final ProductData productData) throws ConversionException
	{

        final Collection<VariantProductModel> variants = getVariants(productModel);
        productData.setMultidimensional(CollectionUtils.isNotEmpty(variants));

        if (productData.getMultidimensional())
		{
            final VariantMatrixElementData nodeZero = createNode(null, null);

            // First tree branch must be influenced by the variant sent as a parameter,
            // because that will determine the values (like the code) of the first elements in the tree.
            createNodesForVariant((VariantProductModel) productModel, nodeZero);

			for (final VariantProductModel variant : variants)
			{
				if (variant instanceof GenericVariantProductModel)
				{
                    //Don't process the first variant again
                    if (!variant.getCode().equals(productModel.getCode())) {
                        createNodesForVariant(variant, nodeZero);
                    }
                }
			}

            orderTree(nodeZero.getElements());
			productData.setVariantMatrix(nodeZero.getElements());
		}
	}

     protected void createNodesForVariant(VariantProductModel variant, VariantMatrixElementData currentParentNode) {

        final List<VariantValueCategoryModel> valuesCategories = getVariantValuesCategories(variant);
        for (VariantValueCategoryModel valueCategory : valuesCategories){

            VariantMatrixElementData existingNode = getExistingNode(currentParentNode, valueCategory);

            if (existingNode == null) {
                VariantMatrixElementData createdNode = createNode(currentParentNode, valueCategory);
                createdNode.getVariantOption().setCode(variant.getCode());
                currentParentNode = createdNode;
            } else {
                currentParentNode = existingNode;
            }
        }

    }


    protected Collection<VariantProductModel> getVariants(final ProductModel productModel)
	{
		Collection<VariantProductModel> variants = Collections.<VariantProductModel>emptyList();
		if (productModel instanceof GenericVariantProductModel)
		{
			variants = ((GenericVariantProductModel) productModel).getBaseProduct().getVariants();
		}

		return variants;
	}

	/**
	 * Get the list of {@link VariantValueCategoryModel} related to a specific {@link GenericVariantProductModel}.
	 * 
	 * @param productModel
	 *           the variant product.
	 * @return The variant value categories, ordered by variant category priority.
	 */
	protected List<VariantValueCategoryModel> getVariantValuesCategories(final ProductModel productModel)
	{
		final List<VariantValueCategoryModel> variantValueCategories = new ArrayList<>();
		for (final CategoryModel categoryProductModel : productModel.getSupercategories())
		{
			if (categoryProductModel instanceof VariantValueCategoryModel)
			{
				variantValueCategories.add((VariantValueCategoryModel) categoryProductModel);
			}
		}
		Collections.sort(variantValueCategories, getValueCategoryComparator());

		return variantValueCategories;
	}


    protected VariantMatrixElementData createNode(VariantMatrixElementData parent, final VariantValueCategoryModel valueCategory)
	{
        VariantMatrixElementData elementData = new VariantMatrixElementData();
        elementData.setElements(new ArrayList<VariantMatrixElementData>());

        if (parent != null){
            elementData.setIsLeaf(true);
            elementData.setElements(new ArrayList<VariantMatrixElementData>());
            elementData.setVariantOption(new VariantOptionData());
            getVariantMatrixElementPopulator().populate(valueCategory, elementData);

            parent.setIsLeaf(false);
            parent.getElements().add(elementData);
        }

        return elementData;
	}


	/**
	 * Sort the tree on each level, by tree element sequence. The method is recursive in a way lists will be sorted
	 * bottom-up (i.e. list of leaves will be organized before of its parents).
	 */
	protected void orderTree(final List<VariantMatrixElementData> elementsList)
	{
		for (final VariantMatrixElementData element : elementsList)
		{
			if (CollectionUtils.isNotEmpty(element.getElements()))
			{
				orderTree(element.getElements());
			}
		}
		Collections.sort(elementsList, new Comparator<VariantMatrixElementData>(){
            @Override
            public int compare(final VariantMatrixElementData e1, final VariantMatrixElementData e2)
            {
                return e1.getVariantValueCategory().getSequence() - e2.getVariantValueCategory().getSequence();
            }
        });
	}

	/**
	 * Finds inside the elements of the parent node if there is a node for this valueCategory
     */
	protected VariantMatrixElementData getExistingNode(final VariantMatrixElementData parent,
                                                       final VariantValueCategoryModel valueCategory)
	{
		for (final VariantMatrixElementData elementData : parent.getElements())
		{
			if (elementData.getVariantValueCategory().getName().equals(valueCategory.getName()))
			{
				return elementData;
			}
		}
		return null;
	}

    protected Comparator<VariantValueCategoryModel> getValueCategoryComparator() {
        return valueCategoryComparator;
    }

    @Required
    public void setValueCategoryComparator(Comparator<VariantValueCategoryModel> valueCategoryComparator) {
        this.valueCategoryComparator = valueCategoryComparator;
    }

	protected Populator<VariantValueCategoryModel, VariantMatrixElementData> getVariantMatrixElementPopulator()
	{
		return variantMatrixElementPopulator;
	}

	@Required
	public void setVariantMatrixElementPopulator(
			final Populator<VariantValueCategoryModel, VariantMatrixElementData> variantMatrixElementPopulator)
	{
		this.variantMatrixElementPopulator = variantMatrixElementPopulator;
	}


}