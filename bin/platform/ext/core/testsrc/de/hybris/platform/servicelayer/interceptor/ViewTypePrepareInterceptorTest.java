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
package de.hybris.platform.servicelayer.interceptor;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ViewTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.interceptor.impl.ViewTypePrepareInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class ViewTypePrepareInterceptorTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private TypeService typeService;

	@Resource
	private ModelService modelService;

	@Test
	public void testInterceptorInstalled()
	{
		final InterceptorRegistry reg = ((DefaultModelService) modelService).getInterceptorRegistry();
		final Collection<PrepareInterceptor> prepares = reg.getPrepareInterceptors("ViewType");
		assertFalse(prepares.isEmpty());

		boolean found = false;
		for (final PrepareInterceptor inter : prepares)
		{
			if (inter instanceof ViewTypePrepareInterceptor)
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testCreateViewTypeModelWithService()
	{
		final ViewTypeModel viewtype = modelService.create(ViewTypeModel.class);
		viewtype.setCode("xxx");
		viewtype.setQuery("select count({pk}) from {product}");
		viewtype.setGenerate(Boolean.TRUE);
		viewtype.setSingleton(Boolean.FALSE);
		viewtype.setCatalogItemType(Boolean.FALSE);
		modelService.save(viewtype);
		assertEquals(typeService.getComposedTypeForCode(ItemModel._TYPECODE), viewtype.getSuperType());
	}

	@Test
	public void testCreateViewTypeModelManually()
	{
		final ViewTypeModel viewtype = modelService.create(ViewTypeModel.class);
		viewtype.setCode("xxx");
		viewtype.setQuery("select count({pk}) from {product}");
		viewtype.setSuperType(typeService.getComposedTypeForCode(ProductModel._TYPECODE));
		viewtype.setGenerate(Boolean.TRUE);
		viewtype.setSingleton(Boolean.FALSE);
		viewtype.setCatalogItemType(Boolean.FALSE);
		modelService.save(viewtype);
		assertEquals(viewtype.getSuperType(), typeService.getComposedTypeForCode(ProductModel._TYPECODE));
	}
}
