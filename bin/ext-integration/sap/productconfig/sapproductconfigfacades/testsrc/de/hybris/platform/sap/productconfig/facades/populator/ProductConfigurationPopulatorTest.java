/**
 * 
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationPopulatorTest
{

	/**
	 * 
	 */
	private static final String SAP_CONFIGURABLE = "sapConfigurable";
	private ProductConfigurationPopulator classUnderTest;
	private ProductData productData;
	private ProductModel productModel;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationPopulator();
		productData = new ProductData();
		productModel = new ProductModel();
		classUnderTest.setModelService(modelService);
		//make sure that test runs not with DEBUG logging as it will fail then
		Logger.getLogger(ProductConfigurationPopulator.class).setLevel(Level.INFO);
		classUnderTest.setConfigurableSource(SAP_CONFIGURABLE);

	}

	@Test
	public void testPopulateConfigurableTrue()
	{
		productModel.setSapConfigurable(Boolean.TRUE);
		Mockito.when(modelService.getAttributeValue(productModel, SAP_CONFIGURABLE)).thenReturn(Boolean.TRUE);
		classUnderTest.populate(productModel, productData);
		assertTrue("config flag not populated", productData.getConfigurable().booleanValue());
	}

	@Test
	public void testPopulateConfigurableFalse()
	{
		productModel.setSapConfigurable(Boolean.FALSE);
		Mockito.when(modelService.getAttributeValue(productModel, SAP_CONFIGURABLE)).thenReturn(Boolean.FALSE);
		classUnderTest.populate(productModel, productData);
		assertFalse("config flag not populated", productData.getConfigurable().booleanValue());
	}
}
