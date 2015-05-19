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
package de.hybris.platform.integration.cis.avs.impl;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@ManualTest
public class CisAddressVerificationServiceIntegrationTest extends ServicelayerTest
{
	private final static String TEST_NAME = "test";

	@Resource
	private DefaultCisAddressVerificationService addressVerificationService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private ModelService modelService;

	@Resource
	private BaseSiteService baseSiteService;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/ondemandcommon/test/testAcceleratorData.csv", "UTF-8");
		final BaseSiteModel site = baseSiteService.getBaseSiteForUID("testSite");
		Assert.assertNotNull("no baseSite with uid 'testSite", site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);
	}

	@Test
	public void shouldValidateUSAddressAccept()
	{
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setLine1("1700 Broadway");
		addressModel.setTown("New York");
		final CountryModel country = commonI18NService.getCountry("US");
		addressModel.setCountry(country);
		addressModel.setFirstname(TEST_NAME);
		addressModel.setLastname(TEST_NAME);
		addressModel.setRegion(commonI18NService.getRegion(country, "US-NY"));
		addressModel.setPostalcode("10019");

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = addressVerificationService
				.verifyAddress(addressModel);
		Assert.assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		Assert.assertNotNull("Suggested addressess should not be null", result.getSuggestedAddresses());
		Assert.assertEquals(1, result.getSuggestedAddresses().size());
	}

	@Test
	public void shouldValidateUSAddressReview()
	{
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setLine1("1700 Broadway");
		addressModel.setTown("New York");
		final CountryModel country = commonI18NService.getCountry("US");
		addressModel.setCountry(country);
		addressModel.setFirstname(TEST_NAME);
		addressModel.setLastname(TEST_NAME);
		addressModel.setRegion(commonI18NService.getRegion(country, "US-NY"));
		addressModel.setPostalcode("11222");

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = addressVerificationService
				.verifyAddress(addressModel);

		Assert.assertEquals(AddressVerificationDecision.REVIEW, result.getDecision());
		Assert.assertNotNull("Suggested addressess should not be null", result.getSuggestedAddresses());
		Assert.assertEquals(1, result.getSuggestedAddresses().size());
		Assert.assertEquals("10019-5905", result.getSuggestedAddresses().get(0).getPostalcode());
	}

	@Test
	public void shouldValidateUSAddressUnknown()
	{
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setLine1("1700 Brooooooadwayyyyy");
		addressModel.setLine2("26th floor");
		addressModel.setTown("New York");
		final CountryModel country = commonI18NService.getCountry("US");
		addressModel.setCountry(country);
		addressModel.setFirstname(TEST_NAME);
		addressModel.setLastname(TEST_NAME);
		addressModel.setRegion(commonI18NService.getRegion(country, "US-MA"));
		addressModel.setPostalcode("11222");

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = addressVerificationService
				.verifyAddress(addressModel);
		Assert.assertEquals(AddressVerificationDecision.UNKNOWN, result.getDecision());
		Assert.assertEquals(0, result.getSuggestedAddresses().size());
	}
}
