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


import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CisAvsAddressMatchingPopulatorTest
{
	private CisAvsAddressMatchingPopulator cisAvsAddressMatchingPopulator;

	@Before
	public void init()
	{
		cisAvsAddressMatchingPopulator = new CisAvsAddressMatchingPopulator();

	}

	@Test
	public void shouldPopulate()
	{
		final AddressModel source = new AddressModel();
		final TitleModel title = new TitleModel();
		title.setCode("Mr");
		source.setTitle(title);
		source.setFirstname("firstname");
		source.setLastname("lastname");
		source.setCompany("company");
		source.setCellphone("cellphone");
		source.setEmail("email");

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> target = new AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>();
		target.setSuggestedAddresses(new ArrayList<AddressModel>());
		target.getSuggestedAddresses().add(new AddressModel());

		cisAvsAddressMatchingPopulator.populate(source, target);

		Assert.assertEquals(1, target.getSuggestedAddresses().size());
		Assert.assertEquals("Mr", target.getSuggestedAddresses().get(0).getTitle().getCode());
		Assert.assertEquals("firstname", target.getSuggestedAddresses().get(0).getFirstname());
		Assert.assertEquals("lastname", target.getSuggestedAddresses().get(0).getLastname());
		Assert.assertEquals("company", target.getSuggestedAddresses().get(0).getCompany());
		Assert.assertEquals("cellphone", target.getSuggestedAddresses().get(0).getCellphone());
		Assert.assertEquals("email", target.getSuggestedAddresses().get(0).getEmail());


	}

}
