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

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.B2BPermissionTypeEnum;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.type.TypeService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link B2BPermissionTypeEnum} to {@link B2BPermissionTypeData}.
 */
public class B2BPermissionTypePopulator implements Populator<B2BPermissionTypeEnum, B2BPermissionTypeData>
{
    private TypeService typeService;

    @Override
    public void populate(final B2BPermissionTypeEnum source, final B2BPermissionTypeData target)
    {
        target.setCode(source.getCode());
        target.setName(getTypeService().getEnumerationValue(source).getName());
    }

    protected TypeService getTypeService()
    {
        return typeService;
    }

    @Required
    public void setTypeService(final TypeService typeService)
    {
        this.typeService = typeService;
    }
}
