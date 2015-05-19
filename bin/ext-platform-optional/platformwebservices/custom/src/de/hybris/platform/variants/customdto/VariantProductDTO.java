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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.webservices.dto.VariantAttributeDTO;
import de.hybris.platform.webservices.objectgraphtransformer.VariantAttributesConverter;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;



@GraphNode(target = VariantProductModel.class, uidProperties = "code")
@XmlRootElement(name = "variantProduct")
public class VariantProductDTO extends ProductDTO
{
	private ProductDTO baseProduct;
	private List<VariantAttributeDTO> variantAttributes;

	/**
	 * @return the baseProduct
	 */
	public ProductDTO getBaseProduct()
	{
		return baseProduct;
	}

	/**
	 * @param baseProduct
	 *           the baseProduct to set
	 */
	@GraphProperty(interceptor = VariantAttributesConverter.class)
	public void setBaseProduct(final ProductDTO baseProduct)
	{
		this.baseProduct = baseProduct;
	}

	/**
	 * @return the variantAttributes
	 */
	@XmlElementWrapper(name = "variantAttributes")
	@XmlElement(name = "variantAttribute")
	public List<VariantAttributeDTO> getVariantAttributes()
	{
		return variantAttributes;
	}

	/**
	 * @param variantAttributes
	 *           the variantAttributes to set
	 */
	public void setVariantAttributes(final List<VariantAttributeDTO> variantAttributes)
	{
		this.variantAttributes = variantAttributes;
	}

}
