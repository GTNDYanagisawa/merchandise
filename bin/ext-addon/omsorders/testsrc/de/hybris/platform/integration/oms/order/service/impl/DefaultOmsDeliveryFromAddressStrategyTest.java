package de.hybris.platform.integration.oms.order.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.oms.order.service.SourcingService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOmsDeliveryFromAddressStrategyTest
{
	private final DefaultOmsDeliveryFromAddressStrategy strategy = new DefaultOmsDeliveryFromAddressStrategy();
	@Mock
	private SourcingService sourcingService;
	@Mock
	private AddressModel addressFromOms;
	@Mock
	private AddressModel addressFromPOS;
	@Mock
	private AddressModel addressFromStore;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this.getClass());
		strategy.setSourcingService(sourcingService);
		strategy.setCallToOmsAllowed(true);
		Mockito.when(sourcingService.getSourcingLocationAddressFromOms(Mockito.any(OrderModel.class))).thenReturn(addressFromOms);
		Mockito.when(sourcingService.getSourcingLocationAddressFromBestPOS(Mockito.any(OrderModel.class))).thenReturn(
				addressFromPOS);
		Mockito.when(sourcingService.getSourcingLocationAddressFromStore(Mockito.any(OrderModel.class))).thenReturn(
				addressFromStore);

		Assert.assertTrue(sourcingService.getSourcingLocationAddressFromOms(new OrderModel()).equals(addressFromOms));
		Assert.assertTrue(sourcingService.getSourcingLocationAddressFromBestPOS(new OrderModel()).equals(addressFromPOS));
		Assert.assertTrue(sourcingService.getSourcingLocationAddressFromStore(new OrderModel()).equals(addressFromStore));
	}

	@Test
	public void shouldGetOrderFromOMS()
	{
		final OrderModel order = new OrderModel();
		order.setEntries(new ArrayList<AbstractOrderEntryModel>());
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setTotalPrice(Double.valueOf(100));
		order.getEntries().add(orderEntryModel);

		final AddressModel address = strategy.getDeliveryFromAddressForOrder(order);
		Assert.assertThat(address, CoreMatchers.equalTo(addressFromOms));
	}

	@Test
	public void shouldGetOrderFromPOS()
	{
		final OrderModel order = new OrderModel();
		order.setEntries(new ArrayList<AbstractOrderEntryModel>());
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setTotalPrice(Double.valueOf(100));
		orderEntryModel.setDeliveryPointOfService(new PointOfServiceModel());
		order.getEntries().add(orderEntryModel);

		final AddressModel address = strategy.getDeliveryFromAddressForOrder(order);
		Assert.assertThat(address, CoreMatchers.equalTo(addressFromPOS));
	}

	@Test
	public void shouldGetOrderFromStore()
	{
		final OrderModel order = new OrderModel();
		order.setEntries(new ArrayList<AbstractOrderEntryModel>());
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setTotalPrice(Double.valueOf(100));
		order.getEntries().add(orderEntryModel);

		Mockito.when(sourcingService.getSourcingLocationAddressFromOms(Mockito.any(AbstractOrderModel.class))).thenReturn(null);

		final AddressModel address = strategy.getDeliveryFromAddressForOrder(order);
		Assert.assertThat(address, CoreMatchers.equalTo(addressFromStore));
	}

	@Test
	public void shouldGetOrderFromSoreWhenOmsNotAllowed()
	{
		strategy.setCallToOmsAllowed(false);
		final OrderModel order = new OrderModel();
		order.setEntries(new ArrayList<AbstractOrderEntryModel>());
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setTotalPrice(Double.valueOf(100));
		order.getEntries().add(orderEntryModel);

		final AddressModel address = strategy.getDeliveryFromAddressForOrder(order);
		Assert.assertThat(address, CoreMatchers.equalTo(addressFromStore));
	}


}
