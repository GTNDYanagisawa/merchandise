/**
 * 
 */
package com.sap.hybris.reco.addon.facade;

import de.hybris.platform.commercefacades.product.data.ProductReferenceData;

import java.util.List;

//import com.sap.hybris.reco.module.bo.RecommendationContext;
import com.sap.hybris.reco.addon.bo.RecommendationContextProvider;


/**
 * @author Administrator
 * 
 */
public interface ProductRecommendationManagerFacade
{

	public List<ProductReferenceData> getProductRecommendation(RecommendationContextProvider context);

	public RecommendationContextProvider createRecommendationContextProvider();

}
