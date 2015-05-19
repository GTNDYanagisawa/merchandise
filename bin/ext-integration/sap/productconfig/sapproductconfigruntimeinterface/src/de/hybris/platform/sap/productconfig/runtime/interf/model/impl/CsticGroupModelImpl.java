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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;

import java.util.List;


public class CsticGroupModelImpl extends BaseModelImpl implements CsticGroupModel
{

	private String name;
	private String description;
	private List<String> csticNames;


	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(final String description)
	{
		this.description = description;
	}

	@Override
	public List<String> getCsticNames()
	{
		return csticNames;
	}

	@Override
	public void setCsticNames(final List<String> csticNames)
	{
		this.csticNames = csticNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((csticNames == null) ? 0 : csticNames.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CsticGroupModelImpl other = (CsticGroupModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}
		if (csticNames == null)
		{
			if (other.csticNames != null)
			{
				return false;
			}
		}
		else if (!csticNames.equals(other.csticNames))
		{
			return false;
		}
		if (description == null)
		{
			if (other.description != null)
			{
				return false;
			}
		}
		else if (!description.equals(other.description))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}

	@Override
	public CsticGroupModel clone()
	{
		CsticGroupModel clonedCsticGroupModel;
		clonedCsticGroupModel = (CsticGroupModel) super.clone();

		return clonedCsticGroupModel;
	}

}
