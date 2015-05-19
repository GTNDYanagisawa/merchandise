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

import static de.hybris.platform.testframework.assertions.ModelStateAssert.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.constants.ServicelayerConstants;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


@IntegrationTest
public class ItemModelFetchLiteralTest extends ItemModelTest
{
	private static final String CODE = "code";
	private static final String LOGIN_DISABLED = "loginDisabled";

	@Override
	@Test
	public void testLoadingNormalAttributeUsingDirectPersistence()
	{
		enableDirectMode();

		final String expectedCode = defaultProduct.getCode();
		final ProductModel model = modelService.get(defaultProduct);

		assertThat(model).hasGetter(CODE).hasSetter(CODE).hasField(CODE);
		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo(expectedCode);
		assertThat(model.getCode()).isEqualTo(expectedCode);
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		modelService.save(model);

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldWithNullValue();
		assertThat(model.getCode()).isEqualTo(expectedCode);

		model.setCode("test");

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo("test");
		assertThat(model.getCode()).isEqualTo("test");
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		modelService.refresh(model);

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo(expectedCode);
		assertThat(model.getCode()).isEqualTo(expectedCode);
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		model.setCode("test");

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo("test");
		assertThat(model.getCode()).isEqualTo("test");
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		modelService.save(model);

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo("test").hasFieldWithNullValue();
		assertThat(model.getCode()).isEqualTo("test");
		assertThat(defaultProduct.getCode()).isEqualTo("test");
	}

	@Override
	public void testLoadingNormalAttributeUsingOldPersistence()
	{
		forceLegacyMode();

		final String expectedCode = defaultProduct.getCode();
		final ProductModel model = modelService.get(defaultProduct);

		assertThat(model).hasGetter(CODE).hasSetter(CODE).hasField(CODE);
		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo(expectedCode);
		assertThat(model.getCode()).isEqualTo(expectedCode);
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		modelService.save(model);

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo(expectedCode);
		assertThat(model.getCode()).isEqualTo(expectedCode);

		model.setCode("test");

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo("test");
		assertThat(model.getCode()).isEqualTo("test");
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		modelService.refresh(model);

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo(expectedCode);
		assertThat(model.getCode()).isEqualTo(expectedCode);
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		model.setCode("test");

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo(expectedCode).hasFieldValueEqualTo("test");
		assertThat(model.getCode()).isEqualTo("test");
		assertThat(defaultProduct.getCode()).isEqualTo(expectedCode);

		modelService.save(model);

		assertThat(model).forProperty(CODE).hasLoadedValueEqualTo("test").hasFieldValueEqualTo("test");
		assertThat(model.getCode()).isEqualTo("test");
		assertThat(defaultProduct.getCode()).isEqualTo("test");
	}

	@Override
	public void testLoadingPrimitiveAttributeUsingDirectPersistence()
	{
		enableDirectMode();
		final Boolean value = Boolean.TRUE;

		UserModel model = createAndSaveCustomerModel(value);
		final PK pk = model.getPk();
		modelService.detach(model);

		// get fresh copy
		model = modelService.get(pk);

		assertThat(model).hasGetter(LOGIN_DISABLED).hasSetter(LOGIN_DISABLED).hasField(LOGIN_DISABLED);
		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.TRUE);
		assertThat(model.isLoginDisabled()).isTrue();

		modelService.save(model);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldWithNullValue();
		assertThat(model.isLoginDisabled()).isTrue();

		model.setLoginDisabled(false);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.FALSE);
		assertThat(model.isLoginDisabled()).isFalse();

		modelService.refresh(model);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.TRUE);
		assertThat(model.isLoginDisabled()).isTrue();

		model.setLoginDisabled(false);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.FALSE);
		assertThat(model.isLoginDisabled()).isFalse();

		modelService.save(model);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.FALSE).hasFieldWithNullValue();
		assertThat(model.isLoginDisabled()).isFalse();
	}

	@Override
	@Test
	public void testLoadingPrimitiveAttributeUsingOldPersistence()
	{
		forceLegacyMode();
		final Boolean value = Boolean.TRUE;

		UserModel model = createAndSaveCustomerModel(value);
		final PK pk = model.getPk();
		modelService.detach(model);

		// get fresh copy
		model = modelService.get(pk);

		assertThat(model).hasGetter(LOGIN_DISABLED).hasSetter(LOGIN_DISABLED).hasField(LOGIN_DISABLED);
		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.TRUE);
		assertThat(model.isLoginDisabled()).isTrue();

		modelService.save(model);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.TRUE);
		assertThat(model.isLoginDisabled()).isTrue();

		model.setLoginDisabled(false);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.FALSE);
		assertThat(model.isLoginDisabled()).isFalse();

		modelService.refresh(model);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.TRUE);
		assertThat(model.isLoginDisabled()).isTrue();

		model.setLoginDisabled(false);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.TRUE).hasFieldValueEqualTo(Boolean.FALSE);
		assertThat(model.isLoginDisabled()).isFalse();

		modelService.save(model);

		assertThat(model).forProperty(LOGIN_DISABLED).hasLoadedValueEqualTo(Boolean.FALSE).hasFieldValueEqualTo(Boolean.FALSE);
		assertThat(model.isLoginDisabled()).isFalse();
	}

	private UserModel createAndSaveCustomerModel(final Boolean value)
	{
		final UserModel model = modelService.create(CustomerModel.class);
		model.setUid("C" + System.nanoTime());
		model.setLoginDisabled(value.booleanValue());
		modelService.save(model);
		return model;
	}

	@Override
	protected String getPrefetchMode()
	{
		return ServicelayerConstants.VALUE_PREFETCH_LITERAL;
	}

	@Override
	protected Map<String, Class<? extends ItemModel>> getModelConvertersToReload()
	{
		final Map<String, Class<? extends ItemModel>> map = new HashMap<>();
		map.put("Product", ProductModel.class);
		map.put("Customer", CustomerModel.class);
		return map;
	}

}
