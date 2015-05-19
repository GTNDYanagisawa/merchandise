// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.chinaaccelerator.services.location.daos.DistrictDao;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;


public class DefaultDistrictDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private DistrictDao districtDao;

	@Resource
	private ModelService modelService;


	@Test
	public void districtDaoTest()
	{
		/*
		 * List<DistrictModel> districtModels = districtDao.findDistrictsByCityCode("TEST-CITY-CODE");
		 * assertTrue("There should be no district found", districtModels.isEmpty());
		 * 
		 * final int size = districtModels.size();
		 * 
		 * final CityModel cityModel = new CityModel(); cityModel.setActive(Boolean.TRUE);
		 * cityModel.setName("CITY-NAME-EN", Locale.ENGLISH); cityModel.setName("CITY-NAME-ZH", Locale.CHINESE);
		 * cityModel.setCode("TEST-CITY-CODE"); this.modelService.save(cityModel);
		 * 
		 * final List<DistrictModel> createdDistrictModels = new ArrayList<DistrictModel>(); final int numberOfDistricts =
		 * 10; for (int runnerId = 0; runnerId < numberOfDistricts; runnerId++) { final DistrictModel districtModel =
		 * createTestDataDistrictModel(runnerId + 1); districtModel.setCity(cityModel);
		 * this.modelService.save(districtModel); createdDistrictModels.add(districtModel); }
		 * 
		 * 
		 * districtModels = districtDao.findDistrictsByCityCode("TEST-CITY-CODE"); assertEquals(size + numberOfDistricts,
		 * districtModels.size()); assertListOfItems(createdDistrictModels, districtModels);
		 * 
		 * 
		 * districtModels = fetchTestDataDistrictModels(10); // 10 districts assertListOfItems(createdDistrictModels,
		 * districtModels);
		 */
	}

	private void assertListOfItems(final List<DistrictModel> expected, final List<DistrictModel> target)
	{
		assertEquals("Unexpected different numbers of districts. Already in the beginning.", expected.size(), target.size());

		int foundMatches = 0;
		for (final DistrictModel expectedDistrictModel : target)
		{
			int i = 0;
			for (final DistrictModel fetchedDistrictModel : expected)
			{
				i++;
				if (expectedDistrictModel.getCode().equals(fetchedDistrictModel.getCode())
						&& expectedDistrictModel.getName(Locale.ENGLISH).equals(fetchedDistrictModel.getName(Locale.ENGLISH))
						&& expectedDistrictModel.getName(Locale.CHINESE).equals(fetchedDistrictModel.getName(Locale.CHINESE)))
				{
					foundMatches++;
					break;
				}
				if (i == 10)
				{
					fail("Didn't find a match for district.");
				}
			}
		}
		assertEquals("Unexpected different numbers of districts.", expected.size(), foundMatches);
	}

	//@Test
	public void districtDaoCornerCases()
	{
		List<DistrictModel> districtModels = districtDao.findDistrictsByCityCode("");
		assertTrue("There should be no District found", districtModels.isEmpty());

		final DistrictModel fetchedDistrictModel = districtDao.findDistrictByCode("");
		assertEquals("There should be no District found.", null, fetchedDistrictModel);

		try
		{
			districtModels = districtDao.findDistrictsByCityCode(null);
			fail("Expected getDistricts(null) to throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e) //NOPMD
		{
			//
		}

		final DistrictModel districtModel = new DistrictModel();
		districtModel.setCode("TEST-DISTRICT-CODE");
		districtModel.setName("TEST-DISTRICT-NAME-EN", Locale.ENGLISH);
		districtModel.setName("TEST-DISTRICT-NAME-ZH", Locale.CHINESE);
		modelService.save(districtModel);
	}

	private List<DistrictModel> fetchTestDataDistrictModels(final int max)
	{

		final List<DistrictModel> districtModels = new ArrayList<DistrictModel>();

		for (int i = 0; i < max; i++)
		{
			final DistrictModel districtModel = districtDao.findDistrictByCode("TEST-DISTRICT-CODE-" + (i + 1));
			districtModels.add(districtModel);
		}
		return districtModels;
	}

	private DistrictModel createTestDataDistrictModel(final int runnerId)
	{
		final DistrictModel districtModel = new DistrictModel();
		districtModel.setActive(Boolean.TRUE);
		districtModel.setCode("TEST-DISTRICT-CODE-" + runnerId);
		districtModel.setName("TEST-DISTRICT-NAME" + runnerId + "-EN", Locale.ENGLISH);
		districtModel.setName("TEST-DISTRICT-NAME" + runnerId + "-ZH", Locale.CHINESE);
		districtModel.setLatitude(new Double("" + runnerId + "." + runnerId));
		districtModel.setLongitude(new Double("" + runnerId + "." + runnerId));

		return districtModel;
	}
}
