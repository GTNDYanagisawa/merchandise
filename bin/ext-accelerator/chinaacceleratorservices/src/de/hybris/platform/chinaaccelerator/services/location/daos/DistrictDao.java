// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location.daos;





import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;

import java.util.List;


public interface DistrictDao
{
	DistrictModel findDistrictByCode(final String districtCode);

	List<DistrictModel> findDistrictsByCityCode(final String cityCode);
}
