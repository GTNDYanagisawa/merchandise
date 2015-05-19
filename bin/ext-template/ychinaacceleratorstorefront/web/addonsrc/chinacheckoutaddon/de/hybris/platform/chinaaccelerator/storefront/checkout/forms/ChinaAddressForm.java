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
package de.hybris.platform.chinaaccelerator.storefront.checkout.forms;


public class ChinaAddressForm extends de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm
{
	private String phone;
	private String landlinePhonePart1;
	private String landlinePhonePart2;
	private String landlinePhonePart3;
	private String cellPhone;
	private String cityCode;
	private String cityDistrictCode;

	public String getCityCode()
	{
		return cityCode;
	}

	public void setCityCode(final String cityCode)
	{
		this.cityCode = cityCode;
	}

	public String getCityDistrictCode()
	{
		return cityDistrictCode;
	}

	public void setCityDistrictCode(final String cityDistrictCode)
	{
		this.cityDistrictCode = cityDistrictCode;
	}

	/**
	 * @return the landlinePhonePart3
	 */
	public String getLandlinePhonePart3()
	{
		return landlinePhonePart3;
	}

	/**
	 * @param landlinePhonePart3
	 *           the landlinePhonePart3 to set
	 */
	public void setLandlinePhonePart3(final String landlinePhonePart3)
	{
		this.landlinePhonePart3 = landlinePhonePart3;
	}

	/**
	 * @return the landlinePhonePart2
	 */
	public String getLandlinePhonePart2()
	{
		return landlinePhonePart2;
	}

	/**
	 * @param landlinePhonePart2
	 *           the landlinePhonePart2 to set
	 */
	public void setLandlinePhonePart2(final String landlinePhonePart2)
	{
		this.landlinePhonePart2 = landlinePhonePart2;
	}

	/**
	 * @return the landlinePhonePart1
	 */
	public String getLandlinePhonePart1()
	{
		return landlinePhonePart1;
	}

	/**
	 * @param landlinePhonePart1
	 *           the landlinePhonePart1 to set
	 */
	public void setLandlinePhonePart1(final String landlinePhonePart1)
	{
		this.landlinePhonePart1 = landlinePhonePart1;
	}

	/**
	 * @return the phone
	 */
	public String getPhone()
	{
		return phone;
	}

	/**
	 * @param phone
	 *           the phone to set
	 */
	public void setPhone(final String phone)
	{
		this.phone = phone;
	}

	/**
	 * @return the cellPhone
	 */
	public String getCellPhone()
	{
		return cellPhone;
	}

	/**
	 * @param cellPhone
	 *           the cellPhone to set
	 */
	public void setCellPhone(final String cellPhone)
	{
		this.cellPhone = cellPhone;
	}
}
