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
package de.hybris.platform.secureportaladdon.jalo.restrictions;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.util.localization.Localization;

import org.apache.log4j.Logger;


public class CMSSecurePortalRestriction extends GeneratedCMSSecurePortalRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(CMSSecurePortalRestriction.class.getName());

	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes)
			throws JaloBusinessException
	{
		return super.createItem(ctx, type, allAttributes);
	}

	/**
	 * @deprecated use
	 *             {@link de.hybris.platform.secureportaladdon.model.restrictions.CMSSecurePortalRestrictionModel#getDescription()}
	 *             instead.
	 */
	@Deprecated
	@Override
	public String getDescription(SessionContext sessionContext)
	{
		return Localization.getLocalizedString("type.CMSSecurePortalRestriction.description.text");
	}
}
