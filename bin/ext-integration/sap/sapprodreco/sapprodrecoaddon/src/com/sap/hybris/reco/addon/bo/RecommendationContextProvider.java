/**
 * 
 */
package com.sap.hybris.reco.addon.bo;

import java.util.List;

import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;



/**
 * @author Administrator
 * 
 */
public interface RecommendationContextProvider
{

	public void addCartItem(final String cartItem);

	/**
	 * @param productId
	 *           the productId to set
	 */
	public void setProductId(final String productId);

	/**
	 * @param userId
	 *           the userId to set
	 */
	public void setUserId(final String userId);

	public String getProductId();

	public String getUserId();

	public List<String> getCartItems();

	public CMSSAPRecommendationComponentModel getComponentModel();
	public void setComponentModel(CMSSAPRecommendationComponentModel component);
	
	public String getRecommendationModel();
	public void setRecommendationModel(String model);
	
	public String getItemDataSourceType();
	public void setItemDataSourceType(String itemType);
	
	public String getIncludeCart();
	public void setIncludeCart(String includeCart);

}
