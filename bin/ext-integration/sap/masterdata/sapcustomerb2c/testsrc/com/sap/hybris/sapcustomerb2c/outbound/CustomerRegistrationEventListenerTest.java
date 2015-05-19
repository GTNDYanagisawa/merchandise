package com.sap.hybris.sapcustomerb2c.outbound;

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.REPLICATEREGISTEREDUSER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Test;


/**
 * Test class for SapPublishCustomerAction class check of all value will be passed and set correctly
 */
@UnitTest
public class CustomerRegistrationEventListenerTest
{

	/**
	 * <b>what to test:</b><br/>
	 * SAP Global configuration exists<br/>
	 * <b>expected result:</b> <li>Business Process should NOT be started</li>
	 * 
	 */
	@Test
	public void checkNoReplicationSAPGobalConfigurationExists()
	{

		// given
		final CustomerRegistrationEventListener customerRegistrationEventListener = new CustomerRegistrationEventListener();
		final CustomerRegistrationEventListener spyCustomerRegistrationEventListener = spy(customerRegistrationEventListener);

		final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService = mock(SAPGlobalConfigurationServiceImpl.class);
		given(sapCoreSAPGlobalConfigurationService.getProperty(REPLICATEREGISTEREDUSER)).willReturn(false);

		final BusinessProcessService businessProcessService = mock(BusinessProcessService.class);
		doReturn(businessProcessService).when(spyCustomerRegistrationEventListener).getBusinessProcessService();

		final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = mock(StoreFrontCustomerProcessModel.class);
		doReturn(storeFrontCustomerProcessModel).when(spyCustomerRegistrationEventListener).createProcess();

		final BaseStoreService baseStoreService = mock(BaseStoreService.class);
		spyCustomerRegistrationEventListener.setBaseStoreService(baseStoreService);

		final BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);

		final ModelService modelService = mock(ModelService.class);
		spyCustomerRegistrationEventListener.setModelService(modelService);

		doNothing().when(modelService).save(storeFrontCustomerProcessModel);
		doNothing().when(businessProcessService).startProcess(storeFrontCustomerProcessModel);

		final RegisterEvent registerEvent = mock(RegisterEvent.class);
		final BaseSiteModel site = new BaseSiteModel();
		given(registerEvent.getSite()).willReturn(site);

		final CustomerModel customerModel = mock(CustomerModel.class);
		given(registerEvent.getCustomer()).willReturn(customerModel);


		//when
		try
		{
			spyCustomerRegistrationEventListener.onEvent(registerEvent);
		}
		catch (final NullPointerException nullPointerException)
		{
			// in case of null pointer exception the "replicateregistereduser" property was in correctly evaluated
			assertNull("BusinessProcess was triggered");
		}

		// then 
		verify(sapCoreSAPGlobalConfigurationService, never()).getProperty(REPLICATEREGISTEREDUSER);
		verify(spyCustomerRegistrationEventListener, never()).createProcess();
		verify(storeFrontCustomerProcessModel, never()).setSite(site);
		verify(storeFrontCustomerProcessModel, never()).setCustomer(customerModel);
		verify(baseStoreService, never()).getCurrentBaseStore();
		verify(storeFrontCustomerProcessModel, never()).setStore(currentBaseStore);
		verify(modelService, never()).save(storeFrontCustomerProcessModel);
		verify(businessProcessService, never()).startProcess(storeFrontCustomerProcessModel);
	}

	/**
	 * <b>what to test:</b><br/>
	 * Replicate Registered User not active<br/>
	 * <b>expected result:</b> <li>Business Process should NOT be started</li>
	 * 
	 */
	@Test
	public void checkNoReplicationIfReplicateRegisteredUserIsNotActive()
	{

		// given
		final CustomerRegistrationEventListener customerRegistrationEventListener = new CustomerRegistrationEventListener();
		final CustomerRegistrationEventListener spyCustomerRegistrationEventListener = spy(customerRegistrationEventListener);

		final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService = mock(SAPGlobalConfigurationServiceImpl.class);
		spyCustomerRegistrationEventListener.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(sapCoreSAPGlobalConfigurationService.getProperty(REPLICATEREGISTEREDUSER)).willReturn(false);

		final BusinessProcessService businessProcessService = mock(BusinessProcessService.class);
		doReturn(businessProcessService).when(spyCustomerRegistrationEventListener).getBusinessProcessService();

		final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = mock(StoreFrontCustomerProcessModel.class);
		doReturn(storeFrontCustomerProcessModel).when(spyCustomerRegistrationEventListener).createProcess();

		final BaseStoreService baseStoreService = mock(BaseStoreService.class);
		spyCustomerRegistrationEventListener.setBaseStoreService(baseStoreService);

		final BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);

		final ModelService modelService = mock(ModelService.class);
		spyCustomerRegistrationEventListener.setModelService(modelService);

		doNothing().when(modelService).save(storeFrontCustomerProcessModel);
		doNothing().when(businessProcessService).startProcess(storeFrontCustomerProcessModel);

		final RegisterEvent registerEvent = mock(RegisterEvent.class);
		final BaseSiteModel site = new BaseSiteModel();
		given(registerEvent.getSite()).willReturn(site);

		final CustomerModel customerModel = mock(CustomerModel.class);
		given(registerEvent.getCustomer()).willReturn(customerModel);


		//when
		try
		{
			spyCustomerRegistrationEventListener.onEvent(registerEvent);
		}
		catch (final NullPointerException nullPointerException)
		{
			// in case of null pointer exception the "replicateregistereduser" property was in correctly evaluated
			assertNull("BusinessProcess was triggered");
		}

		// then 
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(spyCustomerRegistrationEventListener, never()).createProcess();
		verify(storeFrontCustomerProcessModel, never()).setSite(site);
		verify(storeFrontCustomerProcessModel, never()).setCustomer(customerModel);
		verify(baseStoreService, never()).getCurrentBaseStore();
		verify(storeFrontCustomerProcessModel, never()).setStore(currentBaseStore);
		verify(modelService, never()).save(storeFrontCustomerProcessModel);
		verify(businessProcessService, never()).startProcess(storeFrontCustomerProcessModel);
	}

	/**
	 * <b>what to test:</b><br/>
	 * Replicate Registered User active<br/>
	 * <b>expected result:</b> <li>Business Process should be started</li>
	 * 
	 */
	@Test
	public void checkReplicationIfReplicateRegisteredUserIsActive()
	{

		// given
		final CustomerRegistrationEventListener customerRegistrationEventListener = new CustomerRegistrationEventListener();
		final CustomerRegistrationEventListener spyCustomerRegistrationEventListener = spy(customerRegistrationEventListener);

		final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService = mock(SAPGlobalConfigurationServiceImpl.class);
		spyCustomerRegistrationEventListener.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(sapCoreSAPGlobalConfigurationService.getProperty(REPLICATEREGISTEREDUSER)).willReturn(true);

		final BusinessProcessService businessProcessService = mock(BusinessProcessService.class);
		doReturn(businessProcessService).when(spyCustomerRegistrationEventListener).getBusinessProcessService();

		final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = mock(StoreFrontCustomerProcessModel.class);
		doReturn(storeFrontCustomerProcessModel).when(spyCustomerRegistrationEventListener).createProcess();

		final BaseStoreService baseStoreService = mock(BaseStoreService.class);
		spyCustomerRegistrationEventListener.setBaseStoreService(baseStoreService);

		final BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);

		final ModelService modelService = mock(ModelService.class);
		spyCustomerRegistrationEventListener.setModelService(modelService);

		doNothing().when(modelService).save(storeFrontCustomerProcessModel);
		doNothing().when(businessProcessService).startProcess(storeFrontCustomerProcessModel);

		final RegisterEvent registerEvent = mock(RegisterEvent.class);
		final BaseSiteModel site = new BaseSiteModel();
		given(registerEvent.getSite()).willReturn(site);

		final CustomerModel customerModel = mock(CustomerModel.class);
		given(registerEvent.getCustomer()).willReturn(customerModel);

		//when
		try
		{
			spyCustomerRegistrationEventListener.onEvent(registerEvent);
		}
		catch (final NullPointerException nullPointerException)
		{
			assertEquals("BusinessProcess was NOT triggered", false, false);
		}

		// then 
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(spyCustomerRegistrationEventListener, times(1)).createProcess();
		verify(storeFrontCustomerProcessModel, times(1)).setSite(site);
		verify(storeFrontCustomerProcessModel, times(1)).setCustomer(customerModel);
		verify(baseStoreService, times(1)).getCurrentBaseStore();
		verify(storeFrontCustomerProcessModel, times(1)).setStore(currentBaseStore);
		verify(modelService, times(1)).save(storeFrontCustomerProcessModel);
		verify(businessProcessService, times(1)).startProcess(storeFrontCustomerProcessModel);

	}
}
