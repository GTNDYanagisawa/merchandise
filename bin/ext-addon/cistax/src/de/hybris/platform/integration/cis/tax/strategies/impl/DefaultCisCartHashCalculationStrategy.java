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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceCartHashCalculationStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartHashCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceOrderParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.codec.Hex;


public class DefaultCisCartHashCalculationStrategy implements CommerceCartHashCalculationStrategy
{

    private static final Logger LOG = Logger.getLogger(DefaultCommerceCartHashCalculationStrategy.class);

    @Override
    @Deprecated
    public String buildHashForAbstractOrder(final AbstractOrderModel abstractOrderModel, final List<String> additionalValues)
    {
        final CommerceOrderParameter parameter = new CommerceOrderParameter();
        parameter.setOrder(abstractOrderModel);
        parameter.setAdditionalValues(additionalValues);
        return this.buildHashForAbstractOrder(parameter);
    }

    @Override
    public String buildHashForAbstractOrder(final CommerceOrderParameter parameter)
    {
        final AbstractOrderModel abstractOrderModel = parameter.getOrder();
        final List<String> additionalValues = parameter.getAdditionalValues();

        final StringBuilder orderValues = new StringBuilder();

        orderValues.append(abstractOrderModel.getItemtype());

        if (abstractOrderModel.getDeliveryAddress() != null)
        {
            orderValues.append(abstractOrderModel.getDeliveryAddress().getLine1());
            orderValues.append(abstractOrderModel.getDeliveryAddress().getLine2());
            orderValues.append(abstractOrderModel.getDeliveryAddress().getTown());
            if (abstractOrderModel.getDeliveryAddress().getRegion() != null)
            {
                orderValues.append(abstractOrderModel.getDeliveryAddress().getRegion().getIsocode());
            }
            if (abstractOrderModel.getDeliveryAddress().getCountry() != null)
            {
                orderValues.append(abstractOrderModel.getDeliveryAddress().getCountry().getIsocode());
            }
            orderValues.append(abstractOrderModel.getDeliveryAddress().getDistrict());
            orderValues.append(abstractOrderModel.getDeliveryAddress().getPostalcode());
        }

        if (abstractOrderModel.getDeliveryFromAddress() != null)
        {
            orderValues.append(abstractOrderModel.getDeliveryFromAddress().getLine1());
            orderValues.append(abstractOrderModel.getDeliveryFromAddress().getLine2());
            orderValues.append(abstractOrderModel.getDeliveryFromAddress().getTown());
            if (abstractOrderModel.getDeliveryFromAddress().getRegion() != null)
            {
                orderValues.append(abstractOrderModel.getDeliveryFromAddress().getRegion().getIsocode());
            }
            if (abstractOrderModel.getDeliveryFromAddress().getCountry() != null)
            {
                orderValues.append(abstractOrderModel.getDeliveryFromAddress().getCountry().getIsocode());
            }
            orderValues.append(abstractOrderModel.getDeliveryFromAddress().getDistrict());
            orderValues.append(abstractOrderModel.getDeliveryFromAddress().getPostalcode());
        }

        if (abstractOrderModel.getDeliveryMode() != null)
        {
            orderValues.append(abstractOrderModel.getDeliveryMode().getCode());
        }

        if (abstractOrderModel.getCurrency() != null)
        {
            orderValues.append(abstractOrderModel.getCurrency().getIsocode());
        }

        if (abstractOrderModel.getNet() != null)
        {
            orderValues.append((abstractOrderModel.getNet().toString()));
        }

        if (abstractOrderModel.getDate() != null)
        {
            orderValues.append(abstractOrderModel.getDate().getTime());
        }

        for (final AbstractOrderEntryModel abstractOrderEntryModel : abstractOrderModel.getEntries())
        {
            orderValues.append(buildHashForAbstractOrderEntry(abstractOrderEntryModel));
        }


        if (additionalValues != null)
        {
            for (final String additionalValue : additionalValues)
            {
                orderValues.append(additionalValue);
            }
        }

        final String orderValue = orderValues.toString();
        try
        {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(orderValue.getBytes());
            return String.valueOf(Hex.encode(md5.digest()));
        }
        catch (final NoSuchAlgorithmException e)
        {
            LOG.error("NoSuchAlgorithmException while computing the order hash. This should never happen.", e);
        }
        return orderValue;

    }

    protected String buildHashForAbstractOrderEntry(final AbstractOrderEntryModel abstractOrderEntryModel)
    {
        final StringBuilder entryValues = new StringBuilder();

        entryValues.append(abstractOrderEntryModel.getTotalPrice().toString());
        entryValues.append(abstractOrderEntryModel.getProduct().getCode());
        entryValues.append(abstractOrderEntryModel.getQuantity().toString());

        if (abstractOrderEntryModel.getDeliveryMode() != null)
        {
            entryValues.append(abstractOrderEntryModel.getDeliveryMode().getCode());
        }

        if (abstractOrderEntryModel.getDeliveryAddress() != null)
        {
            entryValues.append(abstractOrderEntryModel.getDeliveryAddress().getLine1());
            entryValues.append(abstractOrderEntryModel.getDeliveryAddress().getLine2());
            entryValues.append(abstractOrderEntryModel.getDeliveryAddress().getTown());
            if (abstractOrderEntryModel.getDeliveryAddress().getRegion() != null)
            {
                entryValues.append(abstractOrderEntryModel.getDeliveryAddress().getRegion().getIsocode());
            }
            if (abstractOrderEntryModel.getDeliveryAddress().getCountry() != null)
            {
                entryValues.append(abstractOrderEntryModel.getDeliveryAddress().getCountry().getIsocode());
            }
            entryValues.append(abstractOrderEntryModel.getDeliveryAddress().getDistrict());
            entryValues.append(abstractOrderEntryModel.getDeliveryAddress().getPostalcode());
        }

        if (abstractOrderEntryModel.getDeliveryPointOfService() != null
                && abstractOrderEntryModel.getDeliveryPointOfService().getAddress() != null)
        {
            entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getLine1());
            entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getLine2());
            entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getTown());
            if (abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getRegion() != null)
            {
                entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getRegion().getIsocode());
            }
            if (abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getCountry() != null)
            {
                entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getCountry().getIsocode());
            }
            entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getDistrict());
            entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getPostalcode());
        }


        return entryValues.toString();
    }

}
