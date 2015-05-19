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
package de.hybris.platform.sap.productconfig.runtime.interf.external;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;

import java.util.List;



/**
 * External representation of a configuration
 */
public interface Configuration
{
	/**
	 * @return Root instance of the configuration
	 */
	Instance getRootInstance();

	/**
	 * Sets root instance of the configuration
	 * 
	 * @param rootInstance
	 */
	void setRootInstance(Instance rootInstance);

	/**
	 * @return List of instances belonging to this configuration
	 */
	List<Instance> getInstances();

	/**
	 * @return List of partOf relations between instances
	 */
	List<PartOfRelation> getPartOfRelations();

	/**
	 * @return List of assigned characteristic values
	 */
	List<CharacteristicValue> getCharacteristicValues();

	/**
	 * @return List of context attributes
	 */
	List<ContextAttribute> getContextAttributes();

	/**
	 * Adds an instance to the instance list
	 * 
	 * @param instance
	 *           Instance to be added
	 */
	void addInstance(Instance instance);

	/**
	 * Adds a partOf relation
	 * 
	 * @param partOfRelation
	 */
	void addPartOfRelation(PartOfRelation partOfRelation);

	/**
	 * Adds a new characteristic value to the configuration
	 * 
	 * @param characteristicValue
	 */
	void addCharacteristicValue(CharacteristicValue characteristicValue);

	/**
	 * Adds a new context attribute to the configuration
	 * 
	 * @param contextAttribute
	 */
	void addContextAttribute(ContextAttribute contextAttribute);

	/**
	 * @return knowledge base key data
	 */
	public KBKey getKbKey();

	/**
	 * Sets the knowledge base key data
	 * 
	 * @param kbKey
	 */
	public void setKbKey(KBKey kbKey);

}
