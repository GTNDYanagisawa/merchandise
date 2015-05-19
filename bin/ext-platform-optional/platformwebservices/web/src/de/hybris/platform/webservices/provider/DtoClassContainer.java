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
package de.hybris.platform.webservices.provider;

import java.util.Collection;


/**
 * A utility class which collects all dto classes.
 */
public class DtoClassContainer
{
	private Collection<Class> singleDtoNodes;
	private Collection<Class> collectionDtoNodes;

	/**
	 * @param singleDtoNodes
	 *           the singleDtoNodes to set
	 */
	public void setSingleDtoNodes(final Collection<Class> singleDtoNodes)
	{
		this.singleDtoNodes = singleDtoNodes;
	}

	/**
	 * @return the singleDtoNodes
	 */
	public Collection<Class> getSingleDtoNodes()
	{
		return singleDtoNodes;
	}

	/**
	 * @param collectionDtoNodes
	 *           the colectionDtoNodes to set
	 */
	public void setCollectionDtoNodes(final Collection<Class> collectionDtoNodes)
	{
		this.collectionDtoNodes = collectionDtoNodes;
	}

	/**
	 * @return the colectionDtoNodes
	 */
	public Collection<Class> getCollectionDtoNodes()
	{
		return collectionDtoNodes;
	}
}
