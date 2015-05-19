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
package de.hybris.platform.mobileservices.text;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberListModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberModel;
import de.hybris.platform.mobileservices.text.lists.PhoneListService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * 
 */
public class PhoneListServiceTest extends ServicelayerTest
{
	@Resource
	I18NService i18nService;

	@Resource
	ModelService modelService;

	@Resource
	PhoneListService phoneListService;

	@Test
	public void testWhiteList()
	{
		CountryModel countryModelDE = null;
		try
		{
			countryModelDE = i18nService.getCountry("DE");
		}
		catch (final UnknownIdentifierException e)
		{
			countryModelDE = new CountryModel();
			countryModelDE.setIsocode("DE");
			modelService.save(countryModelDE);
		}
		assertNotNull(countryModelDE);

		final PhoneNumberModel nr1 = new PhoneNumberModel();
		nr1.setNumber("01771234567");
		nr1.setCountry(countryModelDE);

		final PhoneNumberModel nr2 = new PhoneNumberModel();
		nr2.setNumber("491771111111");
		nr2.setCountry(countryModelDE);

		final PhoneNumberListModel list = new PhoneNumberListModel();
		list.setCode("mylist");
		list.setNumbers(Arrays.asList(nr1, nr2));

		modelService.saveAll(nr1, nr2, list);

		assertTrue(phoneListService.isInList(list, "DE", "491771111111"));
		assertTrue(phoneListService.isInList(list, "DE", "+491771111111"));
		assertTrue(phoneListService.isInList(list, null, "+491771111111"));

		assertTrue(phoneListService.isInList(list, "DE", "0177 1234567"));
		assertTrue(phoneListService.isInList(list, "DE", "(0177)1234567"));

		assertFalse(phoneListService.isInList(list, "DE", "(0177)1234566"));

		// use as WHITELIST to allow
		assertTrue(phoneListService.isAllowedByList("DE", "491771111111", null, Collections.singletonList(list)));
		assertFalse(phoneListService.isAllowedByList("DE", "491771111112", null, Collections.singletonList(list)));

		// use as BLACKLIST to block
		assertFalse(phoneListService.isAllowedByList("DE", "491771111111", Collections.singletonList(list), null));
		assertFalse(phoneListService.isAllowedByList("DE", "(0177)1234567", Collections.singletonList(list), null));
		assertTrue(phoneListService.isAllowedByList("DE", "491771111112", Collections.singletonList(list), null));
	}
}
