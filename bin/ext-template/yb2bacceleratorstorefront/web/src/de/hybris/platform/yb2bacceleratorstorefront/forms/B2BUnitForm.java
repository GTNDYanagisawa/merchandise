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
 * Pojo for 'B2b unit' form.
 */
public class B2BUnitForm
{
	private String uid;
	private String name;
	private String parentUnit;
	private boolean active;
	private String originalUid;
	private String approvalProcessCode;
	private boolean rootUnit;

	@NotNull(message = "{unit.uid.invalid}")
	@Size(min = 1, max = 255, message = "{unit.uid.invalid}")
	public String getUid()
	{
		return uid;
	}

	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	@NotNull(message = "{unit.name.invalid}")
	@Size(min = 1, max = 255, message = "{unit.name.invalid}")
	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getParentUnit()
	{
		return parentUnit;
	}

	public void setParentUnit(final String parentUnit)
	{
		this.parentUnit = parentUnit;
	}

	public String getApprovalProcessCode()
	{
		return approvalProcessCode;
	}

	public void setApprovalProcessCode(final String approvalProcessCode)
	{
		this.approvalProcessCode = approvalProcessCode;
	}

	public String getOriginalUid()
	{
		return originalUid;
	}

	public void setOriginalUid(final String originalUid)
	{
		this.originalUid = originalUid;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(final boolean active)
	{
		this.active = active;
	}

	public boolean isRootUnit()
	{
		return rootUnit;
	}

	public void setRootUnit(final boolean rootUnit)
	{
		this.rootUnit = rootUnit;
	}
}
