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
package de.hybris.platform.solrfacetsearch.provider.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Test for {@link SpELValueProvider}
 */
@IntegrationTest
public class SpELValueProviderTest extends AbstractSolrIntegrationTest
{
	@Resource
	private SpELValueProvider springELValueProvider;



	@Test
	public void testUsingExpression() throws Exception
	{
		final IndexConfig indexConfig = getIndexConfig().getIndexConfig();
		assertThat(indexConfig).isNotNull();

		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get("code");
		indexedProperty.setValueProviderParameter("code");
		assertThat(indexedProperty).isNotNull();

		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		final ProductModel model = productService.getProductForCode(cv, "HW2300-2356");
		assertThat(model).isNotNull();

		final Collection<FieldValue> fieldValues = springELValueProvider.getFieldValues(indexConfig, indexedProperty, model);

		assertThat(fieldValues).hasSize(1);
		assertThat(fieldValues.iterator().next().getFieldName()).isEqualTo("code_string");
		assertThat(fieldValues.iterator().next().getValue()).isEqualTo("HW2300-2356");
	}

	@Test
	public void testWithoutExpression() throws Exception
	{
		final IndexConfig indexConfig = getIndexConfig().getIndexConfig();
		assertThat(indexConfig).isNotNull();

		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get("code");
		assertThat(indexedProperty).isNotNull();

		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		final ProductModel model = productService.getProductForCode(cv, "HW2300-2356");
		assertThat(model).isNotNull();

		final Collection<FieldValue> fieldValues = springELValueProvider.getFieldValues(indexConfig, indexedProperty, model);

		assertThat(fieldValues).hasSize(1);
		assertThat(fieldValues.iterator().next().getFieldName()).isEqualTo("code_string");
		assertThat(fieldValues.iterator().next().getValue()).isEqualTo("HW2300-2356");
	}

	@Test
	public void testWithExpressionMultilanguage() throws Exception
	{
		final IndexConfig indexConfig = getIndexConfig().getIndexConfig();
		assertThat(indexConfig).isNotNull();

		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get("name");
		indexedProperty.setValueProviderParameter("getName(#lang)");
		assertThat(indexedProperty).isNotNull();

		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		final ProductModel model = productService.getProductForCode(cv, "HW2300-2356");
		assertThat(model).isNotNull();

		final Collection<FieldValue> fieldValues = springELValueProvider.getFieldValues(indexConfig, indexedProperty, model);

		assertThat(fieldValues).hasSize(4);
		assertThat(fieldValues).onProperty("fieldName").contains("name_text_de", "name_sortable_de_sortabletext", "name_text_en",
				"name_sortable_en_sortabletext");
		System.out.println(fieldValues);

	}

	@Test
	public void testWithExpressionMulticurrency() throws Exception
	{
		final IndexConfig indexConfig = getIndexConfig().getIndexConfig();
		assertThat(indexConfig).isNotNull();

		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get("price");
		indexedProperty
				.setValueProviderParameter("@priceService.getPriceInformationsForProduct(#item).![priceValue].?[currencyIso == #currency.isocode].![value]");

		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		final ProductModel model = productService.getProductForCode(cv, "HW2300-2356");
		assertThat(model).isNotNull();

		final Collection<FieldValue> fieldValues = springELValueProvider.getFieldValues(indexConfig, indexedProperty, model);

		assertThat(fieldValues).hasSize(2);
		assertThat(fieldValues).onProperty("fieldName").containsOnly("price_eur_string", "price_usd_string");
		assertThat(fieldValues).onProperty("value").containsOnly("157.95", "217.97099999999998");
	}
}
