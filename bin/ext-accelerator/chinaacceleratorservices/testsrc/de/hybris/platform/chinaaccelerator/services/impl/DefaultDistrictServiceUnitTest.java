package de.hybris.platform.chinaaccelerator.services.impl;

/**
 * 
 */

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinaaccelerator.services.location.daos.DistrictDao;
import de.hybris.platform.chinaaccelerator.services.location.impl.DefaultDistrictService;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultDistrictServiceUnitTest
{
	private DefaultDistrictService districtService;
	private DistrictDao districtDao;

	private DistrictModel districtModel;


	@Before
	public void setUp()
	{
		// We will be testing StadiumServiceImpl - an implementation of StadiumService
		districtService = new DefaultDistrictService();
		// So as not to implicitly also test the DAO, we will mock out the DAO using EasyMock
		districtDao = mock(DistrictDao.class);
		// and inject this mocked DAO into the StadiumService
		districtService.setDistrictDao(districtDao);

		// This instance of a DistrictModel will be used by the tests
		districtModel = new DistrictModel();
		districtModel.setCode("TEST-DISTRICT-CODE");
		districtModel.setName("TEST-DISTRICT-NAME-EN", Locale.ENGLISH);
		districtModel.setName("TEST-DISTRICT-NAME-ZH", Locale.CHINESE);
	}

	@Test
	public void testGetDistrictsForCityCode()
	{
		// We construct the data we would like the mocked out DAO to return when called
		final List<DistrictModel> districtModels = Arrays.asList(districtModel);
		//Use Mockito and compare results   
		when(districtDao.findDistrictsByCityCode("TEST-CITY-CODE")).thenReturn(districtModels);
		// Now we make the call to StadiumService's getAllStadium which we expect to call the DAOs' getAllStadium method
		final List<DistrictModel> result = districtService.getDistrictsByCityCode("TEST-CITY-CODE");
		// We then verify that the results returned from the service are equals to those returned by the mocked out DAO
		assertEquals("We should find one", 1, result.size());
		assertEquals("And should equals what the mock returned", districtModel, result.get(0));
	}

	@Test
	public void testGetDistrictByCode()
	{
		// Tell Mockito we expect a call to the DAO's getStadium, and if it occurs Mockito should return districtModel
		when(districtDao.findDistrictByCode("TEST-DISTRICT-CODE")).thenReturn(districtModel);
		// We make the call to the Service's getStadium which we expect to call the DAO's getStadium method.
		final DistrictModel result = districtService.getDistrictByCode("TEST-DISTRICT-CODE");
		// We then verify that the result returned form the Service is the same as that returned from the DAO
		assertEquals("And should equals what the mock returned", districtModel, result);
	}
}
