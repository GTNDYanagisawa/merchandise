/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.location.impl;



import de.hybris.platform.chinaaccelerator.facades.data.DistrictData;
import de.hybris.platform.chinaaccelerator.facades.location.DistrictFacade;
import de.hybris.platform.chinaaccelerator.facades.populators.DistrictPopulator;
import de.hybris.platform.chinaaccelerator.services.location.DistrictService;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultDistrictFacade implements DistrictFacade
{
	private DistrictService districtService;

	private DistrictPopulator districtPopulator;





	@Override
	public List<DistrictData> getDistrictsByCityCode(final String cityCode)
	{

		final List<DistrictModel> districtModels = districtService.getDistrictsByCityCode(cityCode);
		final List<DistrictData> districtFacadeData = new ArrayList<DistrictData>();
		for (final DistrictModel districtModel : districtModels)
		{
			final DistrictData districtDto = new DistrictData();
			this.districtPopulator.populate(districtModel, districtDto);

			districtFacadeData.add(districtDto);
		}
		return districtFacadeData;
	}

	@Override
	public DistrictData getDistrictByCode(final String districtCode)
	{
		final DistrictModel districtModel = districtService.getDistrictByCode(districtCode);

		// return null or empty DistrictData object?
		if (districtModel == null)
		{
			return new DistrictData();
		}

		final DistrictData districtDto = new DistrictData();
		this.districtPopulator.populate(districtModel, districtDto);

		return districtDto;
	}

	@Required
	public void setDistrictService(final DistrictService districtService)
	{
		this.districtService = districtService;
	}

	@Required
	public void setDistrictPopulator(final DistrictPopulator districtPopulator)
	{
		this.districtPopulator = districtPopulator;
	}

}
