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
package de.hybris.platform.acceleratorservices.dataexport.googlelocal.model;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;


@DelimitedFile(delimiter = "\t")
public class Business
{
	private String storeCode;
	private String name;
	private String mainPhone;
	private String addressLine1;
	private String city;
	private String state;
	private String postalCode;
	private String countryCode;
	private String homePage;
	private String category; //Custom or Google-recommended categories. May submit as text or category number

	//below are optional fields
	private String addressLine2;
	private String hours;
	private String description;
	private String currency; //ISO 3-letter currency code
	private String establishedDate;
	private String latitude;
	private String longitude;

	@DelimitedFileMethod(position = 1)
	public String getStoreCode()
	{
		return storeCode;
	}

	public void setStoreCode(final String storeCode)
	{
		this.storeCode = storeCode;
	}

	@DelimitedFileMethod(position = 2)
	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	@DelimitedFileMethod(position = 3)
	public String getMainPhone()
	{
		return mainPhone;
	}

	public void setMainPhone(final String mainPhone)
	{
		this.mainPhone = mainPhone;
	}

	@DelimitedFileMethod(position = 4)
	public String getAddressLine1()
	{
		return addressLine1;
	}

	public void setAddressLine1(final String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	@DelimitedFileMethod(position = 5)
	public String getCity()
	{
		return city;
	}

	public void setCity(final String city)
	{
		this.city = city;
	}

	@DelimitedFileMethod(position = 6)
	public String getState()
	{
		return state;
	}

	public void setState(final String state)
	{
		this.state = state;
	}

	@DelimitedFileMethod(position = 7)
	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(final String postalCode)
	{
		this.postalCode = postalCode;
	}

	@DelimitedFileMethod(position = 8)
	public String getCountryCode()
	{
		return countryCode;
	}

	public void setCountryCode(final String countryCode)
	{
		this.countryCode = countryCode;
	}

	@DelimitedFileMethod(position = 9)
	public String getHomePage()
	{
		return homePage;
	}

	public void setHomePage(final String homePage)
	{
		this.homePage = homePage;
	}

	@DelimitedFileMethod(position = 10)
	public String getCategory()
	{
		return category;
	}

	public void setCategory(final String category)
	{
		this.category = category;
	}

	@DelimitedFileMethod(position = 11)
	public String getAddressLine2()
	{
		return addressLine2;
	}

	public void setAddressLine2(final String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	@DelimitedFileMethod(position = 12)
	public String getHours()
	{
		return hours;
	}

	public void setHours(final String hours)
	{
		this.hours = hours;
	}

	@DelimitedFileMethod(position = 13)
	public String getDescription()
	{
		return description;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	@DelimitedFileMethod(position = 14)
	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	@DelimitedFileMethod(position = 15)
	public String getEstablishedDate()
	{
		return establishedDate;
	}

	public void setEstablishedDate(final String establishedDate)
	{
		this.establishedDate = establishedDate;
	}

	@DelimitedFileMethod(position = 16)
	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(final String latitude)
	{
		this.latitude = latitude;
	}

	@DelimitedFileMethod(position = 17)
	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(final String longitude)
	{
		this.longitude = longitude;
	}
}
