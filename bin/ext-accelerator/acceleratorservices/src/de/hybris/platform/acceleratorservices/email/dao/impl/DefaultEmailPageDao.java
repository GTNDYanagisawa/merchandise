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
package de.hybris.platform.acceleratorservices.email.dao.impl;

import de.hybris.platform.acceleratorservices.email.dao.EmailPageDao;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.Map;


/**
 * Default Data Access for looking up the CMS email page for a template name.
 */
public class DefaultEmailPageDao extends AbstractItemDao implements EmailPageDao
{
	protected final String query = "SELECT {" + EmailPageModel.PK + "} " + "FROM {" + EmailPageModel._TYPECODE + " AS p "
			+ "JOIN " + EmailPageTemplateModel._TYPECODE + " AS t ON {p:" + EmailPageModel.MASTERTEMPLATE + "}={t:"
			+ EmailPageTemplateModel.PK + "}} " + "WHERE {t:" + EmailPageTemplateModel.UID + "}=?templateUid AND {t:"
			+ EmailPageTemplateModel.CATALOGVERSION + "}=?catalogVersion";

	@Override
	public EmailPageModel findEmailPageByFrontendTemplateName(final String frontendTemplateName,
			final CatalogVersionModel catalogVersion)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("templateUid", frontendTemplateName);
		params.put("catalogVersion", catalogVersion);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query, params);
		final SearchResult<EmailPageModel> result = getFlexibleSearchService().search(fsq);

		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}
}
