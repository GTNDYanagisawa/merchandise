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
package de.hybris.platform.commercefacades.storesession.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class LanguagePopulatorTest
{
	private static final String LANGUAGE_CODE = "en";

	private final AbstractPopulatingConverter<LanguageModel, LanguageData> languageConverter = new ConverterFactory<LanguageModel, LanguageData, LanguagePopulator>()
			.create(LanguageData.class, new LanguagePopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvert()
	{
		final LanguageModel languageModel = mock(LanguageModel.class);
		given(languageModel.getName(Locale.ENGLISH)).willReturn(LANGUAGE_CODE);
		given(languageModel.getName()).willReturn(LANGUAGE_CODE);
		given(languageModel.getIsocode()).willReturn(LANGUAGE_CODE);
		given(languageModel.getActive()).willReturn(Boolean.TRUE);

		final LanguageData languageData = languageConverter.convert(languageModel);

		Assert.assertEquals(languageModel.getIsocode(), languageData.getIsocode());
		Assert.assertEquals(languageModel.getActive(), Boolean.valueOf(languageData.isActive()));
		Assert.assertEquals(languageModel.getName(), languageData.getNativeName());
		Assert.assertEquals(languageModel.getName(), languageData.getName());
	}
}
