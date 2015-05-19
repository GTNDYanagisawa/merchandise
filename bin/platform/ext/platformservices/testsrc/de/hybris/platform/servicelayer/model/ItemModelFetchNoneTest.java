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
package de.hybris.platform.servicelayer.model;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.constants.ServicelayerConstants;
import de.hybris.platform.servicelayer.internal.converter.util.ModelUtils;
import de.hybris.platform.servicelayer.internal.model.extractor.impl.DefaultPersistenceTypeService;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;


@IntegrationTest
public class ItemModelFetchNoneTest extends ItemModelTest
{

	@Override
	protected String getPrefetchMode()
	{
		return ServicelayerConstants.VALUE_PREFETCH_NONE;
	}

	@Override
	public void testLoadingNormalAttributeUsingDirectPersistence()
	{
		enableDirectMode();
		testLoadingNormalAttribute();
	}

	@Override
	public void testLoadingNormalAttributeUsingOldPersistence()
	{
		forceLegacyMode();
		testLoadingNormalAttribute();
	}

	private void testLoadingNormalAttribute()
	{
		final String expectedCode = defaultProduct.getCode();
		final ProductModel model = modelService.get(defaultProduct);

		assertTrue(ModelUtils.existsMethod(model.getClass(), "setCode"));
		assertTrue(ModelUtils.existsMethod(model.getClass(), "getCode"));
		assertTrue(ModelUtils.existsField(model.getClass(), "_code"));

		assertNotNull(getLoadedValue(model, "code"));
		assertNull(ModelUtils.getFieldValue(model, "_code"));

		assertEquals(expectedCode, model.getCode());

		modelService.save(model);

		assertNotNull(getLoadedValue(model, "code"));
		assertNull(ModelUtils.getFieldValue(model, "_code"));

		assertEquals(expectedCode, model.getCode());

		model.setCode("test");

		assertEquals(expectedCode, getLoadedValue(model, "code"));
		assertEquals("test", ModelUtils.getFieldValue(model, "_code"));
		assertEquals("test", model.getCode());

		assertEquals(expectedCode, defaultProduct.getCode());

		modelService.refresh(model);

		assertNotNull(getLoadedValue(model, "code"));
		assertNull(ModelUtils.getFieldValue(model, "_code"));

		assertEquals(expectedCode, model.getCode());
		assertEquals(expectedCode, defaultProduct.getCode());

		model.setCode("test");

		assertEquals(expectedCode, getLoadedValue(model, "code"));
		assertEquals("test", ModelUtils.getFieldValue(model, "_code"));
		assertEquals("test", model.getCode());

		assertEquals(expectedCode, defaultProduct.getCode());

		modelService.save(model);

		assertNotNull(getLoadedValue(model, "code"));
		assertNull(ModelUtils.getFieldValue(model, "_code"));

		assertEquals("test", model.getCode());
		assertEquals("test", defaultProduct.getCode());
	}

	@Override
	public void testLoadingPrimitiveAttributeUsingDirectPersistence()
	{
		enableDirectMode();
		testLoadingPrimitiveAttribute();
	}

	@Override
	public void testLoadingPrimitiveAttributeUsingOldPersistence()
	{
		forceLegacyMode();
		testLoadingPrimitiveAttribute();
	}

	private void testLoadingPrimitiveAttribute()
	{
		final String QUALIFIER = "loginDisabled";
		final Boolean loginDisabled = Boolean.TRUE;

		UserModel model = modelService.create(CustomerModel.class);
		model.setUid("C" + System.nanoTime());
		model.setLoginDisabled(loginDisabled.booleanValue());
		modelService.save(model);
		final PK pk = model.getPk();
		modelService.detach(model);

		// get fresh copy
		model = modelService.get(pk);

		assertTrue(ModelUtils.existsMethod(model.getClass(), "setLoginDisabled"));
		assertTrue(ModelUtils.existsMethod(model.getClass(), "isLoginDisabled"));
		assertTrue(ModelUtils.existsField(model.getClass(), "_" + QUALIFIER));

		assertNotNull(getLoadedValue(model, QUALIFIER));
		assertNull(ModelUtils.getFieldValue(model, "_" + QUALIFIER));

		assertEquals(loginDisabled.booleanValue(), model.isLoginDisabled());

		modelService.save(model);

		assertNotNull(getLoadedValue(model, QUALIFIER));
		assertNull(ModelUtils.getFieldValue(model, "_" + QUALIFIER));

		assertEquals(loginDisabled.booleanValue(), model.isLoginDisabled());

		final Boolean loginEnabled = Boolean.FALSE;
		model.setLoginDisabled(loginEnabled.booleanValue());

		assertEquals(loginDisabled, getLoadedValue(model, QUALIFIER));
		assertEquals(loginEnabled, ModelUtils.getFieldValue(model, "_" + QUALIFIER));
		assertEquals(loginEnabled.booleanValue(), model.isLoginDisabled());

		modelService.refresh(model);

		assertNotNull(getLoadedValue(model, QUALIFIER));
		assertNull(ModelUtils.getFieldValue(model, "_" + QUALIFIER));

		assertEquals(loginDisabled.booleanValue(), model.isLoginDisabled());

		model.setLoginDisabled(loginEnabled.booleanValue());

		assertEquals(loginDisabled, getLoadedValue(model, QUALIFIER));
		assertEquals(loginEnabled, ModelUtils.getFieldValue(model, "_" + QUALIFIER));
		assertEquals(loginEnabled.booleanValue(), model.isLoginDisabled());

		modelService.save(model);

		assertNotNull(getLoadedValue(model, QUALIFIER));
		assertNull(ModelUtils.getFieldValue(model, "_" + QUALIFIER));

		assertEquals(loginEnabled.booleanValue(), model.isLoginDisabled());
	}

	@Override
	protected Map<String, Class<? extends ItemModel>> getModelConvertersToReload()
	{
		final Map<String, Class<? extends ItemModel>> map = new HashMap<String, Class<? extends ItemModel>>();
		map.put("Product", ProductModel.class);
		return map;
	}

}
