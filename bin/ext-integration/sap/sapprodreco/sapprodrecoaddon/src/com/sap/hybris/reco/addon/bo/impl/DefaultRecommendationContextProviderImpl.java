/**
 * 
 */
package com.sap.hybris.reco.addon.bo.impl;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.reco.addon.bo.RecommendationContextProvider;
import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;


/**
 * @author Administrator
 * 
 */
public class DefaultRecommendationContextProviderImpl implements RecommendationContextProvider
{

	private String productId;
	private String itemType;
	private String includeCart;
	private String userId;
	private String recommendationModel;	
	private final List<String> cartItems = new ArrayList<String>();
	private CMSSAPRecommendationComponentModel componentModel;


	@Override
	public String getProductId()
	{
		return productId;
	}

	@Override
	public String getUserId()
	{
		return userId;
	}

	@Override
	public List<String> getCartItems()
	{
		return cartItems;
	}

	@Override
	public void addCartItem(final String cartItem)
	{
		cartItems.add(cartItem);
	}

	/**
	 * @param productId
	 *           the productId to set
	 */
	@Override
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	/**
	 * @param userId
	 *           the userId to set
	 */
	@Override
	public void setUserId(final String userId)
	{
		this.userId = userId;
	}

	@Override
	public CMSSAPRecommendationComponentModel getComponentModel()
	{
		return componentModel;
	}

	@Override
	public void setComponentModel(final CMSSAPRecommendationComponentModel componentModel)
	{
		this.componentModel = componentModel;
	}

	public String getRecommendationModel()
	{
		return recommendationModel;
	}

	public void setRecommendationModel(String model)
	{
		this.recommendationModel = model;
	}

	public String getItemDataSourceType()
	{
		return itemType;
	}

	public void setItemDataSourceType(String itemType)
	{
      this.itemType = itemType;
	}

	public String getIncludeCart()
	{
      return includeCart;
	}

	public void setIncludeCart(String includeCart)
	{
      this.includeCart = includeCart;
	}



}
