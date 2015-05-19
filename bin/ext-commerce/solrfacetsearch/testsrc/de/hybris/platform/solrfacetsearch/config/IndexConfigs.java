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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.enums.IndexMode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


/**
 *
 */
public class IndexConfigs
{
	public static IndexConfig createIndexConfig(final Collection<IndexedType> indexedTypes,
			final Collection<CatalogVersionModel> catalogVersions, final Collection<LanguageModel> languages,
			final Collection<CurrencyModel> currencies, final String exportPath, final int batchSize, final int numberOfThreads,
			final IndexMode indexMode, final boolean enabledLanguageFallbackMechanism)
	{
		final IndexConfig indexConfig = new IndexConfig();
		indexConfig.setIndexedTypes(new HashMap<String, IndexedType>());
		for (final IndexedType indexedType : indexedTypes)
		{
			indexConfig.getIndexedTypes().put(indexedType.getUniqueIndexedTypeCode(), indexedType);
		}
		indexConfig.setCatalogVersions(Collections.unmodifiableCollection(catalogVersions));
		indexConfig.setLanguages(Collections.unmodifiableCollection(languages));
		indexConfig.setCurrencies(Collections.unmodifiableCollection(currencies));
		indexConfig.setExportPath(exportPath);
		indexConfig.setBatchSize(batchSize);
		indexConfig.setNumberOfThreads(numberOfThreads);
		indexConfig.setIndexMode(indexMode);
		indexConfig.setEnabledLanguageFallbackMechanism(enabledLanguageFallbackMechanism);
		return indexConfig;
	}

	public static IndexConfig createIndexConfig(final Collection<IndexedType> indexedTypes,
			final Collection<CatalogVersionModel> catalogVersions, final Collection<LanguageModel> languages,
			final Collection<CurrencyModel> currencies, final String exportPath, final int batchSize, final int numberOfThreads,
			final boolean enabledLanguageFallbackMechanism)
	{
		return createIndexConfig(indexedTypes, catalogVersions, languages, currencies, exportPath, batchSize, 1, IndexMode.DIRECT,
				enabledLanguageFallbackMechanism);
	}

	public static IndexConfig createIndexConfig(final Collection<IndexedType> indexedTypes,
			final Collection<CatalogVersionModel> catalogVersions, final Collection<LanguageModel> languages,
			final Collection<CurrencyModel> currencies, final String exportPath, final int batchSize,
			final boolean enabledLanguageFallbackMechanism)
	{
		return createIndexConfig(indexedTypes, catalogVersions, languages, currencies, exportPath, batchSize, 1,
				enabledLanguageFallbackMechanism);
	}
}
