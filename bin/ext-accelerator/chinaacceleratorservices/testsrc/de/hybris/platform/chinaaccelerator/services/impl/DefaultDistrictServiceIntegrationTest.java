// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinaaccelerator.services.location.DistrictService;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultDistrictServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private DistrictService districtService;
	@Resource
	private ModelService modelService;

	private DistrictModel districtModel;
	private CityModel cityModel;

	//@Before
	public void setUp()
	{
		districtModel = new DistrictModel();
		districtModel.setCode("TEST-DISTRICT-CODE");
		districtModel.setName("TEST-DISTRICT-NAME-EN", Locale.ENGLISH);
		districtModel.setName("TEST-DISTRICT-NAME-ZH", Locale.CHINESE);


		cityModel = new CityModel(); //modelService.create(CityModel.class);
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME-EN", Locale.ENGLISH); // if just new CityModel(), then [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa
		cityModel.setName("TEST-CITY-NAME-ZH", Locale.CHINESE);
	}

	//@Test(expected = UnknownIdentifierException.class)
	public void testFailBehavior()
	{
		districtService.getDistrictByCode("TEST-UNKNOWN-CODE");
	}


	@Test
	public void testDistrictService()
	{
		/*
		 * List<DistrictModel> districtModels = districtService.getDistrictsByCityCode("TEST-CITY-CODE"); final int size =
		 * districtModels.size();
		 * 
		 * districtModel.setCity(cityModel); modelService.save(cityModel); modelService.save(districtModel);
		 * 
		 * districtModels = districtService.getDistrictsByCityCode("TEST-CITY-CODE"); assertEquals(size + 1,
		 * districtModels.size()); assertEquals("Unexpected district found", districtModel,
		 * districtModels.get(districtModels.size() - 1));
		 * 
		 * 
		 * final DistrictModel persistedDistrictModel = districtService.getDistrictByCode("TEST-DISTRICT-CODE");
		 * assertNotNull("No district found", persistedDistrictModel); assertEquals("Different district found",
		 * districtModel, persistedDistrictModel);
		 */
	}
}