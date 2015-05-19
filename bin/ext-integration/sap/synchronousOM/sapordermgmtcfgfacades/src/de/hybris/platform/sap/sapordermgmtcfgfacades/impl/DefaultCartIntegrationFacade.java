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
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.constants.SapproductconfigruntimeinterfaceConstants;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.servicelayer.session.SessionService;



public class DefaultCartIntegrationFacade implements ConfigurationCartIntegrationFacade
{
	ProductConfigurationService productConfigurationService;
	CartService cartService;
	ProductService productService;
	BackendAvailabilityService backendAvailabilityService;
	CartRestorationFacade cartRestorationFacade;
	SessionService sessionService;

	private ConfigurationCartIntegrationFacade productConfigDefaultCartIntegrationFacade;


	@Override
	public String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException
	{
		if (backendAvailabilityService.isBackendDown())
		{
			final String itemKey = getProductConfigDefaultCartIntegrationFacade().addConfigurationToCart(configuration);
			getSessionService().setAttribute(SapproductconfigruntimeinterfaceConstants.PRODUCT_CONFIG_SESSION_PREFIX + itemKey,
					configuration.getConfigId());
			return itemKey;
		}
		else
		{
			final ConfigModel configModel = productConfigurationService.retrieveConfigurationModel(configuration.getConfigId());
			String itemKey = configuration.getCartItemPK();

			final boolean isItemAvailable = isItemInCartByKey(itemKey);

			if (isItemAvailable)
			{
				cartService.updateConfigurationInCart(itemKey, configModel);
			}
			else
			{
				itemKey = cartService.addConfigurationToCart(configModel);
			}

			//this needs to be done before the call of cart restoration facade as the cart restoration relies on the 
			//availability of the configuration reference in the session
			getSessionService().setAttribute(SapproductconfigruntimeinterfaceConstants.PRODUCT_CONFIG_SESSION_PREFIX + itemKey,
					configuration.getConfigId());

			//Persist the configuration for later cart restoration
			cartRestorationFacade.setSavedCart(cartService.getSessionCart());

			return itemKey;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade#isItemInCartByKey(java.lang.String
	 * )
	 */
	@Override
	public boolean isItemInCartByKey(final String key)
	{
		if (backendAvailabilityService.isBackendDown())
		{
			return productConfigDefaultCartIntegrationFacade.isItemInCartByKey(key);
		}
		else
		{
			return cartService.isItemAvailable(key);
		}
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}


	@Override
	public String copyConfiguration(final String configId)
	{
		//even if backend is down, we do a default copy of the configuration, as we want the CFG session
		//to stay in the hybris session for later reconfiguration
		final String externalConfiguration = productConfigurationService.retrieveExternalConfiguration(configId);
		final ConfigModel configModel = productConfigurationService.retrieveConfigurationModel(configId);
		final KBKey kbKey = getKBKey(configModel.getRootInstance().getName());
		final ConfigModel newConfiguration = productConfigurationService.createConfigurationFromExternal(kbKey,
				externalConfiguration);
		return newConfiguration.getId();
	}

	/**
	 * Creates a KB key for a given product ID, accessing the product model, and returns it.
	 * 
	 * @param productId
	 * @return KBKey, containing KB data for the given product
	 */
	protected KBKey getKBKey(final String productId)
	{

		final KBKey kbKey = new KBKeyImpl(productId);

		return kbKey;

	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	@Override
	public void resetConfiguration(final String configId)
	{
		//nothing needed for us as configuration must stay in session
		//even if backend is down, we want the CFG session to stay in the hybris session to offer a later
		//recovery
	}


	/**
	 * @return the productConfigDefaultCartIntegrationFacade
	 */
	public ConfigurationCartIntegrationFacade getProductConfigDefaultCartIntegrationFacade()
	{
		return productConfigDefaultCartIntegrationFacade;
	}

	/**
	 * @param productConfigDefaultCartIntegrationFacade
	 *           the productConfigDefaultCartIntegrationFacade to set
	 */
	public void setProductConfigDefaultCartIntegrationFacade(
			final ConfigurationCartIntegrationFacade productConfigDefaultCartIntegrationFacade)
	{
		this.productConfigDefaultCartIntegrationFacade = productConfigDefaultCartIntegrationFacade;
	}

	/**
	 * @return the backendAvailabilityService
	 */
	protected BackendAvailabilityService getBackendAvailabilityService()
	{
		return backendAvailabilityService;
	}

	/**
	 * @param backendAvailabilityService
	 *           the backendAvailabilityService to set
	 */
	public void setBackendAvailabilityService(final BackendAvailabilityService backendAvailabilityService)
	{
		this.backendAvailabilityService = backendAvailabilityService;
	}


	/**
	 * @return the cartRestorationFacade
	 */
	public CartRestorationFacade getCartRestorationFacade()
	{
		return cartRestorationFacade;
	}


	/**
	 * @param cartRestorationFacade
	 *           the cartRestorationFacade to set
	 */
	public void setCartRestorationFacade(final CartRestorationFacade cartRestorationFacade)
	{
		this.cartRestorationFacade = cartRestorationFacade;
	}


	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}


	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
