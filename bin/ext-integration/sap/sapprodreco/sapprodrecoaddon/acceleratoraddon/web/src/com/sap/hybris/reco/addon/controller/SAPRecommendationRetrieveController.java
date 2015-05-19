/**
 * 
 */
package com.sap.hybris.reco.addon.controller;

import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.log4j.Logger;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sap.hybris.reco.addon.facade.ProductRecommendationManagerFacade;
import com.sap.hybris.reco.addon.be.cei.ProductRecommendationManagerCEI;
import com.sap.hybris.reco.addon.bo.RecommendationContextProvider;

/**
 * Controller for RecommendationList view.
 */
@Controller("SAPRecommendationRetrieveController")
public class SAPRecommendationRetrieveController
{
	private final static Logger LOG = Logger.getLogger( SAPRecommendationRetrieveController.class.getName() );

	@Resource(name = "sapProductRecommendationManagerFacade")
	private ProductRecommendationManagerFacade productRecommendationManagerFacade;

	@Resource(name = "productService")
	private ProductService productService;


	@RequestMapping(value = "/action/recommendations/{title}/{recommendationModel}/{productCode}/{itemType}/{includeCart}")
	public String retrieveRecommentdations(@PathVariable final String title, @PathVariable final String recommendationModel, 
			@PathVariable final String productCode, @PathVariable final String itemType, @PathVariable final String includeCart,final Model model)
			throws UnsupportedEncodingException
	{

		LOG.debug("Read product recommendations " + productCode + recommendationModel);
		final String viewName = "addon:/sapprodrecoaddon/cms/recommendationlist";
		if (recommendationModel == null || recommendationModel.equals(""))
		{
			LOG.error("Recommendation Model has to be specified.");
			return viewName;
		}
		final RecommendationContextProvider context = productRecommendationManagerFacade.createRecommendationContextProvider();
		context.setProductId(productCode);
		context.setItemDataSourceType(itemType);
		context.setRecommendationModel(recommendationModel);
		context.setIncludeCart(includeCart);
		
		model.addAttribute("title", title);
		final List<ProductReferenceData> productReferences = productRecommendationManagerFacade
					.getProductRecommendation(context);

		model.addAttribute("productReferences", productReferences);
			
		return viewName;
	}


}

