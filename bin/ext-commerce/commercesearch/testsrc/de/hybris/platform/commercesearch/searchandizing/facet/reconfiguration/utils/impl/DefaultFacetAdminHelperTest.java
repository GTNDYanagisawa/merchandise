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
package de.hybris.platform.commercesearch.searchandizing.facet.reconfiguration.utils.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.commercesearch.model.SolrFacetReconfigurationModel;
import de.hybris.platform.commercesearch.searchandizing.facet.dao.FacetDao;
import de.hybris.platform.commercesearch.searchandizing.facet.reconfiguration.FacetReconfigurationService;
import de.hybris.platform.commercesearch.searchandizing.searchprofile.comparator.SolrFacetReconfigurationComparator;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;


public class DefaultFacetAdminHelperTest
{

	DefaultFacetAdminHelper defaultFacetAdminHelper;

	private static final int SOLR_FACET_RECONFIGURATION_MODEL_PRIORITY_LOW = 100;
	private static final int SOLR_FACET_RECONFIGURATION_MODEL_PRIORITY_MEDIUM = 500;
	private static final int SOLR_FACET_RECONFIGURATION_MODEL_PRIORITY_HIGH = 1000;
	private static final int MIN_FACET_PRIORITY = 100;
	private static final String facetNameLow = "FacetLow";
	private static final String facetNameMedium = "FacetMedium";
	private static final String facetNameHigh = "FacetHigh";

	private static final String[] facetNamesInCorrectOrder =
	{ facetNameHigh, facetNameMedium, facetNameLow };

	@Mock
	private FacetDao facetDao;
	@Mock
	private FacetReconfigurationService facetReconfigurationService;

	private SolrFacetReconfigurationModel solrFacetReconfigurationModelLow;
	private SolrFacetReconfigurationModel solrFacetReconfigurationModelMedium;
	private SolrFacetReconfigurationModel solrFacetReconfigurationModelHigh;

	@Mock
	private SolrFacetReconfigurationComparator solrFacetReconfigurationComparator;



	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultFacetAdminHelper = new DefaultFacetAdminHelper();
		defaultFacetAdminHelper.setSolrFacetReconfigurationComparator(solrFacetReconfigurationComparator);

		final SolrIndexedPropertyModel facetLow = new SolrIndexedPropertyModel(facetNameLow, null);
		solrFacetReconfigurationModelLow = new SolrFacetReconfigurationModel(facetLow, null);
		final SolrIndexedPropertyModel facetMedium = new SolrIndexedPropertyModel(facetNameMedium, null);
		solrFacetReconfigurationModelMedium = new SolrFacetReconfigurationModel(facetMedium, null);
		final SolrIndexedPropertyModel facetHigh = new SolrIndexedPropertyModel(facetNameHigh, null);
		solrFacetReconfigurationModelHigh = new SolrFacetReconfigurationModel(facetHigh, null);

		given(
				solrFacetReconfigurationComparator.compareInstances(solrFacetReconfigurationModelLow,
						solrFacetReconfigurationModelLow)).willReturn(0);
		given(
				solrFacetReconfigurationComparator.compareInstances(solrFacetReconfigurationModelMedium,
						solrFacetReconfigurationModelMedium)).willReturn(0);
		given(
				solrFacetReconfigurationComparator.compareInstances(solrFacetReconfigurationModelHigh,
						solrFacetReconfigurationModelHigh)).willReturn(0);
		given(
				solrFacetReconfigurationComparator.compareInstances(solrFacetReconfigurationModelLow,
						solrFacetReconfigurationModelMedium)).willReturn(-1);
		given(
				solrFacetReconfigurationComparator.compareInstances(solrFacetReconfigurationModelLow,
						solrFacetReconfigurationModelHigh)).willReturn(-1);
		given(
				solrFacetReconfigurationComparator.compareInstances(solrFacetReconfigurationModelHigh,
						solrFacetReconfigurationModelMedium)).willReturn(1);
		given(
				solrFacetReconfigurationComparator.compareInstances(solrFacetReconfigurationModelHigh,
						solrFacetReconfigurationModelLow)).willReturn(1);


	}

	@Test
	public void testUpdateReconfigurationsPriority() throws Exception
	{
		//given
		final SolrFacetReconfigurationModel solrFacetReconfigurationModel1 = new SolrFacetReconfigurationModel();
		solrFacetReconfigurationModel1.setPriority(SOLR_FACET_RECONFIGURATION_MODEL_PRIORITY_LOW);
		final SolrFacetReconfigurationModel solrFacetReconfigurationModel2 = new SolrFacetReconfigurationModel();
		solrFacetReconfigurationModel2.setPriority(SOLR_FACET_RECONFIGURATION_MODEL_PRIORITY_MEDIUM);
		final SolrFacetReconfigurationModel solrFacetReconfigurationModel3 = new SolrFacetReconfigurationModel();
		solrFacetReconfigurationModel3.setPriority(SOLR_FACET_RECONFIGURATION_MODEL_PRIORITY_HIGH);
		List<SolrFacetReconfigurationModel> reconfigurations = new ArrayList<>();
		reconfigurations.add(solrFacetReconfigurationModel1);
		reconfigurations.add(solrFacetReconfigurationModel2);
		reconfigurations.add(solrFacetReconfigurationModel3);

		//when
		reconfigurations = defaultFacetAdminHelper.updateReconfigurationsPriority(reconfigurations);
		int facetPriority = MIN_FACET_PRIORITY;


		//then
		for (final SolrFacetReconfigurationModel solrFacetReconfigurationModel : Lists.reverse(reconfigurations))
		{
			assertThat(solrFacetReconfigurationModel.getPriority()).isEqualTo(facetPriority);
			facetPriority = facetPriority + 100;
		}
	}

	@Test
	public void testMoveFacetBelowInternally() throws Exception
	{
		//given
		List<SolrFacetReconfigurationModel> reconfigurations = new ArrayList<>();
		reconfigurations.add(solrFacetReconfigurationModelLow);
		reconfigurations.add(solrFacetReconfigurationModelHigh);
		reconfigurations.add(solrFacetReconfigurationModelMedium);

		//when
		reconfigurations = defaultFacetAdminHelper.moveFacetBelowInternally("FacetLow", "FacetMedium", reconfigurations);

		//then
		for (int i = 0; i < reconfigurations.size(); i++)
		{
			assertThat(reconfigurations.get(i).getFacet().getName()).isEqualTo(facetNamesInCorrectOrder[i]);
		}
	}

	@Test
	public void testMoveFacetAboveInternally() throws Exception
	{
		//given
		List<SolrFacetReconfigurationModel> reconfigurations = new ArrayList<>();
		reconfigurations.add(solrFacetReconfigurationModelMedium);
		reconfigurations.add(solrFacetReconfigurationModelLow);
		reconfigurations.add(solrFacetReconfigurationModelHigh);

		//when
		reconfigurations = defaultFacetAdminHelper.moveFacetBelowInternally("FacetHigh", "FacetMedium", reconfigurations);

		//then
		for (int i = 0; i < reconfigurations.size(); i++)
		{
			assertThat(reconfigurations.get(i).getFacet().getName()).isEqualTo(facetNamesInCorrectOrder[i]);
		}
	}

}
