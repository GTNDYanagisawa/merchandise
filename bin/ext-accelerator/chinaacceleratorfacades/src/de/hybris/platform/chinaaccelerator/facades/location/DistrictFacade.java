/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.location;



import de.hybris.platform.chinaaccelerator.facades.data.DistrictData;

import java.util.List;



public interface DistrictFacade
{
	List<DistrictData> getDistrictsByCityCode(final String cityCode);

	DistrictData getDistrictByCode(final String districtCode);

}
