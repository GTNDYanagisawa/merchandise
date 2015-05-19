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

import java.util.Collection;
import java.util.List;

import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.promotions.model.OrderPromotionModel;


/**
 * Promotion facade interface. Its main purpose is to retrieve promotion related DTOs using existing services.
 */
public interface CommercePromotionFacade
{
	/**
	 * Get the list of product promotions
	 * 
	 * @return The list of product promotions
	 */
	public List<PromotionData> getProductPromotions();

	/**
	 * Get the list of order promotions
	 * 
	 * @return The list of {@link OrderPromotionModel}
	 */
	public List<PromotionData> getOrderPromotions();

	/**
	 * Get list of product promotions that belong to one of given promotion groups
	 * 
	 * @param promotionGroups
	 *           The promotion groups to evaluate
	 * @return The list of product promotions
	 * 
	 * @throws IllegalArgumentException
	 *            - if parameter promotionGroups is <code>null</code> OR promotionGroups is empty OR any of promotion
	 *            group from collection doesn't exists
	 */
	public List<PromotionData> getProductPromotions(Collection<String> promotionGroups);

	/**
	 * Get list of product promotions that belong to given promotion group
	 * 
	 * @param promotionGroup
	 *           The promotion group to evaluate
	 * @return The list of product promotions
	 * 
	 * @throws IllegalArgumentException
	 *            - if parameter promotionGroup is <code>null</code> OR promotionGroup doesn't exists
	 */
	public List<PromotionData> getProductPromotions(String promotionGroup);

	/**
	 * Get list of order promotion instances that belong to one of given promotion groups
	 * 
	 * @param promotionGroups
	 *           The promotion groups to evaluate
	 * @return The list of order promotion
	 * 
	 * @throws IllegalArgumentException
	 *            - if parameter promotionGroups is <code>null</code> OR promotionGroups is empty OR any of promotion
	 *            group from collection doesn't exists
	 */
	public List<PromotionData> getOrderPromotions(Collection<String> promotionGroups);

	/**
	 * Get list of order promotion instances that belong to given promotion group
	 * 
	 * @param promotionGroup
	 *           The promotion group to evaluate
	 * @return The list of order promotion
	 * 
	 * @throws IllegalArgumentException
	 *            - if parameter promotionGroup is <code>null</code> OR promotionGroup doesn't exists
	 */
	public List<PromotionData> getOrderPromotions(String promotionGroup);

	/**
	 * Get promotion base on its code
	 * 
	 * @param code
	 *           - promotion identifier
	 * @return the {@link PromotionData}
	 * 
	 * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *            if no Promotion with the specified code is found
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if more than one Promotion with the specified code is found
	 * @throws IllegalArgumentException
	 *            if parameter code is <code>null</code>
	 */
	public PromotionData getPromotion(String code);

	/**
	 * Get promotion base on its code
	 * 
	 * @param code
	 *           - promotion identifier
	 * @param options
	 *           - options set that determines amount of information that will be attached to the returned promotion.
	 *           BASIC informations are attached by default
	 * @return the {@link PromotionData}
	 * 
	 * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *            if no Promotion with the specified code is found
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if more than one Promotion with the specified code is found
	 * @throws IllegalArgumentException
	 *            if parameter code is <code>null</code>
	 */
	public PromotionData getPromotion(String code, final Collection<PromotionOption> options);
}
