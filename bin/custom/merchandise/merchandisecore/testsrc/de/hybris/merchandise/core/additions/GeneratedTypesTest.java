package de.hybris.merchandise.core.additions;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.merchandise.core.constants.GeneratedMerchandiseCoreConstants.Attributes.Customer;
import de.hybris.merchandise.core.constants.GeneratedMerchandiseCoreConstants.Attributes.Product;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for verifying correct type code generation
 *
 * @author sebastian.mahr $Id: AttributeTest.java 2632 2011-07-13 07:44:55Z Mahr.Hybris $
 */
@UnitTest
public class GeneratedTypesTest
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(GeneratedTypesTest.class.getName());


	@Before
	public void setUp()
	{
		//
	}

	@After
	public void tearDown()
	{
		// implement code executed after each test here
	}

	/**
	 * Test Product if type is extended correctly
	 */
	@Test
	public void testProductAttribute()
	{
		//final boolean testTrue = true;
		//assertTrue("true is not true", testTrue);

		assertTrue(ProductModel.INTERNALONLY.equals(Product.INTERNALONLY));
	}

	/**
	 * Test Customer if type is extended correctly
	 */
	@Test
	public void testCustomerAttribute()
	{
		//final boolean testTrue = true;
		//assertTrue("true is not true", testTrue);

		assertTrue(CustomerModel.ISINTERNAL.equals(Customer.ISINTERNAL));
	}
}