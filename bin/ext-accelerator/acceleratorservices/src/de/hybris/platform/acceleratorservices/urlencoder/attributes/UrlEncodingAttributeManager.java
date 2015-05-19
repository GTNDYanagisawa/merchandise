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
package de.hybris.platform.acceleratorservices.urlencoder.attributes;


import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Collection;

/**
 * Interface for UrlEncodingAttributeManagers. The concrete implementation is injected into {@link de.hybris.platform.acceleratorservices.urlencoder.impl.DefaultUrlEncoderService}
 * as a map. The kep of the map should exactly match the name of the urlEncodingAttribute configured in the CMSSiteModel.
 *
 */
public interface UrlEncodingAttributeManager
{
	/**
	 * Get all the possible/available values for a particular attribute.
	 *
	 * @return List of
	 */
	Collection<String> getAllAvailableValues();

	/**
	 * Checks if the given value is present in it's list of available values.
	 *
	 * @param value to be checked
	 * @return  boolean value
	 */
	boolean isValid(String value);

	/**
	 * Update the store/site for the given value.
	 *
	 * @param value to be updated
	 */
	void updateAndSyncForAttrChange(String value);

	/**
	 * Returns the default value for the attribute
	 *
	 * @return - defaultvalue for the attribute
	 */
	String getDefaultValue();

	/**
	 * Returns the current value for the attribute
	 *
	 * @return - current value
	 */
	String getCurrentValue();

	/**
	 *
	 * @param businessProcessModel
	 * @return
	 */
	String getAttributeValueForEmail(BusinessProcessModel businessProcessModel);

}
