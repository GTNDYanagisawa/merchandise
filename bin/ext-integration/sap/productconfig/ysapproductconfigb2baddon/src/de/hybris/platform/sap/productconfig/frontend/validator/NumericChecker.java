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

import org.springframework.validation.Errors;


public interface NumericChecker
{


	/**
	 * Validates the value of the cstic.
	 * 
	 * @param cstic
	 *           to check
	 * @param errorObj
	 *           to add errors if found
	 */
	public void validate(final CsticData cstic, final Errors errorObj);

	/**
	 * Validates the additional Value of the cstic
	 * 
	 * @param cstic
	 * @param errorObj
	 */
	public void validateAdditionalValue(CsticData cstic, Errors errorObj);

}
