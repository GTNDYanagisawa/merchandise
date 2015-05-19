package de.hybris.platform.integration.oms.ats.futurestock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.oms.api.inventory.InventoryFacade;
import com.hybris.oms.domain.inventory.FutureItemQuantity;
import com.hybris.oms.domain.inventory.FutureItemQuantityStatus;
import com.hybris.oms.domain.inventory.ItemLocation;
import com.hybris.oms.domain.inventory.ItemLocationFuture;
import com.hybris.oms.domain.types.Quantity;


/**
 * Unit test for {@link DefaultOmsFutureStockService} TODO refix the test with the migrated OMS version.
 */
@Ignore
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOmsFutureStockServiceTest
{
	private DefaultOmsFutureStockService service;

	private List<ItemLocation> itemLocations;

	private final Date dateAsKey = new Date();

	//	@Mock
	//	private final Pageable<ItemLocation> pageableItemLocations = new PagedResults(null, null);

	@Mock
	private InventoryFacade omsInventoryRestClient;

	@Before
	public void setup()
	{
		service = new DefaultOmsFutureStockService();
		service.setInventoryRestClient(omsInventoryRestClient);

		itemLocations = createFakeItemLocationList();
		//		Mockito.when(omsInventoryRestClient.findItemLocationsByQuery(Mockito.isA(ItemLocationQueryObject.class))).thenReturn(
		//				pageableItemLocations);
		//
		//		Mockito.when(pageableItemLocations.getResults()).thenReturn(itemLocations);
	}

	@Test
	public void getFutureAvailability()
	{
		final Map<String, Map<Date, Integer>> futureAvailabilities = service.getFutureAvailability(Arrays
				.asList(new ProductModel()));

		Assert.assertTrue(futureAvailabilities.size() == 1);
		Assert.assertTrue(futureAvailabilities.values().iterator().next().containsValue(Integer.valueOf(10)));
	}

	@Test
	public void getFutureAvailabilityGroupingQuantitiesByDate()
	{
		final ItemLocationFuture secondItemLocationFutureTrue = createFakeItemLocationFuture(true, 5);
		itemLocations.add(secondItemLocationFutureTrue);

		final Map<String, Map<Date, Integer>> futureAvailabilities = service.getFutureAvailability(Arrays
				.asList(new ProductModel()));

		Assert.assertTrue(futureAvailabilities.size() == 1);
		Assert.assertTrue(futureAvailabilities.values().iterator().next().containsValue(Integer.valueOf(15)));
	}

	private List<ItemLocation> createFakeItemLocationList()
	{
		final ItemLocationFuture itemLocationFutureTrue = createFakeItemLocationFuture(true, 10);
		final ItemLocationFuture itemLocationFutureFalse = createFakeItemLocationFuture(false, 0);

		final List<ItemLocation> fakeItemLocations = new ArrayList<>();
		fakeItemLocations.add(itemLocationFutureTrue);
		fakeItemLocations.add(itemLocationFutureFalse);

		return fakeItemLocations;
	}

	private ItemLocationFuture createFakeItemLocationFuture(final boolean future, final int intemQuantity)
	{
		final FutureItemQuantity futureItemQuantity = new FutureItemQuantity();
		futureItemQuantity.setExpectedDeliveryDate(dateAsKey);
		futureItemQuantity.setQuantity(new Quantity("9999", intemQuantity));

		final Map<FutureItemQuantityStatus, FutureItemQuantity> itemQuantities = new HashMap<>();
		itemQuantities.put(new FutureItemQuantityStatus(), futureItemQuantity);

		final ItemLocationFuture itemLocationFutureTrue = new ItemLocationFuture();
		itemLocationFutureTrue.setFuture(future);
		itemLocationFutureTrue.setItemQuantities(itemQuantities);

		return itemLocationFutureTrue;
	}

}
