/**
 * 
 */
package de.hybris.platform.sap.productconfig.frontend.beforeview;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class ProductDetailsBeforeViewHandlerTest
{

	private ProductDetailsBeforeViewHandler classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductDetailsBeforeViewHandler();
	}

	@Test
	public void testBeforeViewMatch()
	{
		final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		classUnderTest.beforeView(null, response, null, ProductDetailsBeforeViewHandler.PRODUCT_CONFIG_PAGE);
		Mockito.verify(response).setHeader("Cache-control", "no-cache, no-store");
		Mockito.verify(response).setHeader("Pragma", "no-cache");
		Mockito.verify(response).setHeader("Expires", "-1");
	}

	@Test
	public void testBeforeViewNoMatch()
	{
		final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		classUnderTest.beforeView(null, response, null, "anotherPage");
		Mockito.verifyZeroInteractions(response);
	}
}
