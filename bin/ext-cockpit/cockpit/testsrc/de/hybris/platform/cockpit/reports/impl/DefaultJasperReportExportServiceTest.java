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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.reports.JasperReportCompileService;
import de.hybris.platform.cockpit.reports.JasperReportExportService;
import de.hybris.platform.cockpit.reports.JasperReportFillService;
import de.hybris.platform.cockpit.reports.exceptions.JasperReportExportException;
import de.hybris.platform.cockpit.reports.model.JasperMediaModel;
import de.hybris.platform.cockpit.reports.model.JasperWidgetPreferencesModel;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.servicelayer.media.MediaService;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@IntegrationTest(standalone = false)
public class DefaultJasperReportExportServiceTest extends AbstractVJDBCServicelayerTransactionalTest
{

	@Resource
	private JasperReportExportService jasperReportExportService;

	@Resource
	private JasperReportFillService jasperReportFillService;

	@Resource
	private JasperReportCompileService jasperReportCompileService;

	@Resource
	private MediaService mediaService;

	private JasperReport compiledReport = null;

	protected User testROUser;

	protected UserGroup testROGroup;

	private JasperPrint report;



	@Before
	public void setUp() throws Exception
	{

		createCoreData();
		createDefaultCatalog();
		importCsv("/test/reports/testDataForReports.csv", "UTF-8");
		assureIntegrationTestAllowed();
		//Config.setParameter("cockpit.reports.vjdbc.username", "vjdbcReportsUser");
		//Config.setParameter("cockpit.reports.vjdbc.password", "1234");

		final JasperWidgetPreferencesModel configuration = mock(JasperWidgetPreferencesModel.class);
		final JasperMediaModel media = (JasperMediaModel) mediaService.getMedia("testAverageOrderValue");
		assertThat(media).isNotNull();
		compiledReport = jasperReportCompileService.compileReport(media);
		assertThat(compiledReport).isNotNull();
		report = jasperReportFillService.fillReport(compiledReport, configuration);
		assertThat(report).isNotNull();
	}

	@Test
	public void testExportToExcelWithSuccess()
	{
		//when
		final byte[] excel = jasperReportExportService.exportToExcel(report);

		//then

		assertThat(excel).isNotEmpty();
	}

	@Ignore("PLA-10683")
	@Test
	public void testExportToExcelWithFailure()
	{
		//given
		report = mock(JasperPrint.class);

		//when
		try
		{
			jasperReportExportService.exportToExcel(report);
			fail();
		}
		catch (final JasperReportExportException ex)
		{
			//then OK
		}
	}

	@Test
	public void testExportToPdfWithSuccess()
	{
		//when
		final byte[] pdf = jasperReportExportService.exportToPdf(report);

		//then

		assertThat(pdf).isNotEmpty();
	}

	@Test
	public void testExportToPdfWithFailure()
	{
		//given
		report = mock(JasperPrint.class);

		//when
		try
		{
			jasperReportExportService.exportToPdf(report);
			fail();
		}
		catch (final JasperReportExportException ex)
		{
			//then OK
		}
	}

	@Ignore("PLA-10683")
	@Test
	public void testExportToImageWithSuccess()
	{
		//when
		final BufferedImage image = jasperReportExportService.exportToImage(report);

		//then

		assertThat(image).isNotNull();
		assertThat(image.getWidth()).isGreaterThan(0);
		assertThat(image.getHeight()).isGreaterThan(0);
	}

	@Test
	public void testExportToImageWithFailure()
	{
		//given
		report = mock(JasperPrint.class);
		when(Integer.valueOf(report.getPageWidth())).thenReturn(Integer.valueOf(1));
		when(Integer.valueOf(report.getPageHeight())).thenReturn(Integer.valueOf(1));

		//when
		try
		{
			jasperReportExportService.exportToImage(report);
			fail();
		}
		catch (final JasperReportExportException ex)
		{
			//then OK
		}
	}



}
