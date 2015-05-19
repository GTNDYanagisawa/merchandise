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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigUnknownItemTypeException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeSetModel;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;


/**
 *
 */
public class FacetSearchConfigConversionTest extends FacetSearchConfigServiceTest
{

	@Resource
	private SolrConfigurationConverter solrConfigurationConverter;


	@Override
	public void setUp() throws Exception
	{
		setUpBasic();
		setUpProductData();
		localConfig = setUpSolrFacetSearchConfig();
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
		media.setRealFileName("TestFacetConfig.xml");
		media.setMime("text/xml");
		modelService.save(media);
		final InputStream is = this.getClass().getResourceAsStream("/test/TestFacetConfig.xml");
		mediaService.setStreamForMedia(media, is);
		return media;
	}

	@Override
	protected List<SolrValueRangeSetModel> setUpValueRanges()
	{
		final SolrValueRangeSetModel eurPriceRangeSet = modelService.create(SolrValueRangeSetModel.class);
		eurPriceRangeSet.setName("eurPriceRangeSet");
		eurPriceRangeSet.setType("double");
		eurPriceRangeSet.setQualifier("EUR");

		final SolrValueRangeModel eurRangeModel = modelService.create(SolrValueRangeModel.class);
		eurRangeModel.setName("1-1000");
		eurRangeModel.setFrom("1");
		eurRangeModel.setTo("1000");
		eurRangeModel.setSolrValueRangeSet(eurPriceRangeSet);
		eurPriceRangeSet.setSolrValueRanges(Arrays.asList(eurRangeModel));

		final SolrValueRangeSetModel usdPriceRangeSet = modelService.create(SolrValueRangeSetModel.class);
		usdPriceRangeSet.setName("usdPriceRangeSet");
		usdPriceRangeSet.setType("double");
		usdPriceRangeSet.setQualifier("USD");

		final SolrValueRangeModel usdRangeModel = modelService.create(SolrValueRangeModel.class);
		usdRangeModel.setName("1-2000");
		usdRangeModel.setFrom("1");
		usdRangeModel.setTo("2000");
		usdRangeModel.setSolrValueRangeSet(usdPriceRangeSet);
		usdPriceRangeSet.setSolrValueRanges(Arrays.asList(usdRangeModel));

		final SolrValueRangeSetModel defaultRangeSetModel = modelService.create(SolrValueRangeSetModel.class);
		defaultRangeSetModel.setName("defaultSet");
		defaultRangeSetModel.setType("double");

		final SolrValueRangeModel rangeModel = modelService.create(SolrValueRangeModel.class);
		rangeModel.setName("doubleRange");
		rangeModel.setFrom("1");
		rangeModel.setTo("999");
		rangeModel.setSolrValueRangeSet(defaultRangeSetModel);
		defaultRangeSetModel.setSolrValueRanges(Arrays.asList(rangeModel));

		return Arrays.asList(eurPriceRangeSet, usdPriceRangeSet, defaultRangeSetModel);
	}


	@Test
	public void testFacetSearchConfigServiceUnknownItemType() throws Exception
	{
		boolean passed = false;
		try
		{
			//writing xml data to configuration that contains unknown item type.
			interpretXmlBasedConfig(localConfig, "TestFacetConfigException1.xml");

			facetSearchConfigService.getConfiguration(localConfig.getName());
		}
		catch (final FacetConfigUnknownItemTypeException e)
		{
			passed = true;
		}
		assertTrue(
				"Invocation on facetSearchConfigService.getConfiguration() should have failed with FacetConfigUnknownItemTypeException",
				passed);
	}

	@Ignore
	@Override
	public void testFacetSearchConfigService() throws Exception
	{
		//
	}

	@Ignore
	@Override
	public void testConfigSerializable() throws Exception
	{
		// 
	}


	@Test
	public void testFacetSearchConfigQualifiedRanges() throws Exception
	{
		interpretXmlBasedConfig(localConfig, "TestFacetConfigQualifiedRanges.xml");

		final FacetSearchConfig config = facetSearchConfigService.getConfiguration(localConfig.getName());
		assertNotNull("Config must not be null", config);
		final IndexedType indexedType = config.getIndexConfig().getIndexedTypes().get("Product");
		assertNotNull("IndexedType must not be null", indexedType);
		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get("price");
		assertNotNull("Indexed Property must not be null", indexedProperty);
		final List<ValueRange> eurValueRanges = IndexedProperties.getValueRanges(indexedProperty, "EUR");
		assertNotNull("EUR value ranges must not be null", eurValueRanges);
		assertEquals("Number of ranges for EUR", 1, eurValueRanges.size());
		final ValueRange eurValueRange = eurValueRanges.iterator().next();
		assertEquals("Name of EUR range", "1-1000", eurValueRange.getName());
		assertEquals("Start of EUR range", Double.valueOf(1.0), eurValueRange.getFrom());
		assertEquals("End of EUR range", Double.valueOf(1000), eurValueRange.getTo());
		final List<ValueRange> usdValueRanges = IndexedProperties.getValueRanges(indexedProperty, "USD");
		assertNotNull("USD value ranges must not be null", usdValueRanges);
		assertEquals("Number of ranges for USD", 1, usdValueRanges.size());
		final ValueRange usdValueRange = usdValueRanges.iterator().next();
		assertEquals("Name of USD range", "1-2000", usdValueRange.getName());
		assertEquals("Start of USD range", Double.valueOf(1.0), usdValueRange.getFrom());
		assertEquals("End of USD range", Double.valueOf(2000.0), usdValueRange.getTo());
	}


	protected void interpretXmlBasedConfig(final SolrFacetSearchConfigModel config, final String testXMLFileName)
			throws FacetConfigServiceException
	{
		final InputStream is = this.getClass().getResourceAsStream("/test/" + testXMLFileName);
		if (is != null)
		{
			final MediaModel document = config.getDocument();
			mediaService.setStreamForMedia(document, is);
		}

		solrConfigurationConverter.convertToItemBasedSolrConfiguration(localConfig);
		modelService.save(localConfig);
	}

	@Override
	@Ignore
	public void testFacetSearchConfigDao()
	{
		//ignored because in this class it would fail
		//SolrFacetSearchConfigValidator was changed and object with xml document cannot be save anymore 
	}
}
