package de.hybris.platform.chinaaccelerator.alipay.data;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;

public class AlipayConfigurationTest {

	AlipayConfiguration alipayConfiguration = new AlipayConfiguration();
	
	@Before
	public void setUp()
	{
		alipayConfiguration.setTest_amount(Boolean.FALSE.toString());
	}
	
	@Test
	public void testGetRequestPrice_two_decimals_format(){
		final double price = 0.01000;
		final String result = alipayConfiguration.getRequestPrice(price);
		Assert.assertEquals("0.01", result);
	}
	
	@Test
	public void testGetRefundPrice_two_decimals_format(){
		final double price = 0.01000;
		final String result = alipayConfiguration.getRefundPrice(price);
		Assert.assertEquals("0.01", result);
	}
}
