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
 */
package de.hybris.platform.commercewebservicescommons.mapping;



/**
 * Interface for field set helper
 * 
 */
public interface FieldSetLevelHelper
{
	/**
	 * Field set levels
	 */
	static final String BASIC_LEVEL = "BASIC";
	static final String FULL_LEVEL = "FULL";
	static final String DEFAULT_LEVEL = "DEFAULT";

	/**
	 * Method check if level exists for objectClass
	 * 
	 * @param levelName
	 *           - level name
	 * @param objectClass
	 *           - object class
	 * @return true - if level exists<br/>
	 *         false - if level is not defined for class
	 */
	boolean isLevelName(final String levelName, final Class objectClass);

	/**
	 * Method create BASIC level definition for class based on fields returned from objectClass.getDeclaredFields()
	 * 
	 * @param objectClass
	 *           - object class
	 * @return created basic level definition
	 */
	String createBasicLevelDefinition(final Class objectClass);

	/**
	 * Method create DEFAULT level definition for class
	 * 
	 * @param objectClass
	 *           - object class
	 * @return created default level definition
	 */
	String createDefaultLevelDefinition(final Class objectClass);

	/**
	 * Method create FULL level definition for class based on fields returned from objectClass.getDeclaredFields()
	 * Methods getDeclaredFields is called for objectClass and all its parents
	 * 
	 * @param objectClass
	 *           - object class
	 * @return created basic level definition
	 */
	String createFullLevelDefinition(Class objectClass);

	/**
	 * Method return level definition for class
	 * 
	 * @param objectClass
	 *           - object class
	 * @param levelName
	 *           - level name
	 * @return level description or null if there is no level definition for class
	 */
	String getLevelDefinitionForClass(final Class objectClass, final String levelName);

}
