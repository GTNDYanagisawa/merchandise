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

import de.hybris.platform.commercefacades.address.data.AddressVerificationErrorField;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * Populates Errors for AddressData
 *
 * @author grzegorz lebek
 */

public class AddressDataErrorsPopulator implements Populator<AddressVerificationResult<AddressVerificationDecision>, Errors> {

    private final Map<String, String> attributeMappingMap;

    public AddressDataErrorsPopulator(final Map<String, String> attributeMappingMap)
    {
        this.attributeMappingMap = attributeMappingMap;
    }

    @Override
    public void populate(final AddressVerificationResult<AddressVerificationDecision> source, final Errors target)
            throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        for (Map.Entry<String, AddressVerificationErrorField> entry : source.getErrors().entrySet()) {
            target.rejectValue(
                    attributeMappingMap.containsKey(entry.getKey()) ? attributeMappingMap.get(entry.getKey()) : entry.getKey(),
                    (entry.getValue().isInvalid() ? "field.invalid": "field.required"), new Object[] {entry.getKey()}, "");
        }
    }
}
