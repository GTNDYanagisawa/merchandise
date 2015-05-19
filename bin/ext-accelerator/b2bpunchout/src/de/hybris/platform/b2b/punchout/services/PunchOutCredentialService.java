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
package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;

import org.cxml.Credential;


/**
 * Service to manage entity {@link PunchOutCredentialModel}.
 */
public interface PunchOutCredentialService
{

	/**
	 * Get an {@link PunchOutCredentialModel} based on the domain and identity.
	 * 
	 * @param domain
	 *           The domain of this identity (e.g.: DUNS).
	 * @param identity
	 *           The value of the identity.
	 * @return The {@link PunchOutCredentialModel} or null, if it cannot be found.
	 */
	PunchOutCredentialModel getPunchOutCredential(final String domain, final String identity);

	/**
	 * Get the first valid {@link B2BCustomerModel} that is mapped for one of the pair domain and identity contained in
	 * the credential and validate the shared secret.
	 * 
	 * @param credential
	 *           The credential that contains domain and identity.
	 * @return The {@link B2BCustomerModel} or null, if the credential is not mapped for any customer.
	 */
	B2BCustomerModel getCustomerForCredential(final Credential credential);

	/**
	 * Get the first valid {@link B2BCustomerModel} that is mapped for one of the pair domain and identity contained in
	 * the credential without validating the shared secret.
	 * 
	 * @param credential
	 *           The credential that contains domain and identity.
	 * @return The {@link B2BCustomerModel} or null, if the credential is not mapped for any customer.
	 */
	B2BCustomerModel getCustomerForCredentialNoAuth(final Credential credential);
}
