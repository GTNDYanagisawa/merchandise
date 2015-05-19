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
package de.hybris.platform.b2b.punchout.daos.impl;

import de.hybris.platform.b2b.punchout.daos.PunchOutCredentialDao;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation for {@link PunchOutCredentialDao}.
 */
public class DefaultPunchOutCredentialDao extends DefaultGenericDao<PunchOutCredentialModel> implements PunchOutCredentialDao
{
	public DefaultPunchOutCredentialDao()
	{
		super(PunchOutCredentialModel._TYPECODE);
	}

	@Override
	public PunchOutCredentialModel getPunchOutCredential(final String domain, final String identity)
			throws AmbiguousIdentifierException
	{
		final Map<String, String> params = new HashMap<>();
		params.put(PunchOutCredentialModel.DOMAIN, domain);
		params.put(PunchOutCredentialModel.IDENTITY, identity);
		final List<PunchOutCredentialModel> resList = find(params);
		if (resList.size() > 1)
		{
			throw new AmbiguousIdentifierException("Found " + resList.size() + " PunchOut Credentials with domain '" + domain
					+ "' and indentity '" + identity + "'");
		}
		return resList.isEmpty() ? null : resList.get(0);
	}
}
