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
package com.hybris.backoffice.cockpitng.dataaccess.facades;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.TestUtils;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.hybris.backoffice.cockpitng.dataaccess.facades.common.PlatformFacadeStrategyHandleCache;
import com.hybris.backoffice.cockpitng.dataaccess.facades.object.DefaultPlatformObjectFacadeStrategy;
import com.hybris.backoffice.cockpitng.dataaccess.facades.object.savedvalues.DefaultItemModificationHistoryService;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectCreationException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.dataaccess.facades.object.impl.DefaultObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.impl.ObjectFacadeStrategyRegistry;
import com.hybris.cockpitng.labels.LabelService;


/**
 * Tests for DefaultObjectFacade
 */
@IntegrationTest
public class DefaultObjectFacadeTest extends ServicelayerTransactionalTest
{

	private DefaultObjectFacade objectFacade;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
    @Resource
    private FlexibleSearchService flexibleSearchService;
    @Resource
    private I18NService i18NService;
    @Resource
    private CommonI18NService commonI18NService;
	private String existingUserPk;


	private static final String FILE_ANALYZER_REASON = "ObjectFacade should thrown exception";
	private static final String TEST_OBJECT_FACADE_STRATEGY = "testObjectFacadeStrategy";
	private static final String EXISTING_USER_UID = "oldUser";



	@Before
	public void before()
	{
		objectFacade = new DefaultObjectFacade();
		final DefaultPlatformObjectFacadeStrategy defaultObjectFacadeStrategy = new DefaultPlatformObjectFacadeStrategy();
		defaultObjectFacadeStrategy.setModelService(modelService);
        DefaultItemModificationHistoryService itemModificationHistoryService = new DefaultItemModificationHistoryService();
        itemModificationHistoryService.setModelService(modelService);
        itemModificationHistoryService.setFlexibleSearchService(flexibleSearchService);
        itemModificationHistoryService.setI18NService(i18NService);
        itemModificationHistoryService.setCommonI18NService(commonI18NService);
        defaultObjectFacadeStrategy.setItemModificationHistoryService(itemModificationHistoryService);

		final LabelService labelService = Mockito.mock(LabelService.class);
		Mockito.when(labelService.getObjectLabel(Mockito.any())).thenReturn(StringUtils.EMPTY);
		defaultObjectFacadeStrategy.setLabelService(labelService);
		final PlatformFacadeStrategyHandleCache platformFacadeStrategyHandleCache = new PlatformFacadeStrategyHandleCache();
		platformFacadeStrategyHandleCache.setTypeService(typeService);
		defaultObjectFacadeStrategy.setPlatformFacadeStrategyHandleCache(platformFacadeStrategyHandleCache);


		final ObjectFacadeStrategyRegistry objectFacadeStrategyRegistry = new ObjectFacadeStrategyRegistry();
		final TestObjectFacadeStrategy customStrategy = new TestObjectFacadeStrategy();
		customStrategy.setModelService(modelService);
		objectFacadeStrategyRegistry.setStrategies(Lists.<ObjectFacadeStrategy> newArrayList(customStrategy));
		objectFacadeStrategyRegistry.setDefaultStrategy(defaultObjectFacadeStrategy);

		objectFacade.setStrategyRegistry(objectFacadeStrategyRegistry);

		existingUserPk = prepareUser();

	}

	private String prepareUser()
	{
		final UserModel preparedUser = modelService.create(UserModel.class);
		preparedUser.setUid(EXISTING_USER_UID);
		modelService.save(preparedUser);
		return preparedUser.getPk().toString();
	}


	@Test
	public void testLoadExistingUser()
	{

		Object loadedUser = null;
		try
		{
			loadedUser = objectFacade.load(existingUserPk);
			Assert.assertTrue(loadedUser instanceof UserModel);
			Assert.assertEquals(EXISTING_USER_UID, ((UserModel) loadedUser).getUid());

		}
		catch (final ObjectNotFoundException e)
		{
			Assert.fail("Existing user should be loaded without any exceptions, but was thrown: " + e);
		}

	}

	@Test
	public void createUser()
	{
		final String className = "User";
		try
		{
			final UserModel user = objectFacade.create(className);
			Assert.assertNotNull(user);
			Assert.assertTrue(modelService.isNew(user));

			final UserModel userWithContext = objectFacade.create(className, null);
			Assert.assertNotNull(userWithContext);
			Assert.assertTrue(modelService.isNew(userWithContext));
		}
		catch (final ObjectCreationException e)
		{
			Assert.fail("Object of type " + className + " should be created without any exceptions, but was thrown: " + e);
		}

	}

	@Test
	public void createJavaObjectShouldThrowException()
	{
		final String className = "Object";

		TestUtils.disableFileAnalyzer(FILE_ANALYZER_REASON);
		try
		{
			objectFacade.create(className);
			Assert.fail("ObjectFacade should not allow to create Object which is not subclass of ItemModel");
		}
		catch (final ObjectCreationException ex) // NOPMD
		{
			// expected behavior
		}

		try
		{
			objectFacade.create(className, null);
			Assert.fail("ObjectFacade should not allow to create Object which is not subclass of ItemModel");
		}
		catch (final ObjectCreationException ex) // NOPMD
		{
			// expected behavior
		}
		TestUtils.enableFileAnalyzer();
	}

	@Test
	public void loadUserShouldThrowException()
	{

		TestUtils.disableFileAnalyzer(FILE_ANALYZER_REASON);
		try
		{
			objectFacade.load("null");
			Assert.fail("ObjectFacade should not load not existing object");
		}
		catch (final ObjectNotFoundException ex) // NOPMD
		{
			// expected behavior
		}

		try
		{
			objectFacade.load("null", null);
			Assert.fail("ObjectFacade should not load not existing object");
		}
		catch (final ObjectNotFoundException ex) // NOPMD
		{
			// expected behavior
		}
		TestUtils.enableFileAnalyzer();
	}

	@Test
	public void testPersistWithoutContextAndLoad()
	{
		try
		{
			UserModel user = objectFacade.create("User");
			Assert.assertTrue(modelService.isNew(user));
			user.setUid("temp1");
			user = objectFacade.save(user);
			Assert.assertFalse(modelService.isNew(user));


			final Object loadedUser = objectFacade.load(user.getPk().toString());
			Assert.assertTrue(loadedUser instanceof UserModel);
			Assert.assertEquals("temp1", ((UserModel) loadedUser).getUid());

		}
		catch (final Exception pex)
		{
			Assert.fail("Exception should not be thrown");
		}
	}

	@Test
	public void testPersistWithContextAndLoad()
	{
		try
		{
			UserModel user = objectFacade.create("User");
			Assert.assertTrue(modelService.isNew(user));
			user.setUid("temp1");
			user = objectFacade.save(user, null);
			Assert.assertFalse(modelService.isNew(user));

			final Object loadedUser = objectFacade.load(user.getPk().toString());
			Assert.assertTrue(loadedUser instanceof UserModel);
			Assert.assertEquals("temp1", ((UserModel) loadedUser).getUid());
		}
		catch (final Exception ex)
		{
			Assert.fail("Exception should not be thrown");
		}
	}


	@Test
	public void testPersistWithoutContextShouldThrowException()
	{
		TestUtils.disableFileAnalyzer(FILE_ANALYZER_REASON);
		try
		{
			final Object obj = new Object();
			objectFacade.save(obj);
			Assert.fail("ObjectFacade.persist should throw exception");
		}
		catch (final ObjectSavingException ose) // NOPMD
		{
			// expected behavior
		}
		TestUtils.enableFileAnalyzer();
	}

	@Test
	public void testPersistWithContextShoudlThrowException()
	{
		TestUtils.disableFileAnalyzer(FILE_ANALYZER_REASON);
		try
		{
			final Object obj = new Object();
			objectFacade.save(obj, null);
			Assert.fail("ObjectFacade.persist should throw exception");
		}
		catch (final ObjectSavingException ose) // NOPMD
		{
			// expected behavior
		}
		TestUtils.enableFileAnalyzer();
	}


	@Test
	public void testDifferentObjectFacadeStrategyForProduct()
	{
		TestUtils.disableFileAnalyzer(FILE_ANALYZER_REASON);

		final String className = "Product";
		try
		{
			final ProductModel productModel = objectFacade.create(className);
			Assert.assertNotNull(productModel);
			Assert.assertTrue(modelService.isNew(productModel));
			Assert.assertTrue(StringUtils.equals(productModel.getDescription(), TEST_OBJECT_FACADE_STRATEGY));


			final ProductModel productWithContext = objectFacade.create(className, null);
			Assert.assertNotNull(productWithContext);
			Assert.assertTrue(modelService.isNew(productWithContext));
			Assert.assertTrue(StringUtils.equals(productModel.getDescription(), TEST_OBJECT_FACADE_STRATEGY));
		}
		catch (final ObjectCreationException e)
		{
			Assert.fail("Object of type " + className + " should be created without any exceptions, but was thrown: " + e);
		}

		TestUtils.enableFileAnalyzer();
	}

	private static class TestObjectFacadeStrategy extends DefaultPlatformObjectFacadeStrategy
	{

		@Override
		public boolean canHandle(final Object object)
		{
			return (object instanceof ProductModel) || ObjectUtils.equals(object, "Product");
		}

		@Override
		public ProductModel create(final String typeId, final Context ctx) throws ObjectCreationException
		{
			final ProductModel productModel = super.create(typeId, ctx);
			productModel.setDescription(TEST_OBJECT_FACADE_STRATEGY);
			return productModel;
		}

	}

}
