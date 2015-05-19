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
 *
 *
 */
package com.sap.wec.adtreco.be.intf;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;

import java.net.URISyntaxException;


/**
 *
 */
public interface ADTInitiativesBE extends BackendBusinessObject
{
	public String getPath();

	public String getUser();

	public String getPassword();

	public ODataEntry getInitiative(String select, String keyValue, String entitySetName);

	public ODataFeed getInitiatives(String select, String filter, String entitySetName, String expand) throws ODataException,
			URISyntaxException;

	public void loadDestinations();
}
