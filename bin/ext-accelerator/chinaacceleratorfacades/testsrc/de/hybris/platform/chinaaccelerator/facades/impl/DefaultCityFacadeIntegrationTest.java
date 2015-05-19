// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.facades.location.CityFacade;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;


public class DefaultCityFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private CityFacade cityFacade;

	@Resource
	private ModelService modelService;

	private CityModel cityModel;
	private RegionModel regionModel;
	private CountryModel countryModel;

	//@Before
	public void setUp()
	{
		cityModel = modelService.create(CityModel.class); //new CityModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME"); // if just new CityModel(), then [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa

		regionModel = modelService.create(RegionModel.class);
		regionModel.setIsocode("TEST-REGION-ISOCODE");
		regionModel.setActive(Boolean.TRUE);
		//regionModel.setName("TEST-REGION-NAME", Locale.ENGLISH);
		regionModel.setName("TEST-REGION-NAME");

		countryModel = modelService.create(CountryModel.class);
		countryModel.setActive(Boolean.TRUE);
		countryModel.setIsocode("TEST-COUNTRY-ISOCODE");
		countryModel.setName("TEST-COUNTRY-NAME-EN", Locale.ENGLISH);
		countryModel.setName("TEST-COUNTRY-NAME-ZH", Locale.CHINESE);

		regionModel.setCountry(countryModel);

		cityModel.setRegion(regionModel);
	}


	//@Test(expected = UnknownIdentifierException.class)
	public void testFailBehavior()
	{
		cityFacade.getCityForCode("TEST-CITY-UNKNOWNCODE");
	}


	@Ignore
	@Test
	public void testCityFacade()
	{
		List<CityData> cityListData = cityFacade.getCitiesByRegionCode("TEST-REGION-ISOCODE");
		assertNotNull(cityListData);
		final int size = cityListData.size();

		modelService.save(regionModel);
		modelService.save(cityModel);

		cityListData = cityFacade.getCitiesByRegionCode("TEST-REGION-ISOCODE");
		assertNotNull(cityListData);
		assertEquals(size + 1, cityListData.size());
		assertEquals(cityListData.get(size).getCityName(), "TEST-CITY-NAME");
		assertEquals(cityListData.get(size).getCode(), "TEST-CITY-CODE");

		final CityData persistedCityData = cityFacade.getCityForCode("TEST-CITY-CODE");
		assertNotNull(persistedCityData);
		assertEquals(persistedCityData.getCityName(), "TEST-CITY-NAME");
		assertEquals(persistedCityData.getCode(), "TEST-CITY-CODE");
	}
}