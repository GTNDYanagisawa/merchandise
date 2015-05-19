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
package de.hybris.platform.solrfacetsearch.config;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.loader.ModelLoader;
import de.hybris.platform.solrfacetsearch.provider.IdentityProvider;
import de.hybris.platform.solrfacetsearch.provider.IndexedTypeFieldsValuesProvider;
import de.hybris.platform.solrfacetsearch.search.impl.SolrResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *
 */
public class IndexedTypes
{
	public static IndexedTypeFieldsValuesProvider getFieldsValuesProvider(final IndexedType indexType)
	{
		final String name = indexType.getFieldsValuesProvider();
		return name == null ? null : Registry.getGlobalApplicationContext().getBean(name, IndexedTypeFieldsValuesProvider.class);
	}

	public static IdentityProvider getIdentityProvider(final IndexedType indexType)
	{
		final String name = indexType.getIdentityProvider();
		return name == null ? null : Registry.getGlobalApplicationContext().getBean(name, IdentityProvider.class);
	}

	public static ModelLoader getModelLoader(final IndexedType indexType)
	{
		final String name = indexType.getModelLoader();
		return name == null ? null : Registry.getGlobalApplicationContext().getBean(name, ModelLoader.class);
	}

	public static Converter<SolrResult, ?> getSolrResultConverter(final IndexedType indexType)
	{
		final String name = indexType.getSolrResultConverter();
		return name == null ? null : Registry.getGlobalApplicationContext().getBean(name, Converter.class);
	}

	public static IndexedType createIndexedType(final ComposedTypeModel composedType, final boolean variant,
			final Collection<IndexedProperty> indexedProperties,
			final Map<IndexOperation, IndexedTypeFlexibleSearchQuery> flexibleSearchQueries, final String identityProvider,
			final String modelLoader, final Set<String> typeFacets, final String fieldsValuesProvider, final String indexName,
			final String indexNameFromConfig, final String solrResultConverter)
	{
		final IndexedType indexType = new IndexedType();
		indexType.setComposedType(composedType);
		indexType.setVariant(variant);
		indexType.setIndexedProperties(new HashMap<String, IndexedProperty>());
		for (final IndexedProperty indexedProperty : indexedProperties)
		{
			indexType.getIndexedProperties().put(indexedProperty.getExportId(), indexedProperty);
		}
		indexType.setIdentityProvider(identityProvider);
		indexType.setModelLoader(modelLoader);
		indexType.setTypeFacets(typeFacets);
		indexType.setFieldsValuesProvider(fieldsValuesProvider);
		indexType.setIndexName(indexName);
		indexType.setIndexNameFromConfig(indexNameFromConfig);
		indexType.setStaged(false);
		indexType.setSolrResultConverter(solrResultConverter);
		indexType.setFlexibleSearchQueries(flexibleSearchQueries);
		return indexType;
	}

	public static IndexedType createIndexedType(final ComposedTypeModel composedType, final boolean variant,
			final Collection<IndexedProperty> indexedProperties,
			final Map<IndexOperation, IndexedTypeFlexibleSearchQuery> flexibleSearchQueries, final String identityProvider,
			final String modelLoader, final Set<String> typeFacets, final String fieldsValuesProvider, final String indexName)
	{
		return createIndexedType(composedType, variant, indexedProperties, flexibleSearchQueries, identityProvider, modelLoader,
				typeFacets, fieldsValuesProvider, indexName, null, null);
	}


	public static IndexedType createIndexedType(final ComposedTypeModel composedType, final boolean variant,
			final Collection<IndexedProperty> indexedProperties,
			final Map<IndexOperation, IndexedTypeFlexibleSearchQuery> flexibleSearchQueries, final String identityProvider,
			final String modelLoader, final Set<String> typeFacets, final String fieldsValuesProvider, final String indexName,
			final String indexNameFromConfig)
	{
		return createIndexedType(composedType, variant, indexedProperties, flexibleSearchQueries, identityProvider, modelLoader,
				typeFacets, fieldsValuesProvider, indexName, indexNameFromConfig, null);
	}
}
