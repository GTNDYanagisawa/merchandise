/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location.daos;




import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;

import java.util.List;


public interface CityDao
{

	List<CityModel> findCitiesByRegionCode(final String regionCode);

	CityModel findCityForCode(final String cityCode);
}
