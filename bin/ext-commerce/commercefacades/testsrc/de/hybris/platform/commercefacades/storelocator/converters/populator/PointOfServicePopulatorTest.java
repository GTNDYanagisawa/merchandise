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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.model.storelocator.StoreLocatorFeatureModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.storelocator.model.OpeningScheduleModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PointOfServicePopulatorTest
{
	private static final String POS_NAME = "posName";
	private static final String POS_DESC = "posDesc";
	private static final Double POS_LAT = Double.valueOf(11.22);
	private static final Double POS_LON = Double.valueOf(22.33);
	private static final String POS_STORE_CONTENT = "storeContent";

	private static final String IMG_FORMAT = "imgFormat";
	private static final String FEATURE_CODE = "featureCode";

	@Mock
	private AbstractPopulatingConverter<MediaModel, ImageData> imageConverter;
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractPopulatingConverter<OpeningScheduleModel, OpeningScheduleData> openingScheduleConverter;
	@Mock
	private MediaService mediaService;
	@Mock
	private MediaContainerService mediaContainerService;
	@Mock
	private ImageFormatMapping imageFormatMapping;

	private List<String> imageFormats;

	private final PointOfServicePopulator pointOfServicePopulator = new PointOfServicePopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		imageFormats = new ArrayList<String>();
		imageFormats.add(IMG_FORMAT);

		pointOfServicePopulator.setAddressConverter(addressConverter);
		pointOfServicePopulator.setImageConverter(imageConverter);
		pointOfServicePopulator.setImageFormatMapping(imageFormatMapping);
		pointOfServicePopulator.setMediaContainerService(mediaContainerService);
		pointOfServicePopulator.setMediaService(mediaService);
		pointOfServicePopulator.setImageFormats(imageFormats);
		pointOfServicePopulator.setOpeningScheduleConverter(openingScheduleConverter);
	}

	@Test
	public void testConvert()
	{
		final StoreLocatorFeatureModel feature = mock(StoreLocatorFeatureModel.class);
		final AddressModel addressModel = mock(AddressModel.class);
		final AddressData addressData = mock(AddressData.class);
		final OpeningScheduleModel scheduleModel = mock(OpeningScheduleModel.class);
		final OpeningScheduleData scheduleData = mock(OpeningScheduleData.class);
		final MediaModel mediaModel = mock(MediaModel.class);
		final ImageData mediaData = mock(ImageData.class);
		final MediaContainerModel storeImageContainer = mock(MediaContainerModel.class);
		final MediaFormatModel mediaFormatModel = mock(MediaFormatModel.class);
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		given(pointOfServiceModel.getName()).willReturn(POS_NAME);
		given(pointOfServiceModel.getAddress()).willReturn(addressModel);
		given(pointOfServiceModel.getDescription()).willReturn(POS_DESC);
		given(pointOfServiceModel.getLatitude()).willReturn(POS_LAT);
		given(pointOfServiceModel.getLongitude()).willReturn(POS_LON);
		given(pointOfServiceModel.getMapIcon()).willReturn(mediaModel);
		given(pointOfServiceModel.getOpeningSchedule()).willReturn(scheduleModel);
		given(pointOfServiceModel.getStoreContent()).willReturn(POS_STORE_CONTENT);
		given(pointOfServiceModel.getStoreImage()).willReturn(storeImageContainer);

		final Set<StoreLocatorFeatureModel> features = new HashSet<StoreLocatorFeatureModel>();
		features.add(feature);
		given(pointOfServiceModel.getFeatures()).willReturn(features);


		given(openingScheduleConverter.convert(scheduleModel)).willReturn(scheduleData);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(imageConverter.convert(mediaModel)).willReturn(mediaData);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(IMG_FORMAT)).willReturn(IMG_FORMAT);
		given(mediaService.getFormat(IMG_FORMAT)).willReturn(mediaFormatModel);
		given(mediaContainerService.getMediaForFormat(storeImageContainer, mediaFormatModel)).willReturn(mediaModel);
		given(feature.getCode()).willReturn(FEATURE_CODE);

		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		pointOfServicePopulator.populate(pointOfServiceModel, pointOfServiceData);
		Assert.assertNotNull(pointOfServiceData);
		Assert.assertEquals(POS_NAME, pointOfServiceData.getName());
		Assert.assertEquals(addressData, pointOfServiceData.getAddress());
		Assert.assertEquals(POS_DESC, pointOfServiceData.getDescription());
		Assert.assertEquals(POS_LAT, Double.valueOf(pointOfServiceData.getGeoPoint().getLatitude()));
		Assert.assertEquals(POS_LON, Double.valueOf(pointOfServiceData.getGeoPoint().getLongitude()));
		Assert.assertEquals(mediaData, pointOfServiceData.getMapIcon());
		Assert.assertEquals(scheduleData, pointOfServiceData.getOpeningHours());
		Assert.assertEquals(POS_STORE_CONTENT, pointOfServiceData.getStoreContent());
		Assert.assertEquals(1, pointOfServiceData.getStoreImages().size());
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(pointOfServiceData.getFeatures().containsKey(FEATURE_CODE)));
	}

	@Test
	public void testConvertSomeDataEmpty()
	{
		final StoreLocatorFeatureModel feature = mock(StoreLocatorFeatureModel.class);
		final AddressModel addressModel = mock(AddressModel.class);
		final OpeningScheduleModel scheduleModel = mock(OpeningScheduleModel.class);
		final MediaModel mediaModel = mock(MediaModel.class);
		final MediaContainerModel storeImageContainer = mock(MediaContainerModel.class);
		final MediaFormatModel mediaFormatModel = mock(MediaFormatModel.class);
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		given(pointOfServiceModel.getName()).willReturn(POS_NAME);
		given(pointOfServiceModel.getAddress()).willReturn(addressModel);
		given(pointOfServiceModel.getDescription()).willReturn(POS_DESC);
		given(pointOfServiceModel.getLatitude()).willReturn(POS_LAT);
		given(pointOfServiceModel.getLongitude()).willReturn(POS_LON);
		given(pointOfServiceModel.getMapIcon()).willReturn(mediaModel);
		given(pointOfServiceModel.getOpeningSchedule()).willReturn(scheduleModel);
		given(pointOfServiceModel.getStoreContent()).willReturn(POS_STORE_CONTENT);
		given(pointOfServiceModel.getStoreImage()).willReturn(storeImageContainer);

		final Set<StoreLocatorFeatureModel> features = new HashSet<StoreLocatorFeatureModel>();
		features.add(feature);
		given(pointOfServiceModel.getFeatures()).willReturn(Collections.EMPTY_SET);

		given(openingScheduleConverter.convert(scheduleModel)).willReturn(null);
		given(addressConverter.convert(addressModel)).willReturn(null);
		given(imageConverter.convert(mediaModel)).willReturn(null);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(IMG_FORMAT)).willReturn(null);
		given(mediaService.getFormat(IMG_FORMAT)).willReturn(null);
		given(mediaContainerService.getMediaForFormat(storeImageContainer, mediaFormatModel)).willReturn(mediaModel);
		given(feature.getCode()).willReturn(FEATURE_CODE);

		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		pointOfServicePopulator.populate(pointOfServiceModel, pointOfServiceData);
		Assert.assertNotNull(pointOfServiceData);
		Assert.assertEquals(POS_NAME, pointOfServiceData.getName());
		Assert.assertEquals(null, pointOfServiceData.getAddress());
		Assert.assertEquals(POS_DESC, pointOfServiceData.getDescription());
		Assert.assertEquals(POS_LAT, Double.valueOf(pointOfServiceData.getGeoPoint().getLatitude()));
		Assert.assertEquals(POS_LON, Double.valueOf(pointOfServiceData.getGeoPoint().getLongitude()));
		Assert.assertEquals(null, pointOfServiceData.getMapIcon());
		Assert.assertEquals(null, pointOfServiceData.getOpeningHours());
		Assert.assertEquals(POS_STORE_CONTENT, pointOfServiceData.getStoreContent());
		Assert.assertEquals(0, pointOfServiceData.getStoreImages().size());
		Assert.assertEquals(Boolean.FALSE, Boolean.valueOf(pointOfServiceData.getFeatures().containsKey(FEATURE_CODE)));

	}


	@Test
	public void testConvertSomeAreNull()
	{
		final StoreLocatorFeatureModel feature = mock(StoreLocatorFeatureModel.class);
		final MediaFormatModel mediaFormatModel = mock(MediaFormatModel.class);
		final OpeningScheduleModel scheduleModel = mock(OpeningScheduleModel.class);
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		given(pointOfServiceModel.getName()).willReturn(POS_NAME);
		given(pointOfServiceModel.getAddress()).willReturn(null);
		given(pointOfServiceModel.getDescription()).willReturn(POS_DESC);
		given(pointOfServiceModel.getLatitude()).willReturn(POS_LAT);
		given(pointOfServiceModel.getLongitude()).willReturn(POS_LON);
		given(pointOfServiceModel.getMapIcon()).willReturn(null);
		given(pointOfServiceModel.getOpeningSchedule()).willReturn(scheduleModel);
		given(pointOfServiceModel.getStoreContent()).willReturn(POS_STORE_CONTENT);
		given(pointOfServiceModel.getStoreImage()).willReturn(null);

		final Set<StoreLocatorFeatureModel> features = new HashSet<StoreLocatorFeatureModel>();
		features.add(feature);
		given(pointOfServiceModel.getFeatures()).willReturn(features);

		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(IMG_FORMAT)).willReturn(IMG_FORMAT);
		given(mediaService.getFormat(IMG_FORMAT)).willReturn(mediaFormatModel);
		given(feature.getCode()).willReturn(FEATURE_CODE);

		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		pointOfServicePopulator.populate(pointOfServiceModel, pointOfServiceData);
		Assert.assertNotNull(pointOfServiceData);
		Assert.assertEquals(POS_NAME, pointOfServiceData.getName());
		Assert.assertEquals(POS_DESC, pointOfServiceData.getDescription());
		Assert.assertEquals(POS_LAT, Double.valueOf(pointOfServiceData.getGeoPoint().getLatitude()));
		Assert.assertEquals(POS_LON, Double.valueOf(pointOfServiceData.getGeoPoint().getLongitude()));
		Assert.assertEquals(null, pointOfServiceData.getOpeningHours());
		Assert.assertEquals(POS_STORE_CONTENT, pointOfServiceData.getStoreContent());
		Assert.assertEquals(0, pointOfServiceData.getStoreImages().size());
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(pointOfServiceData.getFeatures().containsKey(FEATURE_CODE)));
	}


	@Test
	public void testConvertMediaFormatIsNull()
	{
		final StoreLocatorFeatureModel feature = mock(StoreLocatorFeatureModel.class);
		final AddressModel addressModel = mock(AddressModel.class);
		final AddressData addressData = mock(AddressData.class);
		final MediaModel mediaModel = mock(MediaModel.class);
		final ImageData mediaData = mock(ImageData.class);
		final MediaContainerModel storeImageContainer = mock(MediaContainerModel.class);
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		given(pointOfServiceModel.getName()).willReturn(POS_NAME);
		given(pointOfServiceModel.getAddress()).willReturn(addressModel);
		given(pointOfServiceModel.getDescription()).willReturn(POS_DESC);
		given(pointOfServiceModel.getLatitude()).willReturn(POS_LAT);
		given(pointOfServiceModel.getLongitude()).willReturn(POS_LON);
		given(pointOfServiceModel.getMapIcon()).willReturn(mediaModel);
		given(pointOfServiceModel.getOpeningSchedule()).willReturn(null);
		given(pointOfServiceModel.getStoreContent()).willReturn(POS_STORE_CONTENT);
		given(pointOfServiceModel.getStoreImage()).willReturn(storeImageContainer);

		final Set<StoreLocatorFeatureModel> features = new HashSet<StoreLocatorFeatureModel>();
		features.add(feature);
		given(pointOfServiceModel.getFeatures()).willReturn(features);

		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(imageConverter.convert(mediaModel)).willReturn(mediaData);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(IMG_FORMAT)).willReturn(IMG_FORMAT);
		given(mediaService.getFormat(IMG_FORMAT)).willReturn(null);
		given(mediaContainerService.getMediaForFormat(storeImageContainer, null)).willReturn(mediaModel);
		given(feature.getCode()).willReturn(FEATURE_CODE);

		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		pointOfServicePopulator.populate(pointOfServiceModel, pointOfServiceData);
		Assert.assertNotNull(pointOfServiceData);
		Assert.assertEquals(POS_NAME, pointOfServiceData.getName());
		Assert.assertEquals(addressData, pointOfServiceData.getAddress());
		Assert.assertEquals(POS_DESC, pointOfServiceData.getDescription());
		Assert.assertEquals(POS_LAT, Double.valueOf(pointOfServiceData.getGeoPoint().getLatitude()));
		Assert.assertEquals(POS_LON, Double.valueOf(pointOfServiceData.getGeoPoint().getLongitude()));
		Assert.assertEquals(mediaData, pointOfServiceData.getMapIcon());
		Assert.assertEquals(null, pointOfServiceData.getOpeningHours());
		Assert.assertEquals(POS_STORE_CONTENT, pointOfServiceData.getStoreContent());
		Assert.assertEquals(0, pointOfServiceData.getStoreImages().size());
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(pointOfServiceData.getFeatures().containsKey(FEATURE_CODE)));
	}


	@Test
	public void testConvertMediaIsNull()
	{
		final StoreLocatorFeatureModel feature = mock(StoreLocatorFeatureModel.class);
		final AddressModel addressModel = mock(AddressModel.class);
		final AddressData addressData = mock(AddressData.class);
		final OpeningScheduleModel scheduleModel = mock(OpeningScheduleModel.class);
		final OpeningScheduleData scheduleData = mock(OpeningScheduleData.class);
		final MediaModel mediaModel = mock(MediaModel.class);
		final ImageData mediaData = mock(ImageData.class);
		final MediaContainerModel storeImageContainer = mock(MediaContainerModel.class);
		final MediaFormatModel mediaFormatModel = mock(MediaFormatModel.class);
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		given(pointOfServiceModel.getName()).willReturn(POS_NAME);
		given(pointOfServiceModel.getAddress()).willReturn(addressModel);
		given(pointOfServiceModel.getDescription()).willReturn(POS_DESC);
		given(pointOfServiceModel.getLatitude()).willReturn(POS_LAT);
		given(pointOfServiceModel.getLongitude()).willReturn(POS_LON);
		given(pointOfServiceModel.getMapIcon()).willReturn(mediaModel);
		given(pointOfServiceModel.getOpeningSchedule()).willReturn(scheduleModel);
		given(pointOfServiceModel.getStoreContent()).willReturn(POS_STORE_CONTENT);
		given(pointOfServiceModel.getStoreImage()).willReturn(storeImageContainer);

		final Set<StoreLocatorFeatureModel> features = new HashSet<StoreLocatorFeatureModel>();
		features.add(feature);

		given(pointOfServiceModel.getFeatures()).willReturn(features);
		given(openingScheduleConverter.convert(scheduleModel)).willReturn(scheduleData);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(imageConverter.convert(mediaModel)).willReturn(mediaData);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(IMG_FORMAT)).willReturn(IMG_FORMAT);
		given(mediaService.getFormat(IMG_FORMAT)).willReturn(mediaFormatModel);
		given(mediaContainerService.getMediaForFormat(storeImageContainer, mediaFormatModel)).willReturn(null);
		given(feature.getCode()).willReturn(FEATURE_CODE);

		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		pointOfServicePopulator.populate(pointOfServiceModel, pointOfServiceData);
		Assert.assertNotNull(pointOfServiceData);
		Assert.assertEquals(POS_NAME, pointOfServiceData.getName());
		Assert.assertEquals(addressData, pointOfServiceData.getAddress());
		Assert.assertEquals(POS_DESC, pointOfServiceData.getDescription());
		Assert.assertEquals(POS_LAT, Double.valueOf(pointOfServiceData.getGeoPoint().getLatitude()));
		Assert.assertEquals(POS_LON, Double.valueOf(pointOfServiceData.getGeoPoint().getLongitude()));
		Assert.assertEquals(mediaData, pointOfServiceData.getMapIcon());
		Assert.assertEquals(scheduleData, pointOfServiceData.getOpeningHours());
		Assert.assertEquals(POS_STORE_CONTENT, pointOfServiceData.getStoreContent());
		Assert.assertEquals(0, pointOfServiceData.getStoreImages().size());
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(pointOfServiceData.getFeatures().containsKey(FEATURE_CODE)));
	}
}
