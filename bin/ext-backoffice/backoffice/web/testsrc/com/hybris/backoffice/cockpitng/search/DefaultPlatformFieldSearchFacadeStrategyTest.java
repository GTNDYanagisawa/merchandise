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
package com.hybris.backoffice.cockpitng.search;

import com.google.common.collect.Sets;
import com.hybris.backoffice.cockpitng.dataaccess.facades.search.DefaultPlatformFieldSearchFacadeStrategy;
import com.hybris.backoffice.cockpitng.search.builder.impl.GenericConditionQueryBuilder;
import com.hybris.backoffice.cockpitng.search.builder.impl.LocalizedGenericConditionQueryBuilder;
import com.hybris.cockpitng.dataaccess.facades.search.FieldSearchFacadeStrategy;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SimpleSearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.genericsearch.GenericSearchService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import junit.framework.Assert;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@IntegrationTest
public class DefaultPlatformFieldSearchFacadeStrategyTest extends ServicelayerTransactionalTest
{

	private static final String TEST_CATALOG_ID = "testCatalog";
	private static final String TEST_VERSION = "summer";
	private static final int PRODUCT_COUNT = 30;
	private static final String TEST_PRODUCT_CODE = "testProductCode_";
	private static final String TEST_PRODUCT_EAN = "testProductEAN_";
	private static final String TEST_MANUFACTURER = "XYZ";

	private FieldSearchFacadeStrategy<ItemModel> fieldSearchFacadeStrategy;
	@Resource
	private GenericSearchService genericSearchService;
	@Resource
	private ModelService modelService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private I18NService i18nService;
	@Resource
	private TypeService typeService;

	private final Set<Character> queryBuilderSeparators = Sets.newHashSet(' ', ',', ';', '\t', '\n', '\r');


	@Before
	public void setUpTestData()
	{
		fieldSearchFacadeStrategy = new DefaultPlatformFieldSearchFacadeStrategy<>();
		((DefaultPlatformFieldSearchFacadeStrategy) fieldSearchFacadeStrategy).setGenericSearchService(genericSearchService);

		final GenericConditionQueryBuilder genericConditionBuilder = new GenericConditionQueryBuilder();
		genericConditionBuilder.setTypeService(typeService);
		genericConditionBuilder.setSeparators(queryBuilderSeparators);
		((DefaultPlatformFieldSearchFacadeStrategy) fieldSearchFacadeStrategy).setGenericQueryBuilder(genericConditionBuilder);


		final LocalizedGenericConditionQueryBuilder localizedConditionQueryBuilder = new LocalizedGenericConditionQueryBuilder();
		localizedConditionQueryBuilder.setCommonI18NService(commonI18NService);
		localizedConditionQueryBuilder.setI18nService(i18nService);
		localizedConditionQueryBuilder.setTypeService(typeService);
		localizedConditionQueryBuilder.setSeparators(queryBuilderSeparators);

		((DefaultPlatformFieldSearchFacadeStrategy) fieldSearchFacadeStrategy)
				.setLocalizedQueryBuilder(localizedConditionQueryBuilder);
		((DefaultPlatformFieldSearchFacadeStrategy) fieldSearchFacadeStrategy).setTypeService(typeService);

		final CatalogModel testCatalog = createTestCatalog();
		final CatalogVersionModel testCatalogVersion = createTestCatalogVersion(testCatalog);
		for (int i = 0; i < PRODUCT_COUNT; i++)
		{
			createTestProduct(i, testCatalogVersion);
		}
		modelService.saveAll();
	}

	private void createTestProduct(final int index, final CatalogVersionModel testCatalogVersion)
	{
		final ProductModel product = modelService.create(ProductModel.class);
		product.setCode(TEST_PRODUCT_CODE + index);
		product.setEan(TEST_PRODUCT_EAN + index);
		product.setManufacturerAID(TEST_MANUFACTURER);
		product.setManufacturerName(TEST_MANUFACTURER);
		product.setCatalogVersion(testCatalogVersion);
	}

	private CatalogVersionModel createTestCatalogVersion(final CatalogModel testCatalog)
	{
		final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setCatalog(testCatalog);
		catalogVersion.setVersion(TEST_VERSION);
		return catalogVersion;
	}

	private CatalogModel createTestCatalog()
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId(TEST_CATALOG_ID);
		return catalog;
	}

	@Test
	public void testFetchAllProducts() // NOPMD
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(PRODUCT_COUNT);

		Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);
		Assertions.assertThat(pageable).isNotNull();
		Assertions.assertThat(pageable.getCurrentPage()).hasSize(PRODUCT_COUNT);
		Assertions.assertThat(pageable.hasNextPage()).isFalse();

		sqd.setPageSize(31);
		pageable = fieldSearchFacadeStrategy.search(sqd);
		Assertions.assertThat(pageable.getCurrentPage()).hasSize(PRODUCT_COUNT);
		Assertions.assertThat(pageable.hasNextPage()).isFalse();
	}

	@Test
	public void testFetchProductsPaging() // NOPMD
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(5);

		// take first 5
		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);
		Assertions.assertThat(pageable).isNotNull();
		Assertions.assertThat(pageable.getCurrentPage()).hasSize(5);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		Assertions.assertThat(pageable.hasPreviousPage()).isFalse();
		Assertions.assertThat(pageable.previousPage()).isEmpty();

		// 10
		Assertions.assertThat(pageable.nextPage()).hasSize(5);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();

		// 15
		Assertions.assertThat(pageable.nextPage()).hasSize(5);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();

		// 20
		Assertions.assertThat(pageable.nextPage()).hasSize(5);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();

		// 25
		final List<ItemModel> page25_1 = pageable.nextPage();
		Assertions.assertThat(page25_1).hasSize(5);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();

		// 25 again
		final List<ItemModel> page25_2 = pageable.getCurrentPage();
		Assertions.assertThat(page25_2).hasSize(5);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		Assertions.assertThat(page25_1).isSameAs(page25_2);

		// 30
		Assertions.assertThat(pageable.nextPage()).hasSize(5);
		Assertions.assertThat(pageable.hasNextPage()).isFalse();
		Assertions.assertThat(pageable.nextPage()).isEmpty();

		pageable.setPageNumber(10);
		Assertions.assertThat(pageable.getCurrentPage()).isEmpty();
	}

	@Test
	public void testFetchProductsPaging2() // NOPMD
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(20);

		// take first 20
		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);
		Assertions.assertThat(pageable).isNotNull();
		Assertions.assertThat(pageable.getCurrentPage()).hasSize(20);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();

		// remaining 10
		Assertions.assertThat(pageable.nextPage()).hasSize(10);
		Assertions.assertThat(pageable.hasNextPage()).isFalse();
	}


	@Test
	public void testFetchProductsGetPageByNumber() // NOPMD
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(11);

		// take last 8 (page no 2)
		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);
		Assertions.assertThat(pageable).isNotNull();
		final List<ItemModel> nextpage = pageable.nextPage();
		Assertions.assertThat(nextpage).hasSize(11);
		Assertions.assertThat(pageable.hasPreviousPage()).isTrue();
		Assertions.assertThat(pageable.hasNextPage()).isTrue();

		final List<ItemModel> last = pageable.nextPage();
		Assertions.assertThat(last).hasSize(8);
		Assertions.assertThat(pageable.hasPreviousPage()).isTrue();
		Assertions.assertThat(pageable.hasNextPage()).isFalse();

		final List<ItemModel> lastpage2 = pageable.getCurrentPage();
		Assertions.assertThat(last).isSameAs(lastpage2);
		Assertions.assertThat(pageable.hasNextPage()).isFalse();
		Assertions.assertThat(pageable.hasPreviousPage()).isTrue();

		final List<ItemModel> middlepage = pageable.previousPage();
		Assertions.assertThat(middlepage).hasSize(11);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		Assertions.assertThat(pageable.hasPreviousPage()).isTrue();

		final List<ItemModel> middlepage2 = pageable.getCurrentPage();
		Assertions.assertThat(middlepage).isSameAs(middlepage2);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		Assertions.assertThat(pageable.hasPreviousPage()).isTrue();

		final List<ItemModel> firstPage = pageable.previousPage();
		Assertions.assertThat(firstPage).hasSize(11);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		Assertions.assertThat(pageable.hasPreviousPage()).isFalse();

		final List<ItemModel> firstPage2 = pageable.getCurrentPage();
		Assertions.assertThat(firstPage).isSameAs(firstPage2);
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		Assertions.assertThat(pageable.hasPreviousPage()).isFalse();

		Assertions.assertThat(pageable.previousPage()).isEmpty();
	}


	@Test
	public void testFetchProductsGetPageByTooHighNumber() // NOPMD
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(10);

		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);

		final List<ItemModel> firstPage = pageable.getCurrentPage();
		// we only have 3 pages
		pageable.setPageNumber(100);
		Assertions.assertThat(pageable.getCurrentPage()).isEmpty();

		pageable.setPageNumber(0);
		final List<ItemModel> firstPageAgain = pageable.getCurrentPage();
		Assertions.assertThat(firstPage).isEqualTo(firstPageAgain);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFetchProductsWithNegativePageSize() // NOPMD
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(-1);

		//O-o!
		fieldSearchFacadeStrategy.search(sqd);
	}

	@Test
	public void testFetchProductsByCodeEqual() // NOPMD
	{
		final String code = "testProductCode_10";

		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(10);
		sqd.setAttributes(Collections.singletonList(new SearchAttributeDescriptor("code")));
		sqd.setSearchQueryText(code);

		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);
		Assertions.assertThat(pageable.getCurrentPage()).hasSize(1);
		final ProductModel testProduct10 = (ProductModel) (pageable.getCurrentPage().get(0));
		Assertions.assertThat(testProduct10.getCode()).isEqualTo(code);
	}

	@Test
	public void testFetchProductsByCodeOREanContains() // NOPMD
	{
		final String text = "EAN";

		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setPageSize(PRODUCT_COUNT);

		sqd.setAttributes(Arrays.asList(new SearchAttributeDescriptor("code"), new SearchAttributeDescriptor("ean")));
		sqd.setSearchQueryText(text);
		sqd.setValueComparisonOperator(ValueComparisonOperator.CONTAINS);

		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);
		Assertions.assertThat(pageable.getCurrentPage()).hasSize(30);
	}

	@Test
	public void testChangePageSize()
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setValueComparisonOperator(ValueComparisonOperator.CONTAINS);
		sqd.setPageSize(14);

		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);

		// we have 30 items , initial page size = 14 (3 pages)

		Assertions.assertThat(pageable.getCurrentPage()).hasSize(14);
		Assert.assertEquals(PRODUCT_COUNT, pageable.getTotalCount());
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		Assertions.assertThat(pageable.nextPage()).hasSize(14);

		// has last page (2 items)
		Assertions.assertThat(pageable.hasNextPage()).isTrue();
		// lets change page size
		final List<ItemModel> afterPageSizeChanged = pageable.setPageSize(16);
		Assertions.assertThat(afterPageSizeChanged).hasSize(16);
		Assertions.assertThat(pageable.getCurrentPage()).isSameAs(afterPageSizeChanged);

		// no items left
		Assertions.assertThat(pageable.hasNextPage()).isFalse();
		Assertions.assertThat(pageable.hasPreviousPage()).isTrue();

		// prev page = first page
		Assertions.assertThat(pageable.previousPage()).hasSize(16);
		Assertions.assertThat(pageable.hasPreviousPage()).isFalse();
	}

	@Test
	public void testSetPageNumber()
	{
		final SimpleSearchQueryData sqd = new SimpleSearchQueryData("Product");
		sqd.setValueComparisonOperator(ValueComparisonOperator.CONTAINS);
		final int pageSize = PRODUCT_COUNT - 2;
		sqd.setPageSize(pageSize);

		final Pageable<ItemModel> pageable = fieldSearchFacadeStrategy.search(sqd);
		pageable.setPageNumber(0);
		List<ItemModel> currentPage = pageable.getCurrentPage();
		Assertions.assertThat(currentPage).hasSize(pageSize);

		pageable.setPageNumber(1);
		currentPage = pageable.getCurrentPage();
		Assertions.assertThat(currentPage).hasSize(2);
	}

}
