package com.sap.hybris.sapcustomerb2b.inbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


@UnitTest
public class B2BUnitAddressDeletionNotificationTranslatorTest
{

	private static final String FIRST = "first";
	private static final String MESSAGEFUNCTION_DELETE = "003";
	private static final String PUBLIC_KEY = "publicKey";
	private static final String SAP_CUSTOMER_ID = "sapCustomerID";
	private static final String SAP_ADDRESS_USAGE = "sapAddressUsage";
	private static final String SAP_ADDRESS_USAGE_COUNTER = "sapAddressUsageCounter";


	@InjectMocks
	private final B2BUnitAddressDeletionNotificationTranslator b2bUnitAddressDeletionNotificationTranslator = new B2BUnitAddressDeletionNotificationTranslator();

	@Mock
	private final B2BUnitAddressDeletionService b2bUnitAddressDeletionService = mock(B2BUnitAddressDeletionService.class);



	@Before
	public void before()
	{
		b2bUnitAddressDeletionNotificationTranslator.setB2BUnitAddressDeletionService(b2bUnitAddressDeletionService);
	}


	@Test
	public void testTransformationAddressUsageCounterEmptyExpression() throws JaloInvalidParameterException, JaloSecurityException
	{

		try
		{
			// given
			final Item item = mock(Item.class);
			given(item.getAttribute(PUBLIC_KEY)).willReturn(FIRST + "|second|third|last");
			given(item.getAttribute(SAP_CUSTOMER_ID)).willReturn(SAP_CUSTOMER_ID);
			given(item.getAttribute(SAP_ADDRESS_USAGE)).willReturn(SAP_ADDRESS_USAGE);
			given(item.getAttribute(SAP_ADDRESS_USAGE_COUNTER)).willReturn(null);

			// when
			b2bUnitAddressDeletionNotificationTranslator.performImport(MESSAGEFUNCTION_DELETE, item);
		}
		catch (final ImpExException e)
		{
			Assert.fail("exception occured:" + e.getMessage());
		}

		// then
		verify(b2bUnitAddressDeletionService, times(0)).processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, FIRST,
				SAP_CUSTOMER_ID, SAP_ADDRESS_USAGE, SAP_ADDRESS_USAGE_COUNTER);
	}

	@Test
	public void testTransformationAddressUsageEmptyExpression() throws JaloInvalidParameterException, JaloSecurityException
	{

		try
		{
			// given
			final Item item = mock(Item.class);
			given(item.getAttribute(PUBLIC_KEY)).willReturn(FIRST + "|second|third|last");
			given(item.getAttribute(SAP_CUSTOMER_ID)).willReturn(SAP_CUSTOMER_ID);
			given(item.getAttribute(SAP_ADDRESS_USAGE)).willReturn(null);
			given(item.getAttribute(SAP_ADDRESS_USAGE_COUNTER)).willReturn(SAP_ADDRESS_USAGE_COUNTER);

			// when
			b2bUnitAddressDeletionNotificationTranslator.performImport(MESSAGEFUNCTION_DELETE, item);
		}
		catch (final ImpExException e)
		{
			Assert.fail("exception occured:" + e.getMessage());
		}

		//then
		verify(b2bUnitAddressDeletionService, times(0)).processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, FIRST,
				SAP_CUSTOMER_ID, SAP_ADDRESS_USAGE, SAP_ADDRESS_USAGE_COUNTER);
	}

	@Test
	public void testTransformationCustomerEmptyExpression() throws JaloInvalidParameterException, JaloSecurityException
	{

		try
		{
			// given
			final Item item = mock(Item.class);
			given(item.getAttribute(PUBLIC_KEY)).willReturn(FIRST + "|second|third|last");
			given(item.getAttribute(SAP_CUSTOMER_ID)).willReturn(null);
			given(item.getAttribute(SAP_ADDRESS_USAGE)).willReturn(SAP_ADDRESS_USAGE);
			given(item.getAttribute(SAP_ADDRESS_USAGE_COUNTER)).willReturn(SAP_ADDRESS_USAGE_COUNTER);

			// when
			b2bUnitAddressDeletionNotificationTranslator.performImport(MESSAGEFUNCTION_DELETE, item);
		}
		catch (final ImpExException e)
		{
			Assert.fail("exception occured:" + e.getMessage());
		}

		// then
		verify(b2bUnitAddressDeletionService, times(0)).processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, FIRST,
				SAP_CUSTOMER_ID, SAP_ADDRESS_USAGE, SAP_ADDRESS_USAGE_COUNTER);
	}

	@Test
	public void testTransformationB2BUnitEmptyExpression() throws JaloInvalidParameterException, JaloSecurityException
	{

		try
		{
			// given
			final Item item = mock(Item.class);
			given(item.getAttribute(PUBLIC_KEY)).willReturn(null);
			given(item.getAttribute(SAP_CUSTOMER_ID)).willReturn(SAP_CUSTOMER_ID);
			given(item.getAttribute(SAP_ADDRESS_USAGE)).willReturn(SAP_ADDRESS_USAGE);
			given(item.getAttribute(SAP_ADDRESS_USAGE_COUNTER)).willReturn(SAP_ADDRESS_USAGE_COUNTER);

			// when
			b2bUnitAddressDeletionNotificationTranslator.performImport(MESSAGEFUNCTION_DELETE, item);
		}
		catch (final ImpExException e)
		{
			Assert.fail("exception occured:" + e.getMessage());
		}

		// then
		verify(b2bUnitAddressDeletionService, times(0)).processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, FIRST,
				SAP_CUSTOMER_ID, SAP_ADDRESS_USAGE, SAP_ADDRESS_USAGE_COUNTER);
	}

	@Test
	public void testTransformationExpression() throws JaloInvalidParameterException, JaloSecurityException
	{

		try
		{
			// given
			final Item item = mock(Item.class);
			given(item.getAttribute(PUBLIC_KEY)).willReturn(FIRST + "|second|third|last");
			given(item.getAttribute(SAP_CUSTOMER_ID)).willReturn(SAP_CUSTOMER_ID);
			given(item.getAttribute(SAP_ADDRESS_USAGE)).willReturn(SAP_ADDRESS_USAGE);
			given(item.getAttribute(SAP_ADDRESS_USAGE_COUNTER)).willReturn(SAP_ADDRESS_USAGE_COUNTER);

			// when
			b2bUnitAddressDeletionNotificationTranslator.performImport(MESSAGEFUNCTION_DELETE, item);
		}
		catch (final ImpExException e)
		{
			Assert.fail("exception occured:" + e.getMessage());
		}

		// then
		verify(b2bUnitAddressDeletionService, times(1)).processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, FIRST,
				SAP_CUSTOMER_ID, SAP_ADDRESS_USAGE, SAP_ADDRESS_USAGE_COUNTER);
	}

	@Test
	public void testEmptyTransformationExpression()
	{
		try
		{
			// when
			b2bUnitAddressDeletionNotificationTranslator.performImport(null, null);
		}
		catch (final ImpExException e)
		{
			Assert.fail("exception occured:" + e.getMessage());
		}

		// then
		verify(b2bUnitAddressDeletionService, times(0)).processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, FIRST,
				SAP_CUSTOMER_ID, SAP_ADDRESS_USAGE, SAP_ADDRESS_USAGE_COUNTER);
	}

	@Test
	public void testUnknownTransformationExpression()
	{
		try
		{
			// when
			b2bUnitAddressDeletionNotificationTranslator.performImport("Test", null);
		}
		catch (final ImpExException e)
		{
			Assert.fail("exception occured:" + e.getMessage());
		}

		// then
		verify(b2bUnitAddressDeletionService, times(0)).processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, FIRST,
				SAP_CUSTOMER_ID, SAP_ADDRESS_USAGE, SAP_ADDRESS_USAGE_COUNTER);
	}

}
