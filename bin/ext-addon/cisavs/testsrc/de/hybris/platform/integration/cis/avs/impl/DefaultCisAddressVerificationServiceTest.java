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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.populators.CisAvsAddressMatchingPopulator;
import de.hybris.platform.integration.cis.avs.strategies.CheckVerificationRequiredStrategy;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.integration.commons.services.OndemandPreferenceSelectorService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.testframework.TestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.cis.api.avs.model.AvsResult;
import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.client.rest.avs.AvsClient;
import com.hybris.commons.client.RestResponse;


@UnitTest
public class DefaultCisAddressVerificationServiceTest
{
	private DefaultCisAddressVerificationService avs;
	@Mock
	private CheckVerificationRequiredStrategy checkVerificationRequiredStrategy;
	@Mock
	private AvsClient avsClient;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private RestResponse<AvsResult> restResponse;
	@Mock
	private SessionService sessionService;
	@Mock
	private TenantService tenantService;
	@Mock
	private Converter<AvsResult, AddressVerificationResultData> avrConverter;
	@Mock
	private Converter<AddressModel, CisAddress> cisAddressConverter;
	@Mock
	private CartService cartService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private OndemandPreferenceSelectorService ondemandPreferenceSelectorService;
	@Mock
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;
	@Mock
	private OndemandHystrixCommandConfiguration config;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		avs = new DefaultCisAddressVerificationService();
		avs.setCisAvsAddressMatchingPopulator(new CisAvsAddressMatchingPopulator());
		avs.setApplyVerificationStrategy(checkVerificationRequiredStrategy);
		avs.setCisAvsResultAddressVerificationResultDataConverter(avrConverter);
		avs.setCisAvsAddressConverter(cisAddressConverter);
		avs.setAvsClient(avsClient);
		avs.setHystrixCommandConfig(config);
		avs.setBaseStoreService(baseStoreService);
		avs.setCartService(cartService);
		avs.setOndemandHystrixCommandFactory(ondemandHystrixCommandFactory);
	}

	@Test
	public void shouldReturnAcceptIfNoValidationRequired()
	{
		final AddressModel address = new AddressModel();

		Mockito.when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.FALSE);
		final AvsResult avsResult = Mockito.mock(AvsResult.class);
		Mockito.when(avsResult.getDecision()).thenReturn(CisDecision.ACCEPT);
		Mockito.when(avsResult.getSuggestedAddresses()).thenReturn(Collections.singletonList(new CisAddress()));
		final OndemandHystrixCommandFactory.OndemandHystrixCommand command = Mockito
				.mock(OndemandHystrixCommandFactory.OndemandHystrixCommand.class);
		Mockito.when(
				ondemandHystrixCommandFactory.newCommand(Mockito.any(OndemandHystrixCommandConfiguration.class),
						Mockito.any(HystrixExecutable.class))).thenReturn(command);
		Mockito.when(command.execute()).thenReturn(avsResult);

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
				.verifyAddress(address);
		Assert.assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		Assert.assertTrue(CollectionUtils.isEmpty(result.getSuggestedAddresses()));
	}

	@Test
	public void shouldReturnUnknownIfClientFails()
	{
		final AddressModel address = new AddressModel();
		Mockito.when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.TRUE);
		Mockito.when(avsClient.verifyAddress(Mockito.any(String.class), Mockito.any(CisAddress.class))).thenThrow(
				new RuntimeException("Fake error from Client"));

		final OndemandHystrixCommandFactory.OndemandHystrixCommand command = Mockito
				.mock(OndemandHystrixCommandFactory.OndemandHystrixCommand.class);
		Mockito.when(
				ondemandHystrixCommandFactory.newCommand(Mockito.any(OndemandHystrixCommandConfiguration.class),
						Mockito.any(HystrixExecutable.class))).thenReturn(command);
		Mockito.when(command.execute()).thenReturn(null);
		try
		{
			TestUtils.disableFileAnalyzer("Expected exception from avs client.");
			final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
					.verifyAddress(address);
			Assert.assertEquals(AddressVerificationDecision.UNKNOWN, result.getDecision());
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void shouldReturnAcceptIfClientReturnAccept()
	{
		final AddressModel address = new AddressModel();
		final CountryModel countryModel = new CountryModel();
		countryModel.setIsocode("US");
		address.setCountry(countryModel);
		address.setFirstname("Der");
		address.setLastname("Buck");
		address.setTown("New York");
		final RegionModel region = new RegionModel();
		region.setIsocode("NY");
		Mockito.when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.TRUE);

		final AvsResult avsResult = Mockito.mock(AvsResult.class);
		Mockito.when(avsResult.getDecision()).thenReturn(CisDecision.ACCEPT);

		final CisAddress suggestedCisAddress = new CisAddress();
		suggestedCisAddress.setCity("New York");
		suggestedCisAddress.setFirstName("Der");
		suggestedCisAddress.setLastName("Buck");
		suggestedCisAddress.setCountry("US");
		suggestedCisAddress.setState("NY");

		Mockito.when(cisAddressConverter.convert(address)).thenReturn(suggestedCisAddress);

		final AddressVerificationResultData addressVerificationResultData = new AddressVerificationResultData();
		addressVerificationResultData.setSuggestedAddresses(null);
		addressVerificationResultData.setDecision(AddressVerificationDecision.ACCEPT);
		Mockito.when(avrConverter.convert(avsResult)).thenReturn(addressVerificationResultData);

		Mockito.when(avsResult.getSuggestedAddresses()).thenReturn(new ArrayList(Collections.singletonList(suggestedCisAddress)));
		final OndemandHystrixCommandFactory.OndemandHystrixCommand command = Mockito
				.mock(OndemandHystrixCommandFactory.OndemandHystrixCommand.class);
		Mockito.when(
				ondemandHystrixCommandFactory.newCommand(Mockito.any(OndemandHystrixCommandConfiguration.class),
						Mockito.any(HystrixExecutable.class))).thenReturn(command);
		Mockito.when(command.execute()).thenReturn(avsResult);

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
				.verifyAddress(address);
		Assert.assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		Assert.assertTrue(CollectionUtils.isEmpty(result.getSuggestedAddresses()));
	}


	@Test
	public void checkAddressEqualityForMissingState()
	{
		final AddressModel address = new AddressModel();
		final CountryModel countryModel = new CountryModel();
		countryModel.setIsocode("US");
		address.setCountry(countryModel);
		address.setFirstname("Der");
		address.setLastname("Buck");
		address.setTown("New York");
		address.setRegion(null);

		final AddressModel suggestedAddressModel = new AddressModel();
		suggestedAddressModel.setCountry(countryModel);
		suggestedAddressModel.setFirstname("Der");
		suggestedAddressModel.setLastname("Buck");
		suggestedAddressModel.setTown("New York");
		final RegionModel region = new RegionModel();
		region.setIsocode("NY");
		suggestedAddressModel.setRegion(region);
		final List<AddressModel> suggestedAddressList = new ArrayList<AddressModel>(1);
		suggestedAddressList.add(suggestedAddressModel);

		Mockito.when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.TRUE);
		final AvsResult avsResult = new AvsResult();

		final CisAddress cisAddress = new CisAddress();
		cisAddress.setCity("New York");
		cisAddress.setFirstName("Der");
		cisAddress.setLastName("Buck");
		cisAddress.setCountry("US");
		cisAddress.setState(null);
		Mockito.when(cisAddressConverter.convert(address)).thenReturn(cisAddress);
		final List<CisAddress> cisAddressList = new ArrayList<CisAddress>(1);
		cisAddressList.add(cisAddress);

		final CisAddress suggestedCisAddress = new CisAddress();
		suggestedCisAddress.setCity("New York");
		suggestedCisAddress.setFirstName("Der");
		suggestedCisAddress.setLastName("Buck");
		suggestedCisAddress.setCountry("US");
		suggestedCisAddress.setState("NY");

		final List<CisAddress> suggestedCisAddressList = new ArrayList<CisAddress>(1);
		suggestedCisAddressList.add(suggestedCisAddress);
		avsResult.setSuggestedAddresses(suggestedCisAddressList);
		avsResult.setDecision(CisDecision.ACCEPT);
		final OndemandHystrixCommandFactory.OndemandHystrixCommand command = Mockito
				.mock(OndemandHystrixCommandFactory.OndemandHystrixCommand.class);
		Mockito.when(
				ondemandHystrixCommandFactory.newCommand(Mockito.any(OndemandHystrixCommandConfiguration.class),
						Mockito.any(HystrixExecutable.class))).thenReturn(command);
		Mockito.when(command.execute()).thenReturn(avsResult);


		final AddressVerificationResultData addressVerificationResultData = new AddressVerificationResultData();
		addressVerificationResultData.setSuggestedAddresses(suggestedAddressList);
		addressVerificationResultData.setDecision(AddressVerificationDecision.ACCEPT);
		Mockito.when(avrConverter.convert(avsResult)).thenReturn(addressVerificationResultData);

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
				.verifyAddress(address);
		Assert.assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		Assert.assertTrue(CollectionUtils.isNotEmpty(result.getSuggestedAddresses()));
		final AddressModel addressModel = result.getSuggestedAddresses().get(0);
		Assert.assertEquals(addressModel.getRegion().getIsocode(), "NY");
	}
}
