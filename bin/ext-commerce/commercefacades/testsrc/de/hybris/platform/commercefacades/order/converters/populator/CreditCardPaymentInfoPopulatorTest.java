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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CreditCardPaymentInfoPopulatorTest
{
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractPopulatingConverter<CreditCardType, CardTypeData> cardTypeConverter;

	private final CreditCardPaymentInfoPopulator creditCardPaymentInfoPopulator = new CreditCardPaymentInfoPopulator();
	private AbstractPopulatingConverter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		creditCardPaymentInfoPopulator.setAddressConverter(addressConverter);
		creditCardPaymentInfoPopulator.setCardTypeConverter(cardTypeConverter);

		creditCardPaymentInfoConverter = new ConverterFactory<CreditCardPaymentInfoModel, CCPaymentInfoData, CreditCardPaymentInfoPopulator>()
				.create(CCPaymentInfoData.class, creditCardPaymentInfoPopulator);
	}

	@Test
	public void testConvert()
	{
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
		final PK pk = PK.parse("123");
		final CreditCardType creditCardType = CreditCardType.MAESTRO;
		final AddressModel addressModel = mock(AddressModel.class);
		final AddressData addressData = mock(AddressData.class);
		final CardTypeData cardTypeData = mock(CardTypeData.class);
		given(cardTypeData.getCode()).willReturn(creditCardType.getCode());
		given(creditCardPaymentInfoModel.getPk()).willReturn(pk);
		given(creditCardPaymentInfoModel.getType()).willReturn(creditCardType);
		given(creditCardPaymentInfoModel.getNumber()).willReturn("cardNumber");
		given(creditCardPaymentInfoModel.getCcOwner()).willReturn("ccOwner");
		given(creditCardPaymentInfoModel.getValidToMonth()).willReturn("validToMonth");
		given(creditCardPaymentInfoModel.getValidToYear()).willReturn("validToYear");
		given(creditCardPaymentInfoModel.getValidFromMonth()).willReturn("validFromMonth");
		given(creditCardPaymentInfoModel.getValidFromYear()).willReturn("validFromYear");
		given(creditCardPaymentInfoModel.getSubscriptionId()).willReturn("subId");
		given(Boolean.valueOf(creditCardPaymentInfoModel.isSaved())).willReturn(Boolean.TRUE);
		given(creditCardPaymentInfoModel.getBillingAddress()).willReturn(addressModel);
		given(cardTypeConverter.convert(creditCardType)).willReturn(cardTypeData);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		final CCPaymentInfoData ccPaymentInfoData = creditCardPaymentInfoConverter.convert(creditCardPaymentInfoModel);
		Assert.assertEquals("cardNumber", ccPaymentInfoData.getCardNumber());
		Assert.assertEquals(CreditCardType.MAESTRO.getCode(), ccPaymentInfoData.getCardType());
		Assert.assertEquals("ccOwner", ccPaymentInfoData.getAccountHolderName());
		Assert.assertEquals("validToMonth", ccPaymentInfoData.getExpiryMonth());
		Assert.assertEquals("validToYear", ccPaymentInfoData.getExpiryYear());
		Assert.assertEquals("validFromMonth", ccPaymentInfoData.getStartMonth());
		Assert.assertEquals("validFromYear", ccPaymentInfoData.getStartYear());
		Assert.assertEquals("subId", ccPaymentInfoData.getSubscriptionId());
		Assert.assertTrue(ccPaymentInfoData.isSaved());
		Assert.assertEquals(addressData, ccPaymentInfoData.getBillingAddress());
		Assert.assertEquals("123", ccPaymentInfoData.getId());


	}
}
