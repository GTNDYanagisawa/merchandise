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

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link B2BCommentModel} with {@link B2BCommentData}.
 */
public class B2BCommentPopulator implements Populator<B2BCommentModel, B2BCommentData>
{
    private Converter<UserModel, PrincipalData> principalConverter;

    @Override
    public void populate(final B2BCommentModel source, final B2BCommentData target)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        target.setComment(source.getComment());
        target.setCode(source.getCode());
        target.setTimeStamp(source.getModifiedDate());
        if (source.getOwner() != null)
        {
            target.setOwnerData(getPrincipalConverter().convert(source.getOwner()));
        }
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
