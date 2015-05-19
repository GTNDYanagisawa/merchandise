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
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.frontend.constants.Sapproductconfigb2baddonConstants;

import org.springframework.validation.FieldError;


public class ConflictError extends FieldError
{
	private final CsticData cstic;

	public ConflictError(final CsticData cstic, final String path, final String rejectedValue, final String[] errorCodes,
			final String defaultMessage)
	{
		super(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE, path, rejectedValue, false, errorCodes, null, defaultMessage);

		this.cstic = cstic;
	}

	public CsticData getCstic()
	{
		return cstic;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cstic == null) ? 0 : cstic.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		final ConflictError other = (ConflictError) obj;
		if (cstic == null)
		{
			if (other.cstic != null)
			{
				return false;
			}
		}
		else if (!cstic.equals(other.cstic))
		{
			return false;
		}
		return true;
	}
}
