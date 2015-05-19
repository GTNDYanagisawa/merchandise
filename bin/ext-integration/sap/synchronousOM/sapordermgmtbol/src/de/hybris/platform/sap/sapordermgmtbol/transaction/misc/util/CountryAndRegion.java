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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.util;

/**
 * Immutable tuple of country and Region<br>
 * 
 */
public class CountryAndRegion
{
	private final String country;
	private final String region;
	private final String taxJurisdictionCode;

	/**
	 * Standard constructor. <br>
	 * 
	 * @param country
	 *           country
	 * @param region
	 *           region
	 * @param taxJurisdictionCode
	 */
	public CountryAndRegion(final String country, final String region, final String taxJurisdictionCode)
	{
		super();
		this.country = country;
		this.region = region;
		this.taxJurisdictionCode = taxJurisdictionCode;
	}

	/**
	 * @return country
	 */
	public String getCountry()
	{
		return country;
	}

	/**
	 * @return region
	 */
	public String getRegion()
	{
		return region;
	}

	/**
	 * @return TaxJurisdictionCode
	 */
	public String getTaxJurisdictionCode()
	{
		return taxJurisdictionCode;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((taxJurisdictionCode == null) ? 0 : taxJurisdictionCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CountryAndRegion other = (CountryAndRegion) obj;
		if (country == null)
		{
			if (other.country != null)
			{
				return false;
			}
		}
		else if (!country.equals(other.country))
		{
			return false;
		}
		if (region == null)
		{
			if (other.region != null)
			{
				return false;
			}
		}
		else if (!region.equals(other.region))
		{
			return false;
		}
		if (taxJurisdictionCode == null)
		{
			if (other.taxJurisdictionCode != null)
			{
				return false;
			}
		}
		else if (!taxJurisdictionCode.equals(other.taxJurisdictionCode))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "country=" + country + "; region=" + region + "; taxJurisdictionCode=" + taxJurisdictionCode;
	}

}
