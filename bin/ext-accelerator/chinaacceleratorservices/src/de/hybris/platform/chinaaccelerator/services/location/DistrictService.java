/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location;





import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;

import java.util.List;


public interface DistrictService
{
	List<DistrictModel> getDistrictsByCityCode(final String cityCode);

	DistrictModel getDistrictByCode(final String districtCode);
}
