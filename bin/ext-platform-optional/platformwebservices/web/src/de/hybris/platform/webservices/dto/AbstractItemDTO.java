/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.dto;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

import de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties;
import de.hybris.platform.webservices.objectgraphtransformer.UriPropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;


/**
 * needs update
 */
public class AbstractItemDTO implements ModifiedProperties, UriDTO
{
	protected final Set<String> modifiedPropsSet = new HashSet<String>();

	private String uri = null;


	@Override
	public Set<String> getModifiedProperties()
	{
		return this.modifiedPropsSet;
	}

	/**
	 * @param uri
	 *           the uri to set
	 */
	@Override
	@GraphProperty(virtual = true, interceptor = UriPropertyInterceptor.class)
	public void setUri(final String uri)
	{
		this.uri = uri;
	}

	@Override
	@XmlAttribute
	public String getUri()
	{
		return this.uri;
	}

}
