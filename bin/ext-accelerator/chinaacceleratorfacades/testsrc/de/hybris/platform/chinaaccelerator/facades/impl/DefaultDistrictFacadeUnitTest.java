// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.chinaaccelerator.facades.data.DistrictData;
import de.hybris.platform.chinaaccelerator.facades.location.impl.DefaultDistrictFacade;
import de.hybris.platform.chinaaccelerator.facades.populators.DistrictPopulator;
import de.hybris.platform.chinaaccelerator.services.location.DistrictService;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class DefaultDistrictFacadeUnitTest
{
	private DefaultDistrictFacade districtFacade;

	private DistrictService districtService;
	private DistrictPopulator districtPopulator;

	//	@Resource
	//	private ModelService modelService;

	private List<DistrictModel> dummyDataDistrictList()
	{
		final DistrictModel districtModel = dummyDataDistrict();
		final List<DistrictModel> districtModels = new ArrayList<DistrictModel>();
		districtModels.add(districtModel);
		return districtModels;
	}

	private DistrictModel dummyDataDistrict()
	{
		final DistrictModel districtModel = new DistrictModel(); // modelService.create(DistrictModel.class); // new DistrictModel();
		districtModel.setCode("TEST-DISTRICT-CODE");
		districtModel.setName("TEST-DISTRICT-NAME-EN", Locale.ENGLISH);
		districtModel.setName("TEST-DISTRICT-NAME-ZH", Locale.CHINESE);

		final CityModel cityModel = new CityModel(); // modelService.create(CityModel.class); //new CityModel();
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME-EN", Locale.ENGLISH);
		cityModel.setName("TEST-CITY-NAME-ZH", Locale.CHINESE);

		districtModel.setCity(cityModel);

		return districtModel;
	}

	@Before
	public void setUp()
	{
		districtFacade = new DefaultDistrictFacade();


		districtService = mock(DistrictService.class);
		districtFacade.setDistrictService(districtService);
		districtPopulator = mock(DistrictPopulator.class); //new DistrictPopulator();
		districtFacade.setDistrictPopulator(districtPopulator);
	}


	@Test
	public void testGetDistrictsByCityCode()
	{
		final List<DistrictModel> districtModels = dummyDataDistrictList();
		final DistrictModel districtModel = dummyDataDistrict();
		when(districtService.getDistrictsByCityCode("TEST-CITY-CODE")).thenReturn(districtModels);


		doAnswer(new Answer<DistrictData>()
		{
			@Override
			public DistrictData answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				if (args[1] instanceof DistrictData)
				{
					final DistrictData dd = (DistrictData) args[1];
					dd.setName("TEST-DISTRICT-NAME-EN"); // assuming now emulating the value for Locale.ENGLISH
					dd.setCode("TEST-DISTRICT-CODE");
				}
				return null;
			}
		}).when(this.districtPopulator).populate(org.mockito.Matchers.any(DistrictModel.class),
				org.mockito.Matchers.any(DistrictData.class));



		final List<DistrictData> dto = districtFacade.getDistrictsByCityCode("TEST-CITY-CODE");
		assertNotNull(dto);
		assertEquals(dto.size(), districtModels.size());
		assertEquals(dto.get(0).getName(), districtModel.getName(Locale.ENGLISH));
		assertEquals(dto.get(0).getCode(), districtModel.getCode());
	}

	@Test
	public void testGetDistrictByCode()
	{
		final DistrictModel districtModel = dummyDataDistrict();

		when(districtService.getDistrictByCode("TEST-DISTRICT-CODE")).thenReturn(districtModel);


		doAnswer(new Answer<DistrictData>()
		{
			@Override
			public DistrictData answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				if (args[1] instanceof DistrictData)
				{
					final DistrictData dd = (DistrictData) args[1];
					dd.setName("TEST-DISTRICT-NAME-EN"); // assuming now emulating the value for Locale.ENGLISH
					dd.setCode("TEST-DISTRICT-CODE");
				}
				return null;
			}
		}).when(this.districtPopulator).populate(org.mockito.Matchers.any(DistrictModel.class),
				org.mockito.Matchers.any(DistrictData.class));


		final DistrictData stadium = districtFacade.getDistrictByCode("TEST-DISTRICT-CODE");
		assertEquals(stadium.getName(), districtModel.getName(Locale.ENGLISH));
		assertEquals(stadium.getCode(), districtModel.getCode());
	}
}
