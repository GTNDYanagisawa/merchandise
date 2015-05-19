/**
 * 
 */
package sap.hybris.integration.models.jalo;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Administrator
 * 
 */
public class BaseStoreAttributesTest extends HybrisJUnit4TransactionalTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(BaseStoreAttributesTest.class.getName());

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
	public void testPresenceOfDCattributeInAPI()
	{
		final BaseStoreModel basestore = new BaseStoreModel(); //NOPMD
		//		basestore.setDistributionChannel("01");
		//		assertTrue("01".equals(basestore.getDistributionChannel()));
		//		//assertTrue(product.getBlocked().booleanValue());
	}
}
