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
package de.hybris.platform.acceleratorstorefrontcommons.forms.verification;


import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.user.data.AddressData;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Strategy for handling an {@link AddressVerificationResult} returned by some
 * address verification service.
 */
public interface AddressVerificationResultHandler
{

	/**
	 * Given the result returned by an address verification service, determine if the
	 * address is valid or requires corrections from the user.
	 *
	 * @return true if the result has errors or suggested addresses, false if it is valid.
	 */
	boolean handleResult(AddressVerificationResult verificationResult, AddressData newAddress, Model model,
			RedirectAttributes redirectModel, Errors bindingResult, boolean customerBypass, String successMsg);
}
