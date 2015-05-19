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

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;

import javax.validation.constraints.NotNull;


/**
 * Pojo for 'b2b permission' form.
 */
public class B2BPermissionForm
{
	private String permissionType;
	private String code;
	private String originalCode;
	private String name;
	private String parentUnitName;
	private String timeSpan;
	private String currency;
	private String value;
	private B2BPermissionTypeData b2BPermissionTypeData;

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	@NotNull(message = "{general.required}")
	public String getParentUnitName()
	{
		return parentUnitName;
	}

	public void setParentUnitName(final String parentUnitName)
	{
		this.parentUnitName = parentUnitName;
	}

	public String getTimeSpan()
	{
		return timeSpan;
	}

	public void setTimeSpan(final String timeSpan)
	{
		this.timeSpan = timeSpan;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	public String getPermissionType()
	{
		return permissionType;
	}

	public void setPermissionType(final String permissionType)
	{
		this.permissionType = permissionType;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getOriginalCode()
	{
		return originalCode;
	}

	public void setOriginalCode(final String originalCode)
	{
		this.originalCode = originalCode;
	}

	public B2BPermissionTypeData getB2BPermissionTypeData()
	{
		return b2BPermissionTypeData;
	}

	public void setB2BPermissionTypeData(final B2BPermissionTypeData b2BPermissionTypeData)
	{
		this.b2BPermissionTypeData = b2BPermissionTypeData;
	}
}
