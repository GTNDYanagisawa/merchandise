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


public class Tp2AnotherProductDTO
{
	private String ean = null;
	private String code = null;
	private Tp2UnitDTO unit = null;

	public Tp2AnotherProductDTO()
	{
		//
	}

	public Tp2AnotherProductDTO(final String code, final String ean, final Tp2UnitDTO unit)
	{
		this.code = code;
		this.ean = ean;
		this.unit = unit;
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
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final Tp2UnitDTO unit)
	{
		this.unit = unit;
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
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @return the unit
	 */
	public Tp2UnitDTO getUnit()
	{
		return unit;
	}

}
