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
package de.hybris.platform.store.daos.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultBaseStoreDaoTest extends ServicelayerTransactionalBaseTest // NOPMD Junt4 test uses annotations
{
	private static final String BASE_STORE_UID2 = "foo2";
	private static final String BASE_STORE_UID1 = "foo1";
	@Resource
	private DefaultBaseStoreDao baseStoreDao;
	@Resource
	private ModelService modelService;

	/**
	 * Test method for
	 * {@link de.hybris.platform.store.daos.impl.DefaultBaseStoreDao#findBaseStoresByUid(java.lang.String)}.
	 */
	@Test
	public void shouldFindBaseStoresByUid()
	{
		// given
		createBaseStores();

		// when
		final List<BaseStoreModel> baseStore = baseStoreDao.findBaseStoresByUid(BASE_STORE_UID1);

		// then
		assertThat(baseStore).isNotNull();
		assertThat(baseStore).hasSize(1);
		assertThat(baseStore.get(0)).isNotNull();
		assertThat(baseStore.get(0).getUid()).isEqualTo(BASE_STORE_UID1);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.store.daos.impl.DefaultBaseStoreDao#findBaseStoresByUid(java.lang.String)}.
	 */
	@Test
	public void shouldReturnEmptyListWhenBaseStoresHaveNotBeenFound()
	{
		// given (we're not creating base stores)

		// when
		final List<BaseStoreModel> baseStore = baseStoreDao.findBaseStoresByUid(BASE_STORE_UID1);

		// then
		assertThat(baseStore).isNotNull();
		assertThat(baseStore).isEmpty();
	}

	/**
	 * Test method for {@link de.hybris.platform.store.daos.impl.DefaultBaseStoreDao#findAllBaseStores()}.
	 */
	@Test
	public void shouldFindAllBaseStores()
	{
		// given
		createBaseStores();

		// when
		final List<BaseStoreModel> baseStore = baseStoreDao.findAllBaseStores();

		// then
		assertThat(baseStore).isNotNull();
		assertThat(baseStore).hasSize(2);
	}

	/**
	 * Test method for {@link de.hybris.platform.store.daos.impl.DefaultBaseStoreDao#findAllBaseStores()}.
	 */
	@Test
	public void shouldReturnEmptyListWhenThereIsNoBaseStoresAtAll()
	{
		// given (we're not creating base stores)

		// when
		final List<BaseStoreModel> baseStore = baseStoreDao.findAllBaseStores();

		// then
		assertThat(baseStore).isNotNull();
		assertThat(baseStore).isEmpty();
	}

	private void createBaseStores()
	{
		final BaseStoreModel baseStore1 = modelService.create(BaseStoreModel.class);
		baseStore1.setUid(BASE_STORE_UID1);
		final BaseStoreModel baseStore2 = modelService.create(BaseStoreModel.class);
		baseStore2.setUid(BASE_STORE_UID2);
		modelService.saveAll(baseStore1, baseStore2);
	}

}
