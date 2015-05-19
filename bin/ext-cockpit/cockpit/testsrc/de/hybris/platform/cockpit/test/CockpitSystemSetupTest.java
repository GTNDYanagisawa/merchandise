/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cockpit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.components.editorarea.export.ReportsConfiguration;
import de.hybris.platform.cockpit.jalo.CockpitTest;
import de.hybris.platform.cockpit.jalo.WidgetPreferences;
import de.hybris.platform.cockpit.reports.model.JasperWidgetPreferencesModel;
import de.hybris.platform.cockpit.systemsetup.CockpitSystemSetup;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Test if cockpitSystemSetup imports reports as expected.
 * 
 * @author Jacek
 */
@IntegrationTest
public class CockpitSystemSetupTest extends CockpitTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CockpitSystemSetupTest.class);

	private static CockpitSystemSetup css = null;

	private static ModelService modelService;
	private static MediaService mediaService;
	private static I18NService i18nService;
	private static FlexibleSearchService flexibleSearchService;
	private static ReportsConfiguration repConf;

	private static CatalogModel catalogModel;
	private static CatalogVersionModel cvm;

	private static MediaFolderModel root;

	@Before
	public void initTest()
	{
		modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		mediaService = (MediaService) Registry.getApplicationContext().getBean("mediaService");
		i18nService = (I18NService) Registry.getApplicationContext().getBean("i18nService");
		flexibleSearchService = (FlexibleSearchService) Registry.getApplicationContext().getBean("flexibleSearchService");
		repConf = new ReportsConfiguration();
		/* create default cat version */
		catalogModel = new CatalogModel();
		catalogModel.setId("cpitSysStpTest" + System.currentTimeMillis());
		catalogModel.setDefaultCatalog(Boolean.TRUE);
		modelService.save(catalogModel);

		cvm = new CatalogVersionModel();
		cvm.setCatalog(catalogModel);
		cvm.setVersion("ver0.0" + System.currentTimeMillis());
		cvm.setActive(Boolean.TRUE);
		modelService.save(cvm);
		modelService.saveAll();

		/* create root folder */
		try
		{
			root = mediaService.getRootFolder();
		}
		catch (final ModelNotFoundException e)
		{
			root = new MediaFolderModel();
			root.setQualifier("root");
			modelService.save(root);
		}
		/* make sure there are no reports in db */
		final Map<String, String> reportsParamNamesMap = repConf.getReportsParamNamesMap();
		final Set<String> keySet = reportsParamNamesMap.keySet();
		for (final String name : keySet)
		{
			try
			{
				final MediaModel reprot = mediaService.getMedia(name);
				//no good -> remove
				modelService.remove(reprot);

			}
			catch (final UnknownIdentifierException e)
			{
				//good
			}
		}
		/* make sure there is no preferences object in db */
		assertNull("Preferences item was already in db!", getMainReportPreferences());
		/* prepare object to test */
		css = new CockpitSystemSetup();
		css.setModelService(modelService);
		css.setMediaService(mediaService);
		css.setI18nService(i18nService);
		css.setFlexibleSearchService(flexibleSearchService);
		css.setReportsConfig(new ReportsConfiguration());
	}

	@Test
	public void testCreateEditorAreaJasperReports()
	{
		/* test if reports and parameters are imported */
		final int params1stRun = importReports();
		/* call it one more time to check if parameters are not doubled */
		final int params2ndRun = importReports();
		assertEquals("Wrong quantity of parameters (Twice as much?)", params1stRun, params2ndRun);
	}

	private int importReports()
	{
		css.createEditorAreaJasperReports();//method under test

		/* verify if preferences object was created */
		final JasperWidgetPreferencesModel reportPref = getMainReportPreferences();
		assertNotNull("Preferences item was not created", reportPref);

		/* verify quantity of parameters in preferences object */
		final int paramtersQa = reportPref.getParameters().size();
		//following magic '5' value is quantity of custom parameters
		final int customParameters = 5;//see 'saveAdditionalParameters' method in CockpitSystemSetup
		final int expectPramsQa = repConf.getReportsParamNamesMap().size() + customParameters;
		assertEquals("Have you added some parameters to report and forgot to update the test?", expectPramsQa, paramtersQa);

		/* verify if all reports were imported */
		final Map<String, String> names = repConf.getReportsParamNamesMap();
		names.keySet();
		for (final String repName : names.keySet())
		{
			try
			{
				mediaService.getMedia(repName);
			}
			catch (final UnknownIdentifierException e)
			{
				fail("Report: " + repName + " was not imported.");
			}
		}
		/* parameters */
		return paramtersQa;
	}

	/**
	 * Helper method for gathering from db the preferences object of main report
	 */
	private JasperWidgetPreferencesModel getMainReportPreferences()
	{
		final List<Object> jasperWidgets = flexibleSearchService.search(
				"select * from {" + JasperWidgetPreferencesModel._TYPECODE + "} where {" + WidgetPreferences.TITLE + "} like '"
						+ repConf.getMainReportPreferencesTitle() + "'").getResult();
		if (CollectionUtils.isEmpty(jasperWidgets))
		{
			return null;
		}
		else
		{
			final JasperWidgetPreferencesModel reportPref = (JasperWidgetPreferencesModel) jasperWidgets.get(0);
			modelService.refresh(reportPref);//surprisingly but it's needed.
			return reportPref;
		}
	}
}
