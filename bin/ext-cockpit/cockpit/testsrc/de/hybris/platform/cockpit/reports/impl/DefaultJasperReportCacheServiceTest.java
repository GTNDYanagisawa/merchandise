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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.reports.JasperMediaService;
import de.hybris.platform.cockpit.reports.JasperReportCompileService;
import de.hybris.platform.cockpit.reports.JasperReportExportService;
import de.hybris.platform.cockpit.reports.JasperReportFillService;
import de.hybris.platform.cockpit.reports.model.CompiledJasperMediaModel;
import de.hybris.platform.cockpit.reports.model.JasperMediaModel;
import de.hybris.platform.cockpit.reports.model.JasperWidgetPreferencesModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.awt.image.BufferedImage;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zkoss.image.AImage;


@UnitTest
public class DefaultJasperReportCacheServiceTest
{
	DefaultJasperReportCacheService jasperReportCacheService = new DefaultJasperReportCacheService();

	@Mock
	private JasperReportCompileService jasperReportCompileService;
	@Mock
	private JasperReportFillService jasperReportFillService;
	@Mock
	private JasperMediaService jasperMediaService;
	@Mock
	private JasperReportExportService jasperReportExportService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		jasperReportCacheService.setJasperMediaService(jasperMediaService);
		jasperReportCacheService.setJasperReportCompileService(jasperReportCompileService);
		jasperReportCacheService.setJasperReportExportService(jasperReportExportService);
		jasperReportCacheService.setJasperReportFillService(jasperReportFillService);
	}

	@Test
	public void testGetCompiledReport()
	{
		//given
		final JasperWidgetPreferencesModel widget = mock(JasperWidgetPreferencesModel.class);
		final JasperMediaModel mockMedia = mock(JasperMediaModel.class);
		when(widget.getReport()).thenReturn(mockMedia);
		final JasperReport mockReport = mock(JasperReport.class);
		when(jasperReportCompileService.compileReport(mockMedia)).thenReturn(mockReport);

		//when
		final JasperReport report = jasperReportCacheService.getCompiledReport(widget);

		//then
		assertThat(report).isNotNull();
		assertThat(report).isSameAs(mockReport);

		//when
		final JasperReport report2 = jasperReportCacheService.getCompiledReport(widget);

		//then
		assertThat(report).isSameAs(report2);
	}

	@Test
	public void testGetCompiledReportWhenCompiledJasperMedia()
	{
		//given
		final JasperWidgetPreferencesModel widget = mock(JasperWidgetPreferencesModel.class);
		final CompiledJasperMediaModel mockMedia = mock(CompiledJasperMediaModel.class);
		final MediaModel media = mock(MediaModel.class);
		when(mockMedia.getCompiledReport()).thenReturn(media);
		when(widget.getReport()).thenReturn(mockMedia);
		final JasperReport mockReport = mock(JasperReport.class);
		when(jasperMediaService.getReportFromMedia(media)).thenReturn(mockReport);

		//when
		final JasperReport report = jasperReportCacheService.getCompiledReport(widget);

		//then
		assertThat(report).isNotNull();
		assertThat(report).isSameAs(mockReport);

		//when
		final JasperReport report2 = jasperReportCacheService.getCompiledReport(widget);

		//then
		assertThat(report).isSameAs(report2);
	}

	@Test
	public void testGetFilled() throws Exception
	{
		//given
		final JasperWidgetPreferencesModel widget = mock(JasperWidgetPreferencesModel.class);
		final JasperReport mockReport = placeWidgetIntoCache(widget);
		final JasperPrint mockPrint = mock(JasperPrint.class);
		when(jasperReportFillService.fillReport(mockReport, widget)).thenReturn(mockPrint);
		final BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		when(jasperReportExportService.exportToImage(mockPrint)).thenReturn(bufferedImage);

		//when
		final AImage image = jasperReportCacheService.getImageForJasperWidgetPreferences(widget);

		//then
		assertThat(image).isNotNull();

		//when
		final AImage image2 = jasperReportCacheService.getImageForJasperWidgetPreferences(widget);

		//then
		assertThat(image2).isSameAs(image);
	}

	@Test
	public void testInvalidateAll() throws Exception
	{
		//given
		final JasperWidgetPreferencesModel widget = mock(JasperWidgetPreferencesModel.class);
		placeWidgetIntoCache(widget);

		//when
		assertThat(jasperReportCacheService.getCompiledReport(widget)).isNotNull();
		jasperReportCacheService.invalidateAll();

		//then
		assertThat(jasperReportCacheService.getCompiledReport(widget)).isNotNull();

		//when
		placeWidgetIntoImageCache(widget);
		final AImage im1 = jasperReportCacheService.getImageForJasperWidgetPreferences(widget);
		assertThat(im1).isNotNull();
		jasperReportCacheService.invalidateAll();
		assertThat(jasperReportCacheService.getImageForJasperWidgetPreferences(widget)).isNotSameAs(im1);
	}

	@Test
	public void testUpdate() throws Exception
	{
		//given
		final JasperWidgetPreferencesModel widget = mock(JasperWidgetPreferencesModel.class);
		placeWidgetIntoImageCache(widget);

		//when
		final boolean updated = jasperReportCacheService.update(widget);

		//then
		assertThat(updated).isTrue();

		//when
		final boolean updated2 = jasperReportCacheService.update(widget);

		//then
		assertThat(updated2).isFalse();

	}

	@Test
	public void testRemoveWhenPlacedIntoImageCache() throws Exception
	{
		//given
		final JasperWidgetPreferencesModel widget = mock(JasperWidgetPreferencesModel.class);
		placeWidgetIntoImageCache(widget);

		//when
		final boolean removed = jasperReportCacheService.remove(widget);

		//then
		assertThat(removed).isTrue();

		//when
		final boolean removed2 = jasperReportCacheService.remove(widget);

		//then
		assertThat(removed2).isFalse();

	}

	@Test
	public void testRemoveWhenPlacedIntoCompiledCache() throws Exception
	{
		//given
		final JasperWidgetPreferencesModel widget = mock(JasperWidgetPreferencesModel.class);

		//when
		final boolean removedBeforeAnyPlace = jasperReportCacheService.remove(widget);

		//then
		assertThat(removedBeforeAnyPlace).isFalse();

		//given
		placeWidgetIntoCache(widget);

		//when
		final boolean removed = jasperReportCacheService.remove(widget);

		//then
		assertThat(removed).isTrue();

		//when
		final boolean removed2 = jasperReportCacheService.remove(widget);

		//then
		assertThat(removed2).isFalse();

	}

	private JasperReport placeWidgetIntoCache(final JasperWidgetPreferencesModel widget)
	{
		final JasperMediaModel mockMedia = mock(JasperMediaModel.class);
		when(widget.getReport()).thenReturn(mockMedia);
		final JasperReport mockReport = mock(JasperReport.class);
		when(jasperReportCompileService.compileReport(mockMedia)).thenReturn(mockReport);

		return jasperReportCacheService.getCompiledReport(widget);
	}

	private AImage placeWidgetIntoImageCache(final JasperWidgetPreferencesModel widget) throws Exception
	{
		final JasperReport mockReport = placeWidgetIntoCache(widget);
		final JasperPrint mockPrint = mock(JasperPrint.class);
		when(jasperReportFillService.fillReport(mockReport, widget)).thenReturn(mockPrint);
		final BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		when(jasperReportExportService.exportToImage(mockPrint)).thenReturn(bufferedImage);
		return jasperReportCacheService.getImageForJasperWidgetPreferences(widget);
	}
}
