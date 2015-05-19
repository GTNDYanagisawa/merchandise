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
package de.hybris.platform.acceleratorservices.solr.keywordredirect;

import de.hybris.platform.acceleratorservices.model.redirect.SolrPageRedirectModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.keywordredirect.KeywordRedirectUrlResolver;


/**
 * KeywordRedirectUrlResolver to create redirect URLs for WCMS pages
 */
public class PageKeywordRedirectUrlResolver implements KeywordRedirectUrlResolver<SolrPageRedirectModel>
{
	@Override
	public String resolve(final SolrPageRedirectModel redirect)
	{
		final AbstractPageModel page = redirect.getRedirectItem();

		if (page instanceof ContentPageModel)
		{
			return ((ContentPageModel) page).getLabel();
		}

		return null;
	}
}
