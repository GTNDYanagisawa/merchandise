// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinaaccelerator.services.location.RegionService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultRegionServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private RegionService regionService;

	@Resource
	private ModelService modelService;

	private RegionModel regionModel;
	private CountryModel countryModel;

	@Before
	public void setUp()
	{
		//		regionModel = new RegionModel();
		//		regionModel.setIsocode("ISO-TEST-1");
		//		regionModel.setActive(Boolean.TRUE);

		//		countryModel = new CountryModel();
		//		countryModel.setActive(Boolean.TRUE);
		//		countryModel.setIsocode("CN");
		//		countryModel.setName("China", Locale.ENGLISH);
	}

	@Test
	public void testNotExistingCountryCode()
	{
		final List result = regionService.getRegionsForCountryCode("CNunknown");
		assertNotNull("Returning result object is null! But should be Collections.EMPTY_LIST.", result);
		assertEquals("Returning result object is not Collections.EMPTY_LIST!", Collections.EMPTY_LIST, result);
	}


	@Test
	public void testRegionService()
	{
		List<RegionModel> regionModels = regionService.getRegionsForCountryCode("CNTEST");
		final int size = regionModels.size();

		countryModel = modelService.create(CountryModel.class);
		countryModel.setActive(Boolean.TRUE);
		countryModel.setIsocode("CNTEST");
		countryModel.setName("ChinaTest", Locale.ENGLISH);
		regionModel = modelService.create(RegionModel.class);
		regionModel.setIsocode("ISO-TEST-1");
		regionModel.setActive(Boolean.TRUE);
		regionModel.setName("TESTREGION", Locale.ENGLISH);

		modelService.save(countryModel);
		regionModel.setCountry(countryModel);
		modelService.save(regionModel);

		modelService.saveAll();

		regionModels = regionService.getRegionsForCountryCode("CNTEST");

		assertEquals(size + 1, regionModels.size());
		assertEquals("Unexpected region found", regionModel, regionModels.get(regionModels.size() - 1));


		final RegionModel persistedRegionModel = regionService.getRegionByCountryAndCode("CNTEST", "ISO-TEST-1");
		assertNotNull("No region found", persistedRegionModel);
		assertEquals("Different region found", regionModel, persistedRegionModel);
	}
}
