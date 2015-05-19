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

package com.hybris.backoffice.jalo;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.model.impl.DefaultModelValueHandler;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;


public class JaloPropertyNotVisibleInModelAccessorTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private PermissionFacade permissionFacade;
	@InjectMocks
	final private JaloPropertyNotVisibleInModelAccessor jaloPropertyNotVisibleInModelAccessor = new JaloPropertyNotVisibleInModelAccessor();

	@Before
	public void before() throws JaloSecurityException
	{
		MockitoAnnotations.initMocks(this);
        productModel = Mockito.mock(ProductModel.class);
    }

	private static final String ITEM_ATTRIBUTE = "abc";
	private static final String PRODUCT_ATTRIBUTE = "def";
	private static final ItemModel itemModel = new ItemModel();
	private ProductModel productModel = new ProductModel();

	/**
	 * "abc" -> {EmployeeModel, ItemModel, UserModel} "def" -> {ProductModel}
	 * 
	 * @return
	 */
	protected  Map<String, Set<Class>> getDefaultSupportedJaloAttributes()
	{
		final Map<String, Set<Class>> map = new HashMap<>();
		final Set<Class> itemSet = new HashSet<>();
		final Set<Class> productSet = new HashSet<>();
		itemSet.add(EmployeeModel.class);
		itemSet.add(ItemModel.class);
		itemSet.add(UserModel.class);

		productSet.add(ProductModel.class);
		map.put(ITEM_ATTRIBUTE, itemSet);
		map.put(PRODUCT_ATTRIBUTE, productSet);
		return map;
	}

	protected Map<String, Set<Class>> getNullJaloAttributes()
	{
		return null;
	}

	protected EvaluationContext getDefaultEvaluationContext()
	{
        final WidgetModel widgetModel = new DefaultWidgetModel(new HashMap<String,Object>(),new DefaultModelValueHandler());
		return new StandardEvaluationContext(widgetModel);
	}


	protected Object getCurrentObject()
	{
		return new Object();
	}

	@Test
	public void testCanReadWhenNoSupportedJaloAtrributes()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		final Object currentObject = getCurrentObject();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getNullJaloAttributes());
		try
		{
			//when
			final boolean canRead = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, currentObject, "aa");
			//then
			Assertions.assertThat(canRead).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanWriteWhenNoSupportedJaloAtrributes()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		final Object currentObject = getCurrentObject();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getNullJaloAttributes());
		try
		{
			//when
			final boolean canWrite = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, currentObject, "aa");
			//then
			Assertions.assertThat(canWrite).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanReadWhenDefaultJaloAttributesAndGrantedPermissions()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		Mockito.when(permissionFacade.canReadInstanceProperty(Mockito.anyObject(), Mockito.anyString())).thenReturn(true);
		try
		{
			final boolean canReadItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelItemAttr).isTrue();
			final boolean canReadProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelItemAttr).isTrue();
			final boolean canReadProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelProductAttr).isTrue();
			final boolean canReadItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelProductAttr).isFalse();
			//
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanWriteWhenDefaultJaloAttributesAndGrantedPermissions() throws TypeNotFoundException
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		Mockito.when(permissionFacade.canReadInstanceProperty(Mockito.anyObject(), Mockito.anyString())).thenReturn(false);
		Mockito.when(permissionFacade.canChangeInstanceProperty(Mockito.anyObject(), Mockito.anyString())).thenReturn(true);

		final DataType dataType = Mockito.mock(DataType.class);
		final DataAttribute dataAttributeNotWritable = Mockito.mock(DataAttribute.class);
		Mockito.when(dataAttributeNotWritable.isWritable()).thenReturn(false);

		final DataAttribute dataAttributeWritable = Mockito.mock(DataAttribute.class);
		Mockito.when(dataAttributeWritable.isWritable()).thenReturn(true);

		Mockito.when(dataType.getAttribute(Mockito.anyString())).thenReturn(dataAttributeNotWritable);
		Mockito.when(typeFacade.load(Mockito.anyString())).thenReturn(dataType);

		try
		{
			boolean canWriteItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelItemAttr).isFalse();
			boolean canWriteProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelItemAttr).isFalse();
			boolean canWriteProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelProductAttr).isFalse();
			boolean canWriteItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelProductAttr).isFalse();

			//
			Mockito.when(dataType.getAttribute(Mockito.anyString())).thenReturn(dataAttributeWritable);
			//
			canWriteItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelItemAttr).isTrue();
			canWriteProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelItemAttr).isTrue();
			canWriteProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelProductAttr).isTrue();
			canWriteItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelProductAttr).isFalse();

		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanReadWhenDefaultJaloAttributesAndRevokedPerimissions()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		Mockito.when(permissionFacade.canReadInstanceProperty(Mockito.anyObject(), Mockito.anyString())).thenReturn(false);
		try
		{
			final boolean canReadItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelItemAttr).isFalse();
			final boolean canReadProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelItemAttr).isFalse();
			final boolean canReadProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelProductAttr).isFalse();
			final boolean canReadItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelProductAttr).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanWriteWhenDefaultJaloAttributesAndRevokedPerimissions() throws TypeNotFoundException
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		Mockito.when(permissionFacade.canReadInstanceProperty(Mockito.anyObject(), Mockito.anyString())).thenReturn(true);
		Mockito.when(permissionFacade.canChangeInstanceProperty(Mockito.anyObject(), Mockito.anyString())).thenReturn(false);
		final DataType dataType = Mockito.mock(DataType.class);
		final DataAttribute dataAttributeWritable = Mockito.mock(DataAttribute.class);
		Mockito.when(dataAttributeWritable.isWritable()).thenReturn(true);


		Mockito.when(dataType.getAttribute(Mockito.anyString())).thenReturn(dataAttributeWritable);
		Mockito.when(typeFacade.load(Mockito.anyString())).thenReturn(dataType);
		try
		{
			final boolean canWriteItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelItemAttr).isFalse();
			final boolean canWriteProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelItemAttr).isFalse();
			final boolean canWriteProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelProductAttr).isFalse();
			final boolean canWriteItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelProductAttr).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}
}