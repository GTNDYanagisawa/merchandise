/**
 * 
 */
package com.sap.hybris.reco.addon.bo;

import java.util.List;

import com.sap.hybris.reco.addon.bo.RecommendationContextProvider;
import com.sap.hybris.reco.addon.dao.ProductRecommendation;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;


/**
 * @author Administrator
 * 
 */
public interface ProductRecommendationManager extends BusinessObject
{

	public List<ProductRecommendation> getProductRecommendation(RecommendationContextProvider context);

}
