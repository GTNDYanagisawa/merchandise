/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.ytelcoacceleratorstorefront.facade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.subscriptionfacades.billing.CreditCardFacade;

import java.util.Collection;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;


@UnitTest
public class DefaultCheckoutFacadeIntegrationTest extends ServicelayerTest
{
	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;

	@Resource
	private CreditCardFacade creditCardFacade;

	@Test
	public void testCardTypeConversion() throws CommerceCartModificationException
	{
		final Collection<CardTypeData> original = checkoutFacade.getSupportedCardTypes();
		final Collection<CardTypeData> creditCards = checkoutFacade.getSupportedCardTypes();
		if (creditCardFacade.mappingStrategy(creditCards))
		{
			Assert.assertFalse(CollectionUtils.isEqualCollection(original, creditCards));
		}
		else
		{
			Assert.assertTrue(CollectionUtils.isEqualCollection(original, creditCards));
		}
	}
}
