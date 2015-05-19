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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link OrderHistoryEntryModel} to {@link B2BOrderHistoryEntryData}.
 */
public class B2BOrderHistoryEntryDataPopulator implements
        Populator<OrderHistoryEntryModel, B2BOrderHistoryEntryData>
{
    private Converter<OrderModel, OrderData> b2bOrderConverter;
    private Converter<B2BCustomerModel, CustomerData> b2bCustomerDataConverter;
    private Converter<UserModel, PrincipalData> principalConverter;

    @Override
    public void populate(final OrderHistoryEntryModel source, final B2BOrderHistoryEntryData target)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        target.setB2bOrderData(getB2bOrderConverter().convert(source.getOrder()));
        if (source.getPreviousOrderVersion() != null)
        {
            target.setPreviousOrderVersionData(getB2bOrderConverter().convert(source.getPreviousOrderVersion()));
        }
        target.setDescription(source.getDescription());
        target.setTimeStamp(source.getTimestamp());

        if (source.getOwner() instanceof B2BCustomerModel)
        {
            target.setOwnerData(getB2bCustomerDataConverter().convert((B2BCustomerModel) source.getOwner()));
        }
        else if (source.getOwner() instanceof UserModel)
        {
            target.setOwnerData(getPrincipalConverter().convert((UserModel) source.getOwner()));
        }
    }

    protected Converter<OrderModel, OrderData> getB2bOrderConverter()
    {
        return b2bOrderConverter;
    }

    @Required
    public void setB2bOrderConverter(final Converter<OrderModel, OrderData> b2bOrderConverter)
    {
        this.b2bOrderConverter = b2bOrderConverter;
    }

    protected Converter<B2BCustomerModel, CustomerData> getB2bCustomerDataConverter()
    {
        return b2bCustomerDataConverter;
    }

    @Required
    public void setB2bCustomerDataConverter(final Converter<B2BCustomerModel, CustomerData> b2bCustomerDataConverter)
    {
        this.b2bCustomerDataConverter = b2bCustomerDataConverter;
    }

    protected Converter<UserModel, PrincipalData> getPrincipalConverter()
    {
        return principalConverter;
    }

    @Required
    public void setPrincipalConverter(final Converter<UserModel, PrincipalData> principalConverter)
    {
        this.principalConverter = principalConverter;
    }
}
