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
package de.hybris.platform.acceleratorservices.solr.attribute;

import de.hybris.platform.acceleratorservices.model.redirect.SolrPageRedirectModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;


/**
 * Default label provider for attribute 'hmcLabel'
 * 
 */
public class DefaultPageRedirectHmcLabelProvider implements DynamicAttributeHandler<String, SolrPageRedirectModel>
{
	@Override
	public String get(final SolrPageRedirectModel model)
	{
		return model.getRedirectItem().getUid();
	}

	@Override
	public void set(final SolrPageRedirectModel model, final String value)
	{
		throw new UnsupportedOperationException("The attribute is readonly");
	}

}
