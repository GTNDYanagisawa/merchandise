// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.chinaaccelerator.services.location.daos.CityDao;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;


public class DefaultCityDaoIntegrationTest extends ServicelayerTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(DefaultCityDaoIntegrationTest.class);

	@Resource
	private CityDao cityDao;

	@Resource
	private ModelService modelService;

	@Test
	public void cityDaoTest()
	{
		/*
		 * List<CityModel> citiesByRegionCode = cityDao.findCitiesByRegionCode("TEST-REGION-ISOCODE");
		 * assertTrue("There should be no City found", citiesByRegionCode.isEmpty());
		 * 
		 * final int size = citiesByRegionCode.size();
		 * 
		 * final CityModel cityModel = new CityModel(); cityModel.setActive(Boolean.TRUE);
		 * cityModel.setName("CITY-NAME-EN", Locale.ENGLISH); cityModel.setName("CITY-NAME-ZH", Locale.CHINESE);
		 * cityModel.setCode("TEST-CITY-CODE");
		 * 
		 * final RegionModel regionModel = new RegionModel(); // regionModel = modelService.create(RegionModel.class);
		 * regionModel.setIsocode("TEST-REGION-ISOCODE"); regionModel.setActive(Boolean.TRUE);
		 * regionModel.setName("TEST-REGION-NAME-EN", Locale.ENGLISH); regionModel.setName("TEST-REGION-NAME-ZH",
		 * Locale.CHINESE);
		 * 
		 * final CountryModel countryModel = new CountryModel(); //modelService.create(CountryModel.class);
		 * countryModel.setActive(Boolean.TRUE); countryModel.setIsocode("CNTEST"); countryModel.setName("ChinaTest",
		 * Locale.ENGLISH);
		 * 
		 * regionModel.setCountry(countryModel);
		 * 
		 * cityModel.setRegion(regionModel);
		 * 
		 * modelService.save(cityModel);
		 * 
		 * 
		 * citiesByRegionCode = cityDao.findCitiesByRegionCode("TEST-REGION-ISOCODE"); assertEquals(size + 1,
		 * citiesByRegionCode.size()); assertEquals("Unexpected city found", cityModel,
		 * citiesByRegionCode.get(citiesByRegionCode.size() - 1));
		 * 
		 * citiesByRegionCode = cityDao.findCitiesByRegionCode("TEST-REGION-ISOCODE");
		 * assertEquals("Find the one we just saved", 1, citiesByRegionCode.size()); assertEquals("Check the code",
		 * "TEST-CITY-CODE", citiesByRegionCode.get(0).getCode()); assertEquals("Check the name EN", "CITY-NAME-EN",
		 * citiesByRegionCode.get(0).getName(Locale.ENGLISH)); assertEquals("Check the name ZH", "CITY-NAME-ZH",
		 * citiesByRegionCode.get(0).getName(Locale.CHINESE));
		 */
	}



	//@Test
	public void cityDaoCornerCases()
	{
		List<CityModel> cityModels = cityDao.findCitiesByRegionCode("");
		assertTrue("There should be no City found", cityModels.isEmpty());

		try
		{
			cityModels = cityDao.findCitiesByRegionCode(null);
			fail("Expected getStadium(null) to throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e) //NOPMD
		{
			//
			LOG.debug("Expected IllegalArgumentException. OK.");
		}

		final CityModel cityModel = modelService.create(CityModel.class); // new CityModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME-EN", Locale.ENGLISH);
		cityModel.setName("TEST-CITY-NAME-ZH", Locale.CHINESE);
		cityModel.setName("TEST-CITY-NAME-nolocale");
		modelService.save(cityModel);
	}


	//@Test
	public void cityDaoTest2()
	{

		CityModel resultCityModel = cityDao.findCityForCode("TEST-CITY-CODE");
		assertEquals("There should be no City found", null, resultCityModel);


		final CityModel cityModel = new CityModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setName("CITY-NAME-EN", Locale.ENGLISH);
		cityModel.setName("CITY-NAME-ZH", Locale.CHINESE);
		cityModel.setCode("TEST-CITY-CODE");

		final RegionModel regionModel = new RegionModel();
		//      			regionModel = modelService.create(RegionModel.class);
		regionModel.setIsocode("TEST-REGION-ISOCODE");
		regionModel.setActive(Boolean.TRUE);
		regionModel.setName("TEST-REGION-NAME-EN", Locale.ENGLISH);
		regionModel.setName("TEST-REGION-NAME-ZH", Locale.CHINESE);

		final CountryModel countryModel = new CountryModel(); //modelService.create(CountryModel.class);
		countryModel.setActive(Boolean.TRUE);
		countryModel.setIsocode("CNTEST");
		countryModel.setName("ChinaTest", Locale.ENGLISH);

		regionModel.setCountry(countryModel);

		cityModel.setRegion(regionModel);

		modelService.save(cityModel);


		resultCityModel = cityDao.findCityForCode("TEST-CITY-CODE");
		assertEquals("Check the code", "TEST-CITY-CODE", resultCityModel.getCode());
		assertEquals("Check the name EN", "CITY-NAME-EN", resultCityModel.getName(Locale.ENGLISH));
		assertEquals("Check the name ZH", "CITY-NAME-ZH", resultCityModel.getName(Locale.CHINESE));
	}
}
