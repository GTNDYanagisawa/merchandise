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

import org.hibernate.validator.constraints.NotBlank;


/**
 * Form object for updating profile.
 */
public class UpdateProfileForm
{
	private String titleCode;
	private String firstName;
	private String lastName;

	@NotNull(message = "{profile.title.invalid}")
	@Size(min = 1, max = 255, message = "{profile.title.invalid}")
	public String getTitleCode()
	{
		return titleCode;
	}

	public void setTitleCode(final String titleCode)
	{
		this.titleCode = titleCode;
	}

	@NotNull(message = "{profile.firstName.invalid}")
	@Size(min = 1, max = 255, message = "{profile.firstName.invalid}")
	@NotBlank(message = "{profile.firstName.invalid}")
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	@NotNull(message = "{profile.lastName.invalid}")
	@Size(min = 1, max = 255, message = "{profile.lastName.invalid}")
	@NotBlank(message = "{profile.lastName.invalid}")
	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}
}
