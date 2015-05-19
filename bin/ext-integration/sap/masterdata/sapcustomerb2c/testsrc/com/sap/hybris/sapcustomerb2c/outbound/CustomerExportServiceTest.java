package com.sap.hybris.sapcustomerb2c.outbound;

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.ADDRESSUSAGE_DE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.ADDRESS_USAGE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.OBJTYPE_KNA1;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.OBJ_TYPE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.PHONE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.STREET;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.hybris.datahub.core.services.DataHubOutboundService;
import com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils;


/**
 * Test class SendCustomerToDataHub
 */
@UnitTest
public class CustomerExportServiceTest implements CustomerConstantsUtils
{

	@InjectMocks
	private final CustomerExportService customerExportService = new CustomerExportService();

	/**
	 * <b>what to test:</b><br/>
	 * <ul>
	 * <li>check if customer, session language, base store name and address is mapped correctly. And Data are send to the
	 * Data Hub</li><br/>
	 * <b>expected result:</b><br/>
	 * <li>everything should be mapped correctly</li>
	 * </ul>
	 */
	@Test
	public void checkIfCustomerIsTransferredCorrectly()
	{

		//given 
		final CustomerExportService spyCustomerExportService = spy(customerExportService);

		final CustomerModel customerModel = mock(CustomerModel.class);
		given(customerModel.getUid()).willReturn(UID);
		given(customerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);

		final TitleModel titleModel = mock(TitleModel.class);
		given(customerModel.getTitle()).willReturn(titleModel);
		given(customerModel.getTitle().getCode()).willReturn(TITLE);

		final CustomerNameStrategy customerNameStrategy = mock(CustomerNameStrategy.class);
		spyCustomerExportService.setCustomerNameStrategy(customerNameStrategy);
		final String[] names = new String[]
		{ FIRSTNAME, LASTNAME };
		given(customerNameStrategy.splitName(customerModel.getName())).willReturn(names);

		spyCustomerExportService.setFeed(FEED);
		final DataHubOutboundService dataHubOutboundService = mock(DataHubOutboundService.class);
		given(spyCustomerExportService.getDataHubOutboundService()).willReturn(dataHubOutboundService);

		// when
		spyCustomerExportService.sendCustomerData(customerModel, BASE_STORE, SESSION_LANGUAGE);

		// then
		verify(spyCustomerExportService, times(1)).sendCustomerData(customerModel, BASE_STORE, SESSION_LANGUAGE, null);

	}

	/**
	 * <b>what to test:</b><br/>
	 * <ul>
	 * <li>check if customer, session language, base store name and address is mapped correctly and in the second doesn't
	 * use the data from the first call</li><br/>
	 * <b>expected result:</b><br/>
	 * <li>everything should be mapped correctly</li>
	 * </ul>
	 */
	@Test
	public void checkIfCustomerIsTransferredCorrectlyInTwoCalls()
	{

		//given 
		final CustomerExportService spyCustomerExportService = spy(customerExportService);

		final CustomerModel customerModel = mock(CustomerModel.class);
		given(customerModel.getUid()).willReturn(UID);
		given(customerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);

		final TitleModel titleModel = mock(TitleModel.class);
		given(customerModel.getTitle()).willReturn(titleModel);
		given(customerModel.getTitle().getCode()).willReturn(TITLE);
		final String name = CUSTOMER_ID;
		given(customerModel.getName()).willReturn(name);

		final HashMap<String, Object> target = new HashMap<String, Object>();
		given(spyCustomerExportService.getTarget()).willReturn(target);

		final DataHubOutboundService dataHubOutboundService = mock(DataHubOutboundService.class);
		given(spyCustomerExportService.getDataHubOutboundService()).willReturn(dataHubOutboundService);
		spyCustomerExportService.setDataHubOutboundService(dataHubOutboundService);

		final CustomerNameStrategy customerNameStrategy = mock(CustomerNameStrategy.class);
		spyCustomerExportService.setCustomerNameStrategy(customerNameStrategy);
		final String[] names = new String[]
		{ FIRSTNAME, LASTNAME };
		given(customerNameStrategy.splitName(customerModel.getName())).willReturn(names);

		spyCustomerExportService.setFeed(FEED);

		// set an empty addressModel
		final AddressModel addressModel = new AddressModel();

		// when
		spyCustomerExportService.sendCustomerData(customerModel, BASE_STORE, SESSION_LANGUAGE, addressModel);

		// then
		// indicates that a new instance of the target map was requested
		verify(spyCustomerExportService, times(1)).getTarget();
		// addressModel is empty then country is null
		assertEquals(null, target.get(COUNTRY));


		// when
		spyCustomerExportService.sendCustomerData(customerModel, BASE_STORE, SESSION_LANGUAGE);

		// then
		// indicates that a second new instance of the target map was requested
		verify(spyCustomerExportService, times(2)).getTarget();
		// if addressModel is null then default country will be set
		assertEquals(COUNTRY_DE, target.get(COUNTRY));

	}

	/**
	 * <b>what to test:</b><br/>
	 * <ul>
	 * *
	 * <li>check if customer, session language, base store name is mapped correctly</li><br/>
	 * <b>expected result:</b><br/>
	 * <li>everything should be mapped correctly</li>
	 * </ul>
	 */
	@Test
	public void checkIfMapIsCorrecltyForCustomer()
	{

		// given
		final CustomerModel customerModel = mock(CustomerModel.class);
		given(customerModel.getUid()).willReturn(UID);
		given(customerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);

		final CustomerNameStrategy customerNameStrategy = mock(CustomerNameStrategy.class);

		customerExportService.setCustomerNameStrategy(customerNameStrategy);
		final String[] names =
		{ FIRSTNAME, LASTNAME };
		given(customerNameStrategy.splitName(customerModel.getName())).willReturn(names);
		final TitleModel titleModel = mock(TitleModel.class);

		given(customerModel.getTitle()).willReturn(titleModel);
		given(customerModel.getTitle().getCode()).willReturn(TITLE);
		final Map<String, Object> target = new HashMap<String, Object>();

		// when
		customerExportService.prepareCustomerData(customerModel, BASE_STORE, SESSION_LANGUAGE, target);

		// then
		assertEquals(10, target.size());
		assertEquals(CUSTOMER_ID, target.get(CUSTOMER_ID));
		assertEquals(CONTACT_ID, target.get(CONTACT_ID));
		assertEquals(FIRSTNAME, target.get(FIRSTNAME));
		assertEquals(LASTNAME, target.get(LASTNAME));
		assertEquals(SESSION_LANGUAGE, target.get(SESSION_LANGUAGE));
		assertEquals(TITLE, target.get(TITLE));
		assertEquals(BASE_STORE, target.get(BASE_STORE));
		assertEquals(OBJTYPE_KNA1, target.get(OBJ_TYPE));
		assertEquals(ADDRESSUSAGE_DE, target.get(ADDRESS_USAGE));

	}


	/**
	 * <b>what to test:</b><br/>
	 * <ul>
	 * <li>check if address is mapped correctly</li><br/>
	 * <b>expected result:</b><br/>
	 * <li>everything should be mapped correctly</li>
	 * </ul>
	 */
	@Test
	public void checkIfMapIsCorrecltyForAddress()
	{

		// given
		final AddressModel addressModel = mock(AddressModel.class);

		final CountryModel countryModel = mock(CountryModel.class);
		given(addressModel.getCountry()).willReturn(countryModel);
		given(countryModel.getIsocode()).willReturn(COUNTRY);
		given(addressModel.getStreetname()).willReturn(STREET);
		given(addressModel.getPhone1()).willReturn(PHONE);
		given(addressModel.getFax()).willReturn(FAX);
		given(addressModel.getTown()).willReturn(TOWN);
		given(addressModel.getPostalcode()).willReturn(POSTALCODE);
		given(addressModel.getStreetnumber()).willReturn(STREETNUMBER);
		final RegionModel regionModel = mock(RegionModel.class);
		given(addressModel.getRegion()).willReturn(regionModel);
		given(addressModel.getRegion().getIsocode()).willReturn(REGION);
		final Map<String, Object> target = new HashMap<String, Object>();

		// when
		customerExportService.prepareAddressData(addressModel, target);

		// then
		assertEquals(8, target.size());
		assertEquals(COUNTRY, target.get(COUNTRY));
		assertEquals(STREET, target.get(STREET));
		assertEquals(PHONE, target.get(PHONE));
		assertEquals(FAX, target.get(FAX));
		assertEquals(TOWN, target.get(TOWN));
		assertEquals(POSTALCODE, target.get(POSTALCODE));
		assertEquals(STREETNUMBER, target.get(STREETNUMBER));
		assertEquals(REGION, target.get(REGION));

	}

}
