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
package com.hybris.instore.widgets.qrcode;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.instore.widgets.qrcode.QRCodeController.ScannerApp;


/**
 * @author johannesdoberer
 * 
 */
@NullSafeWidget(value = false)
@DeclaredInput(socketType = Object.class, value = QRCodeController.SOCKET_IN_SCAN_QR_CODE)
@DeclaredViewEvent(componentID = "", eventName = Events.ON_CREATE)
public class QRCodeControllerTest extends AbstractWidgetUnitTest<QRCodeController>
{
	@InjectMocks
	private final QRCodeController qrCodeController = new QRCodeController();

	@SuppressWarnings("unused")
	@Mock
	private ProductFacade productFacade;

	private final String productCode = "1234";

	@Mock
	private Execution executionMock;



	@Override
	protected QRCodeController getWidgetController()
	{
		return qrCodeController;
	}

	@Before
	public void setUp()
	{
		// Mock required zk environment
		ExecutionsCtrl.setCurrent(executionMock);

		Mockito.when(executionMock.getHeader("host")).thenReturn("testhost");

		final HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
		Mockito.when(executionMock.getNativeRequest()).thenReturn(requestMock);

		Mockito.when(requestMock.getParameter(QRCodeController.URL_PARAMETER_PRODUCT)).thenReturn(productCode);
	}


	@Test
	public void testCheckDeeplinkWhenProductExists()
	{
		// Prepare data
		final ProductData productMock = new ProductData();
		productMock.setCode(productCode);
		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(productCode), Mockito.anyCollection())).thenReturn(
				productMock);

		// Execute method
		qrCodeController.checkDeeplink();

		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(QRCodeController.SOCKET_OUT_SCANNED_PRODUCT), Mockito.eq(productMock));
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(QRCodeController.SOCKET_OUT_PRODUCT_FOUND), Mockito.eq(Boolean.TRUE));
	}



	@Test
	public void testScanCodePic2Shop()
	{
		// Prepare 
		widgetInstanceManager.getWidgetSettings().put(QRCodeController.SETTING_URL_ENCODING, "UTF-8");
		final StringBuilder uriBuilder = new StringBuilder();
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				uriBuilder.append(invocation.getArguments()[0]);
				return null;
			}
		}).when(executionMock).sendRedirect(Mockito.anyString());


		// Execute method
		qrCodeController.scanCode();


		// Verify
		Mockito.verify(executionMock).sendRedirect(Mockito.anyString());
		final String uri = uriBuilder.toString();
		Assert.assertTrue(uri.startsWith(QRCodeController.URL_PREFIX_PIC2SHOP));
		Assert.assertTrue(uri.contains("testhost"));
	}


	@Test
	public void testScanCodePicZXing()
	{
		// Prepare 
		final String testFormats = "test1,test2";
		widgetInstanceManager.getWidgetSettings().put(QRCodeController.SETTING_SCANNER_APP, ScannerApp.zxing.name());
		widgetInstanceManager.getWidgetSettings().put(QRCodeController.SETTING_URL_ENCODING, "UTF-8");
		widgetInstanceManager.getWidgetSettings().put(QRCodeController.SETTING_SCAN_FORMATS, testFormats);

		final StringBuilder uriBuilder = new StringBuilder();
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				uriBuilder.append(invocation.getArguments()[0]);
				return null;
			}
		}).when(executionMock).sendRedirect(Mockito.anyString());


		// Execute method
		qrCodeController.scanCode();


		// Verify
		Mockito.verify(executionMock).sendRedirect(Mockito.anyString());
		final String uri = uriBuilder.toString();
		Assert.assertTrue(uri.startsWith(QRCodeController.URL_PREFIX_ZXING));
		Assert.assertTrue(uri.contains("testhost"));
		Assert.assertTrue(uri.endsWith(QRCodeController.URL_PARAMETER_FORMATS + "=" + testFormats));
	}
}
