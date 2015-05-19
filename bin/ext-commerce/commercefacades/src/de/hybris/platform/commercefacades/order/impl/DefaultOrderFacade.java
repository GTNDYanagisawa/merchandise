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

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;


/**
 * Default implementation of {@link OrderFacade}.
 */
public class DefaultOrderFacade implements OrderFacade
{
	private UserService userService;
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private Converter<OrderModel, OrderData> orderConverter;
	private Converter<OrderModel, OrderHistoryData> orderHistoryConverter;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	@Override
	public OrderData getOrderDetailsForCode(final String code)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCheckoutCustomerStrategy().isAnonymousCheckout()?
				getCustomerAccountService().getOrderDetailsForGUID(code,baseStoreModel):
				getCustomerAccountService().getOrderForCode((CustomerModel)getUserService().getCurrentUser(), code, baseStoreModel);
		if (orderModel == null)
		{
			throw new UnknownIdentifierException("Order with orderGUID " + code + " not found for current user in current BaseStore");
		}
		return getOrderConverter().convert(orderModel);
	}

	@Override
	public OrderData getOrderDetailsForGUID(final String guid)
	{
		final OrderModel orderModel = getCustomerAccountService().getGuestOrderForGUID(guid,
                getBaseStoreService().getCurrentBaseStore());
		if (orderModel == null)
		{
			throw new UnknownIdentifierException("Order with guid " + guid + " not found for current user in current BaseStore");
		}
		return getOrderConverter().convert(orderModel);
	}

	@Override
	public List<OrderHistoryData> getOrderHistoryForStatuses(final OrderStatus... statuses)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final List<OrderModel> orderList = getCustomerAccountService().getOrderList(currentCustomer, currentBaseStore, statuses);
		return Converters.convertAll(orderList, getOrderHistoryConverter());
	}

	@Override
	public SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
			final OrderStatus... statuses)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<OrderModel> orderResults = getCustomerAccountService().getOrderList(currentCustomer, currentBaseStore,
				statuses, pageableData);

		return convertPageData(orderResults, getOrderHistoryConverter());
	}

    @Override
    public OrderData getOrderDetailsForCodeWithoutUser(final String code) {
        final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
        final OrderModel orderModel = getCustomerAccountService().getOrderForCode(code, baseStoreModel);
        if (orderModel == null)
        {
            throw new UnknownIdentifierException("Order with orderGUID " + code + " not found in current BaseStore");
        }
        return getOrderConverter().convert(orderModel);
    }

    protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected <T extends CustomerAccountService> T getCustomerAccountService()
	{
		return (T) customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService service)
	{
		this.baseStoreService = service;
	}

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	protected Converter<OrderModel, OrderHistoryData> getOrderHistoryConverter()
	{
		return orderHistoryConverter;
	}

	@Required
	public void setOrderHistoryConverter(final Converter<OrderModel, OrderHistoryData> orderHistoryConverter)
	{
		this.orderHistoryConverter = orderHistoryConverter;
	}

	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	@Required
	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

}
