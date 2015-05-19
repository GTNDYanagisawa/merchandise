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
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class GeoConditionEntitlementPopulatorTest
{
    private static final String GRANT_PARAMETER_GEO = "geoPath";
    private static final String GRANT_VALUE_GEO = "geoPath";
    private static final String GEO_TYPE = "geo";
    private static final String NOT_GEO_TYPE = "not_geo";
    private static final String GRANT_VALUE_1;
    private static final String GRANT_VALUE_2;
    private static final ArrayList<String> ONE_CONDITION_LIST = new ArrayList<>();
    private static final ArrayList<String> TWO_CONDITIONS_LIST = new ArrayList<>();

    static
    {
        ONE_CONDITION_LIST.add(GRANT_VALUE_GEO);
        GRANT_VALUE_1 = StringUtils.arrayToCommaDelimitedString(ONE_CONDITION_LIST.toArray());

        TWO_CONDITIONS_LIST.add(GRANT_VALUE_GEO);
        TWO_CONDITIONS_LIST.add(GRANT_VALUE_GEO);
        GRANT_VALUE_2 = StringUtils.arrayToCommaDelimitedString(TWO_CONDITIONS_LIST.toArray());
    }


    private final GeoConditionEntitlementPopulator geoConditionEntitlementPopulator = new GeoConditionEntitlementPopulator();

    @Test
    public void testPopulateNotGeo()
    {
        final ConditionData source = new ConditionData();
        source.setType(NOT_GEO_TYPE);

        final EntitlementData result = new EntitlementData();
        geoConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getConditionGeo());
    }


    @Test
    public void testPopulateNoProperty()
    {
        final ConditionData source = new ConditionData();
        source.setType(GEO_TYPE);

        final EntitlementData result = new EntitlementData();
        geoConditionEntitlementPopulator.populate(source, result);

        Assert.assertNull(result.getConditionGeo());
    }

    @Test
    public void testPopulateOneConditionList()
    {
        final ConditionData source = new ConditionData();
        source.setType(GEO_TYPE);
        source.setProperty(GRANT_PARAMETER_GEO, GRANT_VALUE_1);

        final EntitlementData result = new EntitlementData();
        geoConditionEntitlementPopulator.populate(source, result);

        Assert.assertEquals(1, result.getConditionGeo().size());
        Assert.assertEquals(GRANT_PARAMETER_GEO, result.getConditionGeo().iterator().next());
    }

    @Test
    public void testPopulateManyConditionsList()
    {
        final ConditionData source = new ConditionData();
        source.setType(GEO_TYPE);
        source.setProperty(GRANT_PARAMETER_GEO, GRANT_VALUE_2);

        final EntitlementData result = new EntitlementData();
        geoConditionEntitlementPopulator.populate(source, result);

        Assert.assertEquals(2, result.getConditionGeo().size());

        for(String geoCondition: result.getConditionGeo())
        {
            Assert.assertEquals(GRANT_PARAMETER_GEO, geoCondition);
        }
    }
}
