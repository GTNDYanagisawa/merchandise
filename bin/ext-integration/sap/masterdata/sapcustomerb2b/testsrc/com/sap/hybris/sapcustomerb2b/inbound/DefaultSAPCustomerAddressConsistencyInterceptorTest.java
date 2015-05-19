/**
 * 
 */
package com.sap.hybris.sapcustomerb2b.inbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
@UnitTest
public class DefaultSAPCustomerAddressConsistencyInterceptorTest
{
	private static final String kunnr = "0815";
	private static final String QUERYRESULTSTRING0815 = "SELECT {pk} from {Address} where {sapCustomerID} =?kunnr AND {duplicate} =?duplicate";

	@Before
	public void setUp()
	{
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	@Test
	public void findAddressesBySAPCustomerIDDuplicateFalse()
	{
		// given
		//		final MyFlexibleSearchService flexibleSearchService = new MyFlexibleSearchService();
		final FlexibleSearchService flexibleSearchService = mock(FlexibleSearchService.class);
		final DefaultSAPCustomerAddressConsistencyInterceptor sapCustomerAddressConsistencyInterceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();
		final DefaultSAPCustomerAddressConsistencyInterceptor spySapCustomerAddressConsistencyInterceptor = spy(sapCustomerAddressConsistencyInterceptor);
		spySapCustomerAddressConsistencyInterceptor.setFlexibleSearchService(flexibleSearchService);
		final FlexibleSearchQuery fsQuery = mock(FlexibleSearchQuery.class);
		doReturn(fsQuery).when(spySapCustomerAddressConsistencyInterceptor).getFlexibleSearchQuery();

		// when
		spySapCustomerAddressConsistencyInterceptor.findAddressesBySAPCustomerID(kunnr, Boolean.FALSE);

		// then
		Assert.assertEquals(QUERYRESULTSTRING0815, spySapCustomerAddressConsistencyInterceptor.getQuery());
		verify(fsQuery, times(1)).addQueryParameter("kunnr", kunnr);
		verify(fsQuery, times(1)).addQueryParameter("duplicate", Boolean.FALSE);

	}

	@Test
	public void findAddressesBySAPCustomerIDDuplicateTrue()
	{
		// given
		final FlexibleSearchService flexibleSearchService = mock(FlexibleSearchService.class);
		final DefaultSAPCustomerAddressConsistencyInterceptor sapCustomerAddressConsistencyInterceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();
		final DefaultSAPCustomerAddressConsistencyInterceptor spySapCustomerAddressConsistencyInterceptor = spy(sapCustomerAddressConsistencyInterceptor);
		spySapCustomerAddressConsistencyInterceptor.setFlexibleSearchService(flexibleSearchService);
		final FlexibleSearchQuery fsQuery = mock(FlexibleSearchQuery.class);
		doReturn(fsQuery).when(spySapCustomerAddressConsistencyInterceptor).getFlexibleSearchQuery();

		// when
		spySapCustomerAddressConsistencyInterceptor.findAddressesBySAPCustomerID(kunnr, Boolean.TRUE);

		//then
		Assert.assertEquals(QUERYRESULTSTRING0815, spySapCustomerAddressConsistencyInterceptor.getQuery());
		verify(fsQuery, times(1)).addQueryParameter("kunnr", kunnr);
		verify(fsQuery, times(1)).addQueryParameter("duplicate", Boolean.TRUE);

	}

	@Test
	public void uIDOfAddressOwnerIsUserModel()
	{
		// given
		final DefaultSAPCustomerAddressConsistencyInterceptor sapCustomerAddressConsistencyInterceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();
		final AddressModel address = mock(AddressModel.class);
		final UserModel userModel = mock(UserModel.class);
		given(address.getOwner()).willReturn(userModel);
		given(userModel.getUid()).willReturn("UserModel.getUid");

		// when
		final String uId = sapCustomerAddressConsistencyInterceptor.getUIDOfAddressOwner(address);

		// then
		Assert.assertEquals("UserModel.getUid", uId);
	}

	@Test
	public void uIDOfAddressOwnerIsB2BUnitModel()
	{
		// given
		final DefaultSAPCustomerAddressConsistencyInterceptor sapCustomerAddressConsistencyInterceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();
		final AddressModel address = mock(AddressModel.class);
		final B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);
		given(address.getOwner()).willReturn(b2BUnitModel);
		given(b2BUnitModel.getUid()).willReturn("B2BUnitModel.getUid");

		// when
		final String uId = sapCustomerAddressConsistencyInterceptor.getUIDOfAddressOwner(address);

		// then
		Assert.assertEquals("B2BUnitModel.getUid", uId);
	}

	@Test
	public void uIDOfAddressOwnerIsUnknown()
	{
		// given
		final DefaultSAPCustomerAddressConsistencyInterceptor sapCustomerAddressConsistencyInterceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();
		final AddressModel address = mock(AddressModel.class);
		final OrderModel orderModel = mock(OrderModel.class);
		given(address.getOwner()).willReturn(orderModel);

		// when
		final String uId = sapCustomerAddressConsistencyInterceptor.getUIDOfAddressOwner(address);

		// then
		Assert.assertEquals(null, uId);
	}
}
