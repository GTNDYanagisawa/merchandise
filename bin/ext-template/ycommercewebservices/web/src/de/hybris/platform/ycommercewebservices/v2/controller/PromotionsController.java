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
 */
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionFacade;
import de.hybris.platform.commercefacades.promotion.PromotionOption;
import de.hybris.platform.commercewebservicescommons.cache.CacheControl;
import de.hybris.platform.commercewebservicescommons.cache.CacheControlDirective;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main Controller for Promotions
 * 
 * @pathparam code Promotion identifier (code)
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/promotions")
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 300)
public class PromotionsController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(PromotionsController.class);
	private static final String ORDER_PROMOTION = "order";
	private static final String PRODUCT_PROMOTION = "product";
	private static final String ALL_PROMOTIONS = "all";

	private static String PROMOTION_OPTIONS = "";
	private static final Set<PromotionOption> OPTIONS;
	static
	{
		for (final PromotionOption option : PromotionOption.values())
		{
			PROMOTION_OPTIONS = PROMOTION_OPTIONS + option.toString() + " ";
		}
		PROMOTION_OPTIONS = PROMOTION_OPTIONS.trim().replace(" ", YcommercewebservicesConstants.OPTIONS_SEPARATOR);
		OPTIONS = extractOptions(PROMOTION_OPTIONS);
	}

	@Resource(name = "commercePromotionFacade")
	private CommercePromotionFacade commercePromotionFacade;

	/**
	 * Method returns promotions defined for current base site.
	 * 
	 * @queryparam type Defines what type of promotions should be returned. Values supported for that parameter are:
	 *             <ul>
	 *             <li>all: All available promotions are returned</li>
	 *             <li>product: Only product promotions are returned</li>
	 *             <li>order: Only order promotions are returned</li>
	 *             </ul>
	 * @queryparam promotionGroup Only promotions from this group are returned
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of promotions
	 * @throws RequestParameterException
	 *            When value of 'type' parameter is incorrect
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(value = "promotionCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'getPromotions',#type,#promotionGroup,#fields)")
	public PromotionListWsDTO getPromotions(@RequestParam final String type,
			@RequestParam(required = false) final String promotionGroup, @RequestParam(defaultValue = "BASIC") final String fields)
			throws RequestParameterException
	{
		validateTypeParameter(type);

		final PromotionListWsDTO promotionListWsDTO = new PromotionListWsDTO();
		promotionListWsDTO
				.setPromotions(dataMapper.mapAsList(getPromotionList(type, promotionGroup), PromotionWsDTO.class, fields));
		return promotionListWsDTO;
	}

	/**
	 * Returns details of a single promotion specified by a promotion code.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Promotion details
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@Cacheable(value = "promotionCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'getPromotions',#code,#fields)")
	@ResponseBody
	public PromotionWsDTO getPromotionByCode(@PathVariable final String code,
			@RequestParam(defaultValue = "BASIC") final String fields)
	{
		final PromotionData promotionData;
		/*
		 * if (Strings.isNullOrEmpty(options)) { promotionData = commercePromotionFacade.getPromotion(code); } else
		 */
		{
			promotionData = commercePromotionFacade.getPromotion(code, OPTIONS);
		}
		final PromotionWsDTO dto = dataMapper.map(promotionData, PromotionWsDTO.class, fields);
		return dto;
	}

	protected void validateTypeParameter(final String type) throws RequestParameterException
	{
		if (!ORDER_PROMOTION.equals(type) && !PRODUCT_PROMOTION.equals(type) && !ALL_PROMOTIONS.equals(type))
		{
			throw new RequestParameterException("Parameter type=" + type
					+ " is not supported. Permitted values for this parameter are : 'order', 'product' or 'all'",
					RequestParameterException.INVALID, "type");
		}
	}

	protected List<PromotionData> getPromotionList(final String type, final String promotionGroup)
	{
		if (promotionGroup == null || promotionGroup.isEmpty())
		{
			return getPromotionList(type);
		}

		List<PromotionData> promotions = null;
		if (ORDER_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getOrderPromotions(promotionGroup);
		}
		else if (PRODUCT_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions(promotionGroup);
		}
		else if (ALL_PROMOTIONS.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions(promotionGroup);
			promotions.addAll(getCommercePromotionFacade().getOrderPromotions(promotionGroup));
		}
		return promotions;

	}

	protected List<PromotionData> getPromotionList(final String type)
	{
		List<PromotionData> promotions = null;
		if (ORDER_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getOrderPromotions();
		}
		else if (PRODUCT_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions();
		}
		else if (ALL_PROMOTIONS.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions();
			promotions.addAll(getCommercePromotionFacade().getOrderPromotions());
		}
		return promotions;
	}

	protected static Set<PromotionOption> extractOptions(final String options)
	{
		final String optionsStrings[] = options.split(YcommercewebservicesConstants.OPTIONS_SEPARATOR);

		final Set<PromotionOption> opts = new HashSet<PromotionOption>();
		for (final String option : optionsStrings)
		{
			opts.add(PromotionOption.valueOf(option));
		}
		return opts;
	}

	public CommercePromotionFacade getCommercePromotionFacade()
	{
		return commercePromotionFacade;
	}
}
