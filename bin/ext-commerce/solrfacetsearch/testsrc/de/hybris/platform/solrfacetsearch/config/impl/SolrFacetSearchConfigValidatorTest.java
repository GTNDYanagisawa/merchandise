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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.constants.SolrfacetsearchConstants;
import de.hybris.platform.solrfacetsearch.jalo.config.SolrFacetSearchConfig;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SolrFacetSearchConfigValidatorTest
{
	final static private String SOLR_INDEXED_TYPES = "Item types";
	final static private String SOLR_INDEX_CONFIG = "Indexer configuration";
	final static private String SOLR_SERVER_CONFIG = "Solr server confguration";

	private SolrFacetSearchConfigValidator validator;
	@Mock
	private TypeService typeService;
	@Mock
	private AttributeDescriptorModel indexedTypesDescriptor;
	@Mock
	private AttributeDescriptorModel indexConfigDescriptor;
	@Mock
	private AttributeDescriptorModel serverConfigDescriptor;

	@Before
	public void setUp()
	{
		prepareMockObjects();
		validator = new SolrFacetSearchConfigValidator();
		validator.setTypeService(typeService);
	}

	private void prepareMockObjects()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(indexedTypesDescriptor.getName()).thenReturn(SOLR_INDEXED_TYPES);
		Mockito.when(indexConfigDescriptor.getName()).thenReturn(SOLR_INDEX_CONFIG);
		Mockito.when(serverConfigDescriptor.getName()).thenReturn(SOLR_SERVER_CONFIG);

		Mockito.when(
				typeService.getAttributeDescriptor(SolrfacetsearchConstants.TC.SOLRFACETSEARCHCONFIG,
						SolrFacetSearchConfig.SOLRINDEXEDTYPES)).thenReturn(indexedTypesDescriptor);


		Mockito.when(
				typeService.getAttributeDescriptor(SolrfacetsearchConstants.TC.SOLRFACETSEARCHCONFIG,
						SolrFacetSearchConfig.SOLRINDEXCONFIG)).thenReturn(indexConfigDescriptor);


		Mockito.when(
				typeService.getAttributeDescriptor(SolrfacetsearchConstants.TC.SOLRFACETSEARCHCONFIG,
						SolrFacetSearchConfig.SOLRSERVERCONFIG)).thenReturn(serverConfigDescriptor);
	}

	@Test
	public void testModelWithXMLDocument()
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfig = new SolrFacetSearchConfigModel();
		final MediaModel xmlDocument = new MediaModel();
		solrFacetSearchConfig.setDocument(xmlDocument);

		try
		{
			validator.onValidate(solrFacetSearchConfig, null);
		}
		catch (final InterceptorException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().endsWith(SolrFacetSearchConfigValidator.DOCUMENT_DEPRECATED_ERROR));
			return;
		}

		Assert.fail();
	}

	@Test
	public void testModelWithoutRequiredMemberItems()
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfig = new SolrFacetSearchConfigModel();

		try
		{
			validator.onValidate(solrFacetSearchConfig, null);
		}
		catch (final InterceptorException e)
		{
			final String message = e.getMessage();
			Assert.assertNotNull(message);
			Assert.assertTrue(message.contains(SolrFacetSearchConfigValidator.REQUIRED_MEMBER_ITEMS));
			Assert.assertTrue(message.contains(SOLR_INDEXED_TYPES));
			Assert.assertTrue(message.contains(SOLR_INDEX_CONFIG));
			Assert.assertTrue(message.contains(SOLR_SERVER_CONFIG));
			return;
		}

		Assert.fail();
	}
}
