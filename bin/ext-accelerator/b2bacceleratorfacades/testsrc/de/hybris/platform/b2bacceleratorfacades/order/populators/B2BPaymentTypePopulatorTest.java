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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.servicelayer.type.TypeService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


/**
 */
@UnitTest
public class B2BPaymentTypePopulatorTest
{

	private B2BPaymentTypePopulator b2bPaymentTypePopulator;

	private static final String CARD_TYPE_DISPLAY_NAME = "Card Payment";
	private static final String ACCOUNT_TYPE_DISPLAY_NAME = "Account";

	@Mock
	private TypeService typeService;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		b2bPaymentTypePopulator = new B2BPaymentTypePopulator();
		b2bPaymentTypePopulator.setTypeService(typeService);
	}

	@Test
	public void testConverterForCardType()
	{
		final CheckoutPaymentType cardPaymentType = CheckoutPaymentType.CARD;

		final EnumerationValueModel enumerationValueModel = mock(EnumerationValueModel.class);

		given(enumerationValueModel.getName()).willReturn(CARD_TYPE_DISPLAY_NAME);
		given(typeService.getEnumerationValue(cardPaymentType)).willReturn(enumerationValueModel);

		final B2BPaymentTypeData cardTypeData = new B2BPaymentTypeData();
        b2bPaymentTypePopulator.populate(cardPaymentType, cardTypeData);
		Assert.assertEquals(CheckoutPaymentType.CARD.getCode(), cardTypeData.getCode());
	}


	@Test
	public void testConverterForAccountType()
	{
		final CheckoutPaymentType accountPaymentType = CheckoutPaymentType.ACCOUNT;

		final EnumerationValueModel enumerationValueModel = mock(EnumerationValueModel.class);

		given(enumerationValueModel.getName()).willReturn(ACCOUNT_TYPE_DISPLAY_NAME);
		given(typeService.getEnumerationValue(accountPaymentType)).willReturn(enumerationValueModel);

		final B2BPaymentTypeData accountTypeData = new B2BPaymentTypeData();
        b2bPaymentTypePopulator.populate(accountPaymentType, accountTypeData);
		Assert.assertEquals(CheckoutPaymentType.ACCOUNT.getCode(), accountTypeData.getCode());
	}
}
