package de.hybris.platform.integration.oms.order.service.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.DeliveryFromAddressStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
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

import com.hybris.oms.api.fulfillment.SourceSimulationFacade;
import com.hybris.oms.domain.address.Address;
import com.hybris.oms.domain.order.jaxb.SourceSimulationParameter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSourcingServiceTest
{
	private final DefaultSourcingService sourcingService = new DefaultSourcingService();
	@Mock
	private SourceSimulationFacade omsSourceSimulationRestClient;
	@Mock
	private Converter<AbstractOrderModel, SourceSimulationParameter> sourceSimulationParameterConverter;
	@Mock
	private Converter<Address, AddressModel> omsAddressReverseConverter;
	@Mock
	private OndemandHystrixCommandConfiguration hystrixCommandConfig;
	@Mock
	private DeliveryFromAddressStrategy defaultDeliveryFromAddressStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel currentBaseStore;
	@Mock
	private PointOfServiceModel pointOfService;
	@Mock
	private AddressModel pointOfServiceAddress;


	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this.getClass());
		sourcingService.setBaseStoreService(baseStoreService);
		sourcingService.setModelService(modelService);
		sourcingService.setDefaultDeliveryFromAddressStrategy(defaultDeliveryFromAddressStrategy);
		sourcingService.setHystrixCommandConfig(hystrixCommandConfig);
		sourcingService.setOmsAddressReverseConverter(omsAddressReverseConverter);
		sourcingService.setSourceSimulationParameterConverter(sourceSimulationParameterConverter);
		sourcingService.setOmsSourceSimulationRestClient(omsSourceSimulationRestClient);

		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);
		Mockito.when(currentBaseStore.getDefaultDeliveryOrigin()).thenReturn(pointOfService);
		Mockito.when(pointOfService.getAddress()).thenReturn(pointOfServiceAddress);

	}

	@Test
	public void shouldGetBaseStoreAddress()
	{
		final AddressModel result = sourcingService.getSourcingLocationAddressFromStore(new OrderModel());
		Assert.assertThat(result, CoreMatchers.equalTo(pointOfServiceAddress));
	}

	@Test
	public void shouldGetBestPOSAddressWith1Entry()
	{
		final PointOfServiceModel pos1 = new PointOfServiceModel();
		final AddressModel address1 = new AddressModel();
		pos1.setAddress(address1);

		final OrderModel order = new OrderModel();
		order.setEntries(new ArrayList<AbstractOrderEntryModel>());
		final OrderEntryModel entry1 = new OrderEntryModel();
		entry1.setDeliveryPointOfService(pos1);
		entry1.setTotalPrice(Double.valueOf(10));
		order.getEntries().add(entry1);

		final AddressModel result = sourcingService.getSourcingLocationAddressFromBestPOS(order);
		Assert.assertThat(result, CoreMatchers.equalTo(address1));


	}


	@Test
	public void shouldGetBestPOSAddressWith2Entries()
	{
		final PointOfServiceModel pos1 = new PointOfServiceModel();
		final AddressModel address1 = new AddressModel();
		pos1.setAddress(address1);

		final PointOfServiceModel pos2 = new PointOfServiceModel();
		final AddressModel address2 = new AddressModel();
		pos2.setAddress(address2);

		final OrderModel order = new OrderModel();
		order.setEntries(new ArrayList<AbstractOrderEntryModel>());
		final OrderEntryModel entry1 = new OrderEntryModel();
		entry1.setDeliveryPointOfService(pos1);
		entry1.setTotalPrice(Double.valueOf(10));
		order.getEntries().add(entry1);

		final OrderEntryModel entry2 = new OrderEntryModel();
		entry2.setDeliveryPointOfService(pos2);
		entry2.setTotalPrice(Double.valueOf(15));
		order.getEntries().add(entry2);


		final AddressModel result = sourcingService.getSourcingLocationAddressFromBestPOS(order);
		Assert.assertThat(result, CoreMatchers.equalTo(address2));



	}

	@Test
	public void shouldGetBestPOSAddressWith3Entries()
	{
		final PointOfServiceModel pos1 = new PointOfServiceModel();
		final AddressModel address1 = new AddressModel();
		pos1.setAddress(address1);

		final PointOfServiceModel pos2 = new PointOfServiceModel();
		final AddressModel address2 = new AddressModel();
		pos2.setAddress(address2);

		final OrderModel order = new OrderModel();
		order.setEntries(new ArrayList<AbstractOrderEntryModel>());
		final OrderEntryModel entry1 = new OrderEntryModel();
		entry1.setDeliveryPointOfService(pos1);
		entry1.setTotalPrice(Double.valueOf(10));
		order.getEntries().add(entry1);

		final OrderEntryModel entry2 = new OrderEntryModel();
		entry2.setDeliveryPointOfService(pos2);
		entry2.setTotalPrice(Double.valueOf(15));
		order.getEntries().add(entry2);

		final OrderEntryModel entry3 = new OrderEntryModel();
		entry3.setDeliveryPointOfService(pos1);
		entry3.setTotalPrice(Double.valueOf(7));
		order.getEntries().add(entry3);



		final AddressModel result = sourcingService.getSourcingLocationAddressFromBestPOS(order);
		Assert.assertThat(result, CoreMatchers.equalTo(address1));

	}


}
