/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.entitlementfacades.entitlement.populator;

import com.hybris.services.entitlements.condition.ConditionData;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import junit.framework.Assert;
import org.junit.Test;

/*
 * UnitTest for Converter implementation for
 * {@link de.hybris.platform.entitlementfacades.entitlement.populator.PathConditionEntitlementPopulator}
 */
public class PathConditionEntitlementPopulatorTest
{
    private static final String GRANT_PARAMETER_PATH = "path";
    private static final String GRANT_VALUE_PATH = "pathCondition";
    private static final String PATH_TYPE = "path";
    private static final String NOT_PATH_TYPE = "not_path";

    private final PathConditionEntitlementPopulator<ConditionData, EntitlementData> pathConditionEntitlementPopulator =
            new PathConditionEntitlementPopulator();

    @Test
    public void testPopulateNotString()
    {
        final ConditionData source = new ConditionData();
        source.setType(NOT_PATH_TYPE);

        final EntitlementData result = new EntitlementData();
        pathConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getConditionPath());
    }


    @Test
    public void testPopulateNoProperty()
    {
        final ConditionData source = new ConditionData();
        source.setType(PATH_TYPE);

        final EntitlementData result = new EntitlementData();
        pathConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getConditionPath());
    }

    @Test
    public void testPopulateCondition()
    {
        final ConditionData source = new ConditionData();
        source.setType(PATH_TYPE);
        source.setProperty(GRANT_PARAMETER_PATH, GRANT_VALUE_PATH);

        final EntitlementData result = new EntitlementData();
        pathConditionEntitlementPopulator.populate(source, result);

        Assert.assertEquals(GRANT_VALUE_PATH, result.getConditionPath());
    }
}
