package com.sap.hybris.sapcustomerb2c.inbound;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils;


/**
 * Test of CustomerImportService
 */
@UnitTest
public class CustomerImportServiceTest
{

	@InjectMocks
	private final CustomerImportService customerImportService = new CustomerImportService();

	/**
	 * test processed in MyEventSerivce.publishEvent
	 */
	@Test
	public void testEventPublishing()
	{
		// given
		final CustomerImportService spyCustomerImportService = spy(customerImportService);
		final CustomerModel customerModel = mock(CustomerModel.class);
		doReturn(customerModel).when(spyCustomerImportService).readCustomer(CustomerConstantsUtils.CUSTOMER_ID);

		final ModelService modelService = mock(ModelService.class);
		spyCustomerImportService.setModelService(modelService);
		doNothing().when(modelService).save(customerModel);

		final EventService eventService = mock(EventService.class);
		spyCustomerImportService.setEventService(eventService);

		final CustomerReplicationEvent customerReplicationEvent = mock(CustomerReplicationEvent.class);
		when(spyCustomerImportService.createCustomerReplicationEvent(CustomerConstantsUtils.CUSTOMER_ID)).thenReturn(
				customerReplicationEvent);
		doNothing().when(eventService).publishEvent(customerReplicationEvent);

		// when
		spyCustomerImportService.processConsumerReplicationNotificationFromHub(CustomerConstantsUtils.CUSTOMER_ID);

		// then
		verify(customerModel, times(1)).setSapConsumerID(CustomerConstantsUtils.CUSTOMER_ID);
		verify(customerModel, times(1)).setSapIsReplicated(true);
		verify(eventService, times(1)).publishEvent(customerReplicationEvent);

	}
}
