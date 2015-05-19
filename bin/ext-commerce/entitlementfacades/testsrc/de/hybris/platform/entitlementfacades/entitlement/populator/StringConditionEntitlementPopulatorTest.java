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
 * {@link de.hybris.platform.entitlementfacades.entitlement.populator.StringConditionEntitlementPopulator}
 */
public class StringConditionEntitlementPopulatorTest
{

    private static final String GRANT_PARAMETER_STRING = "string";
    private static final String GRANT_VALUE_STRING = "stringCondition";
    private static final String STRING_TYPE = "string";
    private static final String NOT_STRING_TYPE = "not_string";

    private final StringConditionEntitlementPopulator<ConditionData, EntitlementData>
            stringConditionEntitlementPopulator = new StringConditionEntitlementPopulator();

    @Test
    public void testPopulateNotString()
    {
        final ConditionData source = new ConditionData();
        source.setType(NOT_STRING_TYPE);

        final EntitlementData result = new EntitlementData();
        stringConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getConditionString());
    }


    @Test
    public void testPopulateNoProperty()
    {
        final ConditionData source = new ConditionData();
        source.setType(STRING_TYPE);

        final EntitlementData result = new EntitlementData();
        stringConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getConditionString());
    }

    @Test
    public void testPopulateCondition()
    {
        final ConditionData source = new ConditionData();
        source.setType(STRING_TYPE);
        source.setProperty(GRANT_PARAMETER_STRING, GRANT_VALUE_STRING);

        final EntitlementData result = new EntitlementData();
        stringConditionEntitlementPopulator.populate(source, result);

        Assert.assertEquals(GRANT_VALUE_STRING, result.getConditionString());
    }
}
