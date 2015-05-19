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
package de.hybris.platform.payment.order.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.Order;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.payment.PaymentInfo;
import de.hybris.platform.jalo.user.Address;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.payment.jalo.PaymentManager;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Date;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ClonePaymentInfoTest extends ServicelayerTest
{

	@Resource
	public OrderService orderService;

	@Resource
	public ModelService modelService;

	@Resource
	public TypeService typeService;

	private CartModel cart;
	private PaymentInfoModel paymentInfoModel;
	private AddressModel addressModel;
	private UserModel userModel;
	private CurrencyModel currency;

	@Before
	public void setUp() throws Exception
	{
		userModel = modelService.create(UserModel.class);
		userModel.setUid("test");

		addressModel = modelService.create(AddressModel.class);
		addressModel.setOwner(userModel);
		addressModel.setDuplicate(Boolean.FALSE);


		currency = modelService.create(CurrencyModel.class);
		currency.setIsocode("EUR");

		paymentInfoModel = modelService.create(PaymentInfoModel.class);
		paymentInfoModel.setUser(userModel);
		paymentInfoModel.setBillingAddress(addressModel);
		paymentInfoModel.setDuplicate(Boolean.FALSE);
		paymentInfoModel.setCode("test");
		paymentInfoModel.setUser(userModel);

		cart = modelService.create(CartModel.class);
		cart.setPaymentInfo(paymentInfoModel);
		cart.setDate(new Date());
		cart.setUser(userModel);
		cart.setCurrency(currency);

		cart.setNet(Boolean.TRUE);
		modelService.saveAll();
	}

	@Test
	public void testClonePaymentInfoBillingAddressViaSL()
	{
		Assertions.assertThat(userModel.getAddresses()).containsOnly(addressModel);
		Assertions.assertThat(userModel.getPaymentInfos()).containsOnly(paymentInfoModel);

		Assertions.assertThat(addressModel.getDuplicate()).isFalse();
		Assertions.assertThat(addressModel.getOwner()).isEqualTo(userModel);
		Assertions.assertThat(paymentInfoModel.getDuplicate()).isFalse();
		Assertions.assertThat(paymentInfoModel.getUser()).isEqualTo(userModel);
		Assertions.assertThat(cart.getPaymentInfo()).isEqualTo(paymentInfoModel);
		Assertions.assertThat(cart.getPaymentInfo().getBillingAddress()).isEqualTo(addressModel);

		final OrderModel clone = orderService.clone(null, null, cart, "testClone");
		modelService.save(clone);
		modelService.refresh(userModel);
		modelService.refresh(paymentInfoModel);
		modelService.refresh(addressModel);
		modelService.refresh(cart);

		Assertions.assertThat(addressModel.getDuplicate()).isFalse();
		Assertions.assertThat(addressModel.getOwner()).isEqualTo(userModel);
		Assertions.assertThat(paymentInfoModel.getDuplicate()).isFalse();
		Assertions.assertThat(paymentInfoModel.getUser()).isEqualTo(userModel);

		final PaymentInfoModel clonedPaymentInfo = clone.getPaymentInfo();

		Assertions.assertThat(clonedPaymentInfo).isNotEqualTo(paymentInfoModel);
		Assertions.assertThat(clonedPaymentInfo.getUser()).isEqualTo(userModel);

		Assertions.assertThat(clonedPaymentInfo.getBillingAddress()).isNotEqualTo(addressModel);
		Assertions.assertThat(clonedPaymentInfo.getBillingAddress().getDuplicate()).isTrue();
		Assertions.assertThat(clonedPaymentInfo.getBillingAddress().getOwner()).isEqualTo(clone.getPaymentInfo());

		Assertions.assertThat(userModel.getAddresses()).containsOnly(addressModel);
		//	Assertions.assertThat(user.getPaymentInfos()).containsOnly(paymentInfo); //in trunk it is possible to set owner on PI 
	}


	@Test
	public void testClonePaymentInfoBillingAddressViaJalo()
	{
		final User user = modelService.getSource(userModel);
		final Address address = modelService.getSource(addressModel);
		final Cart sourceCart = modelService.getSource(cart);
		final PaymentInfo paymentInfo = modelService.getSource(paymentInfoModel);

		Assertions.assertThat(user.getAddresses()).containsOnly(address);
		Assertions.assertThat(paymentInfo.isDuplicate()).isFalse();
		Assertions.assertThat(paymentInfo.getUser()).isEqualTo(user);

		Assertions.assertThat(address.isDuplicate()).isFalse();
		Assertions.assertThat(address.getOwner()).isEqualTo(user);

		final Order clone = OrderManager.getInstance().createOrder(sourceCart);

		final PaymentManager paymentManager = PaymentManager.getInstance();
		final PaymentInfo cpi = clone.getPaymentInfo();
		final Address billingAddress = paymentManager.getBillingAddress(cpi);

		Assertions.assertThat(cpi).isNotEqualTo(paymentInfo);
		Assertions.assertThat(cpi.isDuplicate()).isTrue();
		Assertions.assertThat(cpi.getOriginal()).isEqualTo(paymentInfo);
		Assertions.assertThat(paymentInfo.isDuplicate()).isFalse();
		Assertions.assertThat(paymentInfo.getUser()).isEqualTo(user);
		Assertions.assertThat(billingAddress).isNotEqualTo(address);
		Assertions.assertThat(address.isDuplicate()).isFalse();
		Assertions.assertThat(billingAddress.isDuplicate()).isTrue();
		Assertions.assertThat(billingAddress.getOwner()).isEqualTo(clone.getPaymentInfo());

		Assertions.assertThat(user.getAddresses()).containsOnly(address);
	}
}
