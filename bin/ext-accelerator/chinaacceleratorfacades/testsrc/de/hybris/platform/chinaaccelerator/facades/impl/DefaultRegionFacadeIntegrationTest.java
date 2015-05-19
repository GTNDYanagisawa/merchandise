// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.chinaaccelerator.facades.location.RegionFacade;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;


public class DefaultRegionFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private RegionFacade regionFacade;

	@Resource
	private ModelService modelService;

	private RegionModel regionModel;
	private CountryModel countryModel;


	//@Before
	public void setUp()
	{
		jaloSession.getSessionContext().setLocale(Locale.ENGLISH);
		System.out.println("Jalo Session Language Name=" + jaloSession.getSessionContext().getLanguage().getName());
		System.out.println("Jalo Session Locale DisplayName=" + jaloSession.getSessionContext().getLocale().getDisplayName());
		System.out.println("Jalo Session Locake locale=" + jaloSession.getSessionContext().getLocale());


		// This instance of a StadiumModel will be used by the tests
		regionModel = modelService.create(RegionModel.class); //new RegionModel();
		regionModel.setIsocode("TEST-REGION-ISOCODE");
		regionModel.setActive(Boolean.TRUE);
		regionModel.setName("TEST-REGION-NAME-EN", Locale.ENGLISH);
		regionModel.setName("TEST-REGION-NAME-ZH", Locale.CHINESE);

		// switch to modelService create method 
		countryModel = modelService.create(CountryModel.class); //new CountryModel();
		countryModel.setActive(Boolean.TRUE);
		countryModel.setIsocode("TEST-COUNTRY-ISOCODE");
		countryModel.setName("TEST-COUNTRY-NAME-EN", Locale.ENGLISH);
		countryModel.setName("TEST-COUNTRY-NAME-ZH", Locale.CHINESE);

		regionModel.setCountry(countryModel);


		//		modelService.save(countryModel);
		//		regionModel.setCountry(countryModel);
		//		modelService.save(regionModel);
		//
		//		modelService.saveAll();
	}


	//	@Test(expected = UnknownIdentifierException.class)
	@Ignore
	@Test
	public void testNonexistingCountryCode()
	{
		final List<RegionData> result = regionFacade.getRegionsForCountryCode("unknown");
		assertNotNull("Result has been null!", result);
		assertEquals(0, result.size());
		assertEquals("Result is not as expected the EMPY_LIST", Collections.EMPTY_LIST, result);

		final RegionData resultRegion = regionFacade.getRegionByCountryAndCode("TEST-COUNTRY-ISOCODE", "unknown");
		assertEquals("Result is not null!", null, resultRegion);
	}

	@Ignore
	@Test
	public void testRegionFacade()
	{
		List<RegionData> regionListData = regionFacade.getRegionsForCountryCode("TEST-COUNTRY-ISOCODE");
		assertNotNull(regionListData);
		final int size = regionListData.size();

		modelService.save(countryModel); // if model save is called in setup() method already, it will save it 2x for 2 tests - obviously rollback only after test class is finished
		modelService.save(regionModel);


		regionListData = regionFacade.getRegionsForCountryCode("TEST-COUNTRY-ISOCODE");
		assertNotNull(regionListData);
		assertEquals(size + 1, regionListData.size());
		assertEquals(regionListData.get(size).getName(), "TEST-REGION-NAME-EN");
		assertEquals(regionListData.get(size).getIsocode(), "TEST-REGION-ISOCODE");

		final RegionData persistedRegionData = regionFacade
				.getRegionByCountryAndCode("TEST-COUNTRY-ISOCODE", "TEST-REGION-ISOCODE");
		assertNotNull(persistedRegionData);
		assertEquals(persistedRegionData.getName(), "TEST-REGION-NAME-EN");
		assertEquals(persistedRegionData.getIsocode(), "TEST-REGION-ISOCODE");
	}
}
