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
package de.hybris.platform.variants.customdto;

import javax.xml.bind.annotation.XmlRootElement;

import de.hybris.platform.core.dto.type.ComposedTypeDTO;
import de.hybris.platform.variants.model.VariantTypeModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;


@GraphNode(target = VariantTypeModel.class, uidProperties = "code")
@XmlRootElement(name = "variantType")
public class VariantTypeDTO extends ComposedTypeDTO
{
	private String code;

	/**
	 * @return the code
	 */
	@Override
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	@Override
	public void setCode(final String code)
	{
		this.code = code;
	}
}
