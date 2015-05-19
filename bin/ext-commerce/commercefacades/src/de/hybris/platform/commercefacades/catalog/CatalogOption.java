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
package de.hybris.platform.commercefacades.catalog;


/**
 * Options for catalog facade. BASIC - only basic informations. CATEGORIES - also informations about categories.
 * PRODUCTS - works with CATEGORIES option to get informations about products as well.
 */
public enum CatalogOption
{
	BASIC("BASIC"), CATEGORIES("CATEGORIES"), PRODUCTS("PRODUCTS"), SUBCATEGORIES("SUBCATEGORIES");

	private final String code;

	private CatalogOption(final String code)
	{
		this.code = code;
	}

}
