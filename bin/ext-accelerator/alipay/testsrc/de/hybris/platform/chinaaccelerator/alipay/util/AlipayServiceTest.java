package de.hybris.platform.chinaaccelerator.alipay.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayRequestData;
import de.hybris.platform.chinaaccelerator.alipay.data.ProcessingRequestData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.RequestServiceType;
import de.hybris.platform.chinaaccelerator.alipay.util.AlipayService;

public class AlipayServiceTest {
	
	private AlipayConfiguration alipayConfiguration;
	
	@Mock
	private AlipayService alipayService;
	
	String basepath = "basepath";
	String web_seller_email = "web_seller_email";
	String partner = "partner";
	String web_gateway = "web_gateway";
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		alipayService = new AlipayService();
		alipayConfiguration = Mockito.mock(AlipayConfiguration.class);
		Mockito.when(alipayConfiguration.getBasepath()).thenReturn(basepath);
		Mockito.when(alipayConfiguration.getWeb_seller_email()).thenReturn(web_seller_email);
		Mockito.when(alipayConfiguration.getWeb_partner()).thenReturn(partner);
		Mockito.when(alipayConfiguration.getWeb_gateway()).thenReturn(web_gateway);
		Mockito.when(alipayConfiguration.getReturnBaseUrl()).thenReturn(basepath);
		alipayService.setAlipayConfiguration(alipayConfiguration);		
	}
	
	@Test
	public void testGetRequestUrl(){
		String direct_pay_service_api_name = "direct_pay_service_api_name";
		String direct_pay_paymethod_name = "direct_pay_paymethod_name";
		alipayService.setDirect_pay_paymethod_name(direct_pay_paymethod_name);
		alipayService.setDirect_pay_service_api_name(direct_pay_service_api_name);
		
		RequestServiceType serviceType = RequestServiceType.DIRECTPAY;
		
		ProcessingRequestData processingRequestData = new ProcessingRequestData();
		AlipayRequestData alipayRequestData = new AlipayRequestData();
		processingRequestData.setRequestServiceType(serviceType);
		processingRequestData.setAlipayRequestData(alipayRequestData);
		processingRequestData.setToSupplyReturnUrl(Boolean.TRUE);
		
		String result = alipayService.getRequestUrl(processingRequestData);
		
		Assert.assertEquals(alipayRequestData.getService(), direct_pay_service_api_name);
		Assert.assertEquals(alipayRequestData.getPartner(), partner);
		Assert.assertEquals(alipayRequestData.get_input_charset(), PaymentConstants.Basic.INPUT_CHARSET);
		Assert.assertEquals(alipayRequestData.getPaymethod(), direct_pay_paymethod_name);
		Assert.assertEquals(alipayRequestData.getReturn_url(), basepath + PaymentConstants.Controller.DIRECT_AND_EXPRESS_RETURN_URL);
		Assert.assertEquals(alipayRequestData.getNotify_url(), basepath + PaymentConstants.Controller.DIRECT_AND_EXPRESS_NOTIFY_URL);
		Assert.assertEquals(alipayRequestData.getError_notify_url(), basepath + PaymentConstants.Controller.ERROR_NOTIFY_URL);
		Assert.assertEquals(alipayRequestData.getSeller_email(), web_seller_email);
		Assert.assertEquals(alipayRequestData.getDefault_login(), null);
		Assert.assertNotNull(result);
		Assert.assertEquals("web_gateway_input_charset=utf-8&error_notify_url=basepath/alipay/errorController&" +
				"notify_url=basepath/alipay/notifyController&partner=partner&payment_type=1&paymethod=direct_pay_paymethod_name&" +
				"return_url=basepath/alipay/returnresponseController&seller_email=web_seller_email&service=direct_pay_service_api_name&" +
				"sign=fbcc8334e80f3e3da3c6bc07842892d0&sign_type=MD5", result);
	}
	

	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}
}
