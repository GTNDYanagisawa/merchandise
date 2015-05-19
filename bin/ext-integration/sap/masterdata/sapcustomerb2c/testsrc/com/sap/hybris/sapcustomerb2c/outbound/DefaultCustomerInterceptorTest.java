package com.sap.hybris.sapcustomerb2c.outbound;

import static com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils.BASE_STORE;
import static com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils.CONTACT_ID;
import static com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils.SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.REPLICATEREGISTEREDUSER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.store.BaseStoreModel;
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
public class DefaultCustomerInterceptorTest
{
	@InjectMocks
	private final DefaultCustomerInterceptor defaultCustomerInterceptor = new DefaultCustomerInterceptor();

	@Mock
	private final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService = mock(SAPGlobalConfigurationServiceImpl.class);
	@Mock
	private final CustomerModel customerModel = mock(CustomerModel.class);
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

	/**
	 * Check if the interceptor call the customerExportService exactly one time
	 * <ul>
	 * <li>
	 * REPLICATEREGISTEREDUSER is set to true</li>
	 * <li>
	 * defaultShipmentAddress is not null</li>
	 * <li>
	 * defaultShipmentAddress modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportData() throws InterceptorException
	{
		// given
		defaultCustomerInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(defaultCustomerInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)).willReturn(true);

		defaultCustomerInterceptor.setBaseStoreService(baseStoreService);

		final String baseStoreUid = BASE_STORE;
		final BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);
		given(baseStoreService.getCurrentBaseStore().getUid()).willReturn(baseStoreUid);

		final String sessionLanguage = SESSION_LANGUAGE;
		final LanguageData languageData = mock(LanguageData.class);
		given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
		given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(sessionLanguage);
		defaultCustomerInterceptor.setStoreSessionFacade(storeSessionFacade);
		defaultCustomerInterceptor.setCustomerExportService(customerExportService);

		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(customerModel, times(1)).getSapContactID();
		verify(customerExportService, times(1)).sendCustomerData(customerModel, baseStoreUid, sessionLanguage, shipmentAddress);

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
		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, never()).getProperty(REPLICATEREGISTEREDUSER);
		verify(ctx, never()).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(customerModel, never()).getDefaultShipmentAddress();
		verify(customerModel, never()).getSapContactID();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, shipmentAddress);

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
		defaultCustomerInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(defaultCustomerInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(false);

		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(ctx, never()).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(customerModel, never()).getDefaultShipmentAddress();
		verify(customerModel, never()).getSapContactID();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, shipmentAddress);

	}

	/**
	 * Check if the interceptor call the customerExportService default shipment address changed
	 * <ul>
	 * <li>
	 * defaultShipmentAddress is null</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportDefaultShipmentAddressNull() throws InterceptorException
	{
		// given
		defaultCustomerInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(defaultCustomerInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(null);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);

		defaultCustomerInterceptor.setBaseStoreService(baseStoreService);

		final String baseStoreUid = BASE_STORE;
		final BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);
		given(baseStoreService.getCurrentBaseStore().getUid()).willReturn(baseStoreUid);

		final String sessionLanguage = SESSION_LANGUAGE;
		final LanguageData languageData = mock(LanguageData.class);
		given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
		given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(sessionLanguage);
		defaultCustomerInterceptor.setStoreSessionFacade(storeSessionFacade);
		defaultCustomerInterceptor.setCustomerExportService(customerExportService);

		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(customerModel, times(1)).getSapContactID();
		verify(customerExportService, times(1)).sendCustomerData(customerModel, baseStoreUid, sessionLanguage, null);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * defaultShipmentAddress not modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportDefaultShipmentAddressNotModified() throws InterceptorException
	{
		//given
		defaultCustomerInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(defaultCustomerInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);

		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)).willReturn(false);

		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(customerModel, never()).getDefaultShipmentAddress();
		verify(customerModel, never()).getSapContactID();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, shipmentAddress);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * sapContactID is null</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportSapContactIDNull() throws InterceptorException
	{
		// given
		defaultCustomerInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(defaultCustomerInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);

		given(customerModel.getDefaultShipmentAddress()).willReturn(shipmentAddress);
		given(customerModel.getSapContactID()).willReturn(null);
		given(ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)).willReturn(true);

		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(customerModel, times(1)).getSapContactID();
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(customerModel, times(1)).getSapContactID();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, shipmentAddress);

	}

	/**
	 * Check if the interceptor call the customerExportService
	 * <ul>
	 * <li>
	 * name modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportNameModified() throws InterceptorException
	{
		// given
		defaultCustomerInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(defaultCustomerInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(ctx.isModified(customerModel, CustomerModel.NAME)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(null);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);

		defaultCustomerInterceptor.setBaseStoreService(baseStoreService);

		final String baseStoreUid = BASE_STORE;
		final BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);
		given(baseStoreService.getCurrentBaseStore().getUid()).willReturn(baseStoreUid);

		final String sessionLanguage = SESSION_LANGUAGE;
		final LanguageData languageData = mock(LanguageData.class);
		given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
		given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(sessionLanguage);
		defaultCustomerInterceptor.setStoreSessionFacade(storeSessionFacade);
		defaultCustomerInterceptor.setCustomerExportService(customerExportService);

		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.NAME);
		verify(customerModel, times(1)).getSapContactID();
		verify(customerExportService, times(1)).sendCustomerData(customerModel, baseStoreUid, sessionLanguage, null);

	}

	/**
	 * Check if the interceptor call the customerExportService
	 * <ul>
	 * <li>
	 * title modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportTitleModified() throws InterceptorException
	{
		// given
		defaultCustomerInterceptor.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(defaultCustomerInterceptor.getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
				.willReturn(true);
		given(ctx.isModified(customerModel, CustomerModel.TITLE)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(null);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);

		defaultCustomerInterceptor.setBaseStoreService(baseStoreService);

		final String baseStoreUid = BASE_STORE;
		final BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);
		given(baseStoreService.getCurrentBaseStore().getUid()).willReturn(baseStoreUid);

		final String sessionLanguage = SESSION_LANGUAGE;
		final LanguageData languageData = mock(LanguageData.class);
		given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
		given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(sessionLanguage);
		defaultCustomerInterceptor.setStoreSessionFacade(storeSessionFacade);
		defaultCustomerInterceptor.setCustomerExportService(customerExportService);

		// when
		defaultCustomerInterceptor.onValidate(customerModel, ctx);

		// then
		verify(sapCoreSAPGlobalConfigurationService, times(1)).getProperty(REPLICATEREGISTEREDUSER);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.NAME);
		verify(ctx, times(1)).isModified(customerModel, CustomerModel.TITLE);
		verify(customerModel, times(1)).getSapContactID();
		verify(customerExportService, times(1)).sendCustomerData(customerModel, baseStoreUid, sessionLanguage, null);

	}
}
