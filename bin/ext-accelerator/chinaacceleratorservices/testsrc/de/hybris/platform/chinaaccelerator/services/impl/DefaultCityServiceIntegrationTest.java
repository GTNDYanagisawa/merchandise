// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinaaccelerator.services.location.CityService;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultCityServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private CityService cityService;
	@Resource
	private ModelService modelService;

	private CityModel cityModel;
	private RegionModel regionModel;


	//@Before
	public void setUp()
	{
		cityModel = modelService.create(CityModel.class); //new CityModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME"); // if just new CityModel(), then here: [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa


		regionModel = modelService.create(RegionModel.class);
		regionModel.setIsocode("TEST-REGION-ISOCODE");
		regionModel.setActive(Boolean.TRUE);
		//regionModel.setName("TEST-REGION-NAME", Locale.ENGLISH);
		regionModel.setName("TEST-REGION-NAME");


		final CountryModel countryModel = modelService.create(CountryModel.class);
		countryModel.setActive(Boolean.TRUE);
		countryModel.setIsocode("TEST-COUNTRY-ISOCODE");
		countryModel.setName("TEST-COUNTRY-NAME-EN", Locale.ENGLISH);
		countryModel.setName("TEST-COUNTRY-NAME-ZH", Locale.CHINESE);

		regionModel.setCountry(countryModel);

		cityModel.setRegion(regionModel);
	}


	@Test
	public void testCityService()
	{
		/*
		 * List<CityModel> cityModels = cityService.getCitiesByRegionCode("TEST-REGION-ISOCODE"); final int size =
		 * cityModels.size();
		 * 
		 * modelService.save(regionModel); modelService.save(cityModel);
		 * 
		 * 
		 * cityModels = cityService.getCitiesByRegionCode("TEST-REGION-ISOCODE"); assertEquals(size + 1,
		 * cityModels.size()); assertEquals("Unexpected city found", cityModel, cityModels.get(cityModels.size() - 1));
		 * 
		 * final CityModel persistedCityModel = cityService.getCityForCode("TEST-CITY-CODE");
		 * assertNotNull("No city found", persistedCityModel); assertEquals("Different city found", cityModel,
		 * persistedCityModel);
		 */
	}
}