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
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;


/**
 * Default implementation of {@link CharacteristicValue}. Just a bean holding the respective attributes.
 */
public class CharacteristicValueImpl implements CharacteristicValue
{
	private String instId;
	private String characteristic;
	private String characteristicText;
	private String value;
	private String valueText;
	private String author;
	private boolean invisible;

	@Override
	public String getInstId()
	{
		return instId;
	}

	@Override
	public void setInstId(final String instId)
	{
		this.instId = instId;
	}

	@Override
	public String getCharacteristic()
	{
		return characteristic;
	}

	@Override
	public void setCharacteristic(final String characteristic)
	{
		this.characteristic = characteristic;
	}

	@Override
	public String getCharacteristicText()
	{
		return characteristicText;
	}

	@Override
	public void setCharacteristicText(final String characteristicText)
	{
		this.characteristicText = characteristicText;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public void setValue(final String value)
	{
		this.value = value;
	}

	@Override
	public String getValueText()
	{
		return valueText;
	}

	@Override
	public void setValueText(final String valueText)
	{
		this.valueText = valueText;
	}

	@Override
	public String getAuthor()
	{
		return author;
	}

	@Override
	public void setAuthor(final String author)
	{
		this.author = author;
	}

	@Override
	public boolean isInvisible()
	{
		return invisible;
	}

	@Override
	public void setInvisible(final boolean invisible)
	{
		this.invisible = invisible;
	}
}
