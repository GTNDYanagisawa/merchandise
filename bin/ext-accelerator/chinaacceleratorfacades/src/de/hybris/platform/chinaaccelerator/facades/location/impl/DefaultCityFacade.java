// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.location.impl;



import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.facades.location.CityFacade;
import de.hybris.platform.chinaaccelerator.facades.populators.CityPopulator;
import de.hybris.platform.chinaaccelerator.services.location.CityService;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCityFacade implements CityFacade
{
	private CityService cityService;
	private CityPopulator cityPopulator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.ychinaaccelerator.facades.CityFacade#getCitiesByRegionCode(java.lang.String)
	 */
	@Override
	public List<CityData> getCitiesByRegionCode(final String regionIsocode)
	{

		final List<CityModel> cityModels = cityService.getCitiesByRegionCode(regionIsocode);
		final List<CityData> cityFacadeData = new ArrayList<CityData>();
		for (final CityModel cityModel : cityModels)
		{
			final CityData cityDto = new CityData();
			//			cityDto.setCode(cityModel.getCode());
			//			cityDto.setName(cityModel.getName());
			this.cityPopulator.populate(cityModel, cityDto);

			cityFacadeData.add(cityDto);
		}
		return cityFacadeData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.ychinaaccelerator.facades.CityFacade#getCityForCode(java.lang.String)
	 */
	@Override
	public CityData getCityForCode(final String cityCode)
	{
		final CityModel cityModel = cityService.getCityForCode(cityCode);

		// return null or empty CityData object?
		if (cityModel == null)
		{
			return new CityData();
		}

		final CityData cityDto = new CityData();
		//		cityDto.setCode(cityModel.getCode());
		//		cityDto.setName(cityModel.getName());
		this.cityPopulator.populate(cityModel, cityDto);

		return cityDto;
	}

	@Required
	public void setCityService(final CityService cityService)
	{
		this.cityService = cityService;
	}

	@Required
	public void setCityPopulator(final CityPopulator cityPopulator)
	{
		this.cityPopulator = cityPopulator;
	}
}