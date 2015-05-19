// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location.impl;



import de.hybris.platform.chinaaccelerator.services.location.CityService;
import de.hybris.platform.chinaaccelerator.services.location.daos.CityDao;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCityService implements CityService
{

	private CityDao cityDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.chinaacceleratorservices.core.service.CityService#getCitiesByRegionCode(java.lang.String)
	 */
	@Override
	public List<CityModel> getCitiesByRegionCode(final String regionIsocode)
	{
		final List<CityModel> result = cityDao.findCitiesByRegionCode(regionIsocode);

		if (result.isEmpty())
		{
			return Collections.EMPTY_LIST;
		}
		return result;
		//TODO null, existing regioncode, not existing regioncode, empty list, 1, many
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.chinaacceleratorservices.core.service.CityService#getCityForCode(java.lang.String)
	 */
	@Override
	public CityModel getCityForCode(final String cityCode) throws UnknownIdentifierException
	{
		final CityModel result = cityDao.findCityForCode(cityCode);
		if (result == null)
		{
			throw new UnknownIdentifierException("City with code '" + cityCode + "' not found!");
		}
		//			else if (result.size() > 1)
		//			{
		//				throw new AmbiguousIdentifierException("City code '" + code + "' is not unique, " + result.size()
		//						+ " cities found!");
		//			}
		//			return result.get(0);
		return result;
	}

	@Required
	public void setCityDao(final CityDao cityDao)
	{
		this.cityDao = cityDao;
	}
}