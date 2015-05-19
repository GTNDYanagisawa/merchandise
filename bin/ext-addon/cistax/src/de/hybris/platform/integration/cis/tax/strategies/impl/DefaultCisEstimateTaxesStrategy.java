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
package de.hybris.platform.integration.cis.tax.strategies.impl;


import de.hybris.platform.commerceservices.externaltax.DeliveryFromAddressStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartEstimateTaxesStrategy;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.integration.cis.tax.service.CisTaxCalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.List;


public class DefaultCisEstimateTaxesStrategy implements CommerceCartEstimateTaxesStrategy
{
	private CisTaxCalculationService cisTaxCalculationService;
	private ModelService modelService;
	private CartService cartService;
	private DeliveryFromAddressStrategy estimatedDeliveryFromAddressStrategy;
	private TypeService typeService;
	private CommonI18NService commonI18NService;



	@Override
	public BigDecimal estimateTaxes(final CartModel cartModel, final String deliveryZipCode, final String deliveryCountryIsocode)
	{
		//since the cartModel is directly modified for estimations, it is probably better to clone it so no one will persist
		//these changes by mistake
		final CartModel clonedCart = cartService.clone(typeService.getComposedTypeForClass(CartModel.class),
				typeService.getComposedTypeForClass(CartEntryModel.class), cartModel, cartModel.getCode());
		clonedCart.setDeliveryFromAddress(estimatedDeliveryFromAddressStrategy.getDeliveryFromAddressForOrder(cartModel));

		final AddressModel deliveryAddress = modelService.create(AddressModel.class);
		deliveryAddress.setPostalcode(deliveryZipCode);
		deliveryAddress.setCountry(commonI18NService.getCountry(deliveryCountryIsocode));
		clonedCart.setDeliveryAddress(deliveryAddress);

		final ExternalTaxDocument externalTaxDocument = cisTaxCalculationService.calculateExternalTaxes(clonedCart);

		BigDecimal totalTaxes = BigDecimal.ZERO;
		for (final List<TaxValue> taxes : externalTaxDocument.getAllTaxes().values())
		{
			for (final TaxValue taxValue : taxes)
			{
				totalTaxes = totalTaxes.add(BigDecimal.valueOf(taxValue.getValue()));
			}
		}

		for (final TaxValue shippingTax : externalTaxDocument.getShippingCostTaxes())
		{
			totalTaxes = totalTaxes.add(BigDecimal.valueOf(shippingTax.getValue()));
		}

		return totalTaxes;
	}


	public CisTaxCalculationService getCisTaxCalculationService()
	{
		return cisTaxCalculationService;
	}

	public void setCisTaxCalculationService(final CisTaxCalculationService cisTaxCalculationService)
	{
		this.cisTaxCalculationService = cisTaxCalculationService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	public DeliveryFromAddressStrategy getEstimatedDeliveryFromAddressStrategy()
	{
		return estimatedDeliveryFromAddressStrategy;
	}

	public void setEstimatedDeliveryFromAddressStrategy(final DeliveryFromAddressStrategy estimatedDeliveryFromAddressStrategy)
	{
		this.estimatedDeliveryFromAddressStrategy = estimatedDeliveryFromAddressStrategy;
	}


	public TypeService getTypeService()
	{
		return typeService;
	}

	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

}
