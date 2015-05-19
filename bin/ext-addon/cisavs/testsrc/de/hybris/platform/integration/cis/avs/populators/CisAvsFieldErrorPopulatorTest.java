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
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.avs.model.CisField;
import com.hybris.cis.api.avs.model.CisFieldError;
import com.hybris.cis.api.avs.model.CisFieldErrorCode;


@UnitTest
public class CisAvsFieldErrorPopulatorTest
{
	private CisAvsFieldErrorPopulator populator;

	@Before
	public void init()
	{
		populator = new CisAvsFieldErrorPopulator();
	}

	@Test
	public void shouldPopulateCityMissing()
	{
		final List<CisFieldError> source = new ArrayList<CisFieldError>();
		final CisFieldError cisFieldError = new CisFieldError();
		cisFieldError.setField(CisField.CITY);
		cisFieldError.setErrorCode(CisFieldErrorCode.MISSING);
		source.add(cisFieldError);

		final List<AddressFieldErrorData> target = new ArrayList<AddressFieldErrorData>();
		populator.populate(source, target);

		Assert.assertEquals(1, target.size());
		Assert.assertEquals(AddressErrorCode.MISSING, target.get(0).getErrorCode());
		Assert.assertEquals(AddressFieldType.CITY, target.get(0).getFieldType());
	}

	@Test
	public void shouldPopulateCountryError()
	{
		final List<CisFieldError> source = new ArrayList<CisFieldError>();
		final CisFieldError cisFieldError = new CisFieldError();
		cisFieldError.setField(CisField.COUNTRY);
		cisFieldError.setErrorCode(CisFieldErrorCode.INVALID);
		source.add(cisFieldError);

		final List<AddressFieldErrorData> target = new ArrayList<AddressFieldErrorData>();
		populator.populate(source, target);

		Assert.assertEquals(1, target.size());
		Assert.assertEquals(AddressErrorCode.INVALID, target.get(0).getErrorCode());
		Assert.assertEquals(AddressFieldType.COUNTRY, target.get(0).getFieldType());
	}
}
