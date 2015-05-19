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
package de.hybris.platform.commercefacades.user.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.attribute.DynamicAttributesProvider;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AddressReversePopulatorTest
{
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private DynamicAttributesProvider dynamicAttributesProvider;

	private AddressReversePopulator addressReversePopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		addressReversePopulator = new AddressReversePopulator();
		addressReversePopulator.setCommonI18NService(commonI18NService);
		addressReversePopulator.setFlexibleSearchService(flexibleSearchService);
	}

	@Test
	public void testPopulateAll()
	{
		final AddressData addressData = mock(AddressData.class);
		final AddressModel addressModel = new AddressModel();
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(addressModel).setDynamicAttributesProvider(dynamicAttributesProvider);
		final TitleModel titleModel = mock(TitleModel.class);
		final CountryData countryData = mock(CountryData.class);
		final CountryModel coutryModel = mock(CountryModel.class);
		given(addressData.getTitleCode()).willReturn("titleCode");
		given(flexibleSearchService.getModelByExample(Mockito.any(TitleModel.class))).willReturn(titleModel);
		given(addressData.getFirstName()).willReturn("firstName");
		given(addressData.getLastName()).willReturn("lastname");
		given(addressData.getCompanyName()).willReturn("companyName");
		given(addressData.getLine1()).willReturn("line1");
		given(addressData.getLine2()).willReturn("line2");
		given(addressData.getTown()).willReturn("town");
		given(addressData.getPostalCode()).willReturn("postalCode");
		given(addressData.getPhone()).willReturn("phone");
		given(Boolean.valueOf(addressData.isBillingAddress())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(addressData.isShippingAddress())).willReturn(Boolean.TRUE);
		given(addressData.getCountry()).willReturn(countryData);
		given(countryData.getIsocode()).willReturn("countryIso");
		given(commonI18NService.getCountry("countryIso")).willReturn(coutryModel);
		addressReversePopulator.populate(addressData, addressModel);
		Assert.assertEquals(titleModel, addressModel.getTitle());
		Assert.assertEquals("firstName", addressModel.getFirstname());
		Assert.assertEquals("lastname", addressModel.getLastname());
		Assert.assertEquals("companyName", addressModel.getCompany());
		Assert.assertEquals("town", addressModel.getTown());
		Assert.assertEquals("postalCode", addressModel.getPostalcode());
		Assert.assertEquals("phone", addressModel.getPhone1());
		Assert.assertEquals(coutryModel, addressModel.getCountry());
		Assert.assertTrue(addressModel.getBillingAddress().booleanValue());
		Assert.assertTrue(addressModel.getShippingAddress().booleanValue());
		verify(dynamicAttributesProvider).set(addressModel, "line1", "line1");
		verify(dynamicAttributesProvider).set(addressModel, "line2", "line2");
	}

	@Test
	public void testPopulateEssencial()
	{
		final AddressData addressData = mock(AddressData.class);
		final AddressModel addressModel = new AddressModel();
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(addressModel).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(addressData.getTitleCode()).willReturn(null);
		given(addressData.getFirstName()).willReturn("firstName");
		given(addressData.getLastName()).willReturn("lastname");
		given(addressData.getCompanyName()).willReturn("companyName");
		given(addressData.getLine1()).willReturn("line1");
		given(addressData.getLine2()).willReturn("line2");
		given(addressData.getTown()).willReturn("town");
		given(addressData.getPostalCode()).willReturn("postalCode");
		given(addressData.getPhone()).willReturn("phone");
		given(Boolean.valueOf(addressData.isBillingAddress())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(addressData.isShippingAddress())).willReturn(Boolean.TRUE);
		given(addressData.getCountry()).willReturn(null);
		addressReversePopulator.populate(addressData, addressModel);
		Assert.assertEquals("firstName", addressModel.getFirstname());
		Assert.assertEquals("lastname", addressModel.getLastname());
		Assert.assertEquals("companyName", addressModel.getCompany());
		Assert.assertEquals("town", addressModel.getTown());
		Assert.assertEquals("postalCode", addressModel.getPostalcode());
		Assert.assertEquals("phone", addressModel.getPhone1());
		Assert.assertTrue(addressModel.getBillingAddress().booleanValue());
		Assert.assertTrue(addressModel.getShippingAddress().booleanValue());
		verify(dynamicAttributesProvider).set(addressModel, "line1", "line1");
		verify(dynamicAttributesProvider).set(addressModel, "line2", "line2");
		Assert.assertNull(addressModel.getTitle());
		Assert.assertNull(addressModel.getCountry());
	}

	@Test(expected = ConversionException.class)
	public void testPopulateCountryUnknownIdent()
	{
		final AddressData addressData = mock(AddressData.class);
		final AddressModel addressModel = new AddressModel();
		final CountryData countryData = mock(CountryData.class);
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(addressModel).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(addressData.getTitleCode()).willReturn(null);
		given(addressData.getCountry()).willReturn(countryData);
		given(countryData.getIsocode()).willReturn("countryIso");
		given(commonI18NService.getCountry("countryIso")).willThrow(new UnknownIdentifierException(""));
		addressReversePopulator.populate(addressData, addressModel);
	}

	@Test(expected = ConversionException.class)
	public void testPopulateCountryAmbiguousIdent()
	{
		final AddressData addressData = mock(AddressData.class);
		final AddressModel addressModel = new AddressModel();
		final CountryData countryData = mock(CountryData.class);
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(addressModel).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(addressData.getTitleCode()).willReturn(null);
		given(addressData.getCountry()).willReturn(countryData);
		given(countryData.getIsocode()).willReturn("countryIso");
		given(commonI18NService.getCountry("countryIso")).willThrow(new AmbiguousIdentifierException(""));
		addressReversePopulator.populate(addressData, addressModel);
	}

	private ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}

}
