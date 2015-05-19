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
package de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2;



public class Tp2ProductModel
{

	private String code;
	private String name;
	private String ean;
	private String description;
	private String manufacturerName;
	private Tp2UnitModel unit;


	/**
	 * @return the manufacturerName
	 */
	public String getManufacturerName()
	{
		return manufacturerName;
	}

	/**
	 * @param manufacturerName
	 *           the manufacturerName to set
	 */
	public void setManufacturerName(final String manufacturerName)
	{
		this.manufacturerName = manufacturerName;
	}


	/**
	 * @return the unit
	 */
	public Tp2UnitModel getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final Tp2UnitModel unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the ean
	 */
	public String getEan()
	{
		return ean;
	}

	/**
	 * @param ean
	 *           the ean to set
	 */
	public void setEan(final String ean)
	{
		this.ean = ean;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}
}
