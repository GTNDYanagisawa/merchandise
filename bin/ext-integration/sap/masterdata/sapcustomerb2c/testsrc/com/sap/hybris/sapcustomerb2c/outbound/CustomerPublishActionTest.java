package com.sap.hybris.sapcustomerb2c.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils;


/**
 * Test class for SapPublishCustomerAction class check of all value will be passed and set correctly
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerPublishActionTest implements CustomerConstantsUtils
{

	@InjectMocks
	private final CustomerPublishAction sapPublishCustomerAction = new CustomerPublishAction();

	@Mock
	private ModelService modelService;

	@Mock
	private DefaultStoreSessionFacade storeSessionFacade;

	@Mock
	private LanguageData languageData;


	/**
	 * <b>what to test:</b><br/>
	 * execute Action<br/>
	 * <b>expected result:</b> <li>update SapReplicationInfo property on the customer model</li> <li>check if
	 * customerModel will be transferred to the SendToDataHub class</li> <li>check if baseStoreName will be set correctly
	 * </li> <li>check if session language will be set correctly</li><br/>
	 * <br/>
	 * 
	 * @throws RetryLaterException
	 *            will be raised if action should be again triggered
	 * 
	 */
	@SuppressWarnings("javadoc")
	@Test
	public void checkIfTheCorrectValuesWillBeSend()
	{
		// given
		final CustomerPublishAction spyCustomerPublishAction = spy(sapPublishCustomerAction);

		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);
		doNothing().when(spyCustomerPublishAction).setSapContactId(businessProcessModel);

		final CustomerModel customerModel = new CustomerModel();
		businessProcessModel.setCustomer(customerModel);
		customerModel.setUid(UID);
		given(businessProcessModel.getCustomer()).willReturn(customerModel);

		final CustomerExportService sendCustomerToDataHub = mock(CustomerExportService.class);
		spyCustomerPublishAction.setSendCustomerToDataHub(sendCustomerToDataHub);

		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		given(businessProcessModel.getStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getUid()).willReturn(BASE_STORE);

		spyCustomerPublishAction.setStoreSessionFacade(storeSessionFacade);
		given(spyCustomerPublishAction.getStoreSessionFacade().getCurrentLanguage()).willReturn(languageData);
		given(spyCustomerPublishAction.getStoreSessionFacade().getCurrentLanguage().getIsocode()).willReturn(SESSION_LANGUAGE);
		spyCustomerPublishAction.setModelService(modelService);

		// when
		Transition returnCode = Transition.NOK;
		try
		{
			returnCode = spyCustomerPublishAction.executeAction(businessProcessModel);
		}
		catch (final RetryLaterException retryLaterException)
		{
			assertEquals("RetryLaterException was raised", retryLaterException);
		}

		// then 
		assertNotNull("SapReplicationInfo not filled", customerModel.getSapReplicationInfo());
		verify(sendCustomerToDataHub, times(1)).sendCustomerData(businessProcessModel.getCustomer(), BASE_STORE, SESSION_LANGUAGE);
		assertEquals(Transition.OK, returnCode);

	}
}
