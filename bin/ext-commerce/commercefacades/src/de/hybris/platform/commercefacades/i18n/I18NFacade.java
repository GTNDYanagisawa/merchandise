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
package de.hybris.platform.commercefacades.i18n;

import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * The Internationalization Facade supports retrieving Country and Region (States/Counties/Prefectures/etc) information.
 * 
 * ISO Codes MUST adhere to ISO 3166-1 alpha-2 for Countries and ISO 3166-2 for Regions.
 */
public interface I18NFacade
{
	/**
	 * Returns the country with the matching isocode parameter.
	 * 
	 * @param isocode
	 *           the isocode of the country we want to return.
	 * @return the country whose isocode matches the input parameter.
	 */
	CountryData getCountryForIsocode(String isocode);

	/**
	 * Get the region data for a county and region ISO code
	 * 
	 * @param countryIso
	 *           the country iso code
	 * @param regionIso
	 *           the region iso code
	 * @return the region data
	 * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *            in case no country or region can be found
	 */
	RegionData getRegion(String countryIso, String regionIso);

	/**
	 * Get the list of regions for a country. The list is sorted by region name.
	 * 
	 * @param countryIso
	 *           the country iso code
	 * @return the list of region datas
	 */
	List<RegionData> getRegionsForCountryIso(String countryIso);

	/**
	 * Get map of country iso code to list of sorted region datas for all known countries.
	 * 
	 * @return map of country iso code to list of sorted region datas
	 */
	Map<String, List<RegionData>> getRegionsForAllCountries();

	/**
	 * Get map of country iso code to list of sorted region datas for specified countries.
	 * 
	 * @param countryIsoCodes
	 *           list of country codes
	 * @return map of country iso code to list of sorted region datas
	 */
	Map<String, List<RegionData>> getRegionsForCountries(Collection<String> countryIsoCodes);
}
