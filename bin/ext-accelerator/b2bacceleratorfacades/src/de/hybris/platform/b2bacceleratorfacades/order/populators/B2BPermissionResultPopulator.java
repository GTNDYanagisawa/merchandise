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

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionResultData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.B2BPermissionTypeEnum;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator implementation for {@link B2BPermissionResultModel} as source and
 * {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionResultData} as target type.
 */
public class B2BPermissionResultPopulator implements Populator<B2BPermissionResultModel, B2BPermissionResultData>
{
    private Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2BPermissionTypeConverter;
    private EnumerationService enumerationService;

    @Override
    public void populate(final B2BPermissionResultModel source, final B2BPermissionResultData target)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setApprover(source.getApprover() == null ? "" : source.getApprover().getName());
        final B2BPermissionModel b2BPermissionModel = source.getPermission();
        target.setPermissionToEvaluate(b2BPermissionModel == null ? "" : b2BPermissionModel.getCode());
        target.setPermissionTypeCode(source.getPermissionTypeCode());
        target.setPermissionTypeData(getB2BPermissionTypeConverter().convert(
                B2BPermissionTypeEnum.valueOf(source.getPermissionTypeCode())));
        target.setStatus(source.getStatus());
        target.setStatusDisplay(source.getStatusDisplay());
        target.setApproverNotes(source.getNote());
    }


    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    @Required
    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    public Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> getB2BPermissionTypeConverter()
    {
        return b2BPermissionTypeConverter;
    }

    @Required
    public void setB2BPermissionTypeConverter(final Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2bPermissionTypeConverter)
    {
        b2BPermissionTypeConverter = b2bPermissionTypeConverter;
    }
}
