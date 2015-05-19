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
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.B2BCartFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade}.
 */
@Deprecated
public class DefaultCartFacade extends de.hybris.platform.commercefacades.order.impl.DefaultCartFacade implements B2BCartFacade {
    protected static final Logger LOG = Logger.getLogger(DefaultCartFacade.class);

    public static final String VARIANT_TYPE = "GenericVariantProduct";

    private ProductFacade b2bProductFacade;

    @Override
    public <T extends AbstractOrderData> void groupMultiDimensionalProducts(final T orderData,
                                                                            final Comparator<VariantOptionData> variantSortStrategy) {
        final Map<Integer, String> baseProductsMap = new HashMap<Integer, String>();

        if (orderData.getEntries() == null) {
            return;
        }

        for (final OrderEntryData entry : orderData.getEntries()) {
            final ProductData product = entry.getProduct();
            if (product.getBaseOptions().size() > 0
                    && product.getBaseOptions().get(0).getVariantType().equalsIgnoreCase(VARIANT_TYPE)
                    && !baseProductsMap.containsValue(product.getBaseProduct())) {
                baseProductsMap.put(orderData.getEntries().indexOf(entry), product.getBaseProduct());
            }
        }

        for (final Map.Entry<Integer, String> entry : baseProductsMap.entrySet()) {
            final ProductData productData = populateProduct(entry.getValue(), variantSortStrategy);
            final OrderEntryData orderEntry = groupOrderEntry(orderData, productData, entry.getValue());

            orderData.getEntries().add(orderEntry);
        }
    }

    protected ProductData populateProduct(final String productCode, final Comparator<VariantOptionData> variantSortStrategy) {
        final ProductModel productModel = getProductService().getProductForCode(productCode);
        final ProductData productData = b2bProductFacade.getProductForOptions(productModel, Arrays.asList(ProductOption.BASIC,
                ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY,
                ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.VARIANT_FULL, ProductOption.STOCK,
                ProductOption.VOLUME_PRICES, ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX));

        sortVariantOptionData(productData, variantSortStrategy);
        return productData;
    }

    protected void sortVariantOptionData(final ProductData productData, final Comparator<VariantOptionData> variantSortStrategy) {
        if (CollectionUtils.isNotEmpty(productData.getBaseOptions())) {
            for (final BaseOptionData baseOptionData : productData.getBaseOptions()) {
                if (CollectionUtils.isNotEmpty(baseOptionData.getOptions())) {
                    Collections.sort(baseOptionData.getOptions(), variantSortStrategy);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(productData.getVariantOptions())) {
            Collections.sort(productData.getVariantOptions(), variantSortStrategy);
        }
    }

    private <T extends AbstractOrderData> OrderEntryData groupOrderEntry(final T orderData, final ProductData productData,
                                                                         final String baseProduct) {
        final OrderEntryData orderEntry = new OrderEntryData();

        orderEntry.setBasePrice(buildZeroPrice());
        orderEntry.setTotalPrice(buildZeroPrice());

        orderEntry.setQuantity(0L);
        orderEntry.setProduct(productData);

        boolean setUrl = false;
        int i = 0;
        while (i < orderData.getEntries().size()) {
            final OrderEntryData currentEntry = orderData.getEntries().get(i);
            final String currentBaseProduct = currentEntry.getProduct().getBaseProduct();
            if (StringUtils.equals(currentBaseProduct, baseProduct)) {
                if (!setUrl) {
                    // set to the url of the first variant in the cart
                    orderEntry.getProduct().setUrl(currentEntry.getProduct().getUrl());
                    setUrl = true;
                }
                final PriceData basePrice = createPrice(currentEntry.getBasePrice(), orderEntry.getBasePrice());
                orderEntry.setBasePrice(basePrice);

                final PriceData totalPrice = createPrice(currentEntry.getTotalPrice(), orderEntry.getTotalPrice());
                orderEntry.setTotalPrice(totalPrice);

                orderEntry.setEntryNumber(currentEntry.getEntryNumber());
                orderEntry.setQuantity(orderEntry.getQuantity() + currentEntry.getQuantity());

                orderEntry.setUpdateable(currentEntry.isUpdateable());

                orderData.getEntries().remove(i);
            } else {
                i++;
            }
        }
        return orderEntry;
    }

    private PriceData createPrice(final PriceData currentEntryPrice, final PriceData orderEntryPrice) {
        return getPriceDataFactory().create(currentEntryPrice.getPriceType(),
                BigDecimal.valueOf(orderEntryPrice.getValue().longValue() + currentEntryPrice.getValue().longValue()),
                currentEntryPrice.getCurrencyIso());
    }

    private PriceData buildZeroPrice() {
        final PriceData price = new PriceData();
        price.setValue(BigDecimal.valueOf(0L));
        return price;
    }

    @Override
    @Required
    public void setCartService(final CartService cartService) {
        if (cartService instanceof B2BCartService) {
            super.setCartService(cartService);
        } else {
            final String msg = "CartService for DefaultB2BCartFacade should be an instance of B2BCartService.";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected B2BCartService getCartService() {
        return (B2BCartService) super.getCartService();
    }

    public ProductFacade getB2bProductFacade() {
        return b2bProductFacade;
    }

    @Required
    public void setB2bProductFacade(final ProductFacade b2bProductFacade) {
        this.b2bProductFacade = b2bProductFacade;
    }


    @Override
    public CartModificationData addOrderEntry(OrderEntryData cartEntry) throws EntityValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CartModificationData updateOrderEntry(OrderEntryData cartEntry) throws EntityValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CartModificationData> addOrderEntryList(List<OrderEntryData> cartEntries) throws EntityValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CartModificationData> updateOrderEntryList(List<OrderEntryData> cartEntries) throws EntityValidationException {
        throw new UnsupportedOperationException();
    }
}
