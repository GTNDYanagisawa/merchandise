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
package de.hybris.platform.commercefacades.promotion;


import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;


/**
 * PromotionRestriction facade interface.
 */
public interface CommercePromotionRestrictionFacade
{
	/**
	 * Enables OrderPromotion by adding current cart to PromotionOrderRestriction
	 * 
	 * @param promotionCode
	 *           promotion
	 * @throws {@link CommercePromotionRestrictionException}
	 */
	public void enablePromotionForCurrentCart(String promotionCode) throws CommercePromotionRestrictionException;

	/**
	 * Disables OrderPromotion by removing current cart from PromotionOrderRestriction
	 *
	 * @param promotionCode
	 * 			promotion
	 * @throws {@link CommercePromotionRestrictionException}
	 */
	public void disablePromotionForCurrentCart(String promotionCode) throws CommercePromotionRestrictionException;
}
