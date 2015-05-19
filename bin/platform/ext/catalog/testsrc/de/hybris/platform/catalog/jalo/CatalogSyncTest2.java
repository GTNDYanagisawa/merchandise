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
package de.hybris.platform.catalog.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloDuplicateCodeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;


/**
 * @author marcel.tietze
 * 
 */
@IntegrationTest
public class CatalogSyncTest2 extends HybrisJUnit4TransactionalTest
{
	private final CatalogManager catalogmanager = CatalogManager.getInstance();
	private final ProductManager productmanager = ProductManager.getInstance();
	private final TypeManager typemanager = TypeManager.getInstance();

	@Test
	public void testPLA7017duplicatedItemsInCV()
	{
		final Category cat1;
		Product product1, product1a, product2, product2a;
		Media media1, media2, media2a;
		try
		{
			final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
			final ComposedType prodCT = typemanager.getComposedType(Product.class);
			assertTrue(catalogmanager.isCatalogItem(prodCT));

			final ComposedType mediaCT = typemanager.getComposedType(Media.class);
			assertTrue(catalogmanager.isCatalogItemType(mediaCT).booleanValue());

			final Catalog test1cat = catalogmanager.createCatalog("test1");
			assertNotNull(test1cat);
			final CatalogVersion catver = catalogmanager.createCatalogVersion(test1cat, "cv1", null);
			assertNotNull(catver);
			product1 = productmanager.createProduct("p1");
			product1a = productmanager.createProduct("p1");
			assertNotNull(product1);
			assertNotNull(product1);
			assertNotSame(product1, product1a);
			catalogmanager.setCatalogVersion(product1, catver);
			catalogmanager.setCatalogVersion(product1a, catver);

			assertEquals(2, catver.getProducts("p1").size());
			assertEquals(1, catver.getDuplicatedCatalogItemsCount(ctx, prodCT));

			product2 = productmanager.createProduct("p2");
			product2a = productmanager.createProduct("p2");
			catalogmanager.setCatalogVersion(product2, catver);
			catalogmanager.setCatalogVersion(product2a, catver);

			cat1 = CategoryManager.getInstance().createCategory("cat1");
			cat1.addProduct(product2a);
			assertEquals(2, catver.getDuplicatedCatalogItemsCount(ctx, prodCT));

			assertEquals(0, catver.getDuplicatedCatalogItemsCount(ctx, mediaCT));

			media1 = MediaManager.getInstance().createMedia("media1");
			media2 = MediaManager.getInstance().createMedia("media2");
			catalogmanager.setCatalogVersion(media1, catver);
			catalogmanager.setCatalogVersion(media2, catver);

			product1.setPicture(media1);
			cat1.setPicture(media2);
			product1a.setPicture(media1);

			assertEquals(0, catver.getDuplicatedCatalogItemsCount(ctx, mediaCT));


			media2a = MediaManager.getInstance().createMedia("media2");
			assertEquals(0, catver.getDuplicatedCatalogItemsCount(ctx, mediaCT));

			catalogmanager.setCatalogVersion(media2a, catver);
			assertEquals(1, catver.getDuplicatedCatalogItemsCount(ctx, mediaCT));
		}
		catch (final Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testPLA7017duplicatedSpecialType()
	{
		try
		{
			final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();

			final ComposedType prod = typemanager.getComposedType(Product.class);
			final ComposedType myCT = typemanager.createComposedType(prod, "myProduct");

			catalogmanager.setCatalogItemType(myCT, Boolean.TRUE);
			final Collection<AttributeDescriptor> uniqueAD = new HashSet<AttributeDescriptor>();
			uniqueAD.add(myCT.getAttributeDescriptor("ean"));
			uniqueAD.add(myCT.getAttributeDescriptor("deliveryTime"));
			uniqueAD.add(myCT.getAttributeDescriptor("endLineNumber"));
			catalogmanager.setUniqueKeyAttributes(myCT, uniqueAD);


			final Catalog catalog = catalogmanager.createCatalog("test1");
			final CatalogVersion catalogVersion = catalogmanager.createCatalogVersion(catalog, "cv1", null);

			final Map<String, Object> values1 = new HashMap<String, Object>();
			values1.put(Product.CODE, "myProductCode1");
			final Item myItem1 = myCT.newInstance(values1);
			final Item myItem2 = myCT.newInstance(values1);
			catalogmanager.setCatalogVersion((Product) myItem1, catalogVersion);
			catalogmanager.setCatalogVersion((Product) myItem2, catalogVersion);
			assertEquals(1, catalogVersion.getDuplicatedCatalogItemsCount(ctx, myCT)); //equal, all given attributes are the same!

			myItem1.setAttribute("ean", "11");
			assertEquals(0, catalogVersion.getDuplicatedCatalogItemsCount(ctx, myCT)); //now they are different!

		}
		catch (final JaloInvalidParameterException e)
		{
			fail(e.getMessage());
		}
		catch (final JaloDuplicateCodeException e)
		{
			fail(e.getMessage());
		}
		catch (final JaloGenericCreationException e)
		{
			fail(e.getMessage());
		}
		catch (final JaloAbstractTypeException e)
		{
			fail(e.getMessage());
		}
		catch (final JaloSecurityException e)
		{
			fail(e.getMessage());
		}
		catch (final JaloBusinessException e)
		{
			fail(e.getMessage());
		}


	}
}
