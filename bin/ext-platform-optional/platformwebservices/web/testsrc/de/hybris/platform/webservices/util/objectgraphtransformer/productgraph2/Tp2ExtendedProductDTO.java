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

public class Tp2ExtendedProductDTO extends Tp2SimpleProductDTO
{
	private String manufacturerName = null;


	public Tp2ExtendedProductDTO()
	{
		super();
	}

	public Tp2ExtendedProductDTO(final String code, final String ean)
	{
		super(code, ean);
	}

	public Tp2ExtendedProductDTO(final String code, final String ean, final String manufacturerName)
	{
		super(code, ean);
		this.manufacturerName = manufacturerName;
	}

	/**
	 * @return the manufacturerName
	 */
	public String getManufacturerName()
	{
		return manufacturerName;
	}

	/**
	 * @param manufacturerName the manufacturerName to set
	 */
	public void setManufacturerName(final String manufacturerName)
	{
		this.manufacturerName = manufacturerName;
	}



}
