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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.dto.VariantProductDTO;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.webservices.dto.VariantAttributeDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;

import java.util.ArrayList;
import java.util.List;


/**
 * This converter is a "trick". It does NO conversion, it passes the value through unchanged. It exists only for it's
 * side-effects: it extracts some information from the model (indirect references) and then injects them into properties
 * of the DTO. Default mechanisms could not be used, because currently only something like this is supported:
 * dto.setA(model.getA()), and here we have something like: dto.setA(model.getB().getC().getD())
 */
public class VariantAttributesConverter implements PropertyInterceptor<ProductDTO, ProductDTO>
{
	@Override
	public ProductDTO intercept(final PropertyContext ctx, final ProductDTO source)
	{
		final Object sourceNode = ctx.getParentContext().getSourceNodeValue(); //Model
		final Object targetNode = ctx.getParentContext().getTargetNodeValue(); //DTO
		final VariantProductModel model = (VariantProductModel) sourceNode;
		final VariantProductDTO dto = (VariantProductDTO) targetNode;
		if (model.getBaseProduct() != null)
		{
			if (model.getBaseProduct().getVariantType() != null)
			{
				final List<VariantAttributeDescriptorModel> attributeList = model.getBaseProduct().getVariantType()
						.getVariantAttributes();
				if (attributeList != null && !attributeList.isEmpty())
				{
					final List<VariantAttributeDTO> targetVariantAttributes = new ArrayList<VariantAttributeDTO>();
					for (final VariantAttributeDescriptorModel attribute : attributeList)
					{
						final String qualifier = attribute.getQualifier();
						if (qualifier != null)
						{
							targetVariantAttributes.add(new VariantAttributeDTO(qualifier, String.valueOf(getAttributeValue(model,
									qualifier))));
						}
					}

					dto.setVariantAttributes(targetVariantAttributes);
				}
			}
		}
		return source;
	}

	protected Object getAttributeValue(final VariantProductModel model, final String qualifier)
	{
		final ModelService modelService = Registry.getApplicationContext().getBean("modelService", ModelService.class);
		return modelService.getAttributeValue(model, qualifier);
	}
}
