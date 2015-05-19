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

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigs;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrSynonymConfigModel;
import de.hybris.platform.solrfacetsearch.solr.LanguageSynonymMappings;
import de.hybris.platform.solrfacetsearch.solr.SolrConfigurationService;
import de.hybris.platform.solrfacetsearch.solr.SolrService;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


/**
 *
 */
public class DefaultSolrSynonymServiceTest
{
	@Mock
	private SolrConfigurationService solrConfigurationService;
	@Mock
	private FacetSearchConfigService facetSearchConfigService;
	@Mock
	private SolrService solrService;
	@Mock
	private CommonI18NService i18nService;
	@Mock
	private org.apache.solr.client.solrj.SolrServer srvImpl;

	@InjectMocks
	private final DefaultSolrSynonymService defaultSolrSynonymService = new DefaultSolrSynonymService();

	private final Locale localeDe = new Locale("DE");
	private final Locale localeEn = new Locale("EN");
	private final Map<Locale, LanguageModel> map = new HashMap<Locale, LanguageModel>();

	private final Set<Locale> locales = Sets.newHashSet(localeDe, localeEn);

	/**
	 * 
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		for (final Locale locale : locales)
		{
			map.put(locale, convertLocaleToLanguage(locale));
		}
	}

	private SolrSynonymConfigModel createSynonym(final String synonymFrom, final String synonymTo, final Locale lang)
	{
		final LanguageModel language = map.get(lang);
		final SolrSynonymConfigModel syn = new SolrSynonymConfigModel();
		syn.setSynonymFrom(synonymFrom);
		syn.setSynonymTo(synonymTo);
		syn.setLanguage(language);
		return syn;
	}

	private LanguageModel convertLocaleToLanguage(final Locale lang)
	{
		final LanguageModel language = Mockito.mock(LanguageModel.class);
		Mockito.when(language.getIsocode()).thenReturn(lang.getLanguage());
		return language;
	}

	private final static String SYN1_FROM = "ala,ma kota";
	private final static String SYN1_TO = "ala,ma kota";

	private final static String SYN2_FROM = "ala,ma kota";
	private final static String SYN2_TO = null;

	private final static String SYN3_FROM = "ala,ma kota";
	private final static String SYN3_TO = "kot,ma,ale";

	private final static String SYN4_FROM = "ala,ma kota";
	private final static String SYN4_TO = null;

	private final static String CNF_NAME = "name";

	/**
	 * Test method for {@link DefaultSolrSynonymService#updateSynonyms(SolrFacetSearchConfigModel)}.
	 */
	@Test
	public void testUpdateSynonymsForConfiguration() throws Exception //NOPMD
	{
		//Data initialization		
		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.EMBEDDED);
		final FacetSearchConfig config = FacetSearchConfigs.createFacetSearchConfig(CNF_NAME, null, null, null, solrConfig);
		final SolrServer srv = new SolrServer(srvImpl, true);

		final SolrFacetSearchConfigModel configuration = new SolrFacetSearchConfigModel();
		configuration.setName(CNF_NAME);
		configuration.setSynonyms(Arrays.asList(createSynonym(SYN1_FROM, SYN1_TO, localeDe),
				createSynonym(SYN2_FROM, SYN2_TO, localeDe), createSynonym(SYN3_FROM, SYN3_TO, localeEn),
				createSynonym(SYN4_FROM, SYN4_TO, localeEn)));

		//result
		final LanguageSynonymMappings synonymsPerLanguage = new LanguageSynonymMappings();
		synonymsPerLanguage.addMapping(localeDe.getLanguage(), SYN1_FROM + "=>" + SYN1_TO + '\n' + SYN2_FROM + '\n');
		synonymsPerLanguage.addMapping(localeEn.getLanguage(), SYN3_FROM + "=>" + SYN3_TO + '\n' + SYN4_FROM + '\n');

		//Mock preparation
		Mockito.when(solrService.getRegistredServersForFacetSearchConfig(config)).thenReturn(Sets.newHashSet(srv));
		final List<LanguageModel> convertLocalesToLanguages = new ArrayList(map.values());
		Mockito.when(i18nService.getAllLanguages()).thenReturn(convertLocalesToLanguages);
		Mockito.when(facetSearchConfigService.getConfiguration(CNF_NAME)).thenReturn(config);

		//Run
		defaultSolrSynonymService.updateSynonyms(configuration);

		//Verification
		Mockito.verify(solrConfigurationService).updateSynonyms(srv, synonymsPerLanguage);
	}



	/**
	 * Test method for {@link DefaultSolrSynonymService#updateSynonyms(Locale, SolrFacetSearchConfigModel)}.
	 */
	@Test
	public void testUpdateSynonymsForConfigurationAndLanguage() throws Exception //NOPMD
	{
		//Data initialization		
		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.EMBEDDED);
		final FacetSearchConfig config = FacetSearchConfigs.createFacetSearchConfig(CNF_NAME, null, null, null, solrConfig);
		final SolrServer srv = new SolrServer(srvImpl, true);

		final SolrFacetSearchConfigModel configuration = new SolrFacetSearchConfigModel();
		configuration.setName(CNF_NAME);
		configuration.setSynonyms(Arrays.asList(createSynonym(SYN1_FROM, SYN1_TO, localeDe),
				createSynonym(SYN2_FROM, SYN2_TO, localeDe), createSynonym(SYN3_FROM, SYN3_TO, localeEn),
				createSynonym(SYN4_FROM, SYN4_TO, localeEn)));

		//result
		final LanguageSynonymMappings synonymsPerLanguage = new LanguageSynonymMappings();
		synonymsPerLanguage.addMapping(localeDe.getLanguage(), SYN1_FROM + "=>" + SYN1_TO + '\n' + SYN2_FROM + '\n');

		//Mock preparation
		Mockito.when(solrService.getRegistredServersForFacetSearchConfig(config)).thenReturn(Sets.newHashSet(srv));
		final List<LanguageModel> convertLocalesToLanguages = new ArrayList(map.values());
		Mockito.when(i18nService.getAllLanguages()).thenReturn(convertLocalesToLanguages);
		final LanguageModel langDe = map.get(localeDe);
		Mockito.when(i18nService.getLanguage(localeDe.getLanguage())).thenReturn(langDe);

		Mockito.when(facetSearchConfigService.getConfiguration(CNF_NAME)).thenReturn(config);

		//Run
		defaultSolrSynonymService.updateSynonyms(localeDe, configuration);

		//Verification
		Mockito.verify(solrConfigurationService).updateSynonyms(Mockito.eq(srv), Mockito.eq(synonymsPerLanguage));
	}
}
