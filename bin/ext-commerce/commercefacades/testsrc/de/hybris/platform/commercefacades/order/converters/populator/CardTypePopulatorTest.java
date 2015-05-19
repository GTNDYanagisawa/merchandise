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
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.servicelayer.type.TypeService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CardTypePopulatorTest
{
	private static final String CARD_TYPE_NAME = "El Maestro";

	@Mock
	private TypeService typeService;

	private final CardTypePopulator cardTypePopulator = new CardTypePopulator();

	private AbstractPopulatingConverter<CreditCardType, CardTypeData> cardTypeConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cardTypePopulator.setTypeService(typeService);

		cardTypeConverter = new ConverterFactory<CreditCardType, CardTypeData, CardTypePopulator>().create(CardTypeData.class,
				cardTypePopulator);
	}


	@Test
	public void testConvert()
	{
		final CreditCardType source = CreditCardType.MAESTRO;
		final EnumerationValueModel cardTypeName = mock(EnumerationValueModel.class);

		given(cardTypeName.getName()).willReturn(CARD_TYPE_NAME);
		given(typeService.getEnumerationValue(source)).willReturn(cardTypeName);

		final CardTypeData result = cardTypeConverter.convert(source);

		Assert.assertEquals(CreditCardType.MAESTRO.getCode(), result.getCode());
		Assert.assertEquals(CARD_TYPE_NAME, result.getName());
	}
}
