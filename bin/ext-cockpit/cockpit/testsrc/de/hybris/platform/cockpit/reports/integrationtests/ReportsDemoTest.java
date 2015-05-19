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
package de.hybris.platform.cockpit.reports.integrationtests;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.enums.RefreshTimeOption;
import de.hybris.platform.cockpit.reports.JasperMediaService;
import de.hybris.platform.cockpit.reports.JasperReportCacheService;
import de.hybris.platform.cockpit.reports.JasperReportCompileService;
import de.hybris.platform.cockpit.reports.JasperReportExportService;
import de.hybris.platform.cockpit.reports.JasperReportFillService;
import de.hybris.platform.cockpit.reports.JasperReportsRefreshService;
import de.hybris.platform.cockpit.reports.impl.AbstractVJDBCServicelayerTransactionalTest;
import de.hybris.platform.cockpit.reports.model.JasperMediaModel;
import de.hybris.platform.cockpit.reports.model.JasperWidgetPreferencesModel;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.zkoss.image.AImage;


@IntegrationTest(standalone = false)
public class ReportsDemoTest extends AbstractVJDBCServicelayerTransactionalTest
{
	@Resource
	private JasperMediaService jasperMediaService;

	@Resource
	private JasperReportExportService jasperReportExportService;

	@Resource
	private JasperReportFillService jasperReportFillService;

	@Resource
	private JasperReportCompileService jasperReportCompileService;

	@Resource
	private JasperReportsRefreshService jasperReportsRefreshService;

	@Resource
	private JasperReportCacheService jasperReportCacheService;

	@Before
	public void setUp() throws Exception
	{

		createCoreData();
		createDefaultCatalog();
		importCsv("/test/reports/testDataForReports.csv", "UTF-8");
		assureIntegrationTestAllowed();
	}

	@Ignore("BAM-363 please fix the test logic")
	@Test
	public void testCreateCompileFillAndExportReport()
	{
		final JasperMediaModel jasperMedia = jasperMediaService.getJasperMediaForCode("testAverageOrderValue");

		assertThat(jasperMedia).isNotNull();

		final JasperReport report = jasperReportCompileService.compileReport(jasperMedia);
		assertThat(report).isNotNull();

		final JasperWidgetPreferencesModel configuration = jasperMediaService.createJasperWidgetPreferencesFromMedia(report,
				jasperMedia, null);

		final JasperPrint filledReport = jasperReportFillService.fillReport(report, configuration);
		assertThat(filledReport).isNotNull();

		final byte[] excelFile = jasperReportExportService.exportToExcel(filledReport);
		final byte[] pdfFile = jasperReportExportService.exportToPdf(filledReport);
		final BufferedImage imageFile = jasperReportExportService.exportToImage(filledReport);

		assertThat(excelFile).isNotEmpty();
		assertThat(pdfFile).isNotEmpty();
		assertThat(imageFile).isNotNull();
	}

	@Test
	public void testReportCaching() throws Exception
	{
		final JasperWidgetPreferencesModel configuration = createConfiguration();

		final JasperReport report2 = jasperReportCacheService.getCompiledReport(configuration);
		assertThat(report2).isNotNull();

		final JasperReport report3 = jasperReportCacheService.getCompiledReport(configuration);
		assertThat(report3).isSameAs(report2);

		final AImage image = jasperReportCacheService.getImageForJasperWidgetPreferences(configuration);
		assertThat(image).isNotNull();

		final AImage image2 = jasperReportCacheService.getImageForJasperWidgetPreferences(configuration);
		assertThat(image2).isSameAs(image);

		final boolean updated = jasperReportCacheService.update(configuration);
		assertThat(updated).isTrue();

		final AImage image3 = jasperReportCacheService.getImageForJasperWidgetPreferences(configuration);
		assertThat(image3).isNotSameAs(image2);

		jasperReportCacheService.invalidateAll();

		final JasperReport report4 = jasperReportCacheService.getCompiledReport(configuration);
		assertThat(report4).isSameAs(report3);

		final AImage image4 = jasperReportCacheService.getImageForJasperWidgetPreferences(configuration);
		assertThat(image4).isNotSameAs(image3);
	}

	@Test
	public void testReportRefreshing() throws Exception
	{
		final JasperWidgetPreferencesModel configuration = createConfiguration();

		jasperReportsRefreshService.startRefreshing(configuration, RefreshTimeOption.FIVESEC);
		final boolean notRefreshedYet = jasperReportsRefreshService.onRefresh();
		assertThat(notRefreshedYet).isFalse();

		Thread.sleep(5100);

		final boolean refreshed = jasperReportsRefreshService.onRefresh();
		assertThat(refreshed).isTrue();

		final boolean refreshed2 = jasperReportsRefreshService.onRefresh();
		assertThat(refreshed2).isFalse();

		jasperReportsRefreshService.stopRefreshing(configuration);

		Thread.sleep(5100);

		final boolean refreshed3 = jasperReportsRefreshService.onRefresh();
		assertThat(refreshed3).isFalse();
	}

	private JasperWidgetPreferencesModel createConfiguration()
	{
		final JasperMediaModel jasperMedia = jasperMediaService.getJasperMediaForCode("testAverageOrderValue");
		assertThat(jasperMedia).isNotNull();

		final JasperReport report = jasperReportCompileService.compileReport(jasperMedia);
		assertThat(report).isNotNull();

		final JasperWidgetPreferencesModel configuration = jasperMediaService.createJasperWidgetPreferencesFromMedia(report,
				jasperMedia, null);
		return configuration;
	}
}
