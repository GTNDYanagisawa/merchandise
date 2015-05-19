package de.hybris.platform.integration.oms.ats.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OmsCartValidationStrategyTest
{
	private static final String POSTAL_CODE = "PostalCode";
	private static final String PRODUCT_CODE = "pCode";
	private static final String STORE_NAME = "StoreName";
	private static final String PHONE_NUMBER = "PhoneNumber";
	private static final Double LONGITUDE = Double.valueOf(3.0);
	private static final Double LATITUDE = Double.valueOf(2.0);
	private static final String COMPANY_NAME = "CompanyName";
	private static final String CITY_NAME = "CityName";
	private static final String BASE_STORE1_UID = "baseStore1";
	private static final String COUNTRY_ISOCODE = "DE";
	private static final String REGION_ISOCODE = "FOO";
	private static final String ADDRESS_LINE2 = "AddressLine2";
	private static final String ADDRESS_LINE1 = "AddressLine1";
	private static final int REQUESTED_ITEM_QUANTITY = 10;
	private static final int AVAILABLE_ITEM_QUANTITY = 2;

	@Mock
	private AddressModel address;

	@Mock
	private CartEntryModel cartEntryModel;

	@Mock
	private CartModel cartModel;

	@Mock
	private CountryModel country;

	@Mock
	private PointOfServiceModel pointOfService;

	@Mock
	private ProductModel product;

	@Mock
	private RegionModel region;

	@Mock
	private BaseStoreModel store;

	@Mock
	private WarehouseModel warehouse;

	@Mock
	private ModelService modelService;

	@Mock
	private ProductService productService;

	@Mock
	private StockService stockService;

	private OmsCartValidationStrategy omsCartValidationStrategy;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		omsCartValidationStrategy = new OmsCartValidationStrategy();
		omsCartValidationStrategy.setModelService(modelService);
		omsCartValidationStrategy.setProductService(productService);
		omsCartValidationStrategy.setStockService(stockService);

		Mockito.when(pointOfService.getWarehouses()).thenReturn(Arrays.asList(warehouse));
		Mockito.when(pointOfService.getAddress()).thenReturn(address);
		Mockito.when(pointOfService.getLatitude()).thenReturn(LATITUDE);
		Mockito.when(pointOfService.getLongitude()).thenReturn(LONGITUDE);
		Mockito.when(pointOfService.getName()).thenReturn(STORE_NAME);

		Mockito.when(warehouse.getPointsOfService()).thenReturn(Arrays.asList(pointOfService));

		Mockito.when(country.getIsocode()).thenReturn(COUNTRY_ISOCODE);
		Mockito.when(region.getIsocode()).thenReturn(REGION_ISOCODE);
		Mockito.when(product.getCode()).thenReturn(PRODUCT_CODE);

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
		Mockito.when(baseStore1.getWarehouses()).thenReturn(Arrays.asList(warehouse));

		Mockito.when(cartModel.getDeliveryAddress()).thenReturn(address);
		Mockito.when(cartModel.getStore()).thenReturn(baseStore1);

		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
	}

	@Test
	public void shouldRemoveItemFromCartWhenAvailableQuantityIsZero()
	{
		final CommerceCartModification cartModification = omsCartValidationStrategy.validateCartEntry(cartModel, cartEntryModel);

		Mockito.verify(omsCartValidationStrategy.getStockService(), Mockito.times(1)).getTotalStockLevelAmount(product,
				Arrays.asList(warehouse));
		Mockito.verify(omsCartValidationStrategy.getModelService(), Mockito.times(1)).remove(cartEntryModel);
		Mockito.verify(omsCartValidationStrategy.getModelService(), Mockito.times(1)).refresh(cartModel);

		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, cartModification.getStatusCode());
	}

	@Test
	public void shouldReduceItemQuantityWhenAvailableQuantityIsLess()
	{
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(new Long(REQUESTED_ITEM_QUANTITY));
		Mockito.when(stockService.getTotalStockLevelAmount(product, Arrays.asList(warehouse))).thenReturn(AVAILABLE_ITEM_QUANTITY);

		final CommerceCartModification cartModification = omsCartValidationStrategy.validateCartEntry(cartModel, cartEntryModel);

		Mockito.verify(omsCartValidationStrategy.getStockService(), Mockito.times(1)).getTotalStockLevelAmount(product,
				Arrays.asList(warehouse));
		Mockito.verify(omsCartValidationStrategy.getModelService(), Mockito.times(1)).save(cartEntryModel);
		Mockito.verify(omsCartValidationStrategy.getModelService(), Mockito.times(1)).refresh(cartModel);

		Assert.assertEquals(CommerceCartModificationStatus.LOW_STOCK, cartModification.getStatusCode());
	}
}
