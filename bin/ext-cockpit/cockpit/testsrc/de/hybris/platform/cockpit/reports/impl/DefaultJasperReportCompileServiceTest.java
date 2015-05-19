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
package de.hybris.platform.cockpit.reports.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.reports.JasperReportCompileService;
import de.hybris.platform.cockpit.reports.exceptions.JasperReportCompileException;
import de.hybris.platform.cockpit.reports.model.JasperMediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.testframework.TestUtils;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperReport;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultJasperReportCompileServiceTest extends AbstractVJDBCServicelayerTransactionalTest
{
	@Resource
	private JasperReportCompileService jasperReportCompileService;

	@Resource
	private MediaService mediaService;

	private JasperMediaModel media = null;

	@Before
	public void setUp() throws Exception
	{

		createCoreData();
		createDefaultCatalog();
		importCsv("/test/reports/testDataForReports.csv", "UTF-8");
		assureIntegrationTestAllowed();
	}

	@Test
	public void testCompileWithSuccess()
	{
		//given
		media = (JasperMediaModel) mediaService.getMedia("testAverageOrderValue");
		assertThat(media).isNotNull();

		//when
		final JasperReport report = jasperReportCompileService.compileReport(media);

		//then
		assertThat(report).isNotNull();
	}

	@Test
	public void testCompileWithFailure()
	{
		//given
		media = (JasperMediaModel) mediaService.getMedia("notAReport");
		assertThat(media).isNotNull();

		try
		{
			// the compilation will throw a SAXParseException
			TestUtils.disableFileAnalyzer();

			//when
			jasperReportCompileService.compileReport(media);
			fail();
		}
		catch (final JasperReportCompileException ex)
		{
			//then OK
		}
		TestUtils.enableFileAnalyzer();
	}
}
