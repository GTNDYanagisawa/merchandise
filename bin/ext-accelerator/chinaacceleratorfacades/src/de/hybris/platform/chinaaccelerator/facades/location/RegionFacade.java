/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.location;

import de.hybris.platform.commercefacades.user.data.RegionData;

import java.util.List;


public interface RegionFacade
{
	List<RegionData> getRegionsForCountryCode(final String countryCode);

	RegionData getRegionByCountryAndCode(final String countryCode, final String regionCode);

}
