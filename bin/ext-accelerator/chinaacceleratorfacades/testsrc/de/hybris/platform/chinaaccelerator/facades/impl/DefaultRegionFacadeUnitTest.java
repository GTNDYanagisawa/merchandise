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

import de.hybris.platform.chinaaccelerator.facades.location.impl.DefaultRegionFacade;
import de.hybris.platform.chinaaccelerator.services.location.RegionService;
import de.hybris.platform.commercefacades.user.converters.populator.RegionPopulator;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class DefaultRegionFacadeUnitTest
{
	private DefaultRegionFacade regionFacade;

	private RegionService regionService;
	private RegionPopulator regionPopulator;


	private List<RegionModel> dummyDataRegionList()
	{
		final RegionModel regionModel = dummyDataRegion();

		final List<RegionModel> regionModels = new ArrayList<RegionModel>();
		regionModels.add(regionModel);
		return regionModels;
	}

	private RegionModel dummyDataRegion()
	{
		final RegionModel regionModel = new RegionModel();

		regionModel.setIsocode("TEST-REGION-ISOCODE");
		regionModel.setName("TEST-REGION-NAME-EN", Locale.ENGLISH);
		regionModel.setName("TEST-REGION-NAME-ZH", Locale.CHINESE);

		final CountryModel countryModel = new CountryModel();
		countryModel.setName("TEST-COUNTRY-NAME-EN", Locale.ENGLISH);
		countryModel.setName("TEST-COUNTRY-NAME-ZH", Locale.CHINESE);
		countryModel.setIsocode("TEST-COUNTRY-ISOCODE");

		regionModel.setCountry(countryModel);

		return regionModel;
	}

	@Before
	public void setUp()
	{
		//jaloSession.getSessionContext().setLocale(Locale.ENGLISH);
		regionFacade = new DefaultRegionFacade();
		regionService = mock(RegionService.class);
		regionFacade.setRegionService(regionService);
		regionPopulator = mock(RegionPopulator.class); //new RegionPopulator(); //mock(RegionPopulator.class);
		regionFacade.setRegionPopulator(regionPopulator);
	}

	@Test
	public void testGetRegionsForCountryCode()
	{
		final List<RegionModel> regionModels = dummyDataRegionList();
		final RegionModel regionModel = dummyDataRegion();
		when(regionService.getRegionsForCountryCode("TEST-COUNTRY-ISOCODE")).thenReturn(regionModels);


		doAnswer(new Answer<RegionData>()
		{
			@Override
			public RegionData answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				if (args[1] instanceof RegionData)
				{
					final RegionData rd = (RegionData) args[1];
					rd.setName("TEST-REGION-NAME-EN"); // assuming now emulating the value for Locale.ENGLISH
					rd.setIsocode("TEST-REGION-ISOCODE");
					rd.setCountryIso("TEST-COUNTRY-ISOCODE");
				}
				return null;
			}
			//}).when(this.regionPopulator).populate(regionModel, new RegionData());//   .populate(anyObject(), any(RegionData.class));
		}).when(this.regionPopulator).populate(org.mockito.Matchers.any(RegionModel.class),
				org.mockito.Matchers.any(RegionData.class));//   .populate(anyObject(), any(RegionData.class));



		final List<RegionData> dto = regionFacade.getRegionsForCountryCode("TEST-COUNTRY-ISOCODE");
		// We now check that dto is a DTO version of regionModels..
		assertNotNull(dto);
		assertEquals(dto.size(), regionModels.size());
		assertEquals(dto.get(0).getName(), regionModel.getName(Locale.ENGLISH));
		assertEquals(dto.get(0).getIsocode(), regionModel.getIsocode());
		assertEquals(dto.get(0).getCountryIso(), regionModel.getCountry().getIsocode());
	}

	@Test
	public void testGetRegionByCountryAndCode()
	{
		final RegionModel regionModel = dummyDataRegion();

		when(regionService.getRegionByCountryAndCode("TEST-COUNTRY-ISOCODE", "TEST-REGION-ISOCODE")).thenReturn(regionModel);

		doAnswer(new Answer<RegionData>()
		{
			@Override
			public RegionData answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				if (args[1] instanceof RegionData)
				{
					final RegionData rd = (RegionData) args[1];
					rd.setName("TEST-REGION-NAME-EN"); // assuming now emulating the value for Locale.ENGLISH
					rd.setIsocode("TEST-REGION-ISOCODE");
					rd.setCountryIso("TEST-COUNTRY-ISOCODE");
				}
				return null;
			}
			//}).when(this.regionPopulator).populate(regionModel, new RegionData());//   .populate(anyObject(), any(RegionData.class));
		}).when(this.regionPopulator).populate(org.mockito.Matchers.any(RegionModel.class),
				org.mockito.Matchers.any(RegionData.class));//   .populate(anyObject(), any(RegionData.class));



		final RegionData regionData = regionFacade.getRegionByCountryAndCode("TEST-COUNTRY-ISOCODE", "TEST-REGION-ISOCODE");
		assertEquals(regionData.getName(), regionModel.getName(Locale.ENGLISH));
		assertEquals(regionData.getCountryIso(), regionModel.getCountry().getIsocode());
		assertEquals(regionData.getIsocode(), regionModel.getIsocode());
	}
}
