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

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;


public class ConflictModelImpl extends BaseModelImpl implements ConflictModel
{

	private String text;

	@Override
	public void setText(final String conflictText)
	{
		this.text = conflictText;

	}

	@Override
	public String getText()
	{

		return this.text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ConflictModel clone()
	{
		return (ConflictModel) super.clone();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		final ConflictModelImpl other = (ConflictModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}
		if (text == null)
		{
			if (other.text != null)
			{
				return false;
			}
		}
		else if (!text.equals(other.text))
		{
			return false;
		}
		return true;
	}



}
