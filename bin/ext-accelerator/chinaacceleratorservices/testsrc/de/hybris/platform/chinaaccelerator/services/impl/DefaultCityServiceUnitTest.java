// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.impl;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinaaccelerator.services.location.daos.CityDao;
import de.hybris.platform.chinaaccelerator.services.location.impl.DefaultCityService;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.core.model.c2l.RegionModel;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultCityServiceUnitTest
{
	private DefaultCityService cityService;
	private CityDao cityDao;

	private CityModel cityModel;
	private RegionModel regionModel;


	@Before
	public void setUp()
	{
		cityService = new DefaultCityService();
		cityDao = mock(CityDao.class);
		cityService.setCityDao(cityDao);


		cityModel = new CityModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME-EN", Locale.ENGLISH);
		cityModel.setName("TEST-CITY-NAME-ZH", Locale.CHINESE); // if using the method without Locale - cityModel.setName("TEST-CITY-NAME") - an implicit LocaleProvider is needed, but then modelService.create(..) needs to be used


		//regionModel = modelService.create(RegionModel.class);
		// 		regionModel = new RegionModel(); // modelService.create(RegionModel.class);
		// 		regionModel.setIsocode("TEST-REGION-ISOCODE");
		// 		regionModel.setActive(Boolean.TRUE);
		// 		regionModel.setName("TEST-REGION-NAME-EN", Locale.ENGLISH);
		// 		regionModel.setName("TEST-REGION-NAME-ZH", Locale.CHINESE);
		//// 		regionModel.setName("TEST-REGION-NAME");
		//
		// 		cityModel.setRegion(regionModel);
	}

	@Test
	public void testGetCitiesByRegionCode()
	{
		final List<CityModel> cityModels = Arrays.asList(cityModel);
		when(cityDao.findCitiesByRegionCode("TEST-REGION-ISOCODE")).thenReturn(cityModels);

		final List<CityModel> result = cityService.getCitiesByRegionCode("TEST-REGION-ISOCODE");
		assertEquals("We should find one", 1, result.size());
		assertEquals("And should equals what the mock returned", cityModel, result.get(0));
	}

	@Test
	public void testGetCityForCode()
	{
		when(cityDao.findCityForCode("TEST-CITY-CODE")).thenReturn(cityModel);

		final CityModel result = cityService.getCityForCode("TEST-CITY-CODE");

		assertEquals("And should equals what the mock returned", cityModel, result);
	}
}