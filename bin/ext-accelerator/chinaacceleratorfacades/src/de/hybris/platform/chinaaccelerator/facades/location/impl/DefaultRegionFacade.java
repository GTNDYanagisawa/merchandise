// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.location.impl;

import de.hybris.platform.chinaaccelerator.facades.location.RegionFacade;
import de.hybris.platform.chinaaccelerator.services.location.RegionService;
import de.hybris.platform.commercefacades.user.converters.populator.RegionPopulator;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.RegionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultRegionFacade implements RegionFacade
{

	private RegionService regionService;
	private RegionPopulator regionPopulator;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.ychinaaccelerator.facades.RegionFacade#getRegionsForCountryCode()
	 */
	@Override
	public List<RegionData> getRegionsForCountryCode(final String countryCode)
	{

		final List<RegionModel> regionModels = regionService.getRegionsForCountryCode(countryCode);

		if (regionModels.isEmpty())
		{
			return Collections.EMPTY_LIST;
		}
		else
		{
			final List<RegionData> regionFacadeData = new ArrayList<RegionData>();
			for (final RegionModel regionmodel : regionModels)
			{
				final RegionData regionData = new RegionData();
				this.regionPopulator.populate(regionmodel, regionData);

				regionFacadeData.add(regionData);
			}
			return regionFacadeData;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.ychinaaccelerator.facades.RegionFacade#getRegionByCountryAndCode(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public RegionData getRegionByCountryAndCode(final String countryCode, final String regionCode)
	{
		final RegionModel regionModel = regionService.getRegionByCountryAndCode(countryCode, regionCode);
		if (regionModel == null)
		{
			return null;
		}

		final RegionData regionData = new RegionData();
		this.regionPopulator.populate(regionModel, regionData);
		return regionData;

	}

	@Required
	public void setRegionService(final RegionService regionService)
	{
		this.regionService = regionService;
	}

	@Required
	public void setRegionPopulator(final RegionPopulator regionPopulator)
	{
		this.regionPopulator = regionPopulator;
	}

}
