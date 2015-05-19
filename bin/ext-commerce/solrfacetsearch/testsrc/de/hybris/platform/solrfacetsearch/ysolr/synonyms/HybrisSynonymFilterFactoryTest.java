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
package de.hybris.platform.solrfacetsearch.ysolr.synonyms;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.enums.ConverterType;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrSynonymConfigModel;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.product.SolrProductData;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class HybrisSynonymFilterFactoryTest extends AbstractSolrIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(HybrisSynonymFilterFactoryTest.class);

	private static final String EN = "en";
	private static final String JA = "ja";
	final String encoding = "utf-8";
    protected final static String CATALOG_ID = "jhwcatalog";

	@Resource
	private CommonI18NService commonI18NService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void prepareIndexForTest() throws Exception
	{
		dropIndex();
	}

	@Override
	@After
	public void tearDown()
	{
		try
		{
			dropIndex();
			removeSynonyms(EN);
			removeSynonyms(JA);

			if (localConfig != null && !modelService.isNew(localConfig))
			{
				modelService.remove(localConfig);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		LOG.info("Creating test catalog..");
		final String resourceName = "/test/solrJapaneseHwcatalogOnline.csv";
		importCsv(resourceName, encoding);
		LOG.info("Finished Creating test catalog ");
		hwOnline = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
	}

	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Arrays.asList(hwOnline);
	}

	@Test
	public void shouldFindEnAndJaSynonyms() throws Exception
	{
		// Search for English Synonyms before updating synonyms.. Empty set expected
		this.searchForSynonyms(EN, "/test/synonymsSearchText_en.csv", true);
		// Search for Japanese Synonyms before updating synonyms.. Empty set expected
		this.searchForSynonyms(JA, "/test/synonymsSearchText_ja.csv", true);

		// create english Synonyms
		createSynonyms(EN, "/test/synonyms_en.csv");
		//	create japanese Synonyms
		createSynonyms(JA, "/test/synonyms_ja.csv");
		// do full indexing
		indexerService.performFullIndex(facetSearchConfig);

		// Search for English Synonyms
		this.searchForSynonyms(EN, "/test/synonymsSearchText_en.csv", false);
		//search for japanese Synonyms.. Empty set not expected
		this.searchForSynonyms(JA, "/test/synonymsSearchText_ja.csv", false);
	}

	private void searchForSynonyms(final String isoCode, final String synonymsFileaName, final boolean isEmptyExpceted)
			throws Exception
	{
		// do full index after creating synonums. Synonums list from solr cannot be empty
		InputStream is = null;
		CSVReader synonymsReader = null;
		try
		{
			is = HybrisSynonymFilterFactoryTest.class.getResourceAsStream(synonymsFileaName);
			synonymsReader = new CSVReader(is, encoding);
			while (synonymsReader.readNextLine())
			{
				final String line = synonymsReader.getSourceLine();
				final SearchQuery query = getSynonymSearchQuery(isoCode, line);
				final List<SolrProductData> resultData = search(query);
				if (isEmptyExpceted)
				{
					// before synonys pudate.. empty set expected
					assertTrue("Empty set expected for" + isoCode + " Synonyms :: " + line, resultData.isEmpty());
				}
				else
				{
					// empty set not expected
					assertFalse("Empty set not expected for" + isoCode + " Synonyms :: " + line, resultData.isEmpty());
				}

			}
		}
		finally
		{
			if (synonymsReader != null)
			{
				synonymsReader.close();
			}

			closeInputStream(is);
		}
	}

	private SearchQuery getSynonymSearchQuery(final String isoCode, final String text) throws Exception
	{
		final SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);
		query.clearAllFields();
		query.setCatalogVersions(getCatalogVersionsForSolrFacetSearchConfig());
		query.setLanguage(isoCode);
		query.setCurrency("eur");
		query.search(text);

		return query;
	}

	private List<SolrProductData> search(final SearchQuery query) throws Exception
	{
		final SearchResult res = facetSearchService.search(query);
		return res.getResultData(ConverterType.DEFAULT);
	}

	private void createSynonyms(final String isoCode, final String filePath) throws Exception
	{
		InputStream is = null;
		CSVReader synonymsCSV = null;
		try
		{
			is = HybrisSynonymFilterFactoryTest.class.getResourceAsStream(filePath);

			final LanguageModel language = commonI18NService.getLanguage(isoCode);
			final List<SolrSynonymConfigModel> saveList = new ArrayList<>();
			synonymsCSV = new CSVReader(is, encoding);
			while (synonymsCSV.readNextLine())
			{
				final String line = synonymsCSV.getSourceLine();
				final String[] temp = line.split(String.valueOf(CSVConstants.HYBRIS_FIELD_SEPARATOR));
				final SolrSynonymConfigModel synonymModel = modelService.create(SolrSynonymConfigModel.class);
				synonymModel.setSynonymFrom(temp[0]);
				synonymModel.setSynonymTo(temp[1]);
				synonymModel.setLanguage(language);
				synonymModel.setFacetSearchConfig(localConfig);
				saveList.add(synonymModel);
			}
			modelService.saveAll(saveList);
			modelService.save(localConfig);
		}
		finally
		{
			if (synonymsCSV != null)
			{
				synonymsCSV.close();
			}

			closeInputStream(is);
		}

	}

	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final SolrIndexedPropertyModel codeProperty = modelService.create(SolrIndexedPropertyModel.class);
		codeProperty.setName("code");
		codeProperty.setType(SolrPropertiesTypes.STRING);

		final SolrIndexedPropertyModel nameProperty = modelService.create(SolrIndexedPropertyModel.class);
		nameProperty.setName("name");
		nameProperty.setType(SolrPropertiesTypes.TEXT);

		nameProperty.setLocalized(true);

		final SolrIndexedPropertyModel descriptionProperty = modelService.create(SolrIndexedPropertyModel.class);
		descriptionProperty.setName("description");
		descriptionProperty.setType(SolrPropertiesTypes.TEXT);
		descriptionProperty.setLocalized(true);

		final SolrIndexedPropertyModel manufacturerProperty = modelService.create(SolrIndexedPropertyModel.class);
		manufacturerProperty.setFacet(true);
		manufacturerProperty.setName("manufacturerName");
		manufacturerProperty.setType(SolrPropertiesTypes.STRING);
		return Arrays.asList(codeProperty, nameProperty, descriptionProperty, manufacturerProperty);
	}

	@Override
	protected List<LanguageModel> setUpLanguages()
	{
		final LanguageModel en = commonI18NService.getLanguage(EN);
		final LanguageModel ja = commonI18NService.getLanguage(JA);
		return Arrays.asList(en, ja);
	}

	private void removeSynonyms(final String isoCode)
	{
		final List<SolrSynonymConfigModel> removeList = new ArrayList<>();
		final LanguageModel language = commonI18NService.getLanguage(isoCode);
		final List<SolrSynonymConfigModel> returnList = localConfig.getSynonyms();
		for (final SolrSynonymConfigModel model : returnList)
		{
			if (model.getLanguage().getIsocode().equals(language.getIsocode()))
			{
				removeList.add(model);
			}
		}
		modelService.removeAll(removeList);
		modelService.save(localConfig);
	}

	private void closeInputStream(final InputStream is)
	{
		try
		{
			if (is != null)
			{
				is.close();
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}
}
