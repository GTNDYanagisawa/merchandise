/**
 * 
 */
package com.sap.hybris.reco.addon.be;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;

import java.util.List;

import com.sap.hybris.reco.addon.dao.ProductRecommendation;
import com.sap.hybris.reco.addon.bo.RecommendationContextProvider;


/**
 * @author Administrator
 * 
 */
public interface ProductRecommendationManagerBackend extends BackendBusinessObject
{
	public List<ProductRecommendation> getProductRecommendation(RecommendationContextProvider context);

}
