package de.hybris.platform.sap.sappricing.services.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;


public class DefaultSapPricingEnablementService implements SapPricingEnablementService
{
	protected static final Logger LOGGER = Logger
			.getLogger(DefaultSapPricingEnablementService.class);
	private ModuleConfigurationAccess moduleConfigurationAccess;

	@Override
	public boolean isCartPricingEnabled()
	{
		
		if (getModuleConfigurationAccess().isSAPConfigurationActive())
		{
			boolean isCartPricingEnabled = getModuleConfigurationAccess().getProperty(SappricingbolConstants.CONF_PROP_IS_ACTIVE_CART_PRICING);
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Cart Pricing Enabled: " + isCartPricingEnabled);
			}
			return isCartPricingEnabled;
		}
		else
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("No Base Store Configuration Assigned");
			}
			return false;
		}
		
	}

	@Override
	public boolean isCatalogPricingEnabled()
	{
		if (getModuleConfigurationAccess().isSAPConfigurationActive())
		{
			boolean isCatalogPricingEnabled =  getModuleConfigurationAccess().getProperty(SappricingbolConstants.CONF_PROP_IS_ACTIVE_CATALOG_PRICING);
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Catalog Pricing Enabled: " + isCatalogPricingEnabled);
			}
			return isCatalogPricingEnabled;
		}
		else
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("No Base Store Configuration Assigned");
			}
			return false;
		}
	}

	public ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		return moduleConfigurationAccess;
	}

	@Required
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}



}
