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
 * {@link de.hybris.platform.entitlementfacades.entitlement.populator.TimeframeConditionEntitlementPopulator}
 */
public class TimeframeConditionEntitlementPopulatorTest
{

    private static final String GRANT_PARAMETER_START = "startTime";
    private static final String GRANT_PARAMETER_END = "endTime";
    private static final String GRANT_VALUE_TIMEFRAME = "2014-01-01T13:14:15Z";
    private static final String TIMEFRAME_TYPE = "timeframe";
    private static final String NOT_TIMEFRAME_TYPE = "not_timeframe";

    private final TimeframeConditionEntitlementPopulator<ConditionData, EntitlementData>
            stringConditionEntitlementPopulator = new TimeframeConditionEntitlementPopulator<>();

    @Test
    public void testPopulateNotString()
    {
        final ConditionData source = new ConditionData();
        source.setType(NOT_TIMEFRAME_TYPE);

        final EntitlementData result = new EntitlementData();
        stringConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getConditionString());
    }


    @Test
    public void testPopulateNoProperty()
    {
        final ConditionData source = new ConditionData();
        source.setType(TIMEFRAME_TYPE);

        final EntitlementData result = new EntitlementData();
        stringConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getStartTime());
        Assert.assertNull(result.getEndTime());
    }

    @Test
    public void testPopulateCondition()
    {
        final ConditionData source = new ConditionData();
        source.setType(TIMEFRAME_TYPE);
        source.setProperty(GRANT_PARAMETER_START, GRANT_VALUE_TIMEFRAME);
        source.setProperty(GRANT_PARAMETER_END, GRANT_VALUE_TIMEFRAME);

        final EntitlementData result = new EntitlementData();
        stringConditionEntitlementPopulator.populate(source, result);

        Assert.assertEquals(GRANT_VALUE_TIMEFRAME, result.getStartTime());
        Assert.assertEquals(GRANT_VALUE_TIMEFRAME, result.getEndTime());
    }

    @Test
    public void testPopulateOpenTimeframeCondition()
    {
        final ConditionData source = new ConditionData();
        source.setType(TIMEFRAME_TYPE);
        source.setProperty(GRANT_PARAMETER_START, GRANT_VALUE_TIMEFRAME);

        final EntitlementData result = new EntitlementData();
        stringConditionEntitlementPopulator.populate(source, result);

        Assert.assertEquals(GRANT_VALUE_TIMEFRAME, result.getStartTime());
        Assert.assertNull(result.getEndTime());
    }
}
