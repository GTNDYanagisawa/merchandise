// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location;





import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;

import java.util.List;


public interface CityService
{
	List<CityModel> getCitiesByRegionCode(final String regionIsocode);

	CityModel getCityForCode(final String cityCode);

}
