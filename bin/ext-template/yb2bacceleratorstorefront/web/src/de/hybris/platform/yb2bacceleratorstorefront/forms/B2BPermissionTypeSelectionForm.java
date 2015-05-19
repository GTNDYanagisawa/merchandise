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


/**
 * Pojo for 'B2b permission type selection' form.
 */
public class B2BPermissionTypeSelectionForm
{
	private String b2BPermissionType;

	@NotNull(message = "{general.required}")
	public String getB2BPermissionType()
	{
		return b2BPermissionType;
	}

	public void setB2BPermissionType(final String b2bPermissionType)
	{
		b2BPermissionType = b2bPermissionType;
	}
}
