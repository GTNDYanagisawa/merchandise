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
package de.hybris.platform.ycommercewebservices.customer.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Extended populator implementation for {@link de.hybris.platform.core.model.user.CustomerModel} as source and
 * {@link de.hybris.platform.commercefacades.user.data.CustomerData} as target type.
 */
public class ExtendedCustomerPopulator implements Populator<CustomerModel, CustomerData>
{
    private Converter<AddressModel, AddressData> addressConverter;

    protected Converter<AddressModel, AddressData> getAddressConverter()
    {
        return addressConverter;
    }

    @Required
    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
    {
        this.addressConverter = addressConverter;
    }

	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getTitle() != null)
		{
			target.setTitle(source.getTitle().getName());
		}

        if (source.getDefaultPaymentAddress() != null)
        {
            target.setDefaultBillingAddress(getAddressConverter().convert(source.getDefaultPaymentAddress()));
        }
        if (source.getDefaultShipmentAddress() != null)
        {
            target.setDefaultShippingAddress(getAddressConverter().convert(source.getDefaultShipmentAddress()));
        }
	}
}
