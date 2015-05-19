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
package de.hybris.platform.acceleratorcms.context.impl;

import de.hybris.platform.acceleratorcms.preview.strategies.PreviewContextInformationLoaderStrategy;
import de.hybris.platform.acceleratorcms.preview.strategies.impl.ActiveBaseSitePreviewStrategy;
import de.hybris.platform.acceleratorcms.preview.strategies.impl.CatalogVersionsPreviewStrategy;
import de.hybris.platform.acceleratorcms.preview.strategies.impl.UiExperienceLevelPreviewStrategy;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class ContextInformationLoaderTest
{
	private DefaultContextInformationLoader contextInformationLoader;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private UiExperienceService uiExperienceService;

	@Mock
	private PreviewDataModel previewDataModel;

	@Mock
	private CMSSiteModel siteModel;

	@Mock
	private Collection<CatalogVersionModel> catalogVersionModel;

	private final UiExperienceLevel uiExperienceLevel = UiExperienceLevel.DESKTOP;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(previewDataModel.getActiveSite()).thenReturn(siteModel);

		Mockito.when(previewDataModel.getCatalogVersions()).thenReturn(catalogVersionModel);

		Mockito.when(previewDataModel.getUiExperience()).thenReturn(uiExperienceLevel);


		final List<PreviewContextInformationLoaderStrategy> strategies = new ArrayList<PreviewContextInformationLoaderStrategy>();
		final ActiveBaseSitePreviewStrategy activeBase = new ActiveBaseSitePreviewStrategy();
		activeBase.setBaseSiteService(baseSiteService);
		final CatalogVersionsPreviewStrategy catalogVersions = new CatalogVersionsPreviewStrategy();
		catalogVersions.setCatalogVersionService(catalogVersionService);
		final UiExperienceLevelPreviewStrategy uiExperience = new UiExperienceLevelPreviewStrategy();
		uiExperience.setUiExperienceService(uiExperienceService);

		strategies.add(activeBase);
		strategies.add(catalogVersions);
		strategies.add(uiExperience);

		contextInformationLoader = new DefaultContextInformationLoader();
		contextInformationLoader.setPreviewRequestStrategies(strategies);
		contextInformationLoader.initializePreviewRequest(previewDataModel);
	}

	@Test
	public void testActiveBase()
	{
		Mockito.verify(baseSiteService).setCurrentBaseSite(previewDataModel.getActiveSite(), true);
	}

	@Test
	public void testCatalogVersion()
	{
		Mockito.verify(catalogVersionService).setSessionCatalogVersions(previewDataModel.getCatalogVersions());
	}

	@Test
	public void testUiExperience()
	{
		Mockito.verify(uiExperienceService).setDetectedUiExperienceLevel(previewDataModel.getUiExperience());
	}
}