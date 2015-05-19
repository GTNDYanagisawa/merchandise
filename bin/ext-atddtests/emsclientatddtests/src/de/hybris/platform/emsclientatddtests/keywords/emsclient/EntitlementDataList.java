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
package de.hybris.platform.emsclientatddtests.keywords.emsclient;

import de.hybris.platform.entitlementfacades.data.EntitlementData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class represent arguments for
 * {@link EntitlementsBuilderKeywordLibrary#verifyGrantsXmlForUser(String, String)}
 * method.
 */
@javax.xml.bind.annotation.XmlRootElement(name = "entitlementsList")
public class EntitlementDataList {

    @javax.xml.bind.annotation.XmlElementWrapper(name = "entitlements")
    @javax.xml.bind.annotation.XmlElement(name = "entitlement")
    private final List<EntitlementData> entitlements = new ArrayList<>();

    public void setEntitlements(final Collection<EntitlementData> entitlements)
    {
        this.entitlements.clear();
        if (entitlements != null)
        {
            this.entitlements.addAll(entitlements);
        }
    }

    public List<EntitlementData> getEntitlements()
    {
        return entitlements;
    }
}
