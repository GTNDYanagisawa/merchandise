package com.sap.hybris.sapcustomerb2b.outbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
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
public class DefaultB2BCustomerAddressInterceptorTest
{
	private static final String CUSTOMER_ID_10013 = "0000010013";
	private static final String CUSTOMER_ID_10017 = "0000010017";
	private static final String PUBLIC_KEY = "0000010017|0000010017|BUS1006001|null";

	@InjectMocks
	private final DefaultB2BCustomerAddressInterceptor defaultCustomerAddressInterceptor = new DefaultB2BCustomerAddressInterceptor();

	@Mock
	private final AddressModel addressModel = mock(AddressModel.class);
	@Mock
	private final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
	@Mock
	private final InterceptorContext ctx = mock(InterceptorContext.class);
	@Mock
	private final B2BCustomerExportService b2bCustomerExportService = mock(B2BCustomerExportService.class);

	/**
	 * Check if the interceptor call the customerExportService
	 * <ul>
	 * <li>
	 * phone number is modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportDataIfPhoneIsModified() throws InterceptorException
	{
		// given
		given(ctx.isModified(addressModel, AddressModel.PHONE1)).willReturn(true);
		given(addressModel.getOwner()).willReturn(b2bCustomerModel);
		given(addressModel.getPublicKey()).willReturn(PUBLIC_KEY);
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID_10017);
		defaultCustomerAddressInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel);
	}

	/**
	 * Check if the interceptor does not call the customerExportService
	 * <ul>
	 * <li>
	 * phone number is not modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportDataIfPhoneIsNotodified() throws InterceptorException
	{
		// given
		given(ctx.isModified(addressModel, AddressModel.PHONE1)).willReturn(false);
		given(addressModel.getOwner()).willReturn(b2bCustomerModel);
		given(addressModel.getPublicKey()).willReturn(PUBLIC_KEY);
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID_10017);
		defaultCustomerAddressInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel);
	}

	/**
	 * Check if the interceptor does not call the customerExportService
	 * <ul>
	 * <li>
	 * generated public key is set not equal addressModel.getPublicKey()</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportDataIfPublicKeyIsInvalid() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(b2bCustomerModel);
		given(addressModel.getPublicKey()).willReturn(PUBLIC_KEY);
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID_10013);
		defaultCustomerAddressInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);
		// then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel);
	}

	/**
	 * Check if the interceptor does not call the customerExportService
	 * <ul>
	 * <li>
	 * customerId is set to space</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportDataIfCustomerIDIsSpace() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(b2bCustomerModel);
		given(b2bCustomerModel.getCustomerID()).willReturn("");
		defaultCustomerAddressInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

		//then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel);
	}

	/**
	 * Check if the interceptor does not call the customerExportService
	 * <ul>
	 * <li>
	 * customerId is set to null</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportDataIfCustomerIDIsNull() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(b2bCustomerModel);
		given(b2bCustomerModel.getCustomerID()).willReturn(null);
		defaultCustomerAddressInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel);
	}

}
