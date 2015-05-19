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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AddressPopulatorTest
{
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;

	private final AddressPopulator addressPopulator = new AddressPopulator();

	@Mock
	private Map<String, Converter<AddressModel, StringBuilder>> addressFormatConverterMap;
	@Mock
	private Converter<AddressModel, StringBuilder> defaultAddressFormatConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		addressPopulator.setAddressFormatConverterMap(addressFormatConverterMap);
		addressPopulator.setDefaultAddressFormatConverter(defaultAddressFormatConverter);
		addressConverter = new ConverterFactory<AddressModel, AddressData, AddressPopulator>().create(AddressData.class,
				addressPopulator);
	}

	@Test
	public void testConvert()
	{
		final AddressModel addressModel = mock(AddressModel.class);
		final PK pk = PK.parse("123");
		final TitleModel titleModel = mock(TitleModel.class);
		final CountryModel countryModel = mock(CountryModel.class);
		given(addressModel.getPk()).willReturn(pk);
		given(addressModel.getBillingAddress()).willReturn(Boolean.TRUE);
		given(addressModel.getShippingAddress()).willReturn(Boolean.TRUE);
		given(addressModel.getTitle()).willReturn(titleModel);
		given(addressModel.getFirstname()).willReturn("firstName");
		given(addressModel.getLastname()).willReturn("lastname");
		given(titleModel.getName()).willReturn("titleName");
		given(titleModel.getCode()).willReturn("titleCode");
		given(addressModel.getCompany()).willReturn("companyName");
		given(addressModel.getLine1()).willReturn("line1");
		given(addressModel.getLine2()).willReturn("line2");
		given(addressModel.getTown()).willReturn("town");
		given(addressModel.getPostalcode()).willReturn("postalCode");
		given(addressModel.getPhone1()).willReturn("phone");
		given(addressModel.getEmail()).willReturn("email");
		given(addressModel.getCountry()).willReturn(countryModel);
		given(countryModel.getIsocode()).willReturn("countryCode");
		given(countryModel.getName()).willReturn("countryName");
		given(defaultAddressFormatConverter.convert(Mockito.any(AddressModel.class))).willReturn(
				new StringBuilder("singleLineAddress"));
		final AddressData addressData = addressConverter.convert(addressModel);
		Assert.assertEquals("123", addressData.getId());
		Assert.assertTrue(addressData.isBillingAddress());
		Assert.assertTrue(addressData.isShippingAddress());
		Assert.assertEquals("titleName", addressData.getTitle());
		Assert.assertEquals("titleCode", addressData.getTitleCode());
		Assert.assertEquals("firstName", addressData.getFirstName());
		Assert.assertEquals("lastname", addressData.getLastName());
		Assert.assertEquals("companyName", addressData.getCompanyName());
		Assert.assertEquals("line1", addressData.getLine1());
		Assert.assertEquals("line2", addressData.getLine2());
		Assert.assertEquals("town", addressData.getTown());
		Assert.assertEquals("postalCode", addressData.getPostalCode());
		Assert.assertEquals("phone", addressData.getPhone());
		Assert.assertEquals("email", addressData.getEmail());
		Assert.assertEquals("countryCode", addressData.getCountry().getIsocode());
		Assert.assertEquals("countryName", addressData.getCountry().getName());

	}

	@Test
	public void testConvertNoTitle()
	{
		final AddressModel addressModel = mock(AddressModel.class);
		final PK pk = PK.parse("123");
		given(addressModel.getPk()).willReturn(pk);
		given(addressModel.getTitle()).willReturn(null);
		given(defaultAddressFormatConverter.convert(Mockito.any(AddressModel.class))).willReturn(
				new StringBuilder("singleLineAddress"));
		final AddressData addressData = addressConverter.convert(addressModel);
		Assert.assertEquals(null, addressData.getTitle());
		Assert.assertEquals(null, addressData.getTitleCode());
	}

	@Test
	public void testConvertNoCountry()
	{
		final AddressModel addressModel = mock(AddressModel.class);
		final PK pk = PK.parse("123");
		given(addressModel.getPk()).willReturn(pk);
		given(addressModel.getCountry()).willReturn(null);
		given(defaultAddressFormatConverter.convert(Mockito.any(AddressModel.class))).willReturn(
				new StringBuilder("singleLineAddress"));
		final AddressData addressData = addressConverter.convert(addressModel);
		Assert.assertEquals(null, addressData.getCountry());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertNull()
	{
		addressConverter.convert(null);
	}
}
