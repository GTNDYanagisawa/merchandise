package de.hybris.platform.sap.sappricing.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.impl.DefaultPromotionsService;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 */
public class DefaultSapPromotionsService extends DefaultPromotionsService
{
	private SapPricingEnablementService sapPricingEnablementService;
	/**
	 * SAP promotions are already calculated from backend
	 */
	@Override
	public PromotionOrderResults updatePromotions(final Collection<PromotionGroupModel> promotionGroups,
			final AbstractOrderModel order)
	{
		if (sapPricingEnablementService.isCartPricingEnabled())
		{
			return null;
		}
		else
		{
			return super.updatePromotions(promotionGroups, order);
		}
		
	}

	/**
	 * SAP promotions are already calculated from backend
	 */
	@Override
	public PromotionOrderResults updatePromotions(final Collection<PromotionGroupModel> promotionGroups,
			final AbstractOrderModel order, final boolean evaluateRestrictions,
			final PromotionsManager.AutoApplyMode productPromotionMode, final PromotionsManager.AutoApplyMode orderPromotionMode,
			final Date date)
	{
		if (sapPricingEnablementService.isCartPricingEnabled())
		{
			return null;
		}
		else
		{
			return super.updatePromotions(promotionGroups, order, evaluateRestrictions, productPromotionMode, orderPromotionMode, date);
		}
	}

	/**
	 * Remove Hybris promotions from product detail page as SAP promotions are calculated in the backend
	 */
	@Override
	public List<ProductPromotionModel> getProductPromotions(final Collection<PromotionGroupModel> promotionGroups,
			final ProductModel product, final boolean evaluateRestrictions, final Date date)
	{
		if (sapPricingEnablementService.isCatalogPricingEnabled())
		{
			return null;
		}
		else
		{
			return super.getProductPromotions(promotionGroups, product, evaluateRestrictions, date);
		}
	}

	/**
	 * @return the sapPricingEnablementService
	 */
	
	public SapPricingEnablementService getSapPricingEnablementService()
	{
		return sapPricingEnablementService;
	}

	/**
	 * @param sapPricingEnablementService the sapPricingEnablementService to set
	 */
	@Required
	public void setSapPricingEnablementService(SapPricingEnablementService sapPricingEnablementService)
	{
		this.sapPricingEnablementService = sapPricingEnablementService;
	}

}
