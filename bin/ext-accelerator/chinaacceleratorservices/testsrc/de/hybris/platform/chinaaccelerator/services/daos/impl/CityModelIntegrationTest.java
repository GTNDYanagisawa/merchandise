// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Resource;

import org.junit.Test;


//import de.hybris.platform.core.model.user.CityModel;


public class CityModelIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@SuppressWarnings("boxing")
	@Test
	public void saveCityTest()
	{

		SearchResult<CityModel> result = flexibleSearchService.search("select {pk} from {City} ");
		final int size = result.getCount();


		final CityModel city1 = this.modelService.create(CityModel.class); // new CityModel();
		city1.setName("c1");
		city1.setSortOrder(20);
		city1.setCode("TEST-CITY-CODE-1");
		modelService.save(city1);

		final CityModel city2 = this.modelService.create(CityModel.class); //new CityModel();
		city2.setName("c2");
		city2.setSortOrder(10);
		city2.setCode("TEST-CITY-CODE-2");
		modelService.save(city2);

		final CityModel city3 = this.modelService.create(CityModel.class); //new CityModel();
		city3.setName("c3");
		city3.setSortOrder(30);
		city3.setCode("TEST-CITY-CODE-3");
		modelService.save(city3);

		result = flexibleSearchService.search("select {pk} from {City} ");
		assertNotNull(result);
		//assertEquals(result.getCount(), 3);
		assertEquals(result.getCount(), size + 3);
		assertTrue(result.getResult().contains(city1));
		assertTrue(result.getResult().contains(city2));
		assertTrue(result.getResult().contains(city3));
	}


}
