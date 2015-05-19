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
package de.hybris.platform.commercefacades.product.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultPriceDataFactoryTest
{

	@InjectMocks
	private final DefaultPriceDataFactory defaultPriceDataFactory = new DefaultPriceDataFactory();
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private CommonI18NService commonI18NService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testFormatPrice()
	{

		Assert.assertEquals(
				"$33.30",
				defaultPriceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(33.3D),
						setUpPriceFactory("USD", "en", Locale.US)).getFormattedValue());

		Assert.assertEquals(
				"33,30 €",
				defaultPriceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(33.3D),
						setUpPriceFactory("EUR", "de", Locale.GERMANY)).getFormattedValue());


	}


	@Test
	public void test1000Threads() throws InterruptedException, ExecutionException
	{
		multiThreadedTest(1000);

	}



	private CurrencyModel setUpPriceFactory(final String currencyIso, final String languageIso, final Locale locale)
	{
		final CurrencyModel currencyModel = Mockito.mock(CurrencyModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);

		given(currencyModel.getIsocode()).willReturn(currencyIso);
		given(currencyModel.getDigits()).willReturn(Integer.valueOf(2));
		given(languageModel.getIsocode()).willReturn(languageIso);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(locale);
		return currencyModel;
	}


	private void multiThreadedTest(final int threadCount) throws InterruptedException, ExecutionException
	{
		final CurrencyModel currencyModel = setUpPriceFactory("USD", "en", Locale.US);

		final Callable<String> task = new Callable<String>()
		{
			@Override
			public String call()
			{
				return defaultPriceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(33.3D), currencyModel)
						.getFormattedValue();
			}
		};

		final List<Callable<String>> tasks = Collections.nCopies(threadCount, task);
		final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		final List<Future<String>> futures = executorService.invokeAll(tasks);
		final List<String> resultList = new ArrayList<>(futures.size());
		// Check for exceptions
		for (final Future<String> future : futures)
		{
			// Throws an exception if an exception was thrown by the task.
			resultList.add(future.get());
		}
		// Validate the IDs
		Assert.assertEquals(threadCount, futures.size());
	}

}
