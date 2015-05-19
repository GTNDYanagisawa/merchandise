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
package de.hybris.platform.sap.core.common.configurer;

import java.util.List;



/**
 * general Interface for holding the Entities of a Configurer.
 * 
 * @param <T>
 *           Type of the Configurer Class
 */
public interface ConfigurerEntitiesList<T>
{
	/**
	 * Add an Entity to the List.
	 * 
	 * @param entity
	 *           entity which will be added to the List
	 */
	public void addEntity(T entity);

	/**
	 * @return the list of entities
	 */
	public List<T> getEntities();

}
