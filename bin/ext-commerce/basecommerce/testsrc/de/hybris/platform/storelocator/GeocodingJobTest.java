/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.storelocator;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.cronjob.constants.CronJobConstants;
import de.hybris.platform.cronjob.jalo.CronJobManager;
import de.hybris.platform.cronjob.jalo.Job;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.jalo.GeocodeAddressesCronJob;
import de.hybris.platform.storelocator.location.LocationService;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.route.RouteService;
import de.hybris.platform.testframework.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;


/**
 *
 */
@IntegrationTest
public class GeocodingJobTest extends BaseCommerceBaseTest
{
	@Resource
	protected LocationService locationService;

	@Resource
	protected GeocodingJob geocodeAddressesJob;

	@Resource
	protected CronJobService cronJobService;

	@Resource
	protected ModelService modelService;

	@Resource
	protected I18NService i18nService;

	@Resource
	protected PointOfServiceDao pointOfServiceDao;

	@Resource
	protected MapService mapService;

	@Resource
	protected RouteService routeService;

	@Resource
	protected GeoWebServiceWrapper geoServiceWrapper;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		//5 batch size - the amount of the test entries is 5
		//1sec internal delay - this is the pause between every geocoding query
		createTestCronJob(Integer.valueOf(5), Integer.valueOf(1));
		createTestPosEntries();
	}

	/**
	 *
	 */
	private void createTestPosEntries() throws Exception
	{
		importCsv("/import/test/PointOfServiceSampleTestData.csv", "UTF-8");

	}

	/**
	 * @throws JaloAbstractTypeException
	 * @throws JaloGenericCreationException
	 * 
	 */
	private void createTestCronJob(final Integer batchSize, final Integer internalDelay) throws JaloGenericCreationException,
			JaloAbstractTypeException
	{
		final CronJobManager cronjobManager = (CronJobManager) JaloSession.getCurrentSession().getExtensionManager()
				.getExtension(CronJobConstants.EXTENSIONNAME);
		final Job job = cronjobManager.createBatchJob("job");

		final ComposedType geocodeAddressesCronJob = jaloSession.getTypeManager().getComposedType(GeocodeAddressesCronJob.class);

		final Map<String, Object> values = new HashMap<String, Object>();
		values.put(GeocodeAddressesCronJob.CODE, "testCronJob");
		values.put(GeocodeAddressesCronJob.BATCHSIZE, batchSize);
		values.put(GeocodeAddressesCronJob.INTERNALDELAY, internalDelay);
		values.put(GeocodeAddressesCronJob.ACTIVE, Boolean.FALSE);
		values.put(GeocodeAddressesCronJob.JOB, job);
		geocodeAddressesCronJob.newInstance(values);

	}

	/**
	 * Tests geocoding of larger amount of test data (5 entries). Checks proper behavior of the service in case of delta
	 * address modifications.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGeocodingCronJob() throws Exception
	{
		assertEquals("Initially, we expect all entries to be submitted for geocoding", 5, pointOfServiceDao.getPosToGeocode()
				.size());
		assertEquals("With batch size limitation:3, dao resulting collection has unexpected size", 5, pointOfServiceDao
				.getPosToGeocode(10).size());
		assertEquals("With batch size limitation:5, dao resulting collection has unexpected size", 5, pointOfServiceDao
				.getPosToGeocode(5).size());

		for (final PointOfServiceModel posModel : pointOfServiceDao.getPosToGeocode())
		{
			assertNull("Initially all pos entries should not be timestamped", posModel.getGeocodeTimestamp());
		}
		TestUtils.disableFileAnalyzer("Testing the Excetions");
		//run the cron job
		geocodeAddressesJob.perform(cronJobService.getCronJob("testCronJob"));

		final List<PointOfServiceModel> geocoded = new ArrayList<PointOfServiceModel>();
		final List<PointOfServiceModel> notGeocoded = new ArrayList<PointOfServiceModel>();

		for (final PointOfServiceModel pos : pointOfServiceDao.getAllPos())
		{
			if (pos.getGeocodeTimestamp() != null)
			{
				geocoded.add(pos);
			}
			else
			{
				notGeocoded.add(pos);
			}
		}
		TestUtils.enableFileAnalyzer();
		//It seems that this test is expecting the job to throw an exception on a incorrect address and therefore only have 4 of the 5 pos being geocoded
		assertNotSame("Geocoded entries amount must be equal to job's batch size", Integer.valueOf(geocoded.size()),
				Integer.valueOf(5));
		assertEquals("Not geocoded entries amount must be equal", 1, notGeocoded.size());

		//If the pos address is changed they should be considered back as not geocoded:
		AddressModel address = null;
		for (int i = 0; i < 3; i++)
		{
			address = geocoded.get(i).getAddress();
			address.setAppartment("1");
			modelService.save(address);
		}
		assertEquals("Not geocoded entries amount should be increased by 3", notGeocoded.size() + 3, pointOfServiceDao
				.getPosToGeocode().size());
	}
}
