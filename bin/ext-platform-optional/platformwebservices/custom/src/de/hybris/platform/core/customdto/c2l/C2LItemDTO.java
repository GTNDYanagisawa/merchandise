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
package de.hybris.platform.core.customdto.c2l;

import de.hybris.platform.core.dto.ItemDTO;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Generated dto class for type C2LItem first defined at extension core
 */
@GraphNode(target = C2LItemModel.class, factory = GenericNodeFactory.class, uidProperties = "pk")
@XmlRootElement(name = "c2litem")
@SuppressWarnings("PMD")
public class C2LItemDTO extends ItemDTO
{
	/**
	 * <i>Generated variable</i> - Variable of <code>C2LItem.active</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private Boolean active;

	/**
	 * <i>Generated variable</i> - Variable of <code>C2LItem.isocode</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private String isocode;

	/**
	 * <i>Generated variable</i> - Variable of <code>C2LItem.name</code> attribute defined at extension <code>core</code>
	 * .
	 */
	private String name;


	/**
	 * <i>Generated constructor</i> - for generic creation.
	 */
	public C2LItemDTO()
	{
		super();

	}


	public Boolean getActive()
	{
		return this.active;
	}

	@XmlAttribute
	public String getIsocode()
	{
		return this.isocode;
	}

	public String getName()
	{
		return this.name;
	}

	public void setActive(final Boolean value)
	{
		this.modifiedPropsSet.add("active");
		this.active = value;
	}

	public void setIsocode(final String value)
	{
		this.modifiedPropsSet.add("isocode");
		this.isocode = value;
	}

	public void setName(final String value)
	{
		this.modifiedPropsSet.add("name");
		this.name = value;
	}

}
