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
package de.hybris.platform.core.customdto.enumeration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.hybris.platform.core.dto.enumeration.EnumerationValueDTO;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties;
import de.hybris.platform.webservices.objectgraphtransformer.UriPropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;


/**
 * DTO for enumeration type
 */
@GraphNode(target = EnumerationMetaTypeModel.class, factory = GenericNodeFactory.class, uidProperties = "code")
@XmlRootElement(name = "enumtype")
public class EnumerationMetaTypeDTO implements ModifiedProperties
{

	private final Boolean catalogItemType = Boolean.FALSE;
	private final Boolean generate = Boolean.FALSE;
	private final Boolean singleton = Boolean.FALSE;

	private String code = null;
	private String uri = null;
	private List<EnumerationValueDTO> values = null;

	protected final Set<String> modifiedPropsSet = new HashSet<String>();

	/**
	 * Default constructor
	 */
	public EnumerationMetaTypeDTO()
	{
		super();
		this.modifiedPropsSet.add("catalogItemType");
		this.modifiedPropsSet.add("generate");
		this.modifiedPropsSet.add("singleton");
	}

	/**
	 * @see de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties#getModifiedProperties()
	 */
	@Override
	public Set<String> getModifiedProperties()
	{
		return this.modifiedPropsSet;
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
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
		this.modifiedPropsSet.add("code");
	}

	/**
	 * @return the values
	 */
	@XmlElementWrapper(name = "enumvalues")
	@XmlElement(name = "enumvalue")
	public List<EnumerationValueDTO> getValues()
	{
		return values;
	}

	/**
	 * @param values
	 *           the values to set
	 */
	public void setValues(final List<EnumerationValueDTO> values)
	{
		this.values = values;
	}

	/**
	 * @return the uri
	 */
	@XmlAttribute
	public String getUri()
	{
		return uri;
	}

	/**
	 * @param uri
	 *           the uri to set
	 */
	@GraphProperty(virtual = true, interceptor = UriPropertyInterceptor.class)
	public void setUri(final String uri)
	{
		this.uri = uri;
	}

	/**
	 * @return the catalogItemType
	 */
	public Boolean getCatalogItemType()
	{
		return catalogItemType;
	}

	/**
	 * @return the generate
	 */
	public Boolean getGenerate()
	{
		return generate;
	}

	/**
	 * @return the singleton
	 */
	public Boolean getSingleton()
	{
		return singleton;
	}

}
