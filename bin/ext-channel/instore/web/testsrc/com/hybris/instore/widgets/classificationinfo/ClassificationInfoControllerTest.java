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
package com.hybris.instore.widgets.classificationinfo;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;


@DeclaredInput(socketType = ProductData.class, value = ClassificationInfoController.SOCKET_IN_PRODUCT)
public class ClassificationInfoControllerTest extends AbstractWidgetUnitTest<ClassificationInfoController>
{
	@InjectMocks
	private final ClassificationInfoController controller = new ClassificationInfoController();

	@SuppressWarnings("unused")
	@Mock
	private ProductFacade productFacade;
	@SuppressWarnings("unused")
	@Mock
	private Grid productPropGrid;
	@SuppressWarnings("unused")
	@Mock
	private Div classificationContainer;

	@Override
	protected ClassificationInfoController getWidgetController()
	{
		return controller;
	}

	/**
	 * Test if setting a product with classificationdata results in properly setting the model of the view component.
	 */
	@Test
	public void testSetProduct()
	{
		// Prepare data
		final List<ListModel<Object>> listModels = new ArrayList<ListModel<Object>>();
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				listModels.clear();
				listModels.add(0, (ListModel<Object>) invocation.getArguments()[0]);
				return null;
			}
		}).when(productPropGrid).setModel(Mockito.any(ListModel.class));

		final ProductData product = new ProductData();
		final Collection<ClassificationData> classifications = new ArrayList<ClassificationData>();
		for (int i = 0; i < 3; i++)
		{
			final ClassificationData classData = new ClassificationData();
			final FeatureData featureData = new FeatureData();
			featureData.setCode("test" + i);
			classData.setFeatures(Collections.singletonList(featureData));
			classifications.add(classData);
		}
		product.setClassifications(classifications);


		// Execute method
		controller.setProduct(product);

		// Assert correct results
		Assert.assertEquals(1, listModels.size());
		Assert.assertEquals(3, listModels.get(0).getSize());
		for (int i = 0; i < 3; i++)
		{
			final Object element = listModels.get(0).getElementAt(i);
			Assert.assertTrue(element instanceof FeatureData);
			Assert.assertEquals("test" + i, ((FeatureData) element).getCode());
		}
	}
}
