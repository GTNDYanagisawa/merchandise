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
package de.hybris.platform.acceleratorservices.store.pickup.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.acceleratorservices.store.pickup.PickupPointOfServiceConsolidationStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultPickupPointOfServiceConsolidationStrategy implements PickupPointOfServiceConsolidationStrategy
{
	private CommerceStockService commerceStockService;
	private StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> storeFinderService;
	private CustomerLocationService customerLocationService;
	private CartService cartService;
	private BaseStoreService baseStoreService;
	private int maxOptions = 2;

	@Override
	public List<PointOfServiceDistanceData> getConsolidationOptions(final CartModel cartModel)
	{
		validateParameterNotNull(cartModel, "Cart model cannot be null");

		final Set<PointOfServiceDistanceData> consolidatedPickupPoints = new TreeSet<PointOfServiceDistanceData>(
				PointOfServiceDistanceDataComparator.INSTANCE);
		final Set<PointOfServiceModel> potentialPickupPoints = new HashSet<PointOfServiceModel>();
		final UserLocationData userLocation = getCustomerLocationService().getUserLocation();
		final Set<ProductModel> productsForPickup = new HashSet<ProductModel>();

		// Collect pickup points from Cart Entries
		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			if (entry.getDeliveryPointOfService() != null)
			{
				potentialPickupPoints.add(entry.getDeliveryPointOfService());
				productsForPickup.add(entry.getProduct());
			}
		}

		if (potentialPickupPoints.isEmpty() || potentialPickupPoints.size() == 1)
		{
			return new ArrayList<PointOfServiceDistanceData>(consolidatedPickupPoints);
		}

		for (final PointOfServiceModel pos : potentialPickupPoints)
		{
			if (checkAllStockAvailableAtPointOfService(productsForPickup, pos, cartModel))
			{
				final PointOfServiceDistanceData posDistance = createPointOfServiceDistanceData();
				posDistance.setPointOfService(pos);
				if (userLocation != null && userLocation.getPoint() != null)
				{
					posDistance.setDistanceKm(getCustomerLocationService().calculateDistance(userLocation.getPoint(), pos));
				}
				consolidatedPickupPoints.add(posDistance);
			}

			if (consolidatedPickupPoints.size() >= getMaxOptions())
			{
				break;
			}
		}

		final PageableData pageableData = getPageableData();
		if (userLocation != null && userLocation.getPoint() != null)
		{
			while (consolidatedPickupPoints.size() < getMaxOptions())
			{
				final StoreFinderSearchPageData<PointOfServiceDistanceData> result = getPOSDistanceDataForUserLocationAndBaseStore(
						userLocation, pageableData);
				if (!CollectionUtils.isEmpty(result.getResults()))
				{
					consolidateResult(cartModel, consolidatedPickupPoints, productsForPickup, result);
				}

				if (pageableData.getCurrentPage() >= result.getPagination().getNumberOfPages())
				{
					break;
				}
				else
				{
					pageableData.setCurrentPage(result.getPagination().getCurrentPage() + 1);
				}
			}
		}

		return new ArrayList<PointOfServiceDistanceData>(consolidatedPickupPoints);
	}

	protected StoreFinderSearchPageData<PointOfServiceDistanceData> getPOSDistanceDataForUserLocationAndBaseStore(
			final UserLocationData userLocation, final PageableData pageableData)
	{
		final StoreFinderSearchPageData<PointOfServiceDistanceData> result = getStoreFinderService().positionSearch(
				getBaseStoreService().getCurrentBaseStore(), userLocation.getPoint(), pageableData);
		return result;
	}

	protected void consolidateResult(final CartModel cartModel, final Set<PointOfServiceDistanceData> consolidatedPickupPoints,
			final Set<ProductModel> productsForPickup, final StoreFinderSearchPageData<PointOfServiceDistanceData> result)
	{
		final PointOfServiceDistanceData posDistance = result.getResults().get(0);
		if (!consolidatedPickupPoints.contains(posDistance)
				&& checkAllStockAvailableAtPointOfService(productsForPickup, posDistance.getPointOfService(), cartModel))
		{
			consolidatedPickupPoints.add(posDistance);
		}
	}

	protected boolean checkAllStockAvailableAtPointOfService(final Set<ProductModel> products, final PointOfServiceModel posModel,
			final CartModel cartModel)
	{
		for (final ProductModel product : products)
		{
			if (!checkStockAvailableAtPointOfService(product, posModel, cartModel))
			{
				return false;
			}
		}
		return true;
	}

	protected boolean checkStockAvailableAtPointOfService(final ProductModel productModel, final PointOfServiceModel posModel,
			final CartModel cartModel)
	{
		final Long stockLevel = getCommerceStockService().getStockLevelForProductAndPointOfService(productModel, posModel);

		return (stockLevel == null) || (stockLevel.intValue() >= calculateCartLevel(productModel, cartModel));
	}

	protected long calculateCartLevel(final ProductModel productModel, final CartModel cartModel)
	{
		long cartLevel = 0;
		for (final CartEntryModel entryModel : getCartService().getEntriesForProduct(cartModel, productModel))
		{
			if (entryModel.getDeliveryPointOfService() != null)
			{
				cartLevel += entryModel.getQuantity() != null ? entryModel.getQuantity().longValue() : 0;
			}
		}
		return cartLevel;
	}

	protected PageableData getPageableData()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(1);
		pageableData.setSort(null);
		return pageableData;
	}

	protected PointOfServiceDistanceData createPointOfServiceDistanceData()
	{
		return new PointOfServiceDistanceData();
	}

	protected static class PointOfServiceDistanceDataComparator extends AbstractComparator<PointOfServiceDistanceData>
	{
		public static final PointOfServiceDistanceDataComparator INSTANCE = new PointOfServiceDistanceDataComparator();

		@Override
		protected int compareInstances(final PointOfServiceDistanceData result1, final PointOfServiceDistanceData result2)
		{
			int result = compareValues(result1.getDistanceKm(), result2.getDistanceKm());
			if (EQUAL == result)
			{
				result = compareValues(result1.getPointOfService().getName(), result2.getPointOfService().getName(), false);
			}
			return result;
		}
	}

	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	@Required
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	protected StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> getStoreFinderService()
	{
		return storeFinderService;
	}

	@Required
	public void setStoreFinderService(
			final StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> storeFinderService)
	{
		this.storeFinderService = storeFinderService;
	}

	protected CustomerLocationService getCustomerLocationService()
	{
		return customerLocationService;
	}

	@Required
	public void setCustomerLocationService(final CustomerLocationService customerLocationService)
	{
		this.customerLocationService = customerLocationService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected int getMaxOptions()
	{
		return maxOptions;
	}

	// Optional
	public void setMaxOptions(final int maxOptions)
	{
		this.maxOptions = maxOptions;
	}
}
