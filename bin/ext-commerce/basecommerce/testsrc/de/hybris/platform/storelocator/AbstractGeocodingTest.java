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

import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
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
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.jalo.GeocodeAddressesCronJob;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.LocationService;
import de.hybris.platform.storelocator.location.impl.LocationDTO;
import de.hybris.platform.storelocator.location.impl.LocationDtoWrapper;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.route.RouteService;
import de.hybris.platform.testframework.Transactional;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Ignore;


/**
 * 
 * 
 */
@Ignore
@Transactional
public abstract class AbstractGeocodingTest extends BaseCommerceBaseTest
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


	/**
	 * @param geoServiceWrapper
	 *           the geoServiceWrapper to set
	 */
	public void setGeoServiceWrapper(final GeoWebServiceWrapper geoServiceWrapper)
	{
		this.geoServiceWrapper = geoServiceWrapper;
	}

	protected void createTestCronJob(final Integer batchSize, final Integer internalDelay) throws JaloGenericCreationException,
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

	protected void createTestCronJob() throws JaloGenericCreationException, JaloAbstractTypeException
	{
		createTestCronJob(Integer.valueOf(100), Integer.valueOf(1));
	}

	protected void createTestPosEntries() throws Exception
	{
		importCsv("/import/test/PointOfServiceLocationsImport.csv", "UTF-8");
	}

	protected Location createAndStoreTestLocation(final String name, final String street, final String buildingNo,
			final String postalCode, final String city, final String countryIsoCode) throws LocationServiceException
	{
		locationService.saveOrUpdateLocation(createTestLocation(name, street, buildingNo, postalCode, city, countryIsoCode));
		return locationService.getLocationByName(name);
	}

	protected Location createTestLocation(final String name, final String street, final String buildingNo,
			final String postalCode, final String city, final String countryIsoCode) throws LocationServiceException
	{
		final LocationDTO dto = new LocationDTO();
		dto.setName(name);
		dto.setType(LocationDTO.LOCATION_TYPE_STORE);
		dto.setStreet(street);
		dto.setBuildingNo(buildingNo);
		dto.setPostalCode(postalCode);
		dto.setCity(city);
		dto.setCountryIsoCode(countryIsoCode);
		return new LocationDtoWrapper(dto);
	}

	/**
	 * @param locationService
	 *           the locationService to set
	 */
	public void setLocationService(final LocationService locationService)
	{
		this.locationService = locationService;
	}


	/**
	 * @param cronJobService
	 *           the cronJobService to set
	 */
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	/**
	 * @param geocodeAddressesJob
	 *           the geocodeAddressesJob to set
	 */
	public void setGeocodeAddressesJob(final GeocodingJob geocodeAddressesJob)
	{
		this.geocodeAddressesJob = geocodeAddressesJob;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	/**
	 * @param pointOfServiceDao
	 *           the pointOfServiceDao to set
	 */
	public void setPointOfServiceDao(final PointOfServiceDao pointOfServiceDao)
	{
		this.pointOfServiceDao = pointOfServiceDao;
	}


	/**
	 * @param mapService
	 *           the mapService to set
	 */
	public void setMapService(final MapService mapService)
	{
		this.mapService = mapService;
	}

	/**
	 * @param routeService
	 *           the routeService to set
	 */
	public void setRouteService(final RouteService routeService)
	{
		this.routeService = routeService;
	}


}
