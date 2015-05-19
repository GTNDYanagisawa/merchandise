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

/**
 * External representation of an instance.
 * 
 */
public interface Instance
{

	/**
	 * Sets instance ID
	 * 
	 * @param id
	 */
	void setId(String id);

	/**
	 * @return Instance ID
	 */
	String getId();

	/**
	 * Sets object type (typically 'KLAH' or 'MARA'). This attribute indicates that a node in the configuration
	 * represents a product or an abstract class node.
	 * 
	 * @param objectType
	 */
	void setObjectType(String objectType);

	/**
	 * @return Instances object type
	 */
	String getObjectType();

	/**
	 * @return Instance complete?
	 */
	boolean isComplete();

	/**
	 * Indicates that instance is complete
	 * 
	 * @param complete
	 */
	void setComplete(boolean complete);

	/**
	 * Indicates that instance does not carry conflicts
	 * 
	 * @param consistent
	 *           Instance consistent?
	 */
	void setConsistent(boolean consistent);

	/**
	 * @return Instance consistent?
	 */
	boolean isConsistent();

	/**
	 * @param quantityUnit
	 */
	void setQuantityUnit(String quantityUnit);

	/**
	 * @return Instance unit
	 */
	String getQuantityUnit();

	/**
	 * Sets quantity
	 * 
	 * @param quantity
	 */
	void setQuantity(String quantity);

	/**
	 * Sub instances might occur n times in a BOM, therefore quantity is needed.
	 * 
	 * @return instance quantity
	 */
	String getQuantity();

	/**
	 * Sets instance author. Following values are possible:<br>
	 * 
	 * 1 - action<br>
	 * 2 - selection condition <br>
	 * 3 - classification <br>
	 * 4 - constraint<br>
	 * 5 - dynamic database<br>
	 * 6 - static knowledgebase (e.g. bill of materials)<br>
	 * 7 - procedure<br>
	 * 8 - default<br>
	 * A - monitoring rule<br>
	 * B - reevaluating rule<br>
	 * X - external system<br>
	 * 
	 * 
	 * @param author
	 */
	void setAuthor(String author);

	/**
	 * @return Instance author
	 */
	String getAuthor();

	/**
	 * Sets type of the underlying class, typically 300
	 * 
	 * @param classType
	 */
	void setClassType(String classType);

	/**
	 * @return Class type
	 */
	String getClassType();

	/**
	 * Sets language dependent text
	 * 
	 * @param objectText
	 */
	void setObjectText(String objectText);

	/**
	 * @return Language dependent text
	 */
	String getObjectText();

	/**
	 * Sets objects key (product ID for material BOM items)
	 * 
	 * @param objectKey
	 */
	void setObjectKey(String objectKey);

	/**
	 * @return Object key
	 */
	String getObjectKey();

}
