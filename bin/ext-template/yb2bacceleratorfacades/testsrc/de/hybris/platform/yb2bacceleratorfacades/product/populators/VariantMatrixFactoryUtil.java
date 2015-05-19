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

import com.google.common.collect.Iterables;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantCategoryModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.entity.VariantValueCategoryModelPriorityComparator;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VariantMatrixFactoryUtil {

    private static final String INDENT_CHARACTER = "***";
    public static final String BASE_PRODUCT_CODE = "product";
    public static final String FIT = "3-Fit";
    public static final String COLOR = "1-Color";
    public static final String SIZE = "2-Size";
    public static final String DIMENSION4 = "4-Size";
    public static final String DIMENSION5 = "5-Size";
    public static final String DIMENSION6 = "6-Size";
    public static final String[] dimensions = new String[]
            {COLOR, FIT, SIZE, DIMENSION4, DIMENSION5, DIMENSION6};

    public static final Map<String, List<String>> categoryNamesMap = new HashMap<>();
    static {
        categoryNamesMap.put(COLOR, Arrays.asList("blue"));
        categoryNamesMap.put(FIT, Arrays.asList("Narrow", "Regular"));
        categoryNamesMap.put(SIZE, Arrays.asList("7", "8", "9"));
        categoryNamesMap.put(DIMENSION4, Arrays.asList("41", "42", "43", "44"));
        categoryNamesMap.put(DIMENSION5, Arrays.asList("51", "52", "53", "54", "55"));
        categoryNamesMap.put(DIMENSION6, Arrays.asList("61", "62", "63"));
    }
    /**
     * Generate sample variants, using the category tree and adds those variants to the productmodel.
     *
     * @param baseProductModel the product to generate variants for
     * @param dimension        the amount of dimensions to create
     * @return the amount of variants created
     */
    public static int addVariantsWithValueCategories(final ProductModel baseProductModel, final int dimension) {

        final List<List<CategoryModel>> allPermutations = createCategoriyModelTree(dimension, categoryNamesMap);
        int id_sequence = 0;
        for (final List<CategoryModel> valueCategories : allPermutations) {
            mockNewVariantModel(baseProductModel, valueCategories, id_sequence++);
        }

        Collections.shuffle((List<?>) baseProductModel.getVariants(), new Random(10));
        return allPermutations.size();

    }

    /**
     * Creates the structure of the categories and value categories.
     */
    public static List<List<CategoryModel>> createCategoriyModelTree(final int dimension, Map<String, List<String>> categoryNamesMap) {

        List<List<CategoryModel>> allPermutations = new ArrayList<>();


        for (int i = 0; i <= dimension; i++) {
            final String categoryName = dimensions[i]; // Each dimention/category: Color,Fit,Size,etc.

            final VariantCategoryModel parentCategory = mock(VariantCategoryModel.class);
            when(parentCategory.getName()).thenReturn(categoryName); // all valueCategories share the same parent/category

            final List<CategoryModel> variantValueCategories = new ArrayList<>();

            //Creates valueCategories based on the map definition
            int sequence = 1;
            for (final String valueCategoryName : categoryNamesMap.get(categoryName)) {
                final VariantValueCategoryModel valueCategory = mockNewValueCategory(parentCategory, sequence, valueCategoryName);
                variantValueCategories.add(valueCategory);
                sequence++;
            }

            allPermutations = multiplyPermutations(allPermutations, variantValueCategories);

        }

        return allPermutations;

    }

    /**
     * Combines the lists of generated values this way:
     * <p/>
     * If allPermutations = [[1,2],[1,3]] and valueCategories = [4,5] the result is = [[1,2,4],[1,3,4],[1,2,5],[1,3,5]]
     *
     * @param allPermutations the existing Permutations
     * @param valueCategories the new Permutation elements to be combined
     * @return the total set of Permutations posible.
     */
    public static List<List<CategoryModel>> multiplyPermutations(final List<List<CategoryModel>> allPermutations,
                                                                 final List<CategoryModel> valueCategories) {
        final List<List<CategoryModel>> permutationsResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allPermutations)) {
            for (final CategoryModel variantValueCategoryModel : valueCategories) {
                for (final List<CategoryModel> existingPermutation : allPermutations) {
                    final List<CategoryModel> newPermutation = new ArrayList<>();
                    newPermutation.addAll(existingPermutation);
                    newPermutation.add(newPermutation.size(), variantValueCategoryModel);// Seems like the order is important
                    permutationsResult.add(newPermutation);
                }
            }
        } else {
            permutationsResult.add(valueCategories);
        }

        return permutationsResult;
    }

    public static GenericVariantProductModel mockNewVariantModel(final ProductModel baseProductModel,
                                                                 final List<CategoryModel> valueCategories, final int id) {

        GenericVariantProductModel variantModel;
        variantModel = new GenericVariantProductModel();
        variantModel.setSupercategories(valueCategories);
        variantModel.setStockLevels(new TreeSet<StockLevelModel>());
        variantModel.setCode(BASE_PRODUCT_CODE + id);
        variantModel.setBaseProduct(baseProductModel);
        variantModel.getBaseProduct().getVariants().add(variantModel);

        return variantModel;
    }


    public static ProductModel mockNewVariantProductModelWithVariantsAndValueCategories(final int dimension) {

        final ProductModel productModel = mockNewProductModel();
        addVariantsWithValueCategories(productModel, dimension);

        // from 0 to size - 1
        int randomIndex = (int) Math.floor(Math.random() * (productModel.getVariants().size()));


        return Iterables.get(productModel.getVariants(), randomIndex);
    }

    public static ProductData mockProductDataWithTree(ProductModel firstVariant){
        ProductData productData = new ProductData();

        ProductVariantMatrixPopulator<ProductModel, ProductData> treePopulator = new ProductVariantMatrixPopulator<>();
        treePopulator.setVariantMatrixElementPopulator(new VariantMatrixElementPopulator<VariantValueCategoryModel, VariantMatrixElementData>());
        treePopulator.setValueCategoryComparator(mock(VariantValueCategoryModelPriorityComparator.class));

        treePopulator.populate(firstVariant, productData);

        return productData;
    }


    public static ProductModel mockNewProductModel() {
        final ProductModel baseProductModel = mock(ProductModel.class);
        when(baseProductModel.getCode()).thenReturn(BASE_PRODUCT_CODE);
        when(baseProductModel.getName()).thenReturn("Base Product Name");
        when(baseProductModel.getSummary()).thenReturn("Base Product Summary Of Variant");
        when(baseProductModel.getVariants()).thenReturn((Collection) new ArrayList<GenericVariantProductModel>());

        return baseProductModel;
    }


    public static VariantValueCategoryModel mockNewValueCategory(final VariantCategoryModel parentCategory, final int sequence,
                                                                 final String valueCategoryName) {
        final VariantValueCategoryModel valueCategory = mock(VariantValueCategoryModel.class);
        when(valueCategory.getName()).thenReturn(valueCategoryName);
        when(valueCategory.getSequence()).thenReturn(sequence);
        when(valueCategory.getSupercategories()).thenReturn(Collections.singletonList((CategoryModel) parentCategory));
        return valueCategory;
    }

    protected static String getIndent(final int level) {
        String indent = INDENT_CHARACTER;
        for (int i = 0; i < level; i++) {
            indent += INDENT_CHARACTER;

        }
        return indent;
    }


}
