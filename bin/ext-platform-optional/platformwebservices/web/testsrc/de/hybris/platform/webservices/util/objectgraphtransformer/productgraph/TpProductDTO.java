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
package de.hybris.platform.webservices.util.objectgraphtransformer.productgraph;

import java.util.List;


public class TpProductDTO
{
	private String code = null;
	private String ean = null;
	private List<TpMediaDTO> thumbnails = null;
	private TpUnitDTO unit = null;


	public TpProductDTO()
	{
		//
	}

	public TpProductDTO(final String code)
	{
		this.code = code;
	}


	public TpProductDTO(final String code, final String ean)
	{
		this.code = code;
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
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
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
	 * @return the thumbnails
	 */
	public List<TpMediaDTO> getThumbnails()
	{
		return thumbnails;
	}

	/**
	 * @param thumbnails
	 *           the thumbnails to set
	 */
	public void setThumbnails(final List<TpMediaDTO> thumbnails)
	{
		this.thumbnails = thumbnails;
	}

	/**
	 * @return the unit
	 */
	public TpUnitDTO getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final TpUnitDTO unit)
	{
		this.unit = unit;
	}

}
