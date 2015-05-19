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

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;


public class CsticValueModelImpl extends BaseModelImpl implements CsticValueModel
{

	/**
	 * 
	 */
	private static final String numericFormat = "-?\\d+(\\.\\d+)?";
	private String name;
	private String languageDependentName;
	private boolean domainValue;
	private String author;
	private String authorExternal = null;
	private boolean selectable = true;
	private PriceModel deltaPrice = PriceModel.NO_PRICE;

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
	public String getLanguageDependentName()
	{
		return languageDependentName;
	}


	@Override
	public void setLanguageDependentName(final String languageDependentName)
	{
		this.languageDependentName = languageDependentName;
	}


	@Override
	public boolean isDomainValue()
	{
		return domainValue;
	}


	@Override
	public void setDomainValue(final boolean domainValue)
	{
		this.domainValue = domainValue;
	}


	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("CsticValueModelImpl [name=");
		builder.append(name);
		builder.append(", languageDependentName=");
		builder.append(languageDependentName);
		builder.append(", domainValue=");
		builder.append(domainValue);
		builder.append(']');
		return builder.toString();
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


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
		final CsticValueModelImpl other = (CsticValueModelImpl) obj;
		if (!super.equals(other))
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
		else
		{
			if (!name.equals(other.name))
			{
				if (name.matches(numericFormat) && other.name.matches(numericFormat))
				{
					if (Double.parseDouble(name) != Double.parseDouble(other.name))
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public CsticValueModel clone()
	{
		CsticValueModel clonedCsticValue;
		clonedCsticValue = (CsticValueModel) super.clone();
		clonedCsticValue.setDeltaPrice(deltaPrice.clone());

		return clonedCsticValue;
	}


	@Override
	public String getAuthor()
	{
		return author;
	}


	@Override
	public void setAuthor(final String author)
	{
		this.author = author;
	}



	@Override
	public boolean isSelectable()
	{
		return this.selectable;
	}


	@Override
	public void setSelectable(final boolean selectable)
	{
		this.selectable = selectable;
	}


	@Override
	public String getAuthorExternal()
	{
		return authorExternal;
	}



	@Override
	public void setAuthorExternal(final String authorExternal)
	{
		this.authorExternal = authorExternal;
	}



	@Override
	public PriceModel getDeltaPrice()
	{
		return deltaPrice;
	}



	@Override
	public void setDeltaPrice(PriceModel deltaPrice)
	{
		if (deltaPrice == null)
		{
			deltaPrice = PriceModel.NO_PRICE;
		}
		this.deltaPrice = deltaPrice;

	}

}
