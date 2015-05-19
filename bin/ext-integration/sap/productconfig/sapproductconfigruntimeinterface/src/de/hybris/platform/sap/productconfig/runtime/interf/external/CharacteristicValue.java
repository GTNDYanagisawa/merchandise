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
 * 
 * External representation of a characteristic value.
 */
public interface CharacteristicValue
{

	/**
	 * Indicate whether value is invisible
	 * 
	 * @param invisible
	 */
	public abstract void setInvisible(boolean invisible);

	/**
	 * @return Value invisible?
	 */
	public abstract boolean isInvisible();

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
	public abstract void setAuthor(String author);

	/**
	 * @return Value author
	 */
	public abstract String getAuthor();

	/**
	 * Sets language dependent text for value
	 * 
	 * @param valueText
	 */
	public abstract void setValueText(String valueText);

	/**
	 * @return Language dependent text
	 */
	public abstract String getValueText();

	/**
	 * Sets value
	 * 
	 * @param value
	 */
	public abstract void setValue(String value);

	/**
	 * @return Value
	 */
	public abstract String getValue();

	/**
	 * Sets language dependent characteristic text
	 * 
	 * @param characteristicText
	 */
	public abstract void setCharacteristicText(String characteristicText);

	/**
	 * @return Language dependent characteristic text
	 */
	public abstract String getCharacteristicText();

	/**
	 * Sets characteristic name
	 * 
	 * @param characteristic
	 */
	public abstract void setCharacteristic(String characteristic);

	/**
	 * @return Characteristic name
	 */
	public abstract String getCharacteristic();

	/**
	 * Sets ID of instance the value belongs to
	 * 
	 * @param instId
	 */
	public abstract void setInstId(String instId);

	/**
	 * 
	 * @return ID of instance the value belongs to
	 */
	public abstract String getInstId();
}
