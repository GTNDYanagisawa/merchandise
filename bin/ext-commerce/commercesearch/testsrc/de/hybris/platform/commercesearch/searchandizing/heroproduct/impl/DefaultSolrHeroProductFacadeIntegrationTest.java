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
package de.hybris.platform.commercesearch.searchandizing.heroproduct.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.commercesearch.searchandizing.AbstractSearchandisingIntegrationTest;
import de.hybris.platform.commercesearch.searchandizing.heroproduct.HeroProductFacade;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService.Executor;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


@ManualTest
public class DefaultSolrHeroProductFacadeIntegrationTest extends AbstractSearchandisingIntegrationTest
{

	protected final String CATEGORY_CODE_WITH_HEROES = "575";
	protected final String[] HERO_PRODUCT_CODES ={ "1391319", "816802" };

	@Resource
	private HeroProductFacade heroProductFacade;

	@Test
	public void addedHeroProductsShouldBeFound() throws Throwable
	{
		final List<String> heroProducts = createAndGetHeroProducts();
		for (int i = 0; i < HERO_PRODUCT_CODES.length; i++)
		{
			assertEquals("Hero product code at position " + i, HERO_PRODUCT_CODES[i], heroProducts.get(i));
		}
	}

	private List<String> createAndGetHeroProducts() throws Throwable
	{
		final List<String> heroProducts = executeInContext(new Executor<List<String>, Throwable>()
		{

			@Override
			public List<String> execute() throws Throwable
			{
				for (int i = 0; i < HERO_PRODUCT_CODES.length; i++)
				{
					final String code = HERO_PRODUCT_CODES[i];
					heroProductFacade.addHeroProduct(CATEGORY_CODE_WITH_HEROES, code);
				}
				return heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
			}

		});
		assertNotNull("Hero products cannot be null", heroProducts);
		assertEquals("Count of hero products", HERO_PRODUCT_CODES.length, heroProducts.size());
		return heroProducts;
	}

	@Test
	public void movedUpProductShouldBeFirst() throws Throwable
	{
		List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		final String secondProduct = heroProducts.get(1);
		heroProductFacade.moveHeroProductUp(CATEGORY_CODE_WITH_HEROES, secondProduct);
		heroProducts = heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
		assertEquals("First Hero product ", secondProduct, heroProducts.get(0));
		assertEquals("Second Hero product ", firstProduct, heroProducts.get(1));
	}

	@Test
	public void movedDownProductShouldBeSecond() throws Throwable
	{
		List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		final String secondProduct = heroProducts.get(1);
		heroProductFacade.moveHeroProductDown(CATEGORY_CODE_WITH_HEROES, firstProduct);
		heroProducts = heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
		assertEquals("First Hero product ", secondProduct, heroProducts.get(0));
		assertEquals("Second Hero product ", firstProduct, heroProducts.get(1));
	}

	@Test
	public void movedUpFirstProductShouldDoNothing() throws Throwable
	{
		final List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		heroProductFacade.moveHeroProductUp(CATEGORY_CODE_WITH_HEROES, firstProduct);
		for (int i = 0; i < HERO_PRODUCT_CODES.length; i++)
		{
			assertEquals("Hero product code at position " + i, HERO_PRODUCT_CODES[i], heroProducts.get(i));
		}
	}

	@Test
	public void movedDownLastProductShouldDoNothing() throws Throwable
	{
		final List<String> heroProducts = createAndGetHeroProducts();
		final String lastProduct = heroProducts.get(heroProducts.size() - 1);
		heroProductFacade.moveHeroProductDown(CATEGORY_CODE_WITH_HEROES, lastProduct);
		for (int i = 0; i < HERO_PRODUCT_CODES.length; i++)
		{
			assertEquals("Hero product code at position " + i, HERO_PRODUCT_CODES[i], heroProducts.get(i));
		}
	}

	@Test
	public void moveToPosition0shouldBeFirst() throws Throwable
	{
		List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		final String secondProduct = heroProducts.get(1);
		heroProductFacade.moveHeroProductToPosition(CATEGORY_CODE_WITH_HEROES, secondProduct, 0);
		heroProducts = heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
		assertEquals("First Hero product ", secondProduct, heroProducts.get(0));
		assertEquals("Second Hero product ", firstProduct, heroProducts.get(1));
	}

	@Test
	public void moveToNegativeShouldBeFirst() throws Throwable
	{
		List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		final String secondProduct = heroProducts.get(1);
		heroProductFacade.moveHeroProductToPosition(CATEGORY_CODE_WITH_HEROES, secondProduct, -1);
		heroProducts = heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
		assertEquals("First Hero product ", secondProduct, heroProducts.get(0));
		assertEquals("Second Hero product ", firstProduct, heroProducts.get(1));
	}

	@Test
	public void moveToPosition1shouldBeSecond() throws Throwable
	{
		List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		final String secondProduct = heroProducts.get(1);
		heroProductFacade.moveHeroProductToPosition(CATEGORY_CODE_WITH_HEROES, firstProduct, 1);
		heroProducts = heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
		assertEquals("First Hero product ", secondProduct, heroProducts.get(0));
		assertEquals("Second Hero product ", firstProduct, heroProducts.get(1));
	}

	@Test
	public void moveToLastPositionshouldBeLast() throws Throwable
	{
		List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		heroProductFacade.moveHeroProductToPosition(CATEGORY_CODE_WITH_HEROES, firstProduct, heroProducts.size() - 1);
		heroProducts = heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
		assertEquals("Last Hero product ", firstProduct, heroProducts.get(heroProducts.size() - 1));
	}

	@Test
	public void moveToOutOfBoundsShouldBeLast() throws Throwable
	{
		List<String> heroProducts = createAndGetHeroProducts();
		final String firstProduct = heroProducts.get(0);
		heroProductFacade.moveHeroProductToPosition(CATEGORY_CODE_WITH_HEROES, firstProduct, 999999);
		heroProducts = heroProductFacade.getHeroProductsForCategory(CATEGORY_CODE_WITH_HEROES);
		assertEquals("Last Hero product ", firstProduct, heroProducts.get(heroProducts.size() - 1));
	}
}
