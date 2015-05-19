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
 package de.hybris.platform.lucenesearch.jalo;

import static de.hybris.platform.testframework.Assert.assertCollectionElements;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.lucenesearch.constants.LucenesearchConstants;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class AttributeConfigurationTest extends HybrisJUnit4TransactionalTest
{
	LuceneIndex index;
	IndexConfiguration indexConfig;
	AttributeConfiguration attrConfig;
	ComposedType productType;

	@Before
	public void setUp() throws Exception
	{
		index = LucenesearchManager.getInstance().createLuceneIndex("index");
		assertNotNull(index);
		productType = TypeManager.getInstance().getComposedType(Product.class);
		indexConfig = index.createIndexConfiguration(productType);
		attrConfig = indexConfig.createAttributeConfiguration(productType.getAttributeDescriptor(Product.CODE));
	}

	@Test
	public void testSetup()
	{
		assertEquals(TypeManager.getInstance().getComposedType(LucenesearchConstants.TC.INDEXCONFIGURATION)
				.getAttributeDescriptorIncludingPrivate(IndexConfiguration.INDEXEDDATAFACTORYCLASSNAME).getDefaultValue(),
				indexConfig.getIndexedDataFactoryClassName());
		assertEquals(1.0, attrConfig.getWeightAsPrimitive(), 0.0);
		assertCollectionElements(indexConfig.getAttributeConfigurations(), attrConfig);
	}

	@Test
	public void testForcedAttributes()
	{
		final AttributeDescriptor unitAD = productType.getAttributeDescriptorIncludingPrivate(Product.UNIT);
		final boolean forced = LucenesearchManager.getInstance().isForceLuceneIndexableAsPrimitive(unitAD);
		assertTrue(forced == indexConfig.getAvailableIndexableAttributes().contains(unitAD));
		try
		{
			LucenesearchManager.getInstance().setForceLuceneIndexable(unitAD, Boolean.TRUE);
			assertTrue(indexConfig.getAvailableIndexableAttributes().contains(unitAD));
			final AttributeConfiguration acUnit = indexConfig.createAttributeConfiguration(unitAD);
			assertTrue(indexConfig.getAttributeConfigurations().contains(acUnit));
		}
		catch (final JaloSystemException e)
		{
			fail("unexpected exception " + e);
		}
		finally
		{
			LucenesearchManager.getInstance().setForceLuceneIndexable(unitAD, forced ? Boolean.TRUE : Boolean.FALSE);
		}
	}

	@Test
	public void testInvalidAttribute()
	{
		final AttributeDescriptor descrPicture = productType.getAttributeDescriptor(Product.PICTURE);
		assertFalse(LucenesearchManager.getInstance().isForceLuceneIndexableAsPrimitive(descrPicture));
		try
		{
			indexConfig.createAttributeConfiguration(descrPicture);
			fail("created AttributeConfiguration for media");
		}
		catch (final JaloSystemException e)
		{
			assertTrue(e instanceof JaloInvalidParameterException);
			assertTrue(e.getMessage(), e.getMessage().indexOf("not in indexable attributes") != -1);

			/*
			 * assertNotNull(e.getThrowable()); assertTrue(e.getThrowable() instanceof JaloGenericCreationException);
			 * JaloGenericCreationException genEx = (JaloGenericCreationException) e.getThrowable();
			 * JaloInvalidParameterException paramEx = (JaloInvalidParameterException) genEx.getThrowable();
			 * assertTrue(e.getMessage() + " / " + paramEx.getMessage(), paramEx.getMessage().indexOf(
			 * "not in indexable attributes") != -1);
			 */
		}
		assertCollectionElements(indexConfig.getAttributeConfigurations(), attrConfig);
	}
}
