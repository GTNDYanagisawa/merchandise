/**
 * 
 */
package de.hybris.platform.integration.oms.ats.dataexport.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.oms.ats.dataexport.model.ItemLocation;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;


/**
 * ItemLocation conversion tests
 */
@UnitTest
public class OmsStockLocationConverterTest
{
	private static final String POSTAL_CODE = "PostalCode";
	private static final String STORE_NAME = "StoreName";
	private static final String PHONE_NUMBER = "PhoneNumber";
	private static final String WAREHOUSE_CODE = "warehouseCode";
	private static final Double LONGITUDE = Double.valueOf(3.0);
	private static final Double LATITUDE = Double.valueOf(2.0);
	private static final String COMPANY_NAME = "CompanyName";
	private static final String CITY_NAME = "CityName";
	private static final String BASE_STORE3_UID = "baseStore3";
	private static final String BASE_STORE2_UID = "baseStore2";
	private static final String BASE_STORE1_UID = "baseStore1";
	private static final String BASE_STORES_CSV = "\"" + BASE_STORE1_UID + "," + BASE_STORE2_UID + "," + BASE_STORE3_UID + "\"";
	private static final String COUNTRY_ISOCODE = "DE";
	private static final String REGION_ISOCODE = "FOO";
	private static final String ACTIVE = "true";
	private static final String ADDRESS_LINE2 = "AddressLine2";
	private static final String ADDRESS_LINE1 = "AddressLine1";

	private OmsStockLocationConverter omsStockLocationConverter;

	@Mock
	private WarehouseModel warehouse;

	@Mock
	private AddressModel address;

	@Mock
	private CountryModel country;

	@Mock
	private RegionModel region;

	@Mock
	private PointOfServiceModel pointOfService;


	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		omsStockLocationConverter = new OmsStockLocationConverter();

		Mockito.when(pointOfService.getWarehouses()).thenReturn(Arrays.asList(warehouse));
		Mockito.when(pointOfService.getAddress()).thenReturn(address);
		Mockito.when(pointOfService.getLatitude()).thenReturn(LATITUDE);
		Mockito.when(pointOfService.getLongitude()).thenReturn(LONGITUDE);
		Mockito.when(pointOfService.getName()).thenReturn(STORE_NAME);

		Mockito.when(country.getIsocode()).thenReturn(COUNTRY_ISOCODE);
		Mockito.when(region.getIsocode()).thenReturn(REGION_ISOCODE);

		Mockito.when(address.getLine1()).thenReturn(ADDRESS_LINE1);
		Mockito.when(address.getLine2()).thenReturn(ADDRESS_LINE2);
		Mockito.when(address.getCountry()).thenReturn(country);
		Mockito.when(address.getRegion()).thenReturn(region);
		Mockito.when(address.getTown()).thenReturn(CITY_NAME);
		Mockito.when(address.getCompany()).thenReturn(COMPANY_NAME);
		Mockito.when(address.getPhone1()).thenReturn(PHONE_NUMBER);
		Mockito.when(address.getPostalcode()).thenReturn(POSTAL_CODE);

		final BaseStoreModel baseStore1 = Mockito.mock(BaseStoreModel.class);
		Mockito.when(baseStore1.getUid()).thenReturn(BASE_STORE1_UID);

		final BaseStoreModel baseStore2 = Mockito.mock(BaseStoreModel.class);
		Mockito.when(baseStore2.getUid()).thenReturn(BASE_STORE2_UID);

		final BaseStoreModel baseStore3 = Mockito.mock(BaseStoreModel.class);
		Mockito.when(baseStore3.getUid()).thenReturn(BASE_STORE3_UID);

		final List<BaseStoreModel> baseStores = Arrays.asList(baseStore1, baseStore2, baseStore3);
		Mockito.when(warehouse.getBaseStores()).thenReturn(baseStores);
		Mockito.when(warehouse.getCode()).thenReturn(WAREHOUSE_CODE);
	}

	@Test
	public void shouldPopulate()
	{
		final Message<PointOfServiceModel> source = new Message<PointOfServiceModel>()
		{
			@Override
			public MessageHeaders getHeaders()
			{
				return null;
			}

			@Override
			public PointOfServiceModel getPayload()
			{
				return pointOfService;
			}
		};

		final ItemLocation target = omsStockLocationConverter.convert(source);

		Assert.assertEquals(ACTIVE, target.getActive());
		Assert.assertEquals(ADDRESS_LINE1, target.getAddressLine1());
		Assert.assertEquals(ADDRESS_LINE2, target.getAddressLine2());
		Assert.assertEquals(BASE_STORES_CSV, target.getBaseStores());
		Assert.assertEquals(CITY_NAME, target.getCityName());
		Assert.assertEquals(COMPANY_NAME, target.getCompanyName());
		Assert.assertEquals(COUNTRY_ISOCODE, target.getCountryIso3166Alpha2Code());
		Assert.assertEquals(REGION_ISOCODE, target.getCountrySubentity());
		Assert.assertEquals(LATITUDE.toString(), target.getLatitudeValue());
		Assert.assertEquals(WAREHOUSE_CODE, target.getLocationId());
		Assert.assertEquals(LONGITUDE.toString(), target.getLongitudeValue());
		Assert.assertEquals(PHONE_NUMBER, target.getPhoneNumber());
		Assert.assertEquals(POSTAL_CODE, target.getPostalZone());
		Assert.assertEquals(STORE_NAME, target.getStoreName());
		Assert.assertEquals(WAREHOUSE_CODE, target.getStoreNumber());
	}
}
