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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartRestorationService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.ProductConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link CartRestorationFacade}
 * 
 */
public class DefaultCartRestorationFacade implements CartRestorationFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCartRestorationFacade.class);

	private CartRestorationService cartRestorationService;
	private CartService cartService;
	private CommerceCartService commerceCartService;
	private ProductService productService;
	private BaseSiteService baseSiteService;
	private ProductConfigurationService productConfigurationService;
	private ModelService modelService;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade#restoreSavedCart(java.lang.String,
	 * de.hybris.platform.core.model.user.UserModel)
	 */
	@Override
	public CartRestorationData restoreSavedCart(final String guid, final UserModel currentUser)
			throws CommerceCartRestorationException
	{
		LOG.info("restoreSavedCart method called with: " + guid);

		if (this.getCartRestorationService().hasInternalSessionCart()
				&& this.getCartRestorationService().getInternalSessionCart().getEntries().isEmpty())
		{
			this.getCartRestorationService().setInternalSessionCart(null);
		}

		final CartModel cartForGuidAndSiteAndUser = this.getCommerceCartService().getCartForGuidAndSiteAndUser(guid,
				getBaseSiteService().getCurrentBaseSite(), currentUser);
		final CartRestorationData restaurationData = this.getCartRestorationService().restoreCart(cartForGuidAndSiteAndUser);
		return restaurationData;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade#removeSavedCart()
	 */
	@Override
	public void removeSavedCart()
	{
		this.getCartRestorationService().removeInternalSessionCart();

		LOG.info("removeSessionCart method called");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade#setSavedCart(de.hybris.platform.
	 * commercefacades.order.data.CartData)
	 */
	@Override
	public void setSavedCart(final CartData newCart) throws CommerceCartModificationException
	{
		emptyCart();

		addCartEntriesToStandardCart(newCart);

	}


	/**
	 * Creates a new cart (in hybris persistence) from the current (BOL based) representation of the cart
	 * 
	 * @param newCart
	 *           to take the entries from
	 * @throws CommerceCartModificationException
	 */
	protected void addCartEntriesToStandardCart(final CartData newCart) throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(this.getCartRestorationService().getInternalSessionCart());
		ProductModel productModel;

		for (final OrderEntryData entry : newCart.getEntries())
		{
			productModel = this.getProductService().getProductForCode(entry.getProduct().getCode());
			parameter.setProduct(productModel);
			parameter.setQuantity(entry.getQuantity().longValue());
			parameter.setUnit(productModel.getUnit());
			parameter.setCreateNewEntry(true);

			final CommerceCartModification modification = this.getCommerceCartService().addToCart(parameter);
			final AbstractOrderEntryModel entryModel = modification.getEntry();
			if (productConfigurationService.isInSession(entry.getHandle()))
			{
				entryModel.setExternalConfiguration(productConfigurationService.getExternalConfiguration(entry.getHandle()));
				entryModel.setBasePrice(productConfigurationService.getTotalPrice(entry.getHandle()));
				modelService.save(entryModel);
			}
		}
	}


	private void emptyCart()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(this.getCartRestorationService().getInternalSessionCart());

		this.getCommerceCartService().removeAllEntries(parameter);
	}


	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}


	protected CartRestorationService getCartRestorationService()
	{
		return cartRestorationService;
	}


	public void setCartRestorationService(final CartRestorationService cartRestorationService)
	{
		this.cartRestorationService = cartRestorationService;
	}


	protected CartService getCartService()
	{
		return cartService;
	}


	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}


	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}


	public void setProductConfigurationService(final ProductConfigurationService configurationContainer)
	{
		this.productConfigurationService = configurationContainer;
	}


	public ModelService getModelService()
	{
		return modelService;
	}


	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
