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

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 */
public class CustomerReversePopulator implements Populator<CustomerData, CustomerModel>
{
	private CommonI18NService commonI18NService;
	private CustomerNameStrategy customerNameStrategy;
	private AddressReversePopulator addressReversePopulator;
	private ModelService modelService;
	private UserService userService;

	@Override
	public void populate(final CustomerData source, final CustomerModel target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getCurrency() != null)
		{
			final String isocode = source.getCurrency().getIsocode();
			try
			{
				target.setSessionCurrency(getCommonI18NService().getCurrency(isocode));
			}
			catch (final UnknownIdentifierException e)
			{
				throw new ConversionException("No currency with the code " + isocode + " found.", e);
			}

		}
		if (source.getLanguage() != null)
		{
			final String isocode = source.getLanguage().getIsocode();
			try
			{
				target.setSessionLanguage(getCommonI18NService().getLanguage(isocode));
			}
			catch (final UnknownIdentifierException e)
			{
				throw new ConversionException("No language with the code " + isocode + " found.", e);
			}

		}

		if (target.getDefaultPaymentAddress() == null)
		{
			if (source.getDefaultBillingAddress() != null)
			{
				final AddressModel paymentAddress = getModelService().create(AddressModel.class);
				getAddressReversePopulator().populate(source.getDefaultBillingAddress(), paymentAddress);
				paymentAddress.setOwner(target);
				target.setDefaultPaymentAddress(paymentAddress);
			}
		}
		else
		{
			getAddressReversePopulator().populate(source.getDefaultBillingAddress(), target.getDefaultPaymentAddress());
		}

		if (target.getDefaultShipmentAddress() == null)
		{
			if (source.getDefaultShippingAddress() != null)
			{
				final AddressModel shipmentAddress = getModelService().create(AddressModel.class);
				getAddressReversePopulator().populate(source.getDefaultShippingAddress(), shipmentAddress);
				shipmentAddress.setOwner(target);
				target.setDefaultShipmentAddress(shipmentAddress);
			}
		}
		else
		{
			getAddressReversePopulator().populate(source.getDefaultShippingAddress(), target.getDefaultShipmentAddress());
		}

		target.setName(getCustomerNameStrategy().getName(source.getFirstName(), source.getLastName()));

		if (StringUtils.isNotBlank(source.getTitleCode()))
		{
			target.setTitle(getUserService().getTitleForCode(source.getTitleCode()));
		}
		else
		{
			target.setTitle(null);
		}

		setUid(source, target);
	}

	protected void setUid(final CustomerData source, final CustomerModel target)
	{
		target.setOriginalUid(source.getDisplayUid());
		target.setUid(source.getUid());
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

	protected CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	protected AddressReversePopulator getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final AddressReversePopulator addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
