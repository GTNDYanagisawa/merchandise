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
package de.hybris.platform.b2b.occ.v2.controllers;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.mapping.DataMapper;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSetBuilder;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller REST resource which replaces/extends some Product URIs
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/products")
@ApiVersion("v2")
public class B2BProductsController
{
	@Resource(name = "b2bProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	@Resource
	private FieldSetBuilder fieldSetBuilder;

	/**
	 * Gets a Product.
	 * 
	 * @param productCode
	 *           the product code
	 * @return a representation of {@link de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO}
	 */
	@RequestMapping(value = "/{productCode}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "b2bocc.B2BProductsController.getProductByCode.priority")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ProductWsDTO getProductByCode(@PathVariable final String productCode,
			@RequestParam(defaultValue = "DEFAULT") final String fields)
	{

		final Set<String> fieldSet = fieldSetBuilder.createFieldSet(ProductWsDTO.class, DataMapper.FIELD_PREFIX, fields);

		final ProductData productData = productFacade.getProductForCodeAndOptions(productCode, Arrays.asList(ProductOption.BASIC,
				ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY,
				ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
				ProductOption.VARIANT_FULL, ProductOption.STOCK, ProductOption.VOLUME_PRICES, ProductOption.PRICE_RANGE,
				ProductOption.VARIANT_MATRIX_ALL_OPTIONS, ProductOption.VARIANT_MATRIX_BASE));

		final ProductWsDTO dto = dataMapper.map(productData, ProductWsDTO.class, fieldSet);

		return dto;
	}
}
