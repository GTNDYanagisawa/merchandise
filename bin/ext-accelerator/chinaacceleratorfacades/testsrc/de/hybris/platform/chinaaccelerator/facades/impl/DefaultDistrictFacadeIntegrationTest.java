/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.chinaaccelerator.facades.data.DistrictData;
import de.hybris.platform.chinaaccelerator.facades.location.DistrictFacade;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class DefaultDistrictFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private DistrictFacade districtFacade;

	@Resource
	private ModelService modelService;

	private DistrictModel districtModel;
	private RegionModel regionModel;
	private CountryModel countryModel;
	private CityModel cityModel;

	@Before
	public void setUp()
	{
		// This instance of a DistrictModel will be used by the tests
		//       districtModel = new DistrictModel();
		districtModel = modelService.create(DistrictModel.class); //new DistrictModel();
		districtModel.setActive(Boolean.TRUE);
		districtModel.setCode("TEST-DISTRICT-CODE");
		districtModel.setName("TEST-DISTRICT-NAME"); // if just new DistrictModel(), then here: [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa

		//		// set Region on City, as we are testing it via Regioncode
		//		regionModel = modelService.create(RegionModel.class);
		//		regionModel.setIsocode("TEST-REGION-ISOCODE");
		//		regionModel.setActive(Boolean.TRUE);
		//		//regionModel.setName("TEST-REGION-NAME", Locale.ENGLISH);
		//		regionModel.setName("TEST-REGION-NAME");

		//		countryModel = modelService.create(CountryModel.class);
		//		countryModel.setActive(Boolean.TRUE);
		//		countryModel.setIsocode("TEST-COUNTRY-ISOCODE");
		//		countryModel.setName("TEST-COUNTRY-NAME-EN", Locale.ENGLISH);
		//		countryModel.setName("TEST-COUNTRY-NAME-ZH", Locale.CHINESE);

		cityModel = modelService.create(CityModel.class); //new DistrictModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME"); // if just new DistrictModel(), then here: [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa

		//		regionModel.setCountry(countryModel);

		districtModel.setCity(cityModel);
	}


	//	@Test(expected = UnknownIdentifierException.class)
	public void testFailBehavior()
	{
		districtFacade.getDistrictByCode("TEST-DISTRICT-UNKNOWNCODE");
	}


	@Test
	public void testDistrictFacade()
	{
		List<DistrictData> districtListData = districtFacade.getDistrictsByCityCode("TEST-CITY-CODE");
		assertNotNull(districtListData);
		final int size = districtListData.size();

		modelService.save(cityModel);
		modelService.save(districtModel);

		districtListData = districtFacade.getDistrictsByCityCode("TEST-CITY-CODE");
		assertNotNull(districtListData);
		assertEquals(size + 1, districtListData.size());
		assertEquals(districtListData.get(size).getName(), "TEST-DISTRICT-NAME");
		assertEquals(districtListData.get(size).getCode(), "TEST-DISTRICT-CODE");

		final DistrictData persistedDistrictData = districtFacade.getDistrictByCode("TEST-DISTRICT-CODE");
		assertNotNull(persistedDistrictData);
		assertEquals(persistedDistrictData.getName(), "TEST-DISTRICT-NAME");
		assertEquals(persistedDistrictData.getCode(), "TEST-DISTRICT-CODE");
	}

}
