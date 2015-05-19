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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.i18n.LocalizationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.Ignore;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
@Ignore
public abstract class PropertyFieldValueProviderTestBase
{
	protected abstract String getPropertyName();

	protected abstract void configure();

	protected static final String TEST_EN_LANG_CODE = "en";
	protected static final String TEST_DE_LANG_CODE = "de";

	//Required by Test class
	private AbstractPropertyFieldValueProvider propertyFieldValueProvider;
	@Mock
	protected FieldNameProvider fieldNameProvider;
	@Mock
	protected CommonI18NService commonI18NService;
	@Mock
	protected I18NService i18nService;
	@Mock
	protected ModelService modelService;
	@SuppressWarnings("deprecation")
	@Mock
	protected LocalizationService localeService;

	//Objects used by test class (some as params)
	@Mock
	protected LanguageModel enLanguageModel;
	@Mock
	protected LanguageModel deLanguageModel;
	@Mock
	protected IndexConfig indexConfig;

	protected final Locale enLocale = Locale.ENGLISH;
	protected final Locale deLocale = Locale.GERMANY;
	protected final String nullString = null;


	protected void configureBase()
	{
		MockitoAnnotations.initMocks(this);

		given(enLanguageModel.getIsocode()).willReturn(TEST_EN_LANG_CODE);
		given(deLanguageModel.getIsocode()).willReturn(TEST_DE_LANG_CODE);
		final Collection<LanguageModel> languages = new ArrayList<LanguageModel>();
		languages.add(enLanguageModel);
		languages.add(deLanguageModel);
		given(indexConfig.getLanguages()).willReturn(languages);

		given(i18nService.getCurrentLocale()).willReturn(enLocale);
		given(commonI18NService.getLocaleForLanguage(enLanguageModel)).willReturn(enLocale);
		given(commonI18NService.getLocaleForLanguage(deLanguageModel)).willReturn(deLocale);

		getPropertyFieldValueProvider().setLocaleService(localeService);
		getPropertyFieldValueProvider().setI18nService(i18nService);
		getPropertyFieldValueProvider().setModelService(modelService);
	}

	protected AbstractPropertyFieldValueProvider getPropertyFieldValueProvider()
	{
		return propertyFieldValueProvider;
	}

	protected void setPropertyFieldValueProvider(final AbstractPropertyFieldValueProvider propertyFieldValueProvider)
	{
		this.propertyFieldValueProvider = propertyFieldValueProvider;
	}
}
