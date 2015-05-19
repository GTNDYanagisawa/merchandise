package com.sap.hybris.sapcustomerb2b.outbound;

import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.CUSTOMER_ID;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.SESSION_LANGUAGE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
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
public class DefaultB2BCustomerInterceptorTest
{
	@InjectMocks
	private final DefaultB2BCustomerInterceptor defaultCustomerInterceptor = new DefaultB2BCustomerInterceptor();

	@Mock
	private final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
	@Mock
	private final InterceptorContext ctx = mock(InterceptorContext.class);
	@Mock
	private final B2BCustomerExportService b2bCustomerExportService = mock(B2BCustomerExportService.class);

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
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		final String sessionLanguage = SESSION_LANGUAGE;
		final LanguageModel languageModel = mock(LanguageModel.class);
		given(b2bCustomerModel.getSessionLanguage()).willReturn(languageModel);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.SESSIONLANGUAGE)).willReturn(true);
		given(b2bCustomerModel.getSessionLanguage().getIsocode()).willReturn(sessionLanguage);
		defaultCustomerInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);

		// then
		verify(b2bCustomerModel, times(2)).getCustomerID();
		verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel);

	}

	/**
	 * Check if the interceptor doesn't call the customerExportService
	 * <ul>
	 * <li>
	 * CustomerId is Null</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testNoExportCustomerIDNull() throws InterceptorException
	{
		// given
		given(b2bCustomerModel.getCustomerID()).willReturn(null);

		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);

		// then
		verify(ctx, never()).isModified(b2bCustomerModel, B2BCustomerModel.CUSTOMERID);
		verify(b2bCustomerModel, times(1)).getCustomerID();
		verify(b2bCustomerExportService, never()).prepareAndSend(b2bCustomerModel);
	}

	/**
	 * Check if the interceptor call the customerExportService
	 * <ul>
	 * <li>
	 * sapContactID is not null</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportCustomerIDNotNull() throws InterceptorException
	{
		// given
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.NAME)).willReturn(true);
		defaultCustomerInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);

		// then
		verify(ctx, times(1)).isModified(b2bCustomerModel, CustomerModel.NAME);
		verify(b2bCustomerModel, times(2)).getCustomerID();
		verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel);

	}

	/**
	 * Check if the interceptor call the customerExportService
	 * <ul>
	 * <li>
	 * title is modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportTitleModified() throws InterceptorException
	{
		// given
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		final TitleModel titelModel = mock(TitleModel.class);
		given(b2bCustomerModel.getTitle()).willReturn(titelModel);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.TITLE)).willReturn(true);
		defaultCustomerInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);

		// then
		verify(ctx, times(1)).isModified(b2bCustomerModel, CustomerModel.NAME);
		verify(b2bCustomerModel, times(2)).getCustomerID();
		verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel);
	}

	/**
	 * Check if the interceptor call the customerExportService
	 * <ul>
	 * <li>
	 * name is modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportNameModified() throws InterceptorException
	{
		// given
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		given(b2bCustomerModel.getName()).willReturn(NAME);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.NAME)).willReturn(true);
		defaultCustomerInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);

		// then
		verify(ctx, times(1)).isModified(b2bCustomerModel, CustomerModel.NAME);
		verify(b2bCustomerModel, times(2)).getCustomerID();
		verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel);
	}


	/**
	 * Check if the interceptor call the customerExportService
	 * <ul>
	 * <li>
	 * language modified</li>
	 * </ul>
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testExportLanguageModified() throws InterceptorException
	{
		// given
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID);
		given(b2bCustomerModel.getName()).willReturn(SESSION_LANGUAGE);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.SESSIONLANGUAGE)).willReturn(true);
		defaultCustomerInterceptor.setB2bCustomerExportService(b2bCustomerExportService);

		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);

		// then
		verify(ctx, times(1)).isModified(b2bCustomerModel, CustomerModel.SESSIONLANGUAGE);
		verify(b2bCustomerModel, times(2)).getCustomerID();
		verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel);
	}
}
