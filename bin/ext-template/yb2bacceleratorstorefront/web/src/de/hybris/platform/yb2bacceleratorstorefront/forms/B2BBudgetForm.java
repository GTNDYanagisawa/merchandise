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

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * Pojo for collection B2BBudget form.
 */
public class B2BBudgetForm
{
	private String originalCode;
	private String code;
	private String name;
	private String parentB2BUnit;
	protected Date startDate;
	protected Date endDate;
	private String currency;
	protected String budget;

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

	public String getBudget()
	{
		return budget;
	}

	public void setBudget(final String budget)
	{
		this.budget = budget;
	}

	public String getOriginalCode()
	{
		return originalCode;
	}

	public void setOriginalCode(final String originalCode)
	{
		this.originalCode = originalCode;
	}

	@NotNull(message = "{general.required}")
	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(final Date startDate)
	{
		this.startDate = startDate;
	}

	@NotNull(message = "{general.required}")
	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(final Date endDate)
	{
		this.endDate = endDate;
	}
}
