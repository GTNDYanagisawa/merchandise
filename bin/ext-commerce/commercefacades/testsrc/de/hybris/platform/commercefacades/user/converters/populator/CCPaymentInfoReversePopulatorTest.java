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
package de.hybris.platform.commercefacades.user.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * @author KKW
 * 
 */
@UnitTest
public class CCPaymentInfoReversePopulatorTest
{
	private CCPaymentInfoReversePopulator paymentReversePopulator;

	@Mock
	private AddressReversePopulator mockAddressReversePopulator;
	@Mock
	private EnumerationService mockEnumerationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		paymentReversePopulator = new CCPaymentInfoReversePopulator();
		paymentReversePopulator.setAddressReversePopulator(mockAddressReversePopulator);
		paymentReversePopulator.setEnumerationService(mockEnumerationService);
	}

	@Test
	public void testPopulateAll()
	{
		final CCPaymentInfoData ccPaymentInfoData = mock(CCPaymentInfoData.class);
		final AddressData billingAddressData = mock(AddressData.class);
		final CreditCardPaymentInfoModel ccPaymentInfoModel = new CreditCardPaymentInfoModel();

		given(mockEnumerationService.getEnumerationValue(CreditCardType.class.getSimpleName(), "visa")).willReturn(
				CreditCardType.VISA);

		given(ccPaymentInfoData.getAccountHolderName()).willReturn("Marian");
		given(ccPaymentInfoData.getCardNumber()).willReturn("1234567890");
		given(ccPaymentInfoData.getCardType()).willReturn("visa");
		given(ccPaymentInfoData.getExpiryMonth()).willReturn("12");
		given(ccPaymentInfoData.getExpiryYear()).willReturn("2018");
		given(ccPaymentInfoData.getIssueNumber()).willReturn("987");
		given(ccPaymentInfoData.getStartMonth()).willReturn("01");
		given(ccPaymentInfoData.getStartYear()).willReturn("2001");
		given(ccPaymentInfoData.getSubscriptionId()).willReturn("456");
		given(ccPaymentInfoData.getBillingAddress()).willReturn(billingAddressData);
		given(Boolean.valueOf(ccPaymentInfoData.isSaved())).willReturn(Boolean.TRUE);

		paymentReversePopulator.populate(ccPaymentInfoData, ccPaymentInfoModel);
		Assert.assertEquals("Marian", ccPaymentInfoModel.getCcOwner());
		Assert.assertEquals("************7890", ccPaymentInfoModel.getNumber());
		Assert.assertEquals("456", ccPaymentInfoModel.getSubscriptionId());
		Assert.assertEquals("01", ccPaymentInfoModel.getValidFromMonth());
		Assert.assertEquals("2001", ccPaymentInfoModel.getValidFromYear());
		Assert.assertEquals("12", ccPaymentInfoModel.getValidToMonth());
		Assert.assertEquals("2018", ccPaymentInfoModel.getValidToYear());
		Assert.assertEquals(Integer.valueOf(987), ccPaymentInfoModel.getIssueNumber());
		Assert.assertEquals(true, ccPaymentInfoModel.isSaved());
		Assert.assertEquals(CreditCardType.VISA, ccPaymentInfoModel.getType());
	}
}
