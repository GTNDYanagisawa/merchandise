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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.fest.assertions.AssertExtension;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSAdminRestrictionServiceIntegrationTest extends ServicelayerTest // NOPMD JUnit4
{
	private class CatalogVersionAssertion implements AssertExtension
	{
		private final ItemModel model;

		public CatalogVersionAssertion(final ItemModel model)
		{
			this.model = model;
		}

		public CatalogVersionAssertion hasSameCatalogVersion(final CatalogVersionModel catalogVersionModel)
		{
			final CatalogVersionModel ctgVer = model instanceof ProductModel ? ((ProductModel) model).getCatalogVersion()
					: ((CategoryModel) model).getCatalogVersion();
			assertThat(catalogVersionModel).isEqualTo(ctgVer);
			return this;
		}
	}

	@Resource
	private CMSAdminRestrictionService cmsAdminRestrictionService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ProductService productService;
	@Resource
	private CategoryService categoryService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	private PreviewDataModel previewContext;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();
		createPreviewContext();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminRestrictionService#getCategories(de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel, de.hybris.platform.cms2.model.preview.PreviewDataModel)}
	 * .
	 */
	@Test
	public void shouldGetCategoriesForRestriction()
	{
		// given
		final List<CategoryModel> categories = new ArrayList<CategoryModel>();
		categories.add(categoryService.getCategory("testCategory0"));
		categories.add(categoryService.getCategory("testCategory1"));
		categories.add(categoryService.getCategory("testCategory2"));
		final CMSCategoryRestrictionModel categoryRestriction = modelService.create(CMSCategoryRestrictionModel.class);
		categoryRestriction.setUid("FooBar");
		categoryRestriction.setCategories(categories);
		modelService.save(categoryRestriction);

		// when
		final List<CategoryModel> restrictedCategories = cmsAdminRestrictionService.getCategories(categoryRestriction,
				previewContext);

		// then
		assertThat(restrictedCategories).hasSize(3);
		assertThat(CollectionUtils.isEqualCollection(restrictedCategories, categories)).isTrue();
		final CatalogVersionAssertion testCategory0 = new CatalogVersionAssertion(restrictedCategories.get(0));
		final CatalogVersionAssertion testCategory1 = new CatalogVersionAssertion(restrictedCategories.get(1));
		final CatalogVersionAssertion testCategory2 = new CatalogVersionAssertion(restrictedCategories.get(2));
		assertThat(testCategory0).hasSameCatalogVersion(getCatalogVersion());
		assertThat(testCategory1).hasSameCatalogVersion(getCatalogVersion());
		assertThat(testCategory2).hasSameCatalogVersion(getCatalogVersion());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminRestrictionService#getProducts(de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel, de.hybris.platform.cms2.model.preview.PreviewDataModel)}
	 * .
	 */
	@Test
	public void shouldGetProductsForRestriction()
	{
		// given
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(productService.getProductForCode("testProduct0"));
		products.add(productService.getProductForCode("testProduct1"));
		products.add(productService.getProductForCode("testProduct2"));
		final CMSProductRestrictionModel productRestriction = modelService.create(CMSProductRestrictionModel.class);
		productRestriction.setUid("FooBar");
		productRestriction.setProducts(products);
		modelService.save(productRestriction);

		// when
		final List<ProductModel> restrictedProducts = cmsAdminRestrictionService.getProducts(productRestriction, previewContext);

		// then
		assertThat(restrictedProducts).hasSize(3);
		assertThat(CollectionUtils.isEqualCollection(restrictedProducts, products)).isTrue();
		final CatalogVersionAssertion testProduct0 = new CatalogVersionAssertion(restrictedProducts.get(0));
		final CatalogVersionAssertion testProduct1 = new CatalogVersionAssertion(restrictedProducts.get(1));
		final CatalogVersionAssertion testProduct2 = new CatalogVersionAssertion(restrictedProducts.get(2));
		assertThat(testProduct0).hasSameCatalogVersion(getCatalogVersion());
		assertThat(testProduct1).hasSameCatalogVersion(getCatalogVersion());
		assertThat(testProduct2).hasSameCatalogVersion(getCatalogVersion());
	}

	private void createPreviewContext()
	{
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<CatalogVersionModel>();
		catalogVersions.add(getCatalogVersion());
		previewContext = modelService.create(PreviewDataModel.class);
		previewContext.setCatalogVersions(catalogVersions);
		previewContext.setUser(userService.getUserForUID("ahertz"));
		previewContext.setLanguage(commonI18NService.getLanguage("en"));
		previewContext.setLiveEdit(Boolean.TRUE);
		modelService.save(previewContext);
	}

	private CatalogVersionModel getCatalogVersion()
	{
		final CatalogModel catalog = flexibleSearchService
				.<CatalogModel> search("SELECT {PK} FROM {Catalog} WHERE {id}='testCatalog'").getResult().get(0);
		return flexibleSearchService
				.<CatalogVersionModel> search("SELECT {PK} FROM {CatalogVersion} WHERE {version}='Online' AND {catalog}=?catalog",
						Collections.singletonMap("catalog", catalog)).getResult().get(0);
	}

}
