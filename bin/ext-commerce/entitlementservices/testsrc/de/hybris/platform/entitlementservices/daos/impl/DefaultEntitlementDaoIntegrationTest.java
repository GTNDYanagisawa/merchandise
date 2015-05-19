package de.hybris.platform.entitlementservices.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.entitlementservices.daos.EntitlementDao;
import de.hybris.platform.entitlementservices.model.EntitlementModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * Integration test suite for {@link DefaultEntitlementDao}.
 */
@IntegrationTest
public class DefaultEntitlementDaoIntegrationTest extends ServicelayerTest {

    private static final Logger LOG = Logger.getLogger(DefaultEntitlementDaoIntegrationTest.class);

    private static final String ENTITLEMENT_MODEL_ID = "ME1";
    private static final String ENTITLEMENT_MODEL_ID_WRONG = "ME_WRONG";
    private static final String ENTITLEMENT_MODEL_NAME = "Metered Entitlement 1";
    private static final String USAGE_UNIT_ID = "minute";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Resource
    private EntitlementDao entitlementDao;

    @Before
    public void setUp() throws Exception
    {
        // Create data for tests
        LOG.info("Creating data for DefaultEntitlementDaoIntegrationTest ..");

        final long startTime = System.currentTimeMillis();

        // importing test impex
        importCsv("/entitlementservices/test/testEntitlements.impex", "utf-8");

        LOG.info("Finished data for DefaultEntitlementDaoIntegrationTest "
                + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Test
    public void testFindEntitlementById() throws CommerceCartModificationException, InvalidCartException
    {
        final EntitlementModel entitlement = entitlementDao.findEntitlementByCode(ENTITLEMENT_MODEL_ID);

        assertEquals("", ENTITLEMENT_MODEL_ID, entitlement.getId());
        assertEquals("", ENTITLEMENT_MODEL_NAME, entitlement.getName());
        assertEquals("", USAGE_UNIT_ID, entitlement.getUsageUnit().getId());
    }

    @Test
    public void testFindEntitlementByIdWrongId() throws CommerceCartModificationException, InvalidCartException
    {
        thrown.expect(ModelNotFoundException.class);
        entitlementDao.findEntitlementByCode(ENTITLEMENT_MODEL_ID_WRONG);
    }

}
