// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinaaccelerator.services.location.impl.DefaultRegionService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import de.hybris.platform.servicelayer.i18n.daos.RegionDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultRegionServiceUnitTest
{

	private DefaultRegionService regionService;

	private RegionDao regionDao;
	private CountryDao countryDao;

	private RegionModel regionModel;
	private CountryModel countryModel;


	@Before
	public void setUp()
	{
		regionService = new DefaultRegionService();
		regionDao = mock(RegionDao.class);
		regionService.setRegionDao(regionDao);

		countryDao = mock(CountryDao.class);
		regionService.setCountryDao(countryDao);


		regionModel = new RegionModel();
		regionModel.setIsocode("ISO-TEST-1");
		regionModel.setName("TESTREGIONNAME", Locale.ENGLISH);
		countryModel = new CountryModel();
		countryModel.setName("TESTCOUNTRYNAME-EN", Locale.ENGLISH);
		countryModel.setName("TESTCOUNTRYNAME-ZH", Locale.CHINESE);
		countryModel.setIsocode("TEST-COUNTRY-ISO-CODE");

		regionModel.setCountry(countryModel);
	}

	@Test
	public void testGetRegionsForCountryCode()
	{
		final List<RegionModel> regionModels = Arrays.asList(regionModel);
		when(regionDao.findRegionsByCountry(countryModel)).thenReturn(regionModels);

		final List<CountryModel> countryModels = Arrays.asList(countryModel);
		when(countryDao.findCountriesByCode("TEST-COUNTRY-ISO-CODE")).thenReturn(countryModels);


		final List<RegionModel> result = regionService.getRegionsForCountryCode("TEST-COUNTRY-ISO-CODE");
		assertEquals("We should find one", 1, result.size());
		assertEquals("And should equals what the mock returned", regionModel, result.get(0));
	}

	@Test
	public void testGetRegionByCountryAndCode()
	{
		when(regionDao.findRegionsByCountryAndCode(countryModel, "ISO-TEST-1")).thenReturn(Collections.singletonList(regionModel));

		final List<CountryModel> countryModels = Arrays.asList(countryModel);
		when(countryDao.findCountriesByCode("TEST-COUNTRY-ISO-CODE")).thenReturn(countryModels);

		final RegionModel resultRegionModel = regionService.getRegionByCountryAndCode("TEST-COUNTRY-ISO-CODE", "ISO-TEST-1");

		assertEquals("And should equals what the mock returned", regionModel, resultRegionModel);
	}
}