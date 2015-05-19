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
package de.hybris.platform.integration.cis.avs.populators;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.strategies.ShowSuggestedAddressesStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.avs.model.AvsResult;
import com.hybris.cis.api.avs.model.CisField;
import com.hybris.cis.api.avs.model.CisFieldError;
import com.hybris.cis.api.avs.model.CisFieldErrorCode;
import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisDecision;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisAddressVerificationResultPopulatorTest
{
	private CisAddressVerificationResultPopulator populator;

	@Mock
	private Converter<List<CisFieldError>, List<AddressFieldErrorData>> fieldErrorConverter;

	@Mock
	private Converter<List<CisAddress>, List<AddressModel>> reverseCisAddressesConverter;

	@Mock
	private ShowSuggestedAddressesStrategy showSuggestedAddressesStrategy;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this.getClass());
		populator = new CisAddressVerificationResultPopulator();
		populator.setCisAvsFieldErrorConverter(fieldErrorConverter);
		populator.setCisAvsReverseAddressesConverter(reverseCisAddressesConverter);
		populator.setShowSuggestedAddressesStrategy(showSuggestedAddressesStrategy);
	}

	@Test
	public void shouldPopulateAcceptNoSuggestedAddress()
	{
		final AvsResult source = new AvsResult();
		source.setDecision(CisDecision.ACCEPT);

		Mockito.when(Boolean.valueOf(showSuggestedAddressesStrategy.shouldAddressSuggestionsBeShown())).thenReturn(Boolean.TRUE);

		final AddressVerificationResultData target = new AddressVerificationResultData();
		populator.populate(source, target);

		Assert.assertEquals(target.getDecision(), AddressVerificationDecision.ACCEPT);
		Assert.assertTrue(target.getSuggestedAddresses() == null || target.getSuggestedAddresses().isEmpty());
		Assert.assertTrue(target.getFieldErrors() == null || target.getFieldErrors().isEmpty());
	}

	@Test
	public void shouldPopulateRejectNoSuggestedAddressNoFieldErrors()
	{
		final AvsResult source = new AvsResult();
		source.setDecision(CisDecision.REJECT);

		Mockito.when(Boolean.valueOf(showSuggestedAddressesStrategy.shouldAddressSuggestionsBeShown())).thenReturn(Boolean.TRUE);

		final AddressVerificationResultData target = new AddressVerificationResultData();
		populator.populate(source, target);

		Assert.assertEquals(target.getDecision(), AddressVerificationDecision.UNKNOWN);
		Assert.assertTrue(target.getSuggestedAddresses() == null || target.getSuggestedAddresses().isEmpty());
		Assert.assertEquals(1, target.getFieldErrors().size());
		Assert.assertEquals(AddressFieldType.UNKNOWN, ((AddressFieldErrorData) target.getFieldErrors().get(0)).getFieldType());
	}

	@Test
	public void shouldPopulateRejectNoSuggestedAddressWithFieldErrors()
	{
		final AvsResult source = new AvsResult();
		source.setDecision(CisDecision.REJECT);
		source.setFieldErrors(new ArrayList<CisFieldError>());
		final CisFieldError cisFieldError = new CisFieldError();
		cisFieldError.setErrorCode(CisFieldErrorCode.MISSING);
		cisFieldError.setField(CisField.CITY);
		source.getFieldErrors().add(cisFieldError);

		final List<AddressFieldErrorData> errors = new ArrayList<AddressFieldErrorData>();
		final AddressFieldErrorData addressFieldErrorData = new AddressFieldErrorData();
		addressFieldErrorData.setErrorCode(AddressErrorCode.MISSING);
		addressFieldErrorData.setFieldType(AddressFieldType.CITY);
		errors.add(addressFieldErrorData);


		Mockito.when(Boolean.valueOf(showSuggestedAddressesStrategy.shouldAddressSuggestionsBeShown())).thenReturn(Boolean.TRUE);
		Mockito.when(fieldErrorConverter.convert(Mockito.anyList())).thenReturn(errors);

		final AddressVerificationResultData target = new AddressVerificationResultData();
		populator.populate(source, target);

		Assert.assertEquals(target.getDecision(), AddressVerificationDecision.REJECT);
		Assert.assertTrue(target.getSuggestedAddresses() == null || target.getSuggestedAddresses().isEmpty());
		Assert.assertEquals(1, target.getFieldErrors().size());
		Assert.assertEquals(AddressFieldType.CITY, ((AddressFieldErrorData) target.getFieldErrors().get(0)).getFieldType());
		Assert.assertEquals(AddressErrorCode.MISSING, ((AddressFieldErrorData) target.getFieldErrors().get(0)).getErrorCode());
	}


	@Test
	public void shouldPopulateReviewWithSuggestedAddresses()
	{
		final AvsResult source = new AvsResult();
		source.setDecision(CisDecision.REVIEW);

		final AddressModel addressModel = new AddressModel();
		addressModel.setStreetnumber("1234");
		final ArrayList<AddressModel> suggestedAddresses = new ArrayList<AddressModel>();
		suggestedAddresses.add(addressModel);

		Mockito.when(Boolean.valueOf(showSuggestedAddressesStrategy.shouldAddressSuggestionsBeShown())).thenReturn(Boolean.TRUE);
		Mockito.when(reverseCisAddressesConverter.convert(Mockito.anyList())).thenReturn(suggestedAddresses);

		final AddressVerificationResultData target = new AddressVerificationResultData();
		populator.populate(source, target);

		Assert.assertEquals(target.getDecision(), AddressVerificationDecision.REVIEW);
		Assert.assertEquals(1, target.getSuggestedAddresses().size());
		Assert.assertEquals("1234", ((AddressModel) target.getSuggestedAddresses().get(0)).getStreetnumber());
		Assert.assertTrue(target.getFieldErrors() == null || target.getFieldErrors().size() == 0);
	}
}
