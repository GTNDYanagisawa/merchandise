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
package de.hybris.platform.ycommercewebservices.mapping.mappers;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressValidationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.mapping.mappers.AbstractCustomMapper;
import de.hybris.platform.ycommercewebservices.user.data.AddressDataList;
import de.hybris.platform.ycommercewebservices.validation.data.AddressValidationData;

import ma.glasnost.orika.MappingContext;


/**
 *   
 */
public class AddressValidationDataMapper extends AbstractCustomMapper<AddressValidationData, AddressValidationWsDTO>
{
	@Override
	public void mapAtoB(final AddressValidationData a, final AddressValidationWsDTO b, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("suggestedAddressesList", getAType(), a, "suggestedAddresses", getBType(), b);
		try
		{
			if (shouldMap(a, b, context))
			{
				if (a.getSuggestedAddressesList() != null)
				{
					b.setSuggestedAddresses(mapperFacade.mapAsList(a.getSuggestedAddressesList().getAddresses(), AddressWsDTO.class,
							context));
				}
			}
		}
		finally
		{
			context.endMappingField();
		}
	}

	@Override
	public void mapBtoA(final AddressValidationWsDTO b, final AddressValidationData a, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("suggestedAddresses", getBType(), b, "suggestedAddressesList", getAType(), a);
		try
		{
			if (shouldMap(b, a, context))
			{
				if (b.getSuggestedAddresses() != null)
				{
					final AddressDataList addressDataList = new AddressDataList();
					addressDataList.setAddresses(mapperFacade.mapAsList(b.getSuggestedAddresses(), AddressData.class, context));
					a.setSuggestedAddressesList(addressDataList);
				}
			}
		}
		finally
		{
			context.endMappingField();
		}

	}
}
