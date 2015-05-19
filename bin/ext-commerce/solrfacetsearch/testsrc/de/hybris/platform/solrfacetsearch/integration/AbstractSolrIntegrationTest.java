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
package de.hybris.platform.solrfacetsearch.integration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerService;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeSetModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Ignore;


/**
 * Abstract class designed for testing of SolrFacetSearch module. Contains methods creating test data and cleaning solr
 * index
 */
@Ignore
@IntegrationTest
public abstract class AbstractSolrIntegrationTest extends AbstractSolrTest
{

	protected static final String SOLR_QUERY_SELECT_ALL = "*:*";
	public static final String SOLR_ID_DELIMITER = "/";

	protected final static String INDEX_CONFIGURATION_NAME = "TestSOLRConfiguration";
	protected final static String INDEX_TYPE_NAME = "Product";

	protected final static String CATALOG_ID = "hwcatalog";
	protected final static String VERSION_ONLINE = "Online";
	protected final static String VERSION_STAGED = "Staged";

	@Resource
	protected ProductService productService;
	@Resource
	protected CommonI18NService commonI18NService;
	@Resource
	protected IndexerService indexerService;
	@Resource
	protected FacetSearchService facetSearchService;


	private static final Logger LOG = Logger.getLogger(AbstractSolrIntegrationTest.class);

	protected SolrValueRangeSetModel stringSetModel;
	protected SolrValueRangeSetModel priceSetModel;
	protected SolrValueRangeSetModel dateSetModel;

	//	private SolrValueRangeSetModel priceRangesEUR;
	//	private SolrValueRangeSetModel priceRangesUSD;

	protected SearchQuery query;

	protected CatalogVersionModel hwOnline;
	protected CatalogVersionModel hwStaged;
	protected CatalogVersionModel classificationVersion;


	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		prepareIndexForTest();
		query = new SearchQuery(facetSearchConfig, indexedType);
	}


	protected void prepareIndexForTest() throws Exception
	{
		dropIndex();
		createIndex();
	}


	@Override
	protected void setUpBasic() throws Exception
	{
		LOG.info("Creating essential data for core ..");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/test/solrBasics.csv", "windows-1252");
		LOG.info("Finished creating essential data for core in " + (System.currentTimeMillis() - startTime) + "ms");

		createUsers();
	}


	@Override
	protected void setUpProductData() throws Exception
	{
		LOG.info("Creating test catalog..");

		importCsv("/test/solrHwcatalogOnline.csv", "utf-8");
		importCsv("/test/solrHwcatalogStaged.csv", "utf-8");
		createClassificationSystem();

		hwOnline = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		hwStaged = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		classificationVersion = catalogVersionService.getCatalogVersion("SampleClassification", "1.0");

	}


	//--- Set up SolrFacetSearchConfig:

	@Override
	protected String getSolrConfigName()
	{
		return INDEX_CONFIGURATION_NAME;
	}

	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Arrays.asList(hwOnline, hwStaged);
	}

	@Override
	protected List<SolrValueRangeSetModel> setUpValueRanges()
	{
		priceSetModel = modelService.create(SolrValueRangeSetModel.class);
		priceSetModel.setName("priceRanges");
		priceSetModel.setType("double");

		final List<SolrValueRangeModel> priceValueRanges = new ArrayList<SolrValueRangeModel>();
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "1-100", "1", "100"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "101-200", "101", "200"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "201-300", "201", "300"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "301-400", "301", "400"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "401-500", "401", "500"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "501-600", "501", "600"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "601-700", "601", "700"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "701-800", "701", "800"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "801-900", "801", "900"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "901-1000", "901", "1000"));
		priceValueRanges.add(setUpSingleRangeForRangeSet(priceSetModel, "1001-INF", "1001", null));

		priceSetModel.setSolrValueRanges(priceValueRanges);

		stringSetModel = modelService.create(SolrValueRangeSetModel.class);
		stringSetModel.setName("stringRange");
		stringSetModel.setType("string");

		final List<SolrValueRangeModel> stringValueRanges = new ArrayList<SolrValueRangeModel>();
		stringValueRanges.add(setUpSingleRangeForRangeSet(stringSetModel, "A-D", "A", "D"));
		stringValueRanges.add(setUpSingleRangeForRangeSet(stringSetModel, "E-K", "E", "K"));
		stringValueRanges.add(setUpSingleRangeForRangeSet(stringSetModel, "L-R", "L", "R"));
		stringValueRanges.add(setUpSingleRangeForRangeSet(stringSetModel, "S-Z", "S", "Z"));
		stringSetModel.setSolrValueRanges(stringValueRanges);

		dateSetModel = modelService.create(SolrValueRangeSetModel.class);
		dateSetModel.setName("dateRange");
		dateSetModel.setType("date");
		final List<SolrValueRangeModel> dateValueRanges = new ArrayList<SolrValueRangeModel>();
		dateValueRanges.add(setUpSingleRangeForRangeSet(dateSetModel, "2009_02", "2009-02-01", "2009-02-28"));
		dateValueRanges.add(setUpSingleRangeForRangeSet(dateSetModel, "2009_12", "2009-12-01", "2009-12-31"));
		dateValueRanges.add(setUpSingleRangeForRangeSet(dateSetModel, "2008_05", "2008-05-01", "2008-05-31"));
		dateSetModel.setSolrValueRanges(dateValueRanges);

		return Arrays.asList(priceSetModel, stringSetModel, dateSetModel);
	}

	@Override
	protected List<LanguageModel> setUpLanguages()
	{
		final LanguageModel en = commonI18NService.getLanguage("en");
		final LanguageModel de = commonI18NService.getLanguage("de");
		return Arrays.asList(en, de);
	}

	@Override
	protected List<CurrencyModel> setUpCurrencies()
	{
		final CurrencyModel eur = commonI18NService.getCurrency("EUR");
		final CurrencyModel usd = commonI18NService.getCurrency("USD");
		return Arrays.asList(eur, usd);
	}

	@Override
	protected String getIndexedTypeIdentifier()
	{
		return INDEX_TYPE_NAME;
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
		nameProperty.setSortableType(SolrPropertiesTypes.SORTABLETEXT);
		nameProperty.setLocalized(true);

		final SolrIndexedPropertyModel descriptionProperty = modelService.create(SolrIndexedPropertyModel.class);
		descriptionProperty.setName("description");
		descriptionProperty.setType(SolrPropertiesTypes.TEXT);
		descriptionProperty.setLocalized(true);

		final SolrIndexedPropertyModel manufacturerProperty = modelService.create(SolrIndexedPropertyModel.class);
		manufacturerProperty.setFacet(true);
		manufacturerProperty.setName("manufacturerName");
		manufacturerProperty.setType(SolrPropertiesTypes.STRING);
		manufacturerProperty.setSortableType(SolrPropertiesTypes.TEXT);

		final SolrIndexedPropertyModel priceProperty = modelService.create(SolrIndexedPropertyModel.class);
		priceProperty.setFacet(true);
		priceProperty.setName("price");
		priceProperty.setType(SolrPropertiesTypes.DOUBLE);
		priceProperty.setFieldValueProvider("productPriceValueProvider");
		priceProperty.setRangeSet(priceSetModel);
		priceProperty.setCurrency(true);

		final SolrIndexedPropertyModel processorProperty = modelService.create(SolrIndexedPropertyModel.class);
		processorProperty.setFacet(true);
		processorProperty.setName("processor");
		processorProperty.setType(SolrPropertiesTypes.STRING);
		processorProperty.setFieldValueProvider("classificationPropertyValueProvider");
		processorProperty.setMultiValue(true);

		final SolrIndexedPropertyModel categoryNameProperty = modelService.create(SolrIndexedPropertyModel.class);
		categoryNameProperty.setFacet(true);
		categoryNameProperty.setName("categoryName");
		categoryNameProperty.setType(SolrPropertiesTypes.STRING);
		categoryNameProperty.setFieldValueProvider("categoryNameValueProvider");
		categoryNameProperty.setLocalized(true);
		categoryNameProperty.setMultiValue(true);

		final SolrIndexedPropertyModel categoryCodeProperty = modelService.create(SolrIndexedPropertyModel.class);
		categoryCodeProperty.setFacet(true);
		categoryCodeProperty.setName("categoryCode");
		categoryCodeProperty.setFacetDisplayNameProvider("categoryFacetDisplayNameProvider");
		categoryCodeProperty.setType(SolrPropertiesTypes.STRING);
		categoryCodeProperty.setFieldValueProvider("categoryCodeValueProvider");
		categoryCodeProperty.setMultiValue(true);

		final SolrIndexedPropertyModel typeProperty = modelService.create(SolrIndexedPropertyModel.class);
		typeProperty.setName("type");
		typeProperty.setFacet(true);
		typeProperty.setType(SolrPropertiesTypes.STRING);
		typeProperty.setFieldValueProvider("classificationPropertyValueProvider");
		typeProperty.setLocalized(true);
		typeProperty.setMultiValue(true);

		return Arrays.asList(codeProperty, nameProperty, descriptionProperty, manufacturerProperty, priceProperty,
				processorProperty, categoryCodeProperty, categoryNameProperty, typeProperty);
	}


	@Override
	protected List<SolrIndexerQueryModel> setUpIndexerQueries()
	{
		final SolrIndexerQueryModel fullQueryModel = modelService.create(SolrIndexerQueryModel.class);
		fullQueryModel.setType(IndexerOperationValues.FULL);
		fullQueryModel.setQuery("select {pk} from {Product}");
		fullQueryModel.setIdentifier("fullQuery");

		final SolrIndexerQueryModel updateQueryModel = modelService.create(SolrIndexerQueryModel.class);
		updateQueryModel.setType(IndexerOperationValues.UPDATE);
		updateQueryModel.setQuery("SELECT {PK} FROM {Product} WHERE {modifiedtime} >= ?lastIndexTime");
		updateQueryModel.setIdentifier("updateQuery");
		updateQueryModel.setInjectLastIndexTime(true);

		final SolrIndexerQueryModel deleteQueryModel = modelService.create(SolrIndexerQueryModel.class);
		deleteQueryModel.setType(IndexerOperationValues.DELETE);
		deleteQueryModel.setQuery("SELECT {PK} FROM {Product} WHERE code = 'HW2310-1001'");
		deleteQueryModel.setIdentifier("deleteQuery");

		return Arrays.asList(fullQueryModel, updateQueryModel, deleteQueryModel);
	}

	//--------------------------------------------------------:



	protected SolrValueRangeModel setUpSingleRangeForRangeSet(final SolrValueRangeSetModel rangeSet, final String name,
			final String from, final String to)
	{
		final SolrValueRangeModel priceRangeModel = modelService.create(SolrValueRangeModel.class);
		priceRangeModel.setName(name);
		priceRangeModel.setFrom(from);
		priceRangeModel.setTo(to);
		priceRangeModel.setSolrValueRangeSet(rangeSet);
		return priceRangeModel;
	}




	/**
	 * Method can be used to clear index for a given type, so that each test run has the same, 'clean' initial conditions
	 * 
	 * @param solrConfig
	 * @param typeCode
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws SolrServiceException
	 */
	protected void dropIndexForType(final SolrConfig solrConfig, final String typeCode) throws SolrServerException, IOException,
			SolrServiceException
	{
		final SolrServer solrServer = getSolrService().getSolrServerMaster(solrConfig, indexedType);
		assertNotNull(solrServer);
		solrServer.deleteByQuery(SOLR_QUERY_SELECT_ALL);
		solrServer.commit();
		final SolrQuery query = new SolrQuery(SOLR_QUERY_SELECT_ALL);
		final QueryResponse response = solrServer.query(query);
		final int resultSize = response.getResults().size();
		assertEquals("Result size", 0, resultSize);
	}

	private void dropIndexForAllTypes(final FacetSearchConfig config) throws SolrServerException, IOException,
			SolrServiceException
	{
		assertNotNull("Config must not be null", config);
		final SolrConfig solrConfig = config.getSolrConfig();
		assertNotNull(solrConfig);
		for (final IndexedType indexedType : config.getIndexConfig().getIndexedTypes().values())
		{
			assertNotNull(indexedType);
			dropIndexForType(solrConfig, indexedType.getCode());
		}
	}



	private void createClassificationSystem() throws Exception
	{
		LOG.info("Creating classification system..");
		importCsv("/test/solrClassificationSystemOnline.csv", "utf-8");
		importCsv("/test/solrClassificationSystemStaged.csv", "utf-8");
	}



	protected FacetSearchConfig getIndexConfig() throws Exception
	{
		return facetSearchConfig;
	}



	protected void createUsers() throws Exception
	{
		LOG.info("Creating test users ..");
		final long startTime = System.currentTimeMillis();
		importCsv("/test/solrUser.csv", "windows-1252");
		LOG.info("Finished creating test users in " + (System.currentTimeMillis() - startTime) + "ms");
	}


	@Override
	protected void dropIndex() throws SolrServerException, IOException, SolrServiceException
	{
		dropIndexForAllTypes(facetSearchConfig);
	}


	protected void createIndex() throws Exception
	{
		indexerService.performFullIndex(facetSearchConfig);
	}


	protected String prepareProductSolrId(final ProductModel productModel)
	{
		final String productCode = productModel.getCode();
		final String catalogName = productModel.getCatalogVersion().getCatalog().getId();
		final String versionName = productModel.getCatalogVersion().getVersion();

		final StringBuffer idStrBuffer = new StringBuffer(catalogName);
		idStrBuffer.append(SOLR_ID_DELIMITER);
		idStrBuffer.append(versionName);
		idStrBuffer.append(SOLR_ID_DELIMITER);
		idStrBuffer.append(productCode);
		return idStrBuffer.toString();
	}

	protected String prepareProductSolrId(final String catalogName, final String versionName, final String productCode)
	{
		final StringBuffer idStrBuffer = new StringBuffer(catalogName);
		idStrBuffer.append(SOLR_ID_DELIMITER);
		idStrBuffer.append(versionName);
		idStrBuffer.append(SOLR_ID_DELIMITER);
		idStrBuffer.append(productCode);
		return idStrBuffer.toString();
	}

	protected Collection<ProductModel> checkProductCollection(final CatalogVersionModel catalogVersion,
			final Collection<? extends ItemModel> items)
	{
		assertNotNull("Items collection must not be null", items);
		assertFalse("Items collection must not be empty", items.isEmpty());
		for (final ItemModel item : items)
		{
			assertTrue("Result item must be of type " + ProductModel.class, item instanceof ProductModel);
			final ProductModel product = (ProductModel) item;
			assertEquals("Catalog version of product", catalogVersion, product.getCatalogVersion());
		}
		return (Collection<ProductModel>) items;
	}

	protected void checkProductsCategory(final Collection<ProductModel> products, final String categoryName)
	{
		for (final ProductModel product : products)
		{
			final Collection<CategoryModel> categories = product.getSupercategories();
			boolean catFound = false;
			for (final CategoryModel category : categories)
			{
				if (categoryName.equals(category.getName()))
				{
					catFound = true;
					break;
				}
			}
			assertTrue("Category not found", catFound);
		}
	}

	protected void checkProductsCategoryByCode(final Collection<ProductModel> products, final String categoryCode)
	{
		for (final ProductModel product : products)
		{
			final Collection<CategoryModel> categories = product.getSupercategories();
			boolean catFound = false;
			for (final CategoryModel category : categories)
			{
				if (categoryCode.equals(category.getCode()))
				{
					catFound = true;
					break;
				}
			}
			assertTrue("Category not found", catFound);
		}
	}

	protected void checkProductsManufacturer(final Collection<ProductModel> products, final String manufacturerName)
	{
		for (final ProductModel product : products)
		{
			assertEquals(manufacturerName, product.getManufacturerName());
		}
	}
}
