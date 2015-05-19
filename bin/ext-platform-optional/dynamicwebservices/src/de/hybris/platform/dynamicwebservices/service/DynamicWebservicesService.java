/**
 * 
 */
package de.hybris.platform.dynamicwebservices.service;

import de.hybris.platform.dynamicwebservices.model.DynamicWebServiceModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;


/**
 * For looking up {@link DynamicWebServiceModel} models.
 * 
 * @author ag
 * 
 */
public interface DynamicWebservicesService
{
	/**
	 * Looks up <b>enabled</b> web service items by the given code and domain.
	 * 
	 * @throws ModelNotFoundException
	 *            in case no such service exists
	 * @see #findWebService(String, String)
	 */
	DynamicWebServiceModel findEnabledWebService(String code, String domain);

	/**
	 * Looks up web service items by the given code and domain.
	 * 
	 * @throws ModelNotFoundException
	 *            in case no such service exists
	 * @see #findEnabledWebService(String, String)
	 */
	DynamicWebServiceModel findWebService(String code, String domain);
}
