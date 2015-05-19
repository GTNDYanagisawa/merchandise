package com.sap.hybris.sapcustomerb2c.outbound;

import static com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils.CONTACT_ID;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.REPLICATEREGISTEREDUSER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


/**
 * JUnit Test for class DefaultCustomerInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 * 
 */
@UnitTest
public class DefaultAddressInterceptorTest
{
	@InjectMocks
	private final DefaultAddressInterceptor defaultAddressInterceptor = new DefaultAddressInterceptor();

	@Mock
	private final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService = mock(SAPGlobalConfigurationServiceImpl.class);
	@Mock
	private final AddressModel addressModel = mock(AddressModel.class);
	@Mock
	private final AddressModel shipmentAddress = mock(AddressModel.class);
	@Mock
	private final InterceptorContext ctx = mock(InterceptorContext.class);
	@Mock
	private final BaseStoreService baseStoreService = mock(BaseStoreService.class);
	@Mock
	private final DefaultStoreSessionFacade storeSessionFacade = mock(DefaultStoreSessionFacade.class);
	@Mock
	private final CustomerExportService customerExportService = mock(CustomerExportService.class);
	@Mock
	private final CustomerModel customerModel = mock(CustomerModel.class);

	/**
	 * Check if the interceptor call the customerExportService exactly one time
	 * <ul>
	 * <li>
	 * REPLICATEREGISTEREDUSER is set to true</li>
	 * <li>
	 * defaultShipmentAddress is not null</li>
	 * <li>
	 * defaultShipmentAddress is the current changed address
	 * <li>
	 * the following fields should be modified
	 * <ul>
	 * <li>AddressModel.COUNTRY</li>
	 * <li>AddressModel.STREETNAME</li>
	 * <li>AddressModel.PHONE1</li>
	 * <li>AddressModel.FAX</li>
	 * <li>AddressModel.TOWN</li>
	 * <li>AddressModel.POSTALCODE</li>
	 * <li>AddressModel.STREETNUMBER</li>
	 * <li>AddressModel.REGION</li>
	 * </ul>
	 * </li></li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportData() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(((CustomerModel) addressModel.getOwner()).getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(ctx.isModified(addressModel, AddressModel.STREETNAME)).willReturn(true);
		given(ctx.isModified(addressModel, AddressModel.PHONE1)).willReturn(true);
		given(ctx.isModified(addressModel, AddressModel.FAX)).willReturn(true);
		given(ctx.isModified(addressModel, AddressModel.TOWN)).willReturn(true);
		given(ctx.isModified(addressModel, AddressModel.POSTALCODE)).willReturn(true);
		given(ctx.isModified(addressModel, AddressModel.STREETNUMBER)).willReturn(true);
		given(ctx.isModified(addressModel, AddressModel.REGION)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * sapGlobalConfiguration not available</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportSAPGlobalConfigurationNoAvailable() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(1)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, never()).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, never()).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, never()).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * REPLICATEREGISTEREDUSER is set to false</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportReplicateRegisterdUserIsFalse() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(false);
		given(addressModel.getOwner()).willReturn(customerModel);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, never()).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, never()).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * Address owner is not customer, but a b2bUnit</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportAddressOwnerIsNotACustomer() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		final B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
		given(addressModel.getOwner()).willReturn(b2bUnitModel);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(addressModel, times(1)).getOwner();
		verify(customerModel, never()).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, never()).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * defaultShipmentAddress is null</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportDefaultShipmentAddressNull() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(null);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, never()).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * an other address was changed not the defaultShipmentAddress</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportDefaultShipmentAddressIsNotChangedAddress() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(((CustomerModel) addressModel.getOwner()).getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		doReturn(false).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * the following fields should are not modified
	 * <ul>
	 * <li>AddressModel.COUNTRY</li>
	 * <li>AddressModel.STREETNAME</li>
	 * <li>AddressModel.PHONE1</li>
	 * <li>AddressModel.FAX</li>
	 * <li>AddressModel.TOWN</li>
	 * <li>AddressModel.POSTALCODE</li>
	 * <li>AddressModel.STREETNUMBER</li>
	 * <li>AddressModel.REGION</li>
	 * </ul>
	 * </li></li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportNoFieldModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(((CustomerModel) addressModel.getOwner()).getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(false);
		given(ctx.isModified(addressModel, AddressModel.STREETNAME)).willReturn(false);
		given(ctx.isModified(addressModel, AddressModel.PHONE1)).willReturn(false);
		given(ctx.isModified(addressModel, AddressModel.FAX)).willReturn(false);
		given(ctx.isModified(addressModel, AddressModel.TOWN)).willReturn(false);
		given(ctx.isModified(addressModel, AddressModel.POSTALCODE)).willReturn(false);
		given(ctx.isModified(addressModel, AddressModel.STREETNUMBER)).willReturn(false);
		given(ctx.isModified(addressModel, AddressModel.REGION)).willReturn(false);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, never()).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, never()).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.COUNTRY modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportCountryModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.STREETNAME modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportStreetnameModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.STREETNAME)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.PHONE1 modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportPhoneModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.PHONE1)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.FAX modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportFaxModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.FAX)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.TOWN modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportTownModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.TOWN)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.POSTALCODE modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportPostalcodeModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.POSTALCODE)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.STREETNUMBER modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportStreetnumberModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.STREETNUMBER)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>Only AddressModel.REGION modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportRegionModified() throws InterceptorException
	{
		// given
		final DefaultAddressInterceptor spyDefaultAddressInterceptor = spy(defaultAddressInterceptor);
		spyDefaultAddressInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(spyDefaultAddressInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.REGION)).willReturn(true);

		doReturn(true).when(spyDefaultAddressInterceptor).isDefaultShipmentAddress(addressModel);
		spyDefaultAddressInterceptor.setBaseStoreService(baseStoreService);
		spyDefaultAddressInterceptor.setStoreSessionFacade(storeSessionFacade);
		spyDefaultAddressInterceptor.setCustomerExportService(customerExportService);

		// when
		spyDefaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(spyDefaultAddressInterceptor, times(3)).getSapCoreSAPGlobalConfigurationService();
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getDefaultShipmentAddress();
		verify(spyDefaultAddressInterceptor, times(1)).isDefaultShipmentAddress(addressModel);
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);
	}
}
