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

import static de.hybris.platform.jalo.type.AttributeDescriptor.INITIAL_FLAG;
import static de.hybris.platform.jalo.type.AttributeDescriptor.OPTIONAL_FLAG;
import static de.hybris.platform.jalo.type.AttributeDescriptor.PARTOF_FLAG;
import static de.hybris.platform.jalo.type.AttributeDescriptor.READ_FLAG;
import static de.hybris.platform.jalo.type.AttributeDescriptor.REMOVE_FLAG;
import static de.hybris.platform.jalo.type.AttributeDescriptor.WRITE_FLAG;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.type.CollectionType;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.ItemCloneCreator;
import de.hybris.platform.jalo.type.ItemCloneCreator.CannotCloneException;
import de.hybris.platform.jalo.type.ItemCloneCreator.CopyContext;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test generic cloning mechanism via {@link ItemCloneCreator}.
 */
@IntegrationTest
public class GenericItemCloneTest extends HybrisJUnit4Test
{
	private Product original;
	private Media partOf1, partOf2, partOf3;

	private Unit u;

	private ComposedType adjustedProductType, adjustedMediaType;
	private CollectionType collType;

	private Map<Language, String> names;

	@Before
	public void setUp() throws Exception
	{
		final TypeManager tm = TypeManager.getInstance();
		adjustedProductType = tm.createComposedType(tm.getComposedType(Product.class), "MyProductType");

		adjustedMediaType = tm.createComposedType(tm.getComposedType(Media.class), "MyMediaType");

		collType = tm.createCollectionType("MyMediaCollType", adjustedMediaType, CollectionType.LIST);

		int modifiers = READ_FLAG + WRITE_FLAG + OPTIONAL_FLAG + PARTOF_FLAG + REMOVE_FLAG;
		adjustedProductType.createAttributeDescriptor("testMedias", collType, modifiers);

		modifiers = READ_FLAG + WRITE_FLAG + OPTIONAL_FLAG + REMOVE_FLAG;
		adjustedProductType.createAttributeDescriptor("testMediaRef", adjustedMediaType, modifiers);

		modifiers = READ_FLAG + INITIAL_FLAG + REMOVE_FLAG;
		adjustedMediaType.createAttributeDescriptor("testProd", adjustedProductType, modifiers);

		modifiers = READ_FLAG + WRITE_FLAG + OPTIONAL_FLAG + REMOVE_FLAG;
		adjustedMediaType.createAttributeDescriptor("testCrossRef", adjustedMediaType, modifiers);

		u = ProductManager.getInstance().createUnit("blah", "fasel");

		names = new HashMap<Language, String>();
		names.put(getOrCreateLanguage("de"), "name-de");
		names.put(getOrCreateLanguage("en"), "name-en");

		final Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(Product.CODE, "foo");
		attributes.put(Product.UNIT, u);
		attributes.put(Product.NAME, names);

		original = (Product) adjustedProductType.newInstance(null, attributes);

		attributes.clear();
		attributes.put(Media.CODE, "m1");
		attributes.put("testProd", original);
		partOf1 = (Media) adjustedMediaType.newInstance(null, attributes);

		attributes.clear();
		attributes.put(Media.CODE, "m3");
		attributes.put("testProd", original);
		partOf2 = (Media) adjustedMediaType.newInstance(null, attributes);

		attributes.clear();
		attributes.put(Media.CODE, "m3");
		attributes.put("testProd", original);
		partOf3 = (Media) adjustedMediaType.newInstance(null, attributes);

		original.setAttribute(null, "testMedias", Arrays.asList(partOf1, partOf2, partOf3));
		original.setAttribute(null, "testMediaRef", partOf3);

		partOf1.setAttribute("testCrossRef", partOf2);
		partOf2.setAttribute("testCrossRef", partOf3);
		partOf3.setAttribute("testCrossRef", partOf1);

	}

	@After
	public void tearDown() throws Exception
	{
		if (adjustedMediaType != null)
		{
			adjustedMediaType.getAttributeDescriptorIncludingPrivate("testProd").remove();
			adjustedMediaType.getAttributeDescriptorIncludingPrivate("testCrossRef").remove();
		}
		if (adjustedProductType != null)
		{
			adjustedProductType.getAttributeDescriptorIncludingPrivate("testMedias").remove();
			adjustedProductType.getAttributeDescriptorIncludingPrivate("testMediaRef").remove();
		}
		if (collType != null)
		{
			collType.remove();
		}
	}

	@Test
	public void testClone() throws Exception
	{

		CopyContext ctx = new CopyContext();
		final ItemCloneCreator creator = new ItemCloneCreator();

		Product copy = (Product) creator.copy(original, ctx);

		assertNotNull(copy);

		assertTrue(ctx.mustBeTranslated(original));
		assertTrue(ctx.mustBeTranslated(partOf1));
		assertTrue(ctx.mustBeTranslated(partOf2));
		assertTrue(ctx.mustBeTranslated(partOf3));

		assertEquals(copy, ctx.getCopy(original));

		assertEquals(original.getCode(), copy.getCode());
		assertEquals(original.getAllNames(null), copy.getAllNames(null));
		assertEquals(original.getUnit(), copy.getUnit());

		final Media copy1 = (Media) ctx.getCopy(partOf1);
		final Media copy2 = (Media) ctx.getCopy(partOf2);
		final Media copy3 = (Media) ctx.getCopy(partOf3);

		assertNotNull(copy1);
		assertEquals(partOf1.getCode(), copy1.getCode());
		assertEquals(copy, copy1.getAttribute("testProd"));
		assertEquals(copy2, copy1.getAttribute("testCrossRef"));


		assertNotNull(copy2);
		assertEquals(partOf2.getCode(), copy2.getCode());
		assertEquals(copy, copy2.getAttribute("testProd"));
		assertEquals(copy3, copy2.getAttribute("testCrossRef"));

		assertNotNull(copy3);
		assertEquals(partOf3.getCode(), copy3.getCode());
		assertEquals(copy, copy3.getAttribute("testProd"));
		assertEquals(copy1, copy3.getAttribute("testCrossRef"));

		assertEquals(Arrays.asList(copy1, copy2, copy3), copy.getAttribute("testMedias"));
		assertEquals(copy3, copy.getAttribute("testMediaRef"));

		// test presets
		assertEquals("foo", original.getCode());

		ctx = new CopyContext();
		ctx.addPreset(original, Product.CODE, "xxx");
		ctx.addPreset(original, Product.UNIT, null);

		copy = (Product) creator.copy(original, ctx);

		assertNotNull(copy);
		assertEquals("xxx", copy.getCode());
		assertNull(copy.getUnit());
		assertFalse(original.getCode().equals(copy.getCode()));
		assertFalse(original.getUnit().equals(copy.getUnit()));
	}

	@Test
	public void testCycleError()
	{
		// by making media-media cross reference mandatory it's impossible to create partOf medias !!! 
		adjustedMediaType.getAttributeDescriptorIncludingPrivate("testCrossRef").setOptional(false);

		final CopyContext ctx = new CopyContext();
		final ItemCloneCreator creator = new ItemCloneCreator();

		try
		{
			creator.copy(original, ctx);
			fail("extexted " + CannotCloneException.class.getName());
		}
		catch (final CannotCloneException e)
		{
			assertEquals(ctx, e.getCopyContext());
			assertEquals(new HashSet(Arrays.asList(original, partOf1, partOf2, partOf3)), new HashSet(e.getPendingItems()));
			assertNotNull(ctx.getCopy(original));
			assertNull(ctx.getCopy(partOf1));
			assertNull(ctx.getCopy(partOf2));
			assertNull(ctx.getCopy(partOf3));
		}
		catch (final JaloBusinessException e)
		{
			e.printStackTrace();
			fail("unexpected error " + e);
		}
	}

}
