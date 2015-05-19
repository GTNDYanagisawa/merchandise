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

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;


/**

 * 
 */
public class ConfigModelImpl extends BaseModelImpl implements ConfigModel
{
	private String id;
	private String name;
	private InstanceModel rootInstance;
	private boolean complete;
	private boolean consistent;
	private PriceModel basePrice;
	private PriceModel selectedOptionsPrice;
	private PriceModel currentTotalPrice;

	@Override
	public String getId()
	{
		return id;
	}


	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

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
	public InstanceModel getRootInstance()
	{
		return rootInstance;
	}

	@Override
	public void setRootInstance(final InstanceModel rootInstance)
	{
		this.rootInstance = rootInstance;
	}


	@Override
	public boolean isComplete()
	{
		return complete;
	}

	@Override
	public void setComplete(final boolean complete)
	{
		this.complete = complete;
	}


	@Override
	public boolean isConsistent()
	{
		return consistent;
	}

	@Override
	public void setConsistent(final boolean consistent)
	{
		this.consistent = consistent;
	}


	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("ConfigModelImpl [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", rootInstance=");
		builder.append(rootInstance);
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", consistent=");
		builder.append(consistent);
		builder.append(", basePrice=");
		builder.append(basePrice);
		builder.append(", selectedOptionsPrice=");
		builder.append(selectedOptionsPrice);
		builder.append(", currentTotalPrice=");
		builder.append(currentTotalPrice);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public ConfigModel clone()
	{
		ConfigModel clonedConfigModel;

		clonedConfigModel = (ConfigModel) super.clone();

		if (rootInstance != null)
		{
			clonedConfigModel.setRootInstance(this.rootInstance.clone());
		}
		if (basePrice != null)
		{
			clonedConfigModel.setBasePrice(this.basePrice.clone());
		}
		if (selectedOptionsPrice != null)
		{
			clonedConfigModel.setSelectedOptionsPrice(this.selectedOptionsPrice.clone());
		}
		if (currentTotalPrice != null)
		{
			clonedConfigModel.setCurrentTotalPrice(this.currentTotalPrice.clone());
		}
		return clonedConfigModel;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + (consistent ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((basePrice == null) ? 0 : basePrice.hashCode());
		result = prime * result + ((selectedOptionsPrice == null) ? 0 : selectedOptionsPrice.hashCode());
		result = prime * result + ((currentTotalPrice == null) ? 0 : currentTotalPrice.hashCode());
		result = prime * result + ((rootInstance == null) ? 0 : rootInstance.hashCode());
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

		final ConfigModelImpl other = (ConfigModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}
		if (complete != other.complete)
		{
			return false;
		}
		if (consistent != other.consistent)
		{
			return false;
		}
		if (!objectsEqual(id, other.id))
		{
			return false;
		}
		if (!objectsEqual(name, other.name))
		{
			return false;
		}
		if (!objectsEqual(rootInstance, other.rootInstance))
		{
			return false;
		}
		if (!objectsEqual(basePrice, other.basePrice))
		{
			return false;
		}
		if (!objectsEqual(selectedOptionsPrice, other.selectedOptionsPrice))
		{
			return false;
		}
		if (!objectsEqual(currentTotalPrice, other.currentTotalPrice))
		{
			return false;
		}
		return true;
	}

	private boolean objectsEqual(final Object obj1, final Object obj2)
	{
		if (obj1 == null)
		{
			if (obj2 != null)
			{
				return false;
			}
		}
		else if (!obj1.equals(obj2))
		{
			return false;
		}

		return true;
	}


	@Override
	public PriceModel getBasePrice()
	{
		return basePrice;
	}

	@Override
	public void setBasePrice(final PriceModel basePrice)
	{
		this.basePrice = basePrice;
	}

	@Override
	public PriceModel getSelectedOptionsPrice()
	{
		return selectedOptionsPrice;
	}

	@Override
	public void setSelectedOptionsPrice(final PriceModel selectedOptionsPrice)
	{
		this.selectedOptionsPrice = selectedOptionsPrice;
	}

	@Override
	public PriceModel getCurrentTotalPrice()
	{
		return currentTotalPrice;
	}

	@Override
	public void setCurrentTotalPrice(final PriceModel currentTotalPrice)
	{
		this.currentTotalPrice = currentTotalPrice;
	}
}
