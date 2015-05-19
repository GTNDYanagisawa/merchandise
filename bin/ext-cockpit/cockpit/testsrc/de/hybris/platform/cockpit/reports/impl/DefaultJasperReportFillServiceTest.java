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

import de.hybris.bootstrap.annotations.DemoTest;
import de.hybris.platform.cockpit.model.WidgetParameterModel;
import de.hybris.platform.cockpit.reports.JasperReportCompileService;
import de.hybris.platform.cockpit.reports.JasperReportFillService;
import de.hybris.platform.cockpit.reports.exceptions.JasperReportFillException;
import de.hybris.platform.cockpit.reports.model.JasperMediaModel;
import de.hybris.platform.cockpit.reports.model.JasperWidgetPreferencesModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@DemoTest
public class DefaultJasperReportFillServiceTest extends AbstractVJDBCServicelayerTransactionalTest
{
	@Resource
	private JasperReportFillService jasperReportFillService;

	@Resource
	private JasperReportCompileService jasperReportCompileService;

	@Resource
	private MediaService mediaService;

	private JasperReport compiledReport = null;

	@Before
	public void setUp() throws Exception
	{

		createCoreData();
		createDefaultCatalog();
		importCsv("/test/reports/testDataForReports.csv", "UTF-8");
		assureIntegrationTestAllowed();
		Config.setParameter("cockpit.reports.vjdbc.username", "demoUser");
		Config.setParameter("cockpit.reports.vjdbc.password", "1234");
	}

	@Test
	public void testFillWithSuccess()
	{
		//given
		final JasperWidgetPreferencesModel configuration = mock(JasperWidgetPreferencesModel.class);
		final JasperMediaModel media = (JasperMediaModel) mediaService.getMedia("testAverageOrderValue");
		assertThat(media).isNotNull();
		compiledReport = jasperReportCompileService.compileReport(media);

		//when
		final JasperPrint report = jasperReportFillService.fillReport(compiledReport, configuration);

		//then
		assertThat(report).isNotNull();
	}

	@Test
	public void testFillWithFailure()
	{
		//given
		final JasperWidgetPreferencesModel configuration = mock(JasperWidgetPreferencesModel.class);

		final JasperMediaModel media = (JasperMediaModel) mediaService.getMedia("testAverageOrderValue");
		assertThat(media).isNotNull();
		compiledReport = jasperReportCompileService.compileReport(media);
		Mockito.when(configuration.getTitle()).thenReturn("notAReport");

		final WidgetParameterModel wrongParameter = mock(WidgetParameterModel.class);
		Mockito.when(wrongParameter.getName()).thenReturn("To");
		Mockito.when(wrongParameter.getValue()).thenReturn(Integer.valueOf(1));
		final TypeModel mockType = mock(TypeModel.class);
		Mockito.when(mockType.getCode()).thenReturn("java.util.Date");
		Mockito.when(wrongParameter.getType()).thenReturn(mockType);
		Mockito.when(wrongParameter.getTargetType()).thenReturn("java.util.Date");
		final Collection<WidgetParameterModel> mockParameters = Collections.singletonList(wrongParameter);
		Mockito.when(configuration.getParameters()).thenReturn(mockParameters);

		//when
		try
		{

			jasperReportFillService.fillReport(compiledReport, configuration);
			fail();
		}
		catch (final JasperReportFillException ex)
		{
			//then OK
		}

	}
}
