package com.sap.hybris.sapcustomerb2b.inbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/**
 * Tests the deletion of an B2BUnit address in case of update or deletion of customer's partner function
 */
@UnitTest
public class B2BUnitAddressDeletionServiceTest
{

	private static final String B2BUNIT_ID_0000099912 = "0000099912";
	private static final String MESSAGEFUNCTION_DELETE = "003";
	private static final String MESSAGEFUNCTION_UPDATE = "004";

	private static final String ADDRESS_USAGE_WE = "WE";
	private static final String ADDRESS_USAGE_RE = "RE";

	private static final String ADDRESS_USAGE_COUNTER_000 = "000";
	private static final String ADDRESS_USAGE_COUNTER_001 = "001";
	private static final String ADDRESS_USAGE_COUNTER_002 = "002";

	private static final String SAP_CUSTOMER_ID_0000099912 = "0000099912";
	private static final String SAP_CUSTOMER_ID_0000000012 = "0000000012";
	private static final String SAP_CUSTOMER_ID_0000000013 = "0000000013";
	private static final String SAP_CUSTOMER_ID_0000000017 = "0000000017";

	@InjectMocks
	private final B2BUnitAddressDeletionService b2bUnitAddressDeletionService = new B2BUnitAddressDeletionService();

	@Mock
	private final FlexibleSearchService flexibleSearchService = mock(FlexibleSearchService.class);

	@Mock
	private final FlexibleSearchQuery flexibleSearchQuery = mock(FlexibleSearchQuery.class);

	private final MyB2BUnitModel b2bUnitModel = new MyB2BUnitModel();

	private final List<AddressModel> b2bUnitAddressesForInput = new ArrayList<AddressModel>();

	private List<AddressModel> b2bUnitAddressesInReturn = new ArrayList<AddressModel>();

	@Mock
	private final AddressModel address_RE_000_0000099912 = mock(AddressModel.class);

	@Mock
	private final AddressModel address_RE_001_0000000017 = mock(AddressModel.class);

	@Mock
	private final AddressModel address_WE_000_0000099912 = mock(AddressModel.class);

	@Mock
	private final AddressModel address_WE_001_0000000012 = mock(AddressModel.class);

	@Mock
	private final AddressModel address_WE_002_0000000013 = mock(AddressModel.class);

	class MyB2BUnitModel extends B2BUnitModel
	{
		@Override
		public void setAddresses(final Collection<AddressModel> value)
		{
			b2bUnitAddressesInReturn = (ArrayList<AddressModel>) value;
		}

		@Override
		public Collection<AddressModel> getAddresses()
		{
			return b2bUnitAddressesForInput;
		}

		@Override
		public String getLocName(final Locale loc)
		{
			return "LocalizedName";
		}
	}

	@Mock
	private final ModelService modelService = mock(ModelService.class);

	/**
	 * Setup test data
	 */
	@Before
	public void setupTests()
	{
		given(address_RE_000_0000099912.getSapAddressUsage()).willReturn(ADDRESS_USAGE_RE);
		given(address_RE_000_0000099912.getSapAddressUsageCounter()).willReturn(ADDRESS_USAGE_COUNTER_000);
		given(address_RE_000_0000099912.getSapCustomerID()).willReturn(SAP_CUSTOMER_ID_0000099912);
		b2bUnitAddressesForInput.add(address_RE_000_0000099912);

		given(address_RE_001_0000000017.getSapAddressUsage()).willReturn(ADDRESS_USAGE_RE);
		given(address_RE_001_0000000017.getSapAddressUsageCounter()).willReturn(ADDRESS_USAGE_COUNTER_001);
		given(address_RE_001_0000000017.getSapCustomerID()).willReturn(SAP_CUSTOMER_ID_0000000017);
		b2bUnitAddressesForInput.add(address_RE_001_0000000017);

		given(address_WE_000_0000099912.getSapAddressUsage()).willReturn(ADDRESS_USAGE_WE);
		given(address_WE_000_0000099912.getSapAddressUsageCounter()).willReturn(ADDRESS_USAGE_COUNTER_000);
		given(address_WE_000_0000099912.getSapCustomerID()).willReturn(SAP_CUSTOMER_ID_0000099912);
		b2bUnitAddressesForInput.add(address_WE_000_0000099912);

		given(address_WE_001_0000000012.getSapAddressUsage()).willReturn(ADDRESS_USAGE_WE);
		given(address_WE_001_0000000012.getSapAddressUsageCounter()).willReturn(ADDRESS_USAGE_COUNTER_001);
		given(address_WE_001_0000000012.getSapCustomerID()).willReturn(SAP_CUSTOMER_ID_0000000012);
		b2bUnitAddressesForInput.add(address_WE_001_0000000012);

		given(address_WE_002_0000000013.getSapAddressUsage()).willReturn(ADDRESS_USAGE_WE);
		given(address_WE_002_0000000013.getSapAddressUsageCounter()).willReturn(ADDRESS_USAGE_COUNTER_002);
		given(address_WE_002_0000000013.getSapCustomerID()).willReturn(SAP_CUSTOMER_ID_0000000013);
		b2bUnitAddressesForInput.add(address_WE_002_0000000013);
	}

	/**
	 * Tests deletion of a B2BUnit addresses in case of customer's partner function change and deletion
	 */
	@Test
	public void testChangeAndDeletionOfPartnerFunction()
	{
		final B2BUnitAddressDeletionService b2bUnitAddressDeletionService = getB2BUnitAddressDeletionService();

		// delete address with SAP address usage = "WE", SAP address usage counter = "000" and SAp customer ID not equals to "0000000012"
		b2bUnitAddressDeletionService.processB2BUnitAddressDeletion(MESSAGEFUNCTION_UPDATE, B2BUNIT_ID_0000099912,
				SAP_CUSTOMER_ID_0000000012, ADDRESS_USAGE_WE, ADDRESS_USAGE_COUNTER_000);
		b2bUnitAddressesForInput.remove(address_WE_000_0000099912);

		// delete all addresses with SAP address usage = "WE" and SAP address usage counter = "001"
		b2bUnitAddressDeletionService.processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, B2BUNIT_ID_0000099912, null,
				ADDRESS_USAGE_WE, ADDRESS_USAGE_COUNTER_001);

		// check whether two addresses are deleted
		Assert.assertEquals(3, b2bUnitAddressesInReturn.size());

		// check whether the right two are deleted
		boolean deletedPartnerFunctionAddressExists = false;
		boolean changedPartnerFunctionAddressExists = false;
		for (final AddressModel b2bUnitAddress : b2bUnitAddressesInReturn)
		{
			final String addressUsage = b2bUnitAddress.getSapAddressUsage();
			final String addressUsageCounter = b2bUnitAddress.getSapAddressUsageCounter();
			final String customerId = b2bUnitAddress.getSapCustomerID();

			// check whether the deleted address in case of customer's partner function deletion exists
			if (ADDRESS_USAGE_WE.equalsIgnoreCase(addressUsage) && ADDRESS_USAGE_COUNTER_001.equalsIgnoreCase(addressUsageCounter))
			{
				deletedPartnerFunctionAddressExists = true;
			}

			// check whether the deleted address in case of customer's partner function change exists
			if (ADDRESS_USAGE_WE.equalsIgnoreCase(addressUsage) && ADDRESS_USAGE_COUNTER_000.equalsIgnoreCase(addressUsageCounter)
					&& SAP_CUSTOMER_ID_0000000012.equalsIgnoreCase(customerId))
			{
				changedPartnerFunctionAddressExists = true;
			}

		}

		Assert.assertFalse("Deleted partner function B2BUnit address already exists", deletedPartnerFunctionAddressExists);
		Assert.assertFalse("Changed partner function B2BUnit address already exists", changedPartnerFunctionAddressExists);
	}

	/**
	 * Tests deletion of a B2BUnit address in case of customer's partner function change
	 */
	@Test
	public void testChangeOfPartnerFunction()
	{
		final B2BUnitAddressDeletionService b2bUnitAddressDeletionService = getB2BUnitAddressDeletionService();

		// delete address with SAP address usage = "WE", SAP address usage counter = "001" and SAp customer ID not equals to "0000000017"
		b2bUnitAddressDeletionService.processB2BUnitAddressDeletion(MESSAGEFUNCTION_UPDATE, B2BUNIT_ID_0000099912,
				SAP_CUSTOMER_ID_0000000017, ADDRESS_USAGE_WE, ADDRESS_USAGE_COUNTER_001);

		// check whether one address is deleted
		Assert.assertEquals(4, b2bUnitAddressesInReturn.size());

		// check whether the right one is deleted
		boolean changedPartnerFunctionAddressExists = false;
		for (final AddressModel b2bUnitAddress : b2bUnitAddressesInReturn)
		{
			final String addressUsage = b2bUnitAddress.getSapAddressUsage();
			final String addressUsageCounter = b2bUnitAddress.getSapAddressUsageCounter();
			final String customerId = b2bUnitAddress.getSapCustomerID();

			// check whether the deleted address in case of customer's partner function change exists
			if (ADDRESS_USAGE_WE.equalsIgnoreCase(addressUsage) && ADDRESS_USAGE_COUNTER_001.equalsIgnoreCase(addressUsageCounter)
					&& SAP_CUSTOMER_ID_0000000017.equalsIgnoreCase(customerId))
			{
				changedPartnerFunctionAddressExists = true;
			}

		}

		Assert.assertFalse("Changed partner function B2BUnit address already exists", changedPartnerFunctionAddressExists);
	}

	/**
	 * Tests deletion of a B2BUnit address in case of customer's partner function deletion
	 */
	@Test
	public void testDeletionOfPartnerFunction()
	{
		final B2BUnitAddressDeletionService b2bUnitAddressDeletionService = getB2BUnitAddressDeletionService();

		// delete all addresses with SAP address usage = "WE" and SAP address usage counter = "002"
		b2bUnitAddressDeletionService.processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, B2BUNIT_ID_0000099912, null,
				ADDRESS_USAGE_WE, ADDRESS_USAGE_COUNTER_002);

		// check whether one address is deleted
		Assert.assertEquals(4, b2bUnitAddressesInReturn.size());

		// check whether the right one is deleted 
		boolean deletedPartnerFunctionAddressExists = false;
		for (final AddressModel b2bUnitAddress : b2bUnitAddressesInReturn)
		{
			final String addressUsage = b2bUnitAddress.getSapAddressUsage();
			final String addressUsageCounter = b2bUnitAddress.getSapAddressUsageCounter();

			// check whether the deleted address in case of customer's partner function deletion exists
			if (ADDRESS_USAGE_WE.equalsIgnoreCase(addressUsage) && ADDRESS_USAGE_COUNTER_002.equalsIgnoreCase(addressUsageCounter))
			{
				deletedPartnerFunctionAddressExists = true;
			}

		}

		Assert.assertFalse("Deleted partner function B2BUnit address already exists", deletedPartnerFunctionAddressExists);
	}

	/**
	 * Tests deletion of a B2BUnit addresses in case of customer's partner function deletion and change
	 */
	@Test
	public void testDeletionOfPartnerFunctionAndChangeOfAnotherPartnerFunction()
	{
		final B2BUnitAddressDeletionService b2bUnitAddressDeletionService = getB2BUnitAddressDeletionService();

		// delete all addresses with SAP address usage = "WE" and SAP address usage counter = "001"
		b2bUnitAddressDeletionService.processB2BUnitAddressDeletion(MESSAGEFUNCTION_DELETE, B2BUNIT_ID_0000099912, null,
				ADDRESS_USAGE_WE, ADDRESS_USAGE_COUNTER_001);
		b2bUnitAddressesForInput.remove(address_WE_001_0000000012);

		// delete address with SAP address usage = "WE", SAP address usage counter = "000" and SAp customer ID not equals to "0000000012"
		b2bUnitAddressDeletionService.processB2BUnitAddressDeletion(MESSAGEFUNCTION_UPDATE, B2BUNIT_ID_0000099912,
				SAP_CUSTOMER_ID_0000000012, ADDRESS_USAGE_WE, ADDRESS_USAGE_COUNTER_000);

		// check whether two addresses are deleted
		Assert.assertEquals(3, b2bUnitAddressesInReturn.size());

		// check whether the right two are deleted
		boolean deletedPartnerFunctionAddressExists = false;
		boolean changedPartnerFunctionAddressExists = false;
		for (final AddressModel b2bUnitAddress : b2bUnitAddressesInReturn)
		{
			final String addressUsage = b2bUnitAddress.getSapAddressUsage();
			final String addressUsageCounter = b2bUnitAddress.getSapAddressUsageCounter();
			final String customerId = b2bUnitAddress.getSapCustomerID();

			// check whether the deleted address in case of customer's partner function deletion exists
			if (ADDRESS_USAGE_WE.equalsIgnoreCase(addressUsage) && ADDRESS_USAGE_COUNTER_001.equalsIgnoreCase(addressUsageCounter))
			{
				deletedPartnerFunctionAddressExists = true;
			}

			// check whether the deleted address in case of customer's partner function change exists
			if (ADDRESS_USAGE_WE.equalsIgnoreCase(addressUsage) && ADDRESS_USAGE_COUNTER_000.equalsIgnoreCase(addressUsageCounter)
					&& SAP_CUSTOMER_ID_0000000012.equalsIgnoreCase(customerId))
			{
				changedPartnerFunctionAddressExists = true;
			}

		}

		Assert.assertFalse("Deleted partner function B2BUnit address already exists", deletedPartnerFunctionAddressExists);
		Assert.assertFalse("Changed partner function B2BUnit address already exists", changedPartnerFunctionAddressExists);
	}

	private B2BUnitAddressDeletionService getB2BUnitAddressDeletionService()
	{
		final B2BUnitAddressDeletionService b2bUnitAddressDeletionServiceSpy = spy(b2bUnitAddressDeletionService);
		given(b2bUnitAddressDeletionServiceSpy.getB2BUnitFlexibleSearchQuery(B2BUNIT_ID_0000099912))
				.willReturn(flexibleSearchQuery);
		given(flexibleSearchService.searchUnique(flexibleSearchQuery)).willReturn(b2bUnitModel);
		b2bUnitAddressDeletionServiceSpy.setFlexibleSearchService(flexibleSearchService);
		b2bUnitAddressDeletionServiceSpy.setModelService(modelService);
		return b2bUnitAddressDeletionServiceSpy;
	}

}
