/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.facades.location.impl.DefaultCityFacade;
import de.hybris.platform.chinaaccelerator.facades.populators.CityPopulator;
import de.hybris.platform.chinaaccelerator.services.location.CityService;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.core.model.c2l.RegionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class DefaultCityFacadeUnitTest
{
	private DefaultCityFacade cityFacade;

	private CityService cityService;
	private CityPopulator cityPopulator;

	// Convenience method for returning a list of Stadium
	private List<CityModel> dummyDataCityList()
	{
		final CityModel cityModel = dummyDataCity();

		final List<CityModel> cityModels = new ArrayList<CityModel>();
		cityModels.add(cityModel);
		return cityModels;
	}


	// Convenience method for returning a Stadium
	private CityModel dummyDataCity()
	{
		final CityModel cityModel = new CityModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME-EN", Locale.ENGLISH); // if just new CityModel(), then here: [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa
		cityModel.setName("TEST-CITY-NAME-ZH", Locale.CHINESE);

		final RegionModel regionModel = new RegionModel();
		regionModel.setIsocode("TEST-REGION-ISOCODE");
		regionModel.setActive(Boolean.TRUE);
		regionModel.setName("TEST-REGION-NAME-EN", Locale.ENGLISH);
		regionModel.setName("TEST-REGION-NAME-ZH", Locale.CHINESE);

		cityModel.setRegion(regionModel);
		return cityModel;
	}

	@Before
	public void setUp()
	{
		// We will be testing the POJO DefaultCityFacade - the implementation of the 
		// StadiumFacade interface.
		cityFacade = new DefaultCityFacade();

		/**
		 * The facade is expected to make calls to an implementation of CityService but in this test we want to verify the
		 * correct behaviour of DefaultCityFacade itself and not also implicitly test the behaviour of a CityService. In
		 * fact, as of writing this class, we do only have the interface CityService and no implementation. This requires
		 * that we mock out the CityService interface. There are several strong arguments for following this practice:
		 * 
		 * If we were to include a real implementation of CityService rather than mocking it out...
		 * 
		 * 1) we will not get "false failures" in DefaultCityFacade due to errors in the CityService implementation. Such
		 * errors should be caught in tests that are focusing on CityService instead.
		 * 
		 * 2) The condition could arise where an error in the facade gets hidden by a complimentary error in the
		 * CityService implementation - resulting in a "false positive".
		 * 
		 * By mocking out the interface CityService..
		 * 
		 * 3) we do not actually need an implementation of it. This therefore helps us to focus our tests on this POJO
		 * before having to implement other POJOs on which it depends - allowing us to write tests early.
		 * 
		 * 4) by focusing on the behaviour of the facade and the interfaces it uses, we are forced to focus also on the
		 * those interface, improving them before writing their implementation.
		 * 
		 * 
		 * Therefore we create a mock of the CityService in the next line.
		 */

		cityService = mock(CityService.class);
		// We then wire this service into the StadiumFacade implementation.
		cityFacade.setCityService(cityService);

		cityPopulator = mock(CityPopulator.class);
		cityFacade.setCityPopulator(cityPopulator);
	}

	/**
	 * The aim of this test is to test that:
	 * 
	 * 1) The facade's method getStadiums makes a call to the CityService's method getStadiums
	 * 
	 * 2) The facade then correctly wraps StadiumModels that are returned to it from the CityService's getStadiums into
	 * Data Transfer Objects of type CityData.
	 */
	@Test
	public void testGetCitiesByRegionCode()
	{
		/**
		 * We instantiate an object that we would like to be returned to StadiumFacade when the mocked out CityService's
		 * method getStadiums is called. This will be a list of two StadiumModels.
		 */
		final List<CityModel> cityModels = dummyDataCityList();
		final CityModel cityModel = dummyDataCity();
		// We tell Mockito we expect CityService's method getStadiums to be called, 
		// and that when it is, cityModels should be returned
		when(cityService.getCitiesByRegionCode("TEST-REGION-ISOCODE")).thenReturn(cityModels);

		doAnswer(new Answer<CityData>()
		{
			@Override
			public CityData answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				if (args[1] instanceof CityData)
				{
					final CityData rd = (CityData) args[1];
					rd.setName("TEST-CITY-NAME-EN"); // assuming now emulating the value for Locale.ENGLISH
					rd.setCode("TEST-CITY-CODE");
				}
				return null;
			}
		}).when(this.cityPopulator).populate(org.mockito.Matchers.any(CityModel.class), org.mockito.Matchers.any(CityData.class));




		/**
		 * We now make the call to StadiumFacade's getStadiums. If within this method a call is made to CityService's
		 * getStadiums, Mockito will return the cityModels instance to it. Mockito will also remember that the call was
		 * made.
		 */
		final List<CityData> dto = cityFacade.getCitiesByRegionCode("TEST-REGION-ISOCODE");
		// We now check that dto is a DTO version of cityModels..
		assertNotNull(dto);
		assertEquals(dto.size(), cityModels.size());
		assertEquals(dto.get(0).getName(), cityModel.getName(Locale.ENGLISH));
		assertEquals(dto.get(0).getCode(), cityModel.getCode());
	}

	@Test
	public void testGetCityForCode()
	{
		/**
		 * We instantiate an object that we would like to be returned to StadiumFacade when the mocked out CityService's
		 * method getStadiums is called. This will be a list of two StadiumModels.
		 */
		final CityModel cityModel = dummyDataCity();


		// We tell Mockito we expect CityService's method getStadiumForCode to be
		// called, and that when it is, cityModel should be returned
		when(cityService.getCityForCode("TEST-CITY-CODE")).thenReturn(cityModel);

		doAnswer(new Answer<CityData>()
		{
			@Override
			public CityData answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				if (args[1] instanceof CityData)
				{
					final CityData rd = (CityData) args[1];
					rd.setName("TEST-CITY-NAME-EN"); // assuming now emulating the value for Locale.ENGLISH
					rd.setCode("TEST-CITY-CODE");
				}
				return null;
			}
		}).when(this.cityPopulator).populate(org.mockito.Matchers.any(CityModel.class), org.mockito.Matchers.any(CityData.class));



		/**
		 * We now make the call to StadiumFacade's getStadium. If within this method a call is made to CityService's
		 * getStadium, Mockito will return the cityModel instance to it. Mockito will also remember that the call was
		 * made.
		 */
		final CityData cityData = cityFacade.getCityForCode("TEST-CITY-CODE");
		// Check that cityData is a correct DTO representation of the cityModel ServiceModel
		assertEquals(cityData.getName(), cityModel.getName(Locale.ENGLISH));
		assertEquals(cityData.getCode(), cityModel.getCode());
	}

}
