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
package de.hybris.platform.sap.core.configuration.datahub;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

import java.util.HashMap;
import java.util.Map;


/**
 * HashMap which holds models to be deleted.
 */
public class RemoveModelMap extends HashMap<PK, ItemModel>
{

	/**
	 * Add a model with primary key (pk) to the HashMap.
	 * 
	 * @param pk
	 *           primary key to be set
	 * @param model
	 *           itemModel to be set
	 */
	public void addModelToBeDeleted(final PK pk, final ItemModel model)
	{
		this.put(pk, model);
	}

	/**
	 * get alls models which has to be deleted.
	 * 
	 * @return Map of models which will be deleted
	 */
	public Map<PK, ItemModel> getModelsToBeDeleted()
	{
		return this;
	}

	/**
	 * Delete a model in the HashMap.
	 * 
	 * @param pk
	 *           primary key to be set
	 */
	public void deleteModel(final PK pk)
	{
		this.remove(pk);
	}
}
