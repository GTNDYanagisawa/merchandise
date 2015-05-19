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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ConfigurationCartIntegrationFacadeImpl implements ConfigurationCartIntegrationFacade
{
	private ProductConfigurationService configurationService;
	private CartService cartService;
	private ModelService modelService;
	private ProductService productService;
	private CommerceCartService commerceCartService;
	private String externalConfigurationDestination;


	private static final Logger LOG = Logger.getLogger(ConfigurationCartIntegrationFacadeImpl.class);

	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	@Override
	public String addConfigurationToCart(final ConfigurationData configContent) throws CommerceCartModificationException
	{

		final ProductModel product = productService.getProductForCode(configContent.getKbKey().getProductCode());

		final AbstractOrderEntryModel cartItem = getOrCreateCartItem(product, configContent);
		final PriceData currentTotal = configContent.getPricing().getCurrentTotal();
		if (currentTotal != ConfigPricing.NO_PRICE)
		{
			cartItem.setBasePrice(Double.valueOf(currentTotal.getValue().doubleValue()));
			modelService.save(cartItem);
			commerceCartService.calculateCart(cartService.getSessionCart());
		}

		final String configId = configContent.getConfigId();
		final String xml = configurationService.retrieveExternalConfiguration(configId);
		modelService.setAttributeValue(cartItem, externalConfigurationDestination, xml);

		modelService.save(cartItem);


		final String key = cartItem.getPk().toString();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Added product '" + product.getCode() + "' with configId '" + configContent.getConfigId()
					+ "' to cart, referenced by cart entry PK '" + key + "'");
			LOG.debug("Configuration saved to database: " + xml);
		}
		return key;

	}

	protected AbstractOrderEntryModel getOrCreateCartItem(final ProductModel product, final ConfigurationData configData)
			throws CommerceCartModificationException
	{
		final String pkString = configData.getCartItemPK();
		final PK cartItemPk = convertStringToPK(pkString);
		AbstractOrderEntryModel cartItem = findItemInCartByPK(cartItemPk);
		if (cartItem == null)
		{
			final CartModel cart = cartService.getSessionCart();
			final CommerceCartModification commerceItem = commerceCartService.addToCart(cart, product, 1l, product.getUnit(), true);
			cartItem = commerceItem.getEntry();
		}
		return cartItem;
	}

	protected PK convertStringToPK(final String pkString)
	{
		final PK cartItemPk;
		if (pkString != null && !pkString.isEmpty())
		{
			cartItemPk = PK.parse(pkString);
		}
		else
		{
			cartItemPk = PK.NULL_PK;
		}
		return cartItemPk;
	}


	public AbstractOrderEntryModel findItemInCartByPK(final PK cartItemPk)
	{
		AbstractOrderEntryModel cartItem = null;
		if (LOG.isDebugEnabled())
		{
			LOG.debug("search for cartItem with PK " + cartItemPk);
		}
		if (cartItemPk != null && !PK.NULL_PK.equals(cartItemPk))
		{
			for (final AbstractOrderEntryModel entry : cartService.getSessionCart().getEntries())
			{
				if (entry.getPk().equals(cartItemPk) && !modelService.isRemoved(entry))
				{
					cartItem = entry;
					if (LOG.isDebugEnabled())
					{
						LOG.debug("cartItem found for PK " + cartItemPk);
					}
					break;
				}
			}
		}
		return cartItem;
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
		final PK cartItemPK = PK.parse(key);
		final AbstractOrderEntryModel item = findItemInCartByPK(cartItemPK);

		final boolean itemExistsInCart = item != null;

		return itemExistsInCart;

	}


	@Override
	public String copyConfiguration(final String configId)
	{
		//Since we store the configuration as XML, not copying (thus returning the input) is sufficient
		return configId;
	}


	@Override
	public void resetConfiguration(final String configId)
	{
		configurationService.releaseSession(configId);
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	public void setExternalConfigurationDestination(final String externalConfigurationDestination)
	{
		this.externalConfigurationDestination = externalConfigurationDestination;
	}

	protected String getExternalConfigurationDestination()
	{
		return externalConfigurationDestination;
	}

}
