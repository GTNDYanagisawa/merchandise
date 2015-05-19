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
package de.hybris.platform.solrfacetsearch.config.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.ClusterConfig;
import de.hybris.platform.solrfacetsearch.config.EndpointURL;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperties;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrConfigurationConverter;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.testframework.Transactional;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@Transactional
@IntegrationTest
public class SolrConfigurationConverterTest extends AbstractSolrTest
{

	@Resource
	private MediaService mediaService;
	@Resource
	private SolrConfigurationConverter solrConfigurationConverter;
	@Resource
	private ModelService modelService;

	private SolrFacetSearchConfigModel xmlBasedConfiguration;
	private String configName;

	@Override
	@Before
	public void setUp() throws Exception
	{
		setUpBasic();
		setUpProductData();

		xmlBasedConfiguration = setUpSolrFacetSearchConfig();
		configName = xmlBasedConfiguration.getName();
	}

	@Override
	@After
	public void tearDown()
	{
		if (xmlBasedConfiguration != null && !modelService.isNew(xmlBasedConfiguration))
		{
			modelService.remove(xmlBasedConfiguration);
		}
	}

	@Override
	protected boolean isItemBasedConfig()
	{
		return false;
	}

	@Override
	protected MediaModel setUpXmlConfiguration()
	{
		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode("testMedia");
		media.setRemovable(Boolean.TRUE);
		final CatalogVersionModel cv = getCatalogVersionsForSolrFacetSearchConfig().get(0);
		media.setCatalogVersion(cv);
		media.setRealFileName("TestConfigConverion.xml");
		media.setMime("text/xml");
		modelService.save(media);
		final InputStream is = this.getClass().getResourceAsStream("/test/TestConfigConversion.xml");
		mediaService.setStreamForMedia(media, is);
		return media;
	}

	@Test
	public void testSolrConfigConversion() throws FacetConfigServiceException
	{
		assertNotNull(xmlBasedConfiguration.getDocument());
		assertNull(xmlBasedConfiguration.getSolrIndexConfig());
		assertNull(xmlBasedConfiguration.getSolrServerConfig());
		Assertions.assertThat(xmlBasedConfiguration.getSolrIndexedTypes()).isNullOrEmpty();

		final List<CatalogVersionModel> cvListBefore = xmlBasedConfiguration.getCatalogVersions();
		assertNotNull(cvListBefore);

		solrConfigurationConverter.convertToItemBasedSolrConfiguration(xmlBasedConfiguration);
		modelService.save(xmlBasedConfiguration);

		final List<CatalogVersionModel> cvListAfter = xmlBasedConfiguration.getCatalogVersions();
		assertNotNull(cvListAfter);

		for (final CatalogVersionModel cvBefore : cvListBefore)
		{
			Assertions.assertThat(cvListAfter).contains(cvBefore);
		}

		assertNull(xmlBasedConfiguration.getDocument());
		Assertions.assertThat(xmlBasedConfiguration.getSolrIndexedTypes()).hasSize(1);
		assertNotNull(xmlBasedConfiguration.getSolrIndexConfig());
		assertNotNull(xmlBasedConfiguration.getSolrServerConfig());

		final SolrIndexedTypeModel indexedTypeModel = xmlBasedConfiguration.getSolrIndexedTypes().get(0);
		Assertions.assertThat(indexedTypeModel.getSolrIndexedProperties()).hasSize(3);

		final FacetSearchConfig itemBasedPojo = facetSearchConfigService.getConfiguration(configName);

		assertNotNull(itemBasedPojo);

		final SolrConfig itemBasedSolrConfig = itemBasedPojo.getSolrConfig();

		assertEquals(SolrServerMode.EMBEDDED, itemBasedSolrConfig.getMode());

		final ClusterConfig itemBasedClusterConfig = itemBasedSolrConfig.getClusterConfig();

		assertEquals(1, itemBasedClusterConfig.getAliveCheckInterval().intValue());
		assertEquals(2, itemBasedClusterConfig.getConnectionTimeout().intValue());


		final Comparator<? super EndpointURL> endpointUrlComparator = new Comparator<EndpointURL>()
		{
			@Override
			public int compare(final EndpointURL o1, final EndpointURL o2)
			{
				return o1.getUrl().compareTo(o2.getUrl());
			}
		};
		Collections.sort(itemBasedClusterConfig.getEndpointURLs(), endpointUrlComparator);

		assertEquals("http://localhost/test1", itemBasedClusterConfig.getEndpointURLs().get(0).getUrl());
		assertTrue(itemBasedClusterConfig.getEndpointURLs().get(0).isMaster());

		assertEquals("http://localhost/test2", itemBasedClusterConfig.getEndpointURLs().get(1).getUrl());
		assertFalse(itemBasedClusterConfig.getEndpointURLs().get(1).isMaster());

		assertEquals("http://localhost/test3", itemBasedClusterConfig.getEndpointURLs().get(2).getUrl());
		assertFalse(itemBasedClusterConfig.getEndpointURLs().get(2).isMaster());

		final IndexConfig itemBasedIndexConfig = itemBasedPojo.getIndexConfig();
		Assert.assertNotNull(itemBasedIndexConfig);

		final IndexedType indexedType = itemBasedIndexConfig.getIndexedTypes().get("Product_conversionTest");
		assertNotNull(indexedType);
		assertFalse(indexedType.isVariant());
		assertEquals(indexedType.getIndexName(), "conversionTest");
		Assertions.assertThat(indexedType.getIndexedProperties()).hasSize(3);
		final IndexedProperty codeProperty = indexedType.getIndexedProperties().get("code");
		assertNotNull(codeProperty);
		assertTrue(codeProperty.isFacet());
		final IndexedProperty descriptionProperty = indexedType.getIndexedProperties().get("description");
		assertNotNull(descriptionProperty);
		final IndexedProperty nameProperty = indexedType.getIndexedProperties().get("name");
		assertNotNull(nameProperty);
		assertFalse(nameProperty.isFacet());
		assertTrue(IndexedProperties.isRanged(nameProperty));
		Assertions.assertThat(IndexedProperties.getValueRanges(nameProperty, null)).hasSize(1);

		final IndexedTypeFlexibleSearchQuery full = indexedType.getFlexibleSearchQueries().get(IndexOperation.FULL);
		assertNotNull(full);
		assertEquals("fullIndexQuery", full.getQuery());
		assertEquals("anonymous", full.getUserId());
		final IndexedTypeFlexibleSearchQuery update = indexedType.getFlexibleSearchQueries().get(IndexOperation.UPDATE);
		assertNotNull(update);
		assertEquals("updateIndexQuery", update.getQuery());
		assertEquals("anonymous", update.getUserId());
		final IndexedTypeFlexibleSearchQuery delete = indexedType.getFlexibleSearchQueries().get(IndexOperation.DELETE);
		assertNotNull(delete);
		assertEquals("deleteFromIndexQuery", delete.getQuery());
		assertEquals("anonymous", delete.getUserId());

	}
}
