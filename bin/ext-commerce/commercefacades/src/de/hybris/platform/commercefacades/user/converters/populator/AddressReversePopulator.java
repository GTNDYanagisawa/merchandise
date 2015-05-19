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

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 */
public class AddressReversePopulator implements Populator<AddressData, AddressModel>
{
	private CommonI18NService commonI18NService;
	private FlexibleSearchService flexibleSearchService;

	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel) throws ConversionException
	{
		Assert.notNull(addressData, "Parameter addressData cannot be null.");
		Assert.notNull(addressModel, "Parameter addressModel cannot be null.");

		if (addressData.getTitleCode() != null)
		{
			final TitleModel title = new TitleModel();
			title.setCode(addressData.getTitleCode());
			addressModel.setTitle(flexibleSearchService.getModelByExample(title));
		}
		addressModel.setFirstname(addressData.getFirstName());
		addressModel.setLastname(addressData.getLastName());
		addressModel.setCompany(addressData.getCompanyName());
		addressModel.setLine1(addressData.getLine1());
		addressModel.setLine2(addressData.getLine2());
		addressModel.setTown(addressData.getTown());
		addressModel.setPostalcode(addressData.getPostalCode());
		addressModel.setPhone1(addressData.getPhone());

		if (addressData.getCountry() != null)
		{
			final String isocode = addressData.getCountry().getIsocode();
			try
			{
				final CountryModel countryModel = getCommonI18NService().getCountry(isocode);
				addressModel.setCountry(countryModel);
			}
			catch (final UnknownIdentifierException e)
			{
				throw new ConversionException("No country with the code " + isocode + " found.", e);
			}
			catch (final AmbiguousIdentifierException e)
			{
				throw new ConversionException("More than one country with the code " + isocode + " found.", e);
			}
		}

		if (addressData.getRegion() != null)
		{
			final String isocode = addressData.getRegion().getIsocode();
			try
			{
				final RegionModel regionModel = getCommonI18NService().getRegion(
						getCommonI18NService().getCountry(addressData.getCountry().getIsocode()), isocode);
				addressModel.setRegion(regionModel);
			}
			catch (final UnknownIdentifierException e)
			{
				throw new ConversionException("No region with the code " + isocode + " found.", e);
			}
			catch (final AmbiguousIdentifierException e)
			{
				throw new ConversionException("More than one region with the code " + isocode + " found.", e);
			}
		}

		addressModel.setBillingAddress(Boolean.valueOf(addressData.isBillingAddress()));
		addressModel.setShippingAddress(Boolean.valueOf(addressData.isShippingAddress()));
		addressModel.setVisibleInAddressBook(Boolean.valueOf(addressData.isVisibleInAddressBook()));
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
