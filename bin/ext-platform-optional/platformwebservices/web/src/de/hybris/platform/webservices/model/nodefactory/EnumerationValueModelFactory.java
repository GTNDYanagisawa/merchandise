/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.model.nodefactory;

import de.hybris.platform.core.dto.enumeration.EnumerationValueDTO;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.webservices.objectgraphtransformer.YNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;


/**
 * This class loads the enumeration value using Service Layer
 */
public class EnumerationValueModelFactory extends YNodeFactory<EnumerationValueDTO, EnumerationValueModel>
{

	/**
	 * @see de.hybris.platform.webservices.objectgraphtransformer.YNodeFactory#getModel(de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphContext,
	 *      NodeContext, java.lang.Object)
	 */
	@Override
	protected EnumerationValueModel getModel(final YObjectGraphContext ctx, final NodeContext nodeCtx,
			final EnumerationValueDTO dto)
	{
		EnumerationValueModel result = null;
		if (dto.getItemtype() != null)
		{
			final TypeService typeService = ctx.getServices().getTypeService();
			result = typeService.getEnumerationValue(dto.getItemtype().getCode(), dto.getCode());
		}
		return result;
	}

}
