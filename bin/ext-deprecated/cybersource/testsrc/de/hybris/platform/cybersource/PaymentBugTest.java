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
package de.hybris.platform.cybersource;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.NewSubscription;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;


@ManualTest
//manual test: sended request count to external ressouce is limited
public class PaymentBugTest extends ServicelayerTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private PaymentService paymentService;

	private static final String TEST_CC_NUMBER = "4111111111111111";
	private static final int TEST_CC_EXPIRATION_MONTH = 12;
	private static final int TEST_CC_EXPIRATION_YEAR = (Calendar.getInstance().get(Calendar.YEAR) + 2);

	@Test
	public void testForPAY50()
	{
		final String merchandTransactionCode = "Transaction_" + System.currentTimeMillis();
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setFirstname("John");
		addressModel.setLastname("Doe");
		//			 addressModel.setEmail("nobody@cybersource.com");
		addressModel.setStreetnumber("1295");
		addressModel.setStreetname("Charleston Road");
		addressModel.setTown("Mountain View");
		//	addressModel.setRegion(getState("CA"));
		addressModel.setPostalcode("94401");
		final CountryModel countryModel = modelService.create(CountryModel.class);
		countryModel.setIsocode("us");
		countryModel.setName("United States");
		countryModel.setActive(Boolean.TRUE);
		addressModel.setCountry(countryModel);

		final NewSubscription subscription = paymentService.createSubscription(merchandTransactionCode, "Cybersource",
				Currency.getInstance("USD"), addressModel, getCardInfo(createBillingInfo()));
		Assert.assertNotNull(subscription);
		//Assert.assertNotNull(subscription.getSubscriptionID());
	}

	@Test
	public void testForPAY51()
	{
		final String merchandTransactionCode = "Subscription_" + System.currentTimeMillis();

		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setFirstname("John");
		addressModel.setLastname("Doe");
		addressModel.setEmail("nobody@cybersource.com");
		addressModel.setStreetnumber("1295");
		addressModel.setStreetname("Charleston Road");
		addressModel.setTown("Mountain View");

		addressModel.setPostalcode("94401");
		final CountryModel countryModel = modelService.create(CountryModel.class);
		countryModel.setIsocode("us");
		countryModel.setName("United States");
		countryModel.setActive(Boolean.TRUE);
		addressModel.setCountry(countryModel);

		final RegionModel region = modelService.create(RegionModel.class);
		region.setCountry(countryModel);
		region.setName("CA");
		addressModel.setRegion(region);


		final CardInfo cardInfo = getCardInfo(createBillingInfo());

		final Currency currency = Currency.getInstance("USD");
		final NewSubscription subscription = paymentService.createSubscription(merchandTransactionCode, "Cybersource", currency,
				addressModel, cardInfo);
		Assert.assertNotNull(subscription);
		Assert.assertNotNull(subscription.getSubscriptionID());

		final CurrencyModel currencyModel = modelService.create(CurrencyModel.class);
		currencyModel.setIsocode("USD");
		currencyModel.setBase(Boolean.TRUE);
		modelService.save(currencyModel);

		final String merchandTransactionAuthCode = "Authorize_" + System.currentTimeMillis();
		final PaymentTransactionEntryModel authTransactionEntryModel = paymentService.authorize(merchandTransactionAuthCode,
				BigDecimal.valueOf(9.99d), currency, addressModel, subscription.getSubscriptionID());
		Assert.assertNotNull(authTransactionEntryModel);

		final PaymentTransactionModel paymentTransaction = authTransactionEntryModel.getPaymentTransaction();
		final PaymentTransactionEntryModel captureTransactionEntryModel = paymentService.capture(paymentTransaction);
		Assert.assertNotNull(captureTransactionEntryModel);
	}


	private CardInfo getCardInfo(final BillingInfo billingInfo)
	{
		final CardInfo card = new CardInfo();
		card.setCardHolderFullName("John Doe");
		card.setCardNumber(TEST_CC_NUMBER);
		card.setExpirationMonth(Integer.valueOf(TEST_CC_EXPIRATION_MONTH));
		card.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR));
		card.setBillingInfo(billingInfo);
		card.setCardType(CreditCardType.VISA);
		return card;
	}

	private BillingInfo createBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setCity("Mountain View");
		billingInfo.setCountry("US");
		billingInfo.setEmail("nobody@cybersource.com");
		billingInfo.setFirstName("John");
		billingInfo.setIpAddress("10.7" + "." + "7.7");
		billingInfo.setLastName("Doe");
		billingInfo.setPhoneNumber("650-965-6000");
		billingInfo.setPostalCode("94043");
		billingInfo.setState("CA");
		billingInfo.setStreet1("1295 Charleston Road");
		return billingInfo;
	}
}
