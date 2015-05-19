package de.hybris.platform.sap.sappricingbol.backend.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * SapPricingCachedBackendERP to read cached catalog prices
 */
public class SapPricingCachedBackendERP
{
	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(SapPricingCachedBackendERP.class.getName());
	private ModuleConfigurationAccess moduleConfigurationAccess = null;
	private CacheAccess cacheAccess;

	/**
	 * @param productModel
	 * @param partnerFunction
	 * @return List<PriceInformation>
	 */
	@SuppressWarnings("unchecked")
	public List<PriceInformation> readCachedPriceInformationForProduct(final ProductModel productModel,
			final SapPricingPartnerFunction partnerFunction)
	{
		sapLogger.entering("readCachedPriceInformationForProduct(...)");
		if (isCatalogPriceCacheEnabled())
		{
			sapLogger.exiting();
			return (List<PriceInformation>) cacheAccess.get(getPriceCacheKey(productModel, partnerFunction));
		}

		sapLogger.exiting();
		return null;
	}
	
	/**
	 * @param productModels
	 * @param partnerFunction
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PriceInformation> readCachedPriceInformationForProducts(List<ProductModel> productModels,
			SapPricingPartnerFunction partnerFunction)
	{
		sapLogger.entering("readCachedPriceInformationForProducts(...)");
		if (isCatalogPriceCacheEnabled())
		{
			sapLogger.exiting();
			return (List<PriceInformation>) cacheAccess.get(getPriceCacheKey(productModels, partnerFunction));
		}

		sapLogger.exiting();
		return null;
	}

	/**
	 * Add catalog price to the cache
	 * 
	 * @param productModel
	 * @param partnerFunction
	 * @param priceInformationList
	 */
	public void cachePriceInformationForProduct(final ProductModel productModel, final SapPricingPartnerFunction partnerFunction,
			final List<PriceInformation> priceInformationList)
	{
		sapLogger.entering("cachePriceInformationForProduct(...)");
		if (isCatalogPriceCacheEnabled())
		{
			try
			{
				cacheAccess.put(getPriceCacheKey(productModel, partnerFunction), priceInformationList);
			}
			catch (final SAPHybrisCacheException e)
			{
				throw new BackendRuntimeException("Error while accessing the SAP prices cache!" , e);
			}
			finally
			{
				sapLogger.exiting();
			}
		}
		sapLogger.exiting();
	}
	
	/**
	 * @param productModels
	 * @param partnerFunction
	 * @param priceInformationList
	 */
	public void cachePriceInformationForProducts(List<ProductModel> productModels, SapPricingPartnerFunction partnerFunction,
			List<PriceInformation> priceInformationList)
	{
		sapLogger.entering("cachePriceInformationForProducts(...)");
		if (isCatalogPriceCacheEnabled())
		{
			try
			{
				cacheAccess.put(getPriceCacheKey(productModels, partnerFunction), priceInformationList);
			}
			catch (final SAPHybrisCacheException e)
			{
				throw new BackendRuntimeException("Error while accessing the SAP prices cache!", e);
			}
			finally
			{
				sapLogger.exiting();
			}
		}
		sapLogger.exiting();
		
	}
	
	protected String getPriceCacheKey(final ProductModel productModel, final SapPricingPartnerFunction partnerFunction)
	{
		final StringBuffer sapPricingCacheKey = new StringBuffer();

		sapPricingCacheKey.append(SappricingbolConstants.CACHEKEY_SAP_PRICING);
		sapPricingCacheKey.append(productModel.getPk());
		sapPricingCacheKey.append(productModel.getCode());
		sapPricingCacheKey.append(partnerFunction.getSoldTo());
		sapPricingCacheKey.append(partnerFunction.getCurrency());
		sapPricingCacheKey.append(partnerFunction.getLanguage());

		return sapPricingCacheKey.toString();
	}
	
	protected Object getPriceCacheKey(List<ProductModel> productModels, SapPricingPartnerFunction partnerFunction)
	{
		final StringBuffer sapPricingCacheKey = new StringBuffer();

		sapPricingCacheKey.append(SappricingbolConstants.CACHEKEY_SAP_PRICING);

		for (ProductModel productModel : productModels)
		{
			sapPricingCacheKey.append(productModel.getPk());
			sapPricingCacheKey.append(productModel.getCode());
		}
		
		sapPricingCacheKey.append(partnerFunction.getSoldTo());
		sapPricingCacheKey.append(partnerFunction.getCurrency());
		sapPricingCacheKey.append(partnerFunction.getLanguage());

		return sapPricingCacheKey.toString();
	}

	protected boolean isCatalogPriceCacheEnabled()
	{
		
		boolean isCacheEnabled = getModuleConfigurationAccess().getProperty(SappricingbolConstants.CONF_PROP_IS_CACHED_CATALOG_PRICE);
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("Cache Enabled: " + isCacheEnabled );
		}
		return isCacheEnabled;
	}

	/**
	 * @return CacheAccess
	 */
	public CacheAccess getCacheAccess()
	{
		return cacheAccess;
	}

	/**
	 * @param cacheAccess
	 */
	@Required
	public void setCacheAccess(final CacheAccess cacheAccess)
	{
		this.cacheAccess = cacheAccess;
	}

	/**
	 * @return ModuleConfigurationAccess
	 */
	public ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		return moduleConfigurationAccess;
	}

	/**
	 * @param moduleConfigurationAccess
	 */
	@Required
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

}
