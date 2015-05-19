/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.product;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class PriceServiceTest extends ServicelayerTransactionalTest
{
	@Resource
	private PriceService priceService;
	@Resource
	private ProductService productService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void testGetPriceInformations() throws Exception
	{
		final ProductModel product = productService.getProduct("testProduct0");
		assertNotNull("Product", product);
		final List<PriceInformation> priveInformations = priceService.getPriceInformationsForProduct(product);
		assertNotNull("Price Informations", priveInformations);
		assertFalse("Price Informations empty", priveInformations.isEmpty());
	}



}
