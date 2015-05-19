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
package de.hybris.platform.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.Test;


@IntegrationTest
public class ProductTest extends HybrisJUnit4TransactionalTest
{
	@Test
	public void testSome()
	{
		final Product product = jaloSession.getProductManager().createProduct("sabbers");
		final Unit unit = jaloSession.getProductManager().createUnit("dossels", "quabbels");

		assertEquals("sabbers", product.getCode());
		product.setCode("newcode");
		assertEquals("newcode", product.getCode());

		assertNull(product.getUnit());
		product.setUnit(unit);
		assertEquals(unit, product.getUnit());

		assertNull(unit.getName());
		unit.setName("fizzels");
		assertEquals("fizzels", unit.getName());
		assertEquals("dossels", unit.getUnitType());
	}
}
