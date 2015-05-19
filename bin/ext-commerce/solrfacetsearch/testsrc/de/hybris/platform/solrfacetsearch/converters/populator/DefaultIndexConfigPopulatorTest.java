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
package de.hybris.platform.solrfacetsearch.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.enums.IndexMode;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.enums.SolrServerModes;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultIndexConfigPopulatorTest
{
	private static final String TYPE_CODE = "typeCode";
	private DefaultIndexConfigPopulator populator;
	private SolrFacetSearchConfigModel source;
	private IndexConfig target;
	private SolrIndexConfigModel solrIndexConfigModel;

	@Mock
	private Converter<SolrIndexedTypeModel, IndexedType> mockIndexedTypeConverter;
	@Mock
	private Converter<SolrServerConfigModel, SolrConfig> mockSolrServerConfigConverter;
	private SolrIndexedTypeModel indexTypeModel1;
	private IndexedType indexedType1;
	private SolrIndexerQueryModel fullQuery;
	private SolrServerConfigModel solrServerConfigModel;
	private SolrConfig solrServerConfig;
	private CatalogVersionModel catalogVersion1;
	private CatalogVersionModel catalogVersion2;
	private LanguageModel lang1;
	private LanguageModel lang2;
	private CurrencyModel curr1;
	private CurrencyModel curr2;
	@Mock
	private ComposedTypeModel mockComposedType;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		populator = new DefaultIndexConfigPopulator();
		populator.setIndexedTypeConverter(mockIndexedTypeConverter);
		populator.setSolrServerConfigConverter(mockSolrServerConfigConverter);

		fullQuery = new SolrIndexerQueryModel();
		fullQuery.setType(IndexerOperationValues.FULL);
		fullQuery.setQuery("query");
		indexTypeModel1 = new SolrIndexedTypeModel();
		indexTypeModel1.setSolrIndexerQueries(Arrays.asList(fullQuery));
		indexTypeModel1.setType(mockComposedType);
		indexTypeModel1.setIdentifier("ABC");
		solrIndexConfigModel = new SolrIndexConfigModel();
		solrIndexConfigModel.setIndexMode(IndexMode.DIRECT);
		solrIndexConfigModel.setExportPath("export Path");
		source = new SolrFacetSearchConfigModel();
		source.setSolrIndexConfig(solrIndexConfigModel);
		source.setSolrIndexedTypes(Arrays.asList(indexTypeModel1));
		solrServerConfigModel = new SolrServerConfigModel();
		solrServerConfigModel.setMode(SolrServerModes.EMBEDDED);
		source.setSolrServerConfig(solrServerConfigModel);

		target = new IndexConfig();
		solrServerConfig = new SolrConfig();
		indexedType1 = new IndexedType();
		indexedType1.setUniqueIndexedTypeCode("typeCode_ABC");

		BDDMockito.when(mockIndexedTypeConverter.convert(indexTypeModel1)).thenReturn(indexedType1);
		BDDMockito.when(mockSolrServerConfigConverter.convert(solrServerConfigModel)).thenReturn(solrServerConfig);
		BDDMockito.when(mockComposedType.getCode()).thenReturn(TYPE_CODE);

		catalogVersion1 = new CatalogVersionModel();
		catalogVersion2 = new CatalogVersionModel();
		source.setCatalogVersions(Arrays.asList(catalogVersion1, catalogVersion2));

		lang1 = new LanguageModel();
		lang2 = new LanguageModel();
		source.setLanguages(Arrays.asList(lang1, lang2));

		curr1 = new CurrencyModel();
		curr2 = new CurrencyModel();
		source.setCurrencies(Arrays.asList(curr1, curr2));

	}

	@Test
	public void testPopulate()
	{
		populator.populate(source, target);
		Assert.assertEquals(solrIndexConfigModel.getBatchSize(), target.getBatchSize());
		de.hybris.platform.testframework.Assert.assertCollection("Catalog versions were not populated corrently",
				source.getCatalogVersions(), target.getCatalogVersions());
		de.hybris.platform.testframework.Assert.assertCollection("Currencies were not populated corrently", source.getCurrencies(),
				target.getCurrencies());
		de.hybris.platform.testframework.Assert.assertCollection("Languages were not populated corrently", source.getLanguages(),
				target.getLanguages());

		Assert.assertEquals(source.getSolrIndexConfig().getExportPath(), target.getExportPath());
		Assert.assertTrue(target.getIndexedTypes().containsKey("typeCode_ABC"));
		Assert.assertEquals(source.getSolrIndexConfig().getIndexMode(), target.getIndexMode());
	}
}
