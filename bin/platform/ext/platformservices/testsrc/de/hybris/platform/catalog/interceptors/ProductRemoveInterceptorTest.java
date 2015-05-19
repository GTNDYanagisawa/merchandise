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
package de.hybris.platform.catalog.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.daos.ProductReferencesDao;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductRemoveInterceptorTest
{
	private ProductRemoveInterceptor productRemoveInterceptor;

	@Mock
	private ModelService modelService;
	@Mock
	private ProductReferencesDao productReferencesDao;
	@Mock
	private InterceptorContext ctx;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		productRemoveInterceptor = new ProductRemoveInterceptor();
		productRemoveInterceptor.setProductReferencesDao(productReferencesDao);

		Mockito.when(ctx.getModelService()).thenReturn(modelService);
	}

	@Test
	public void Remove() throws InterceptorException
	{
		final ProductModel product = new ProductModel();
		final ProductFeatureModel feature1 = new ProductFeatureModel();
		final ProductFeatureModel feature2 = new ProductFeatureModel();
		final ProductFeatureModel feature3 = new ProductFeatureModel();
		product.setFeatures(Arrays.asList(feature1, feature2, feature3));

		final ProductReferenceModel productReference1 = new ProductReferenceModel();
		final ProductReferenceModel productReference2 = new ProductReferenceModel();
		final ProductReferenceModel productReference3 = new ProductReferenceModel();

		Mockito.when(productReferencesDao.findAllReferences(product)).thenReturn(
				Arrays.asList(productReference1, productReference2, productReference3));

		productRemoveInterceptor.onRemove(product, ctx);

		Mockito.verify(modelService).remove(feature1);
		Mockito.verify(modelService).remove(feature2);
		Mockito.verify(modelService).remove(feature3);
		Mockito.verify(modelService).remove(productReference1);
		Mockito.verify(modelService).remove(productReference2);
		Mockito.verify(modelService).remove(productReference3);


	}

}
