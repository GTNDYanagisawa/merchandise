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
package de.hybris.platform.commerceservices.search.solrfacetsearch.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSortProvider;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.config.impl.DefaultFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.config.impl.DefaultFacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.impl.FacetSearchXMLConfigParser;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.enums.SolrIndexedPropertyFacetType;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.enums.SolrServerModes;
import de.hybris.platform.solrfacetsearch.loader.ModelLoader;
import de.hybris.platform.solrfacetsearch.model.config.SolrEndpointUrlModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeSetModel;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.IdentityProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.DemoIndexedTypeFieldsValuesProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.ProductIdentityProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.ProductPriceValueProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.IndexStatistics;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;


//@RunWith(PowerMockRunner.class)
//@PrepareForTest(
//		{DefaultCommerceFacetSearchConfigService.class, Logger.class})
//@SuppressStaticInitializationFor(
//		{"org.apache.log4j.Logger", "de.hybris.platform.util.Config"})
@Ignore("ACCEL-2 - removing powermock and disabling test")
@UnitTest
public class DefaultCommerceFacetSearchConfigServiceTest
{
	//	private static final String SOLR_INDEXEDPROPERTY_FORBIDDEN_CHAR = "_";

	//Catalog Version
	//	private static final String TEST_CATALOG_VERSION_VER = "Online";
	//	private static final String TEST_CATALOG_VERSION_ID = "testProductCatalog";

	//Search Config
	private static final String TEST_FACET_SEARCH_CONFIG_NAME = "testConfigName";
	private static final String TEST_FACET_SEARCH_CONFIG_DESC = "Test Config Index";
	private static final String TEST_FACET_SEARCH_CONFIG_INDEX_NAME_PREFIX = "testConfigPrefix";

	//Indexed Property
	private static final String TEST_SOLR_INDEXED_PROPERTY_NAME = "TestIndexedPropertyName";

	//Indexed Type
	//	private static final String TEST_INDEX_TYPE_IDENTIFIER = "testProductType";
	//	private static final String TEST_COMPOSED_TYPE_CODE = "Product";

	//provider names
	private static final String TEST_INDEX_TYPE_FIELDS_VALUES_PROVIDER_NAME = "IndexTypeFieldsValuesProvider";
	private static final String TEST_FIELDS_VALUES_PROVIDER_NAME = "FieldsValuesProvider";
	private static final String TEST_IDENTITY_PROVIDER_NAME = "IdentityProvider";
	private static final String TEST_MODEL_LOADER_NAME = "ModelLoader";
	private static final String TEST_CUSTOM_FACET_SORT_PROVIDER_NAME = "CustomFacetSortProvider";
	private static final String TEST_DISPLAY_NAME_PROVIDER = "DisplayNameProvider";

	//	private static final int TEST_SOLR_INDEX_CONFIG_BATCH_SIZE = 100;
	//	private static final int FACET_LIMIT_DEFAULT = 50;

	//Required
	@Mock
	private FieldValueProvider defaultFieldValueProvider; //NOPMD
	@Mock
	private IdentityProvider defaultIdentityProvider; //NOPMD
	@Mock
	private ModelLoader defaultModelLoader; //NOPMD
	@Mock
	private DefaultFacetSearchConfigDao facetSearchConfigDao;
	@Mock
	private FacetSearchXMLConfigParser xmlConfigParser; //NOPMD
	@Mock
	private MediaService mediaService; //NOPMD
	@Mock
	private BeanFactory beanFactory;
	@Mock
	private TypeService typeService; //NOPMD
	@Mock
	private SolrService solrService;

	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private SolrFacetSearchConfigModel configModel;
	@Mock
	private SolrServerConfigModel itemConfig;
	@Mock
	private SolrIndexConfigModel solrIndexConfigModel; //NOPMD
	@Mock
	private SolrIndexedTypeModel solrIndexedTypeModel;
	@Mock
	private SolrServer solrServer;

	@Mock
	private SolrIndexerQueryModel solrIndexerQueryModelFull;
	@Mock
	private SolrIndexerQueryModel solrIndexerQueryModelUpdate;
	@Mock
	private SolrIndexerQueryModel solrIndexerQueryModelDelete;
	private List<SolrIndexerQueryModel> queryModels;

	@Mock
	private SolrIndexedPropertyModel solrIndexedPropertyModel;

	@Mock
	private ModelLoader modelLoader;
	@Mock
	private ProductIdentityProvider identityProvider;
	@Mock
	private ProductPriceValueProvider fieldsValuesProvider;
	@Mock
	private DemoIndexedTypeFieldsValuesProvider indexedTypeFieldsValuesProvider;
	@Mock
	private FacetSortProvider customFacetSortProvider;

	@Mock
	private SolrSearchConfigModel searchConfigModel; //NOPMD

	//Class being tested
	private DefaultFacetSearchConfigService facetSearchConfigService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		configure();
	}

	protected void configure() throws Exception
	{
		//static Log4j
		//ACCEL-2
		//		PowerMockito.mockStatic(Logger.class, Mockito.RETURNS_MOCKS);
		//		final Logger LOG = mock(Logger.class);
		//		PowerMockito.doReturn(Boolean.TRUE).when(LOG).isDebugEnabled();
		//		PowerMockito.doNothing().when(LOG).info(anyString());
		//		PowerMockito.doNothing().when(LOG).debug(anyString());
		//		PowerMockito.doNothing().when(LOG).warn(anyString());
		//		given(Logger.getLogger(Matchers.<String> any())).willReturn(LOG);
		//
		//		//static Config utils
		//		PowerMockito.mockStatic(Config.class, Mockito.RETURNS_MOCKS);
		//		given(Config.getString(anyString(), anyString())).willReturn(SOLR_INDEXEDPROPERTY_FORBIDDEN_CHAR);
		//		given(Integer.valueOf(Config.getInt(anyString(), anyInt()))).willReturn(Integer.valueOf(FACET_LIMIT_DEFAULT));

		configureCatalogVersion();
		configureQueryModels();
		configureIndexedTypeModel();
		configureIndexedPropertyModel();
		configureConfigModel();
		configureBeanFactory();

		given(solrService.getSolrServerMaster(Matchers.<SolrConfig> any(), anyString())).willReturn(solrServer);

		final IndexStatistics indexStatistics = Mockito.mock(IndexStatistics.class);
		given(indexStatistics.getLastModifiedTime()).willReturn(Calendar.getInstance());
		given(solrServer.getIndexStatistics()).willReturn(indexStatistics);
		//ACCEL-2
		//		defaultCommerceFacetSearchConfigService = PowerMockito.spy(new DefaultCommerceFacetSearchConfigService());
		//		defaultCommerceFacetSearchConfigService.setDefaultFieldValueProvider(defaultFieldValueProvider);
		//		defaultCommerceFacetSearchConfigService.setDefaultIdentityProvider(defaultIdentityProvider);
		//		defaultCommerceFacetSearchConfigService.setDefaultModelLoader(defaultModelLoader);
		//		defaultCommerceFacetSearchConfigService.setFacetSearchConfigDao(facetSearchConfigDao);
		//		defaultCommerceFacetSearchConfigService.setXmlConfigParser(xmlConfigParser);
		//		defaultCommerceFacetSearchConfigService.setMediaService(mediaService);
		//		defaultCommerceFacetSearchConfigService.setBeanFactory(beanFactory);
		//		defaultCommerceFacetSearchConfigService.setTypeService(typeService);
		//		defaultCommerceFacetSearchConfigService.setSolrService(solrService);
	}

	protected void configureCatalogVersion()
	{
		//ACCEL-2
		//		given(catalogVersion.getVersion()).willReturn(TEST_CATALOG_VERSION_VER);
		//		final CatalogModel catalogModel = mock(CatalogModel.class);
		//		given(catalogModel.getId()).willReturn(TEST_CATALOG_VERSION_ID);
		//		given(catalogVersion.getCatalog()).willReturn(catalogModel);
		//		given(catalogVersion.getCategorySystemName()).willReturn(null);
		//		given(catalogVersion.getFacetSearchConfigs()).willReturn(Collections.singletonList(configModel));
	}

	protected void configureQueryModels()
	{
		//solrIndexedTypeModel Query Models
		given(Boolean.valueOf(solrIndexerQueryModelFull.isInjectLastIndexTime())).willReturn(Boolean.TRUE);
		given(solrIndexerQueryModelFull.getType()).willReturn(IndexerOperationValues.FULL);
		given(solrIndexerQueryModelFull.getQuery()).willReturn("TEST_FULL_QUERY");

		given(Boolean.valueOf(solrIndexerQueryModelUpdate.isInjectLastIndexTime())).willReturn(Boolean.TRUE);
		given(solrIndexerQueryModelUpdate.getType()).willReturn(IndexerOperationValues.UPDATE);
		given(solrIndexerQueryModelUpdate.getQuery()).willReturn("TEST_UPDATE_QUERY");

		given(Boolean.valueOf(solrIndexerQueryModelDelete.isInjectLastIndexTime())).willReturn(Boolean.TRUE);
		given(solrIndexerQueryModelDelete.getType()).willReturn(IndexerOperationValues.DELETE);
		given(solrIndexerQueryModelDelete.getQuery()).willReturn("TEST_DELETE_QUERY");

		queryModels = new ArrayList<SolrIndexerQueryModel>();
		queryModels.add(solrIndexerQueryModelFull);
		queryModels.add(solrIndexerQueryModelUpdate);
		queryModels.add(solrIndexerQueryModelDelete);
	}

	protected void configureIndexedTypeModel()
	{
		//ACCEL-2
		//		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		//		given(composedType.getCode()).willReturn(TEST_COMPOSED_TYPE_CODE);
		//		given(solrIndexedTypeModel.getType()).willReturn(composedType);
		//		given(solrIndexedTypeModel.getIndexName()).willReturn(null);
		//		given(solrIndexedTypeModel.getValuesProvider()).willReturn(null);
		//		given(solrIndexedTypeModel.getIdentifier()).willReturn(TEST_INDEX_TYPE_IDENTIFIER);
		//		given(solrIndexedTypeModel.getIdentityProvider()).willReturn(null);
		//		given(solrIndexedTypeModel.getSolrIndexerQueries()).willReturn(queryModels);
		//		given(solrIndexedTypeModel.getModelLoader()).willReturn(null);
		//		given(Boolean.valueOf(solrIndexedTypeModel.isVariant())).willReturn(Boolean.FALSE);
		//		given(solrIndexedTypeModel.getSorts()).willReturn(Collections.<SolrSortModel> emptyList());
	}

	protected void configureIndexedPropertyModel()
	{
		//Indexed Properties
		given(solrIndexedPropertyModel.getName()).willReturn(TEST_SOLR_INDEXED_PROPERTY_NAME);
		given(solrIndexedPropertyModel.getType()).willReturn(SolrPropertiesTypes.STRING); //Options:[DOUBLE][STRING][FLOAT][INT][DATE]
		given(Boolean.valueOf(solrIndexedPropertyModel.isCurrency())).willReturn(Boolean.FALSE); //Options:[FALSE][TRUE]
		given(solrIndexedPropertyModel.getSortableType()).willReturn(null); //Options:[null][SolrPropertiesTypes.STRING]
		given(solrIndexedPropertyModel.getFacetSort()).willReturn(null); //Options:[CUSTOM][ALPHA][COUNT]
		given(solrIndexedPropertyModel.getCustomFacetSortProvider()).willReturn(null); //Options:[null][TEST_CUSTOM_FACET_SORT_PROVIDER_NAME]
		given(solrIndexedPropertyModel.getRangeSet()).willReturn(null); //Options:[null][SolrValueRangeSetModel]
		given(solrIndexedPropertyModel.getRangeSets()).willReturn(Collections.<SolrValueRangeSetModel> emptyList()); //Options:[empty][singleton]
		given(solrIndexedPropertyModel.getFieldValueProvider()).willReturn(null); //Options: [null][TEST_INDEX_TYPE_FIELDS_VALUES_PROVIDER_NAME]
		given(solrIndexedPropertyModel.getFacetDisplayNameProvider()).willReturn(null); //Options: [null][TEST_DISPLAY_NAME_PROVIDER]
		given(Boolean.valueOf(solrIndexedPropertyModel.isFacet())).willReturn(Boolean.FALSE); //Options:[TRUE][FALSE]
		given(Boolean.valueOf(solrIndexedPropertyModel.isCategoryField())).willReturn(Boolean.FALSE); //Options:[TRUE][FALSE]
		given(Integer.valueOf(solrIndexedPropertyModel.getPriority())).willReturn(Integer.valueOf(0));
		given(solrIndexedPropertyModel.getDisplayName()).willReturn(TEST_SOLR_INDEXED_PROPERTY_NAME);
		given(solrIndexedPropertyModel.getClassAttributeAssignment()).willReturn(null); //Options:[null][ClassAttributeAssignmentModel]
		given(solrIndexedPropertyModel.getExportId()).willReturn(TEST_SOLR_INDEXED_PROPERTY_NAME);
		given(solrIndexedPropertyModel.getFacetType()).willReturn(SolrIndexedPropertyFacetType.MULTISELECTAND); //Options:[MULTISELECTAND][MULTISELECTOR][REFINE]

		given(solrIndexedTypeModel.getSolrIndexedProperties()).willReturn(Collections.singletonList(solrIndexedPropertyModel));
	}

	protected void configureConfigModel()
	{
		given(itemConfig.getSolrEndpointUrls()).willReturn(Collections.<SolrEndpointUrlModel> emptyList());
		given(itemConfig.getMode()).willReturn(SolrServerModes.EMBEDDED);
		given(itemConfig.getAliveCheckInterval()).willReturn(null);
		given(itemConfig.getConnectionTimeout()).willReturn(null);
		given(itemConfig.getReadTimeout()).willReturn(null);
		given(Boolean.valueOf(itemConfig.isEmbeddedMaster())).willReturn(Boolean.TRUE);

		//Config Model
		given(configModel.getName()).willReturn(TEST_FACET_SEARCH_CONFIG_NAME);
		given(configModel.getDescription()).willReturn(TEST_FACET_SEARCH_CONFIG_DESC);
		given(configModel.getIndexNamePrefix()).willReturn(TEST_FACET_SEARCH_CONFIG_INDEX_NAME_PREFIX);
		given(configModel.getDocument()).willReturn(null);
		given(configModel.getSolrServerConfig()).willReturn(itemConfig);
		//ACCEL-2
		//		given(configModel.getLanguages()).willReturn(Collections.singletonList(mock(LanguageModel.class)));
		//		given(configModel.getCurrencies()).willReturn(Collections.singletonList(mock(CurrencyModel.class)));
		//
		//		given(configModel.getSolrIndexConfig()).willReturn(solrIndexConfigModel);
		//		given(solrIndexConfigModel.getExportPath()).willReturn(null);
		//		given(Integer.valueOf(solrIndexConfigModel.getNumberOfThreads())).willReturn(Integer.valueOf(1));
		//		given(Integer.valueOf(solrIndexConfigModel.getBatchSize())).willReturn(Integer.valueOf(TEST_SOLR_INDEX_CONFIG_BATCH_SIZE));
		//
		//		final SolrIndexedTypeModel indexedProductType = Mockito.mock(SolrIndexedTypeModel.class);
		//		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		//		given(composedType.getCode()).willReturn(TEST_COMPOSED_TYPE_CODE);
		//		given(indexedProductType.getType()).willReturn(composedType);
		//		given(configModel.getIndexedProductType()).willReturn(indexedProductType);
		//
		//		given(configModel.getSolrIndexedTypes()).willReturn(Collections.singletonList(solrIndexedTypeModel));
		//
		//		given(searchConfigModel.getPageSize()).willReturn(Integer.valueOf(20));
		//		given(searchConfigModel.getDefaultSortOrder()).willReturn(null);
		//		given(configModel.getSolrSearchConfig()).willReturn(searchConfigModel);
		//
		//		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(TEST_FACET_SEARCH_CONFIG_NAME)).willReturn(configModel);
	}

	protected void configureBeanFactory()
	{
		given(beanFactory.getBean(TEST_INDEX_TYPE_FIELDS_VALUES_PROVIDER_NAME)).willReturn(indexedTypeFieldsValuesProvider);
		given(beanFactory.getBean(TEST_FIELDS_VALUES_PROVIDER_NAME)).willReturn(fieldsValuesProvider);
		given(beanFactory.getBean(TEST_IDENTITY_PROVIDER_NAME)).willReturn(identityProvider);
		given(beanFactory.getBean(TEST_MODEL_LOADER_NAME)).willReturn(modelLoader);
		given(beanFactory.getBean(TEST_CUSTOM_FACET_SORT_PROVIDER_NAME)).willReturn(customFacetSortProvider);
	}

	protected void configureProviders()
	{
		given(solrIndexedTypeModel.getValuesProvider()).willReturn(TEST_INDEX_TYPE_FIELDS_VALUES_PROVIDER_NAME); //Options: [null][TEST_INDEX_TYPE_FIELDS_VALUES_PROVIDER_NAME]
		given(solrIndexedTypeModel.getModelLoader()).willReturn(TEST_MODEL_LOADER_NAME); //Options: [null][TEST_MODEL_LOADER_NAME]
		given(solrIndexedTypeModel.getIdentityProvider()).willReturn(TEST_IDENTITY_PROVIDER_NAME); //Options: [null][TEST_IDENTITY_PROVIDER_NAME]
		given(solrIndexedPropertyModel.getFieldValueProvider()).willReturn(TEST_FIELDS_VALUES_PROVIDER_NAME); //Options: [null][TEST_FIELDS_VALUES_PROVIDER_NAME]
		given(solrIndexedPropertyModel.getFacetDisplayNameProvider()).willReturn(TEST_DISPLAY_NAME_PROVIDER); //Options: [null][TEST_DISPLAY_NAME_PROVIDER]
	}

	@Test
	public void testDefault() throws Exception
	{
		final FacetSearchConfig result = facetSearchConfigService.getConfiguration(catalogVersion);
		Assert.assertNotNull(result);
	}

	@Test
	public void testWithProviders() throws Exception
	{
		configureProviders();
		final FacetSearchConfig result = facetSearchConfigService.getConfiguration(catalogVersion);
		Assert.assertNotNull(result);
	}

	@Test
	public void testIndexPropertyAsFacet() throws Exception
	{
		configureIndexedPropertyModel();
		//ACCEL-2
		//		final SolrSortModel sortModel = mock(SolrSortModel.class);
		//		given(solrIndexedTypeModel.getSorts()).willReturn(Collections.singletonList(sortModel));
		//
		//		//Test the different sort types and facet types
		//		given(Boolean.valueOf(solrIndexedPropertyModel.isFacet())).willReturn(Boolean.TRUE);
		//		given(solrIndexedPropertyModel.getFacetSort()).willReturn(SolrIndexedPropertyFacetSort.ALPHA);
		//		given(solrIndexedPropertyModel.getFacetType()).willReturn(SolrIndexedPropertyFacetType.MULTISELECTOR);
		//		given(solrIndexedTypeModel.getSolrIndexedProperties()).willReturn(Collections.singletonList(solrIndexedPropertyModel));
		//		FacetSearchConfig result = defaultCommerceFacetSearchConfigService.getConfiguration(catalogVersion);
		//		Assert.assertNotNull(result);
		//
		//		//Additional CustomFacetSortProvider Test 1
		//		given(solrIndexedPropertyModel.getCustomFacetSortProvider()).willReturn(TEST_CUSTOM_FACET_SORT_PROVIDER_NAME);
		//		given(solrIndexedPropertyModel.getFacetSort()).willReturn(SolrIndexedPropertyFacetSort.CUSTOM);
		//		given(solrIndexedPropertyModel.getFacetType()).willReturn(SolrIndexedPropertyFacetType.MULTISELECTAND);
		//		result = defaultCommerceFacetSearchConfigService.getConfiguration(catalogVersion);
		//		Assert.assertNotNull(result);
		//		//Additional CustomFacetSortProvider Test 2
		//		given(solrIndexedPropertyModel.getCustomFacetSortProvider()).willReturn(null);
		//		result = defaultCommerceFacetSearchConfigService.getConfiguration(catalogVersion);
		//		Assert.assertNotNull(result);
		//
		//		given(solrIndexedPropertyModel.getFacetSort()).willReturn(SolrIndexedPropertyFacetSort.COUNT);
		//		given(solrIndexedPropertyModel.getFacetType()).willReturn(SolrIndexedPropertyFacetType.REFINE);
		//		result = defaultCommerceFacetSearchConfigService.getConfiguration(catalogVersion);
		//		Assert.assertNotNull(result);
	}

	@Test
	public void testFindIndexedTypeByTypeCodeReturnsNull() throws Exception
	{
		//ACCEL-2
		//		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		//		given(composedType.getCode()).willReturn("Category");
		//		given(solrIndexedTypeModel.getType()).willReturn(composedType);
		//		final FacetSearchConfig result = defaultCommerceFacetSearchConfigService.getConfiguration(catalogVersion);
		//		Assert.assertNotNull(result);
	}

	@Test(expected = FacetConfigServiceException.class)
	public void testSolrServerException() throws SolrServiceException, FacetConfigServiceException
	{
		given(solrService.getSolrServerMaster(Matchers.<SolrConfig> any(), anyString())).willThrow(
				new SolrServiceException("Test Exception Thrown on solrServer.getSolrServerMaster()"));
		verify(facetSearchConfigService.getConfiguration(catalogVersion));
	}

	@Test(expected = FacetConfigServiceException.class)
	public void testExceptionWhenNoConfigModel() throws SolrServiceException, FacetConfigServiceException
	{
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(TEST_FACET_SEARCH_CONFIG_NAME)).willReturn(null);
		verify(facetSearchConfigService.getConfiguration(catalogVersion));
	}
}
