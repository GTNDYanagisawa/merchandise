/**
 * 
 */
package sap.hybris.integration.models.jalo;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Administrator
 * 
 */
public class BlockedAttributeTest extends HybrisJUnit4TransactionalTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(BlockedAttributeTest.class.getName());

	@Before
	public void setUp()
	{
		// implement here code executed before each test
	}

	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}

	/**
	 * This is a sample test method.
	 */
	@Test
	public void testPresenceOfBlockedAttributeInAPI()
	{
		final ProductModel product = new ProductModel();
		product.setSapBlocked(Boolean.TRUE);
		assertTrue(product.getSapBlocked().booleanValue());
	}
}
