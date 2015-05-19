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
package de.hybris.platform.category.daos;

import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.List;


/**
 * Dao to manage types that are catalog aware.
 */
public interface CatalogTypeDao
{
	/**
	 * Find all composed types with CATALOGITEMTYPE = true
	 * 
	 * @return the list of composed type models
	 */
	List<ComposedTypeModel> findAllCatalogVersionAwareTypes();
}
