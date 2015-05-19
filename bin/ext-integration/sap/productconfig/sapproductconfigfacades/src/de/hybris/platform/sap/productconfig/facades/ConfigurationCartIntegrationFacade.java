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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;


/**
 * Facade containing for integration between the shopping cart and configurable products. <br>
 * Pure configuration related behavior is handled by the {@link ConfigurationFacade}.
 */
public interface ConfigurationCartIntegrationFacade
{

	/**
	 * Adds the current configuration to shopping cart. The configuration is attached the shopping cart item as external
	 * configuration, which is an XML-String.
	 *
	 * @param configuration
	 *           configuration to add to the shopping cart
	 * @return key/handle to re-identify the item within the session
	 * @throws CommerceCartModificationException
	 *            in case the update of the cart failed
	 */
	public String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException;


	/**
	 * @param key
	 *           /handle to re-identify the item within the session
	 * @return <code>true</code>, only if the item is in the cart
	 */
	public boolean isItemInCartByKey(String key);

	/**
	 * Copies a configuration. The implementation can decide if a deep copy is needed; if not, the input ID is simply
	 * returned.
	 *
	 * @param configId
	 *           ID of existing configuration
	 * @return ID of new configuration if a deep copy was performed; input otherwise
	 */
	public String copyConfiguration(String configId);

	/**
	 * Resets the configuration to the initial state
	 *
	 * @param configId
	 */
	public void resetConfiguration(String configId);



}
