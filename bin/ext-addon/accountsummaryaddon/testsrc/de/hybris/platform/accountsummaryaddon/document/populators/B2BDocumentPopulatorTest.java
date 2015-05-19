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
package de.hybris.platform.accountsummaryaddon.document.populators;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.formatters.AmountFormatter;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;

@UnitTest
public class B2BDocumentPopulatorTest
{

	private static final String CUR_ISOCODE = "currIsoCode";
	private static final String CUR_USD = "USD";

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	
	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	
	@Mock
	private AmountFormatter amountFormatter;
	
	@InjectMocks
	private final B2BDocumentPopulator b2BDocumentPopulator = new B2BDocumentPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldPopulateTargetObject()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final CurrencyData curData = new CurrencyData();
		curData.setIsocode(CUR_ISOCODE);
		curData.setName(CUR_USD);

		final B2BDocumentTypeModel invoiceType = mock(B2BDocumentTypeModel.class);
		given(invoiceType.getCode()).willReturn("Invoice");
		given(invoiceType.getName()).willReturn("Invoice");

		final B2BDocumentModel source = new B2BDocumentModel();
		source.setDocumentNumber("1");
		source.setAmount(new BigDecimal("100.10"));
		source.setOpenAmount(new BigDecimal("50.25"));
		source.setDate(new Date());
		source.setDueDate(new Date());
		source.setDocumentType(invoiceType);
		source.setStatus(DocumentStatus.OPEN);
		source.setCurrency(currencyModel);

		final B2BDocumentData target = new B2BDocumentData();

		given(currencyModel.getIsocode()).willReturn(CUR_ISOCODE);
		given(currencyConverter.convert(currencyModel)).willReturn(curData);

		final LanguageModel language = new LanguageModel();
		final Locale locale = Locale.CANADA;
		given(commerceCommonI18NService.getLocaleForLanguage(language)).willReturn(locale);
		given(commonI18NService.getCurrentLanguage()).willReturn(language);
		given(amountFormatter.formatAmount(source.getAmount(), currencyModel, locale )).willReturn("100.10");
		given(amountFormatter.formatAmount(source.getOpenAmount(), currencyModel, locale )).willReturn("50.25");

		b2BDocumentPopulator.populate(source, target);

		assertEquals("The documentNumber should be equals", source.getDocumentNumber(), target.getDocumentNumber());
		assertEquals("The amount should be equals", source.getAmount(), target.getAmount());
		assertEquals("The open amount should be equals", source.getOpenAmount(), target.getOpenAmount());
		assertEquals("The date should be equals", source.getDate(), target.getDate());
		assertEquals("The due date should be equals", source.getDueDate(), target.getDueDate());
		assertEquals("The documenttype.code should be equals", source.getDocumentType().getCode(), target.getDocumentType()
				.getCode());
		assertEquals("The documenttype.name should be equals", source.getDocumentType().getName(), target.getDocumentType()
				.getName());
		assertEquals("The status should be equals", source.getStatus().getCode(), target.getStatus());
		assertEquals("The currency.isocode should be equals", CUR_ISOCODE, target.getCurrency().getIsocode());
		assertEquals("The currency.name should be equals", CUR_USD, target.getCurrency().getName());
	}
}
