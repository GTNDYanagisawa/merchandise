/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location;

import de.hybris.platform.core.model.c2l.RegionModel;

import java.util.List;


public interface RegionService
{
	RegionModel getRegionByCountryAndCode(final String countryCode, final String regionCode);

	List<RegionModel> getRegionsForCountryCode(final String countryCode);

}
