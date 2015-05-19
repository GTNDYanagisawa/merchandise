/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.commercefacades.order.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@UnitTest
public class DefaultOrderFacadeTest
{
	private DefaultOrderFacade defaultOrderFacade;

	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private AbstractPopulatingConverter<OrderModel, OrderData> orderConverter;
	@Mock
	private UserService userService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private AbstractPopulatingConverter<OrderModel, OrderHistoryData> orderHistoryConverter;
	@Mock
	private CheckoutCustomerStrategy defaultCheckoutCustomerStrategy;

	private OrderModel orderModel;

	private ProductModel productModel;

	private ProductData productData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultOrderFacade = new DefaultOrderFacade();
		defaultOrderFacade.setCustomerAccountService(customerAccountService);
		defaultOrderFacade.setOrderConverter(orderConverter);
		defaultOrderFacade.setUserService(userService);
		defaultOrderFacade.setBaseStoreService(baseStoreService);
		defaultOrderFacade.setOrderHistoryConverter(orderHistoryConverter);
		defaultOrderFacade.setCheckoutCustomerStrategy(defaultCheckoutCustomerStrategy);

		orderModel = new OrderModel();
		orderModel.setCode("order");
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		final ProductModel productModel1 = new ProductModel();
		entryModel.setProduct(productModel1);
		final List<AbstractOrderEntryModel> entryModelList = new ArrayList<AbstractOrderEntryModel>();
		entryModelList.add(entryModel);
		orderModel.setEntries(entryModelList);

		final OrderData orderData = Mockito.mock(OrderData.class);
		final List<OrderEntryData> listData = new ArrayList<OrderEntryData>();
		final OrderEntryData entryData = new OrderEntryData();
		entryData.setProduct(productData);
		listData.add(entryData);
		given(orderData.getEntries()).willReturn(listData);
		given(orderConverter.convert(orderModel)).willReturn(orderData);

		productModel = Mockito.mock(ProductModel.class);
		productData = Mockito.mock(ProductData.class);
	}

	@Test
	public void testGetOrderDetailsForCode()
	{
		given(
				customerAccountService.getOrderForCode(Mockito.any(CustomerModel.class), Mockito.anyString(),
						Mockito.any(BaseStoreModel.class))).willReturn(orderModel);
		given(productModel.getPicture()).willReturn(null);
		defaultOrderFacade.getOrderDetailsForCode("1234");
		verify(orderConverter).convert(orderModel);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetOrderDetailsForCodeNull()
	{
		given(
				customerAccountService.getOrderForCode(Mockito.any(CustomerModel.class), Mockito.anyString(),
						Mockito.any(BaseStoreModel.class))).willReturn(null);

		defaultOrderFacade.getOrderDetailsForCode("1234");
	}

	@Test
	public void testGetOrderHistory()
	{
		final CustomerModel customerModel = new CustomerModel();
		given(userService.getCurrentUser()).willReturn(customerModel);
		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setUid("baseStoreModel");
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(customerAccountService.getOrderList(customerModel, baseStoreModel, null)).willReturn(Collections.EMPTY_LIST);
		final List list = defaultOrderFacade.getOrderHistoryForStatuses();
		Assert.assertEquals(Collections.EMPTY_LIST.size(), list.size());
	}

    @Test
    public void testGetOrderDetailsForCodeWithoutUser()
    {
        given(
                customerAccountService.getOrderForCode(Mockito.anyString(),
                        Mockito.any(BaseStoreModel.class))).willReturn(orderModel);
        defaultOrderFacade.getOrderDetailsForCodeWithoutUser("1234");
        verify(orderConverter).convert(orderModel);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testGetOrderDetailsForCodeWithoutUserNull()
    {
        given(
                customerAccountService.getOrderForCode(Mockito.anyString(),
                        Mockito.any(BaseStoreModel.class))).willReturn(null);

        defaultOrderFacade.getOrderDetailsForCodeWithoutUser("1234");
    }
}
