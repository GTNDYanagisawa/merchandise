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
package de.hybris.platform.catalog.customdto;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.ClassificationAttributeUnitDtoToStringConverter;
import de.hybris.platform.webservices.objectgraphtransformer.ClassificationAttributeValueModelToStringConverter;
import de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties;
import de.hybris.platform.webservices.objectgraphtransformer.ProductFeatureConverter;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@GraphNode(target = ProductFeatureModel.class, factory = GenericNodeFactory.class, uidProperties = "qualifier")
@XmlRootElement(name = "classificationvalue")
public class ProductFeatureDTO implements ModifiedProperties
{

	protected final Set<String> modifiedPropsSet = new HashSet<String>();

	private String qualifier;
	private String name;
	private String code;
	private String value;
	private String unit;


	public ProductFeatureDTO() //NOPMD
	{
		super();
	}

	@Override
	public Set<String> getModifiedProperties()
	{
		return this.modifiedPropsSet;
	}

	/**
	 * @return the qualifier
	 */
	@XmlAttribute
	public String getQualifier()
	{
		return qualifier;
	}

	/**
	 * @param qualifier
	 *           the qualifier to set
	 */

	public void setQualifier(final String qualifier)
	{
		this.modifiedPropsSet.add("qualifier");
		this.qualifier = qualifier;
	}

	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName()
	{
		return name;
	}

	/**
	 * 
	 * @param name
	 *           the name to set
	 */
	@GraphProperty(virtual = true, interceptor = ProductFeatureConverter.class)
	public void setName(final String name)
	{
		this.modifiedPropsSet.add("name");
		this.name = name;
	}

	/**
	 * @return the code
	 */
	@XmlAttribute
	public String getCode()
	{
		return code;
	}

	/**
	 * 
	 * @param code
	 *           the code to set
	 */
	@GraphProperty(virtual = true, interceptor = ProductFeatureConverter.class)
	public void setCode(final String code)
	{
		this.modifiedPropsSet.add("code");
		this.code = code;
	}

	/**
	 * @return the value
	 */
	@XmlAttribute
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	@GraphProperty(interceptor = ClassificationAttributeValueModelToStringConverter.class)
	public void setValue(final String value)
	{
		this.modifiedPropsSet.add("value");
		this.value = value;
	}

	/**
	 * @return the unit
	 */
	@XmlAttribute
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	@GraphProperty(interceptor = ClassificationAttributeUnitDtoToStringConverter.class)
	public void setUnit(final String unit)
	{
		this.modifiedPropsSet.add("unit");
		this.unit = unit;
	}

}
