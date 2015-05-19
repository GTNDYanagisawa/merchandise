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
 */
package de.hybris.platform.ycommercewebservices.populator;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Populates {@link AddressData} instance based on http request parameters:<br>
 * <ul>
 * <li>id</li>
 * <li>titleCode</li>
 * <li>firstName</li>
 * <li>lastName</li>
 * <li>line1</li>
 * <li>line2</li>
 * <li>town</li>
 * <li>postalCode</li>
 * <li>country.isocode</li>
 * <li>region.isocode</li>
 * <li>shippingAddress</li>
 * <li>billingAddress</li>
 * </ul>
 * 
 * You can set a parameter prefix.. I.e 'billingAddress'. Then the populator would search parameters with the prefix,
 * i.e : 'billingAddress.firstName', etc..
 * 
 * @author KKW
 * 
 */
@Component("httpRequestAddressDataPopulator")
@Scope("prototype")
public class HttpRequestAddressDataPopulator extends AbstractHttpRequestDataPopulator implements
		Populator<HttpServletRequest, AddressData>
{

	private static final String ADDRESS_ID = "id";
	private static final String TITLE_CODE = "titleCode";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String LINE1 = "line1";
	private static final String LINE2 = "line2";
	private static final String TOWN = "town";
	private static final String POSTCODE = "postalCode";
	private static final String COUNTRY = "country.isocode";
	private static final String REGION = "region.isocode";
	private static final String DEFAULT_ADDRESS = "defaultAddress";
	private String addressPrefix;
	@Resource
	private I18NFacade i18nFacade;
	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Override
	public void populate(final HttpServletRequest request, final AddressData addressData)
	{
		Assert.notNull(request, "Parameter request cannot be null.");
		Assert.notNull(addressData, "Parameter addressData cannot be null.");

		addressData.setId(updateStringValueFromRequest(request, ADDRESS_ID, addressData.getId()));
		addressData.setTitleCode(updateStringValueFromRequest(request, TITLE_CODE, addressData.getTitleCode()));
		addressData.setFirstName(updateStringValueFromRequest(request, FIRST_NAME, addressData.getFirstName()));
		addressData.setLastName(updateStringValueFromRequest(request, LAST_NAME, addressData.getLastName()));
		addressData.setLine1(updateStringValueFromRequest(request, LINE1, addressData.getLine1()));
		addressData.setLine2(updateStringValueFromRequest(request, LINE2, addressData.getLine2()));
		addressData.setTown(updateStringValueFromRequest(request, TOWN, addressData.getTown()));
		addressData.setPostalCode(updateStringValueFromRequest(request, POSTCODE, addressData.getPostalCode()));
		addressData.setCountry(updateCountryFromRequest(request, addressData.getCountry()));
		addressData.setRegion(updateRegionFromRequest(request, addressData.getRegion()));
		addressData.setDefaultAddress(updateBooleanValueFromRequest(request, DEFAULT_ADDRESS, addressData.isDefaultAddress()));
	}

	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	public void setAddressPrefix(final String addressPrefix)
	{
		this.addressPrefix = addressPrefix;
	}

	protected CountryData updateCountryFromRequest(final HttpServletRequest request, final CountryData defaultValue)
	{
		final String countryIsoCode = getRequestParameterValue(request, COUNTRY);
		if (StringUtils.isNotBlank(countryIsoCode))
		{
			final CountryData countryDataFromFacade;
			try
			{
				countryDataFromFacade = getCheckoutFacade().getCountryForIsocode(countryIsoCode);
			}
			catch (final UnknownIdentifierException e)
			{
				throw new RequestParameterException("No country with the code " + countryIsoCode + " found",
						RequestParameterException.UNKNOWN_IDENTIFIER, COUNTRY, e);
			}
			if (countryDataFromFacade != null)
			{
				return countryDataFromFacade;
			}
			else
			{
				throw new RequestParameterException("No country with the code " + countryIsoCode + " found",
						RequestParameterException.UNKNOWN_IDENTIFIER, COUNTRY);
			}
		}
		return defaultValue;
	}

	protected RegionData updateRegionFromRequest(final HttpServletRequest request, final RegionData defaultValue)
	{
		final String countryIsoCode = getRequestParameterValue(request, COUNTRY);
		final String regionIsoCode = getRequestParameterValue(request, REGION);
		if (StringUtils.isNotBlank(countryIsoCode) && StringUtils.isNotBlank(regionIsoCode))
		{
			final RegionData regionDataFromFacade;
			try
			{
				regionDataFromFacade = i18nFacade.getRegion(countryIsoCode, regionIsoCode);
			}
			catch (final UnknownIdentifierException ex)
			{
				throw new RequestParameterException("No region with the code " + regionIsoCode + " found.",
						RequestParameterException.UNKNOWN_IDENTIFIER, REGION, ex);
			}
			if (regionDataFromFacade != null)
			{
				return regionDataFromFacade;
			}
			else
			{
				throw new RequestParameterException("No region with the code " + regionIsoCode + " found.",
						RequestParameterException.UNKNOWN_IDENTIFIER, REGION);
			}
		}
		return defaultValue;
	}

	@Override
	protected String getRequestParameterValue(final HttpServletRequest request, final String paramName)
	{
		if (addressPrefix == null)
		{
			return request.getParameter(paramName);
		}
		return request.getParameter(addressPrefix + '.' + paramName);
	}
}
