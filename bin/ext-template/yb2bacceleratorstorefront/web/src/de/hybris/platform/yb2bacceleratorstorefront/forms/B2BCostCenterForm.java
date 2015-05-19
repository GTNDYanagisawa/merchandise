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
package de.hybris.platform.yb2bacceleratorstorefront.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * Pojo for 'B2B cost center' form.
 */
public class B2BCostCenterForm
{
	private String originalCode;
	private String code;
	private String name;
	private String parentB2BUnit;
	private String parentB2BUnitCode;
	private String currency;

	@NotNull(message = "{general.required}")
	@Size(min = 1, max = 255, message = "{general.required}")
	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	@NotNull(message = "{general.required}")
	@Size(min = 1, max = 255, message = "{general.required}")
	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	@NotNull(message = "{general.required}")
	public String getParentB2BUnit()
	{
		return parentB2BUnit;
	}

	public void setParentB2BUnit(final String parentB2BUnit)
	{
		this.parentB2BUnit = parentB2BUnit;
	}

	@NotNull(message = "{general.required}")
	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}


	public String getParentB2BUnitCode()
	{
		return parentB2BUnitCode;
	}

	public void setParentB2BUnitCode(final String parentB2BUnitCode)
	{
		this.parentB2BUnitCode = parentB2BUnitCode;
	}

	public String getOriginalCode()
	{
		return originalCode;
	}

	public void setOriginalCode(final String originalCode)
	{
		this.originalCode = originalCode;
	}
}
