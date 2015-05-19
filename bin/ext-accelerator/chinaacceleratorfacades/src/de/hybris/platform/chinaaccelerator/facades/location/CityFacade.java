/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.location;



import de.hybris.platform.chinaaccelerator.facades.data.CityData;

import java.util.List;


public interface CityFacade
{

	List<CityData> getCitiesByRegionCode(final String regionIsocode);

	CityData getCityForCode(final String cityCode);
}
