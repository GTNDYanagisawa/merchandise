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
package de.hybris.platform.wishlist.impl;

import static junit.framework.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.wishlist2.Wishlist2Service;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class WishlistServiceTest extends ServicelayerTransactionalTest
{
	private static final String WLDEFAULT = "default";
	private static final String WLANOTHER = "another";

	@Resource
	private Wishlist2Service wishlistService;
	@Resource
	private UserService userService;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;

	private ProductModel product1;
	private ProductModel product2;
	private UserModel demoUser;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createHardwareCatalog();
		createDefaultUsers();

		final CatalogVersionModel catVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(catVersion);

		product1 = productService.getProductForCode("HW2300-2356");
		product2 = productService.getProductForCode("HW2300-4121");
		demoUser = userService.getUserForUID("demo");
		userService.setCurrentUser(demoUser);
	}

	@Test
	public void testWishlistService()
	{
		if (!wishlistService.hasDefaultWishlist())
		{
			wishlistService.createDefaultWishlist(WLDEFAULT, "My default wishlist");
		}

		wishlistService.addWishlistEntry(product1, Integer.valueOf(1), Wishlist2EntryPriority.MEDIUM, "I like it");
		wishlistService.addWishlistEntry(product2, Integer.valueOf(2), Wishlist2EntryPriority.HIGH, "Must have");
		assertEquals("Products on wishlist", 2, wishlistService.getDefaultWishlist().getEntries().size());

		wishlistService.createWishlist(WLANOTHER, "Another");

		assertEquals("Number of wishlists", 2, wishlistService.getWishlists().size());

		final Wishlist2Model dWL = wishlistService.getDefaultWishlist();
		final Wishlist2EntryModel entry = dWL.getEntries().iterator().next();

		wishlistService.removeWishlistEntry(dWL, entry);

		assertEquals("Products on wishlist", 1, wishlistService.getDefaultWishlist().getEntries().size());
	}

	@Test
	public void testGetWishlistEntryForProduct()
	{
		final Wishlist2Model wishlist = createDefaultWishlist();
		final Wishlist2EntryModel entry = wishlistService.getWishlistEntryForProduct(product1, wishlist);
		assertEquals("product should be the same", product1, entry.getProduct());
		assertEquals("wishlist should be the same", wishlist.getName(), entry.getWishlist().getName());
	}

	@Test
	public void testRemoveWishlistEntryForProduct()
	{
		final Wishlist2Model wishlist = createDefaultWishlist();
		assertEquals("one product in wishlist", 1, wishlist.getEntries().size());
		wishlistService.removeWishlistEntryForProduct(product1, wishlist);
		assertEquals("no product in wishlist", 0, wishlist.getEntries().size());
	}

	private Wishlist2Model createDefaultWishlist()
	{
		final Wishlist2Model wishlist = wishlistService.createDefaultWishlist(WLDEFAULT, "My default wishlist");
		wishlistService.addWishlistEntry(product1, Integer.valueOf(1), Wishlist2EntryPriority.MEDIUM, "good");
		return wishlist;
	}

}
