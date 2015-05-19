package com.sap.hybris.sapcustomerb2b.outbound;

import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_B2BUNIT;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_EMAIL;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_FIRST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_LAST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_TITLE_CODE;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.CUSTOMER_ID;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_CUSTOMER_ID;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_FIRST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_LAST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_TITLE;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_UID;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.RAW_HYBRIS_B2B_CUSTOMER;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * JUnit Test for class DefaultCustomerInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 * 
 */
@UnitTest
public class B2BCustomerExportServiceTest
{

	@InjectMocks
	private final B2BCustomerExportService b2bCustomerExportService = new B2BCustomerExportService();

	@Mock
	private final CustomerNameStrategy customerNameStrategy = mock(CustomerNameStrategy.class);

	@Mock
	private final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);

	@Mock
	private final DataHubOutboundService dataHubOutboundService = mock(DataHubOutboundService.class);


	/**
	 * Check if send call to datahub works correct
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testSendB2BCustomerData() throws InterceptorException
	{
		// given
		mockBaseCustomerData();

		final List<Map<String, Object>> b2bCustomerData = new ArrayList<>();
		b2bCustomerData.add(b2bCustomerExportService.prepareB2BCustomerData(b2bCustomerModel));
		b2bCustomerExportService.setDataHubOutboundService(dataHubOutboundService);

		// when
		b2bCustomerExportService.sendCustomerToDataHub(b2bCustomerData);

		// then
		try
		{
			verify(dataHubOutboundService, times(1)).sendToDataHub(b2bCustomerExportService.getFeed(), RAW_HYBRIS_B2B_CUSTOMER,
					b2bCustomerData);
		}
		catch (final DataHubOutboundException e)
		{
			fail("Error processing sending data to Data Hub. DataHubOutboundException: " + e.getMessage());
		}
		catch (final DataHubCommunicationException e)
		{
			fail("Error processing sending data to Data Hub. DataHubCommunicationException: " + e.getMessage());
		}
	}

	/**
	 * Check if send call to datahub is not executed for empty customer data (null)
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNotSendEmptyB2BCustomerDataNull() throws InterceptorException
	{
		// given
		final List<Map<String, Object>> b2bCustomerData = null;

		// when
		b2bCustomerExportService.sendCustomerToDataHub(b2bCustomerData);

		// then
		try
		{
			verify(dataHubOutboundService, times(0)).sendToDataHub(b2bCustomerExportService.getFeed(), RAW_HYBRIS_B2B_CUSTOMER,
					b2bCustomerData);
		}
		catch (final DataHubOutboundException e)
		{
			fail("Error processing sending data to Data Hub. DataHubOutboundException: " + e.getMessage());
		}
		catch (final DataHubCommunicationException e)
		{
			fail("Error processing sending data to Data Hub. DataHubCommunicationException: " + e.getMessage());
		}
	}

	/**
	 * Check if send call to datahub is not executed for empty customer data (empty arrayList)
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNotSendEmptyB2BCustomerDataEmptyArrayList() throws InterceptorException
	{
		// given
		final List<Map<String, Object>> b2bCustomerData = new ArrayList<>();
		b2bCustomerExportService.sendCustomerToDataHub(b2bCustomerData);
		try
		{
			verify(dataHubOutboundService, times(0)).sendToDataHub(b2bCustomerExportService.getFeed(), RAW_HYBRIS_B2B_CUSTOMER,
					b2bCustomerData);
		}
		catch (final DataHubOutboundException e)
		{
			fail("Error processing sending data to Data Hub. DataHubOutboundException: " + e.getMessage());
		}
		catch (final DataHubCommunicationException e)
		{
			fail("Error processing sending data to Data Hub. DataHubCommunicationException: " + e.getMessage());
		}

	}

	/**
	 * Check if the preparation of customer data works correct
	 * <ul>
	 * <li>
	 * UID is set to email Hans.Meiser@sap.com</li>
	 * <li>
	 * FIRST_NAME is set to Hans</li>
	 * <li>
	 * LAST_NAME is set to Meiser</li>
	 * <li>
	 * TITLE_CODE is set to 0001</li>
	 * <li>
	 * LANGUAGE is set to de</li>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testPrepareB2BCustomerData() throws InterceptorException
	{
		// given
		mockBaseCustomerData();

		final TitleModel title = mock(TitleModel.class);
		given(title.getCode()).willReturn(B2BCUSTOMER_TITLE_CODE);
		given(b2bCustomerModel.getTitle()).willReturn(title);

		final LanguageModel languageModel = mock(LanguageModel.class);
		given(languageModel.getIsocode()).willReturn(B2BCUSTOMER_SESSION_LANGUAGE);
		given(b2bCustomerModel.getSessionLanguage()).willReturn(languageModel);

		// when
		final Map<String, Object> b2bCustomerData = b2bCustomerExportService.prepareB2BCustomerData(b2bCustomerModel);

		// then
		checkBaseCustomerData(b2bCustomerData);
		Assert.assertEquals(b2bCustomerData.get(KEY_SESSION_LANGUAGE), B2BCUSTOMER_SESSION_LANGUAGE);
		Assert.assertEquals(b2bCustomerData.get(KEY_TITLE), B2BCUSTOMER_TITLE_CODE);
	}


	/**
	 * Check if the preparation of customer data with missing language code is working
	 * <ul>
	 * <li>
	 * UID is set to email Hans.Meiser@sap.com</li>
	 * <li>
	 * FIRST_NAME is set to Hans</li>
	 * <li>
	 * LAST_NAME is set to Meiser</li>
	 * <li>
	 * TITLE_CODE is set to 0001</li>
	 * <li>
	 * LANGUAGE is not set</li>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testPrepareB2BCustomerDataNoLnaguage() throws InterceptorException
	{
		// given
		mockBaseCustomerData();

		final TitleModel title = mock(TitleModel.class);
		given(title.getCode()).willReturn(B2BCUSTOMER_TITLE_CODE);
		given(b2bCustomerModel.getTitle()).willReturn(title);

		// when
		final Map<String, Object> b2bCustomerData = b2bCustomerExportService.prepareB2BCustomerData(b2bCustomerModel);

		// then
		checkBaseCustomerData(b2bCustomerData);

		Assert.assertEquals(b2bCustomerData.get(KEY_TITLE), B2BCUSTOMER_TITLE_CODE);
		Assert.assertEquals(b2bCustomerData.get(KEY_SESSION_LANGUAGE), null);
	}

	/**
	 * Check if the preparation of customer data with missing language code is working
	 * <ul>
	 * <li>
	 * UID is set to email Hans.Meiser@sap.com</li>
	 * <li>
	 * FIRST_NAME is set to Hans</li>
	 * <li>
	 * LAST_NAME is set to Meiser</li>
	 * <li>
	 * TITLE_CODE is not set</li>
	 * <li>
	 * LANGUAGE is not set</li>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testPrepareB2BCustomerDataNoLnaguageNoTitleCode() throws InterceptorException
	{
		// given
		mockBaseCustomerData();

		// when
		final Map<String, Object> b2bCustomerData = b2bCustomerExportService.prepareB2BCustomerData(b2bCustomerModel);

		// then
		checkBaseCustomerData(b2bCustomerData);

		Assert.assertEquals(b2bCustomerData.get(KEY_TITLE), null);
		Assert.assertEquals(b2bCustomerData.get(KEY_SESSION_LANGUAGE), null);
	}

	protected void mockBaseCustomerData()
	{
		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		given(b2bCustomerModel.getDefaultB2BUnit()).willReturn(b2bUnit);
		given(b2bCustomerModel.getDefaultB2BUnit().getUid()).willReturn(B2BCUSTOMER_B2BUNIT);
		given(b2bCustomerModel.getUid()).willReturn(B2BCUSTOMER_EMAIL);
		given(b2bCustomerModel.getName()).willReturn(B2BCUSTOMER_NAME);
		final String[] names = new String[]
		{ B2BCUSTOMER_FIRST_NAME, B2BCUSTOMER_LAST_NAME };
		given(customerNameStrategy.splitName(b2bCustomerModel.getName())).willReturn(names);
		b2bCustomerExportService.setCustomerNameStrategy(customerNameStrategy);
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID);
	}

	protected void checkBaseCustomerData(final Map<String, Object> b2bCustomerData)
	{
		Assert.assertFalse(b2bCustomerData.isEmpty());
		Assert.assertEquals(b2bCustomerData.get(KEY_UID), B2BCUSTOMER_EMAIL);
		Assert.assertEquals(b2bCustomerData.get(KEY_CUSTOMER_ID), CUSTOMER_ID);
		Assert.assertEquals(b2bCustomerData.get(KEY_FIRST_NAME), B2BCUSTOMER_FIRST_NAME);
		Assert.assertEquals(b2bCustomerData.get(KEY_LAST_NAME), B2BCUSTOMER_LAST_NAME);
	}
}
