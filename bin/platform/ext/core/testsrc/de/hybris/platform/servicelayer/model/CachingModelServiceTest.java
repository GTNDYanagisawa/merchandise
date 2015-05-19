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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.internal.model.impl.CachingModelService;
import de.hybris.platform.spring.TenantScope;
import de.hybris.platform.test.TestThreadsHolder;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.util.WebSessionFunctions;

import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockHttpServletRequest;


@IntegrationTest
public class CachingModelServiceTest extends ServicelayerBaseTest
{
	private Product product;
	@Resource
	private ModelService defaultModelService;
	@Resource
	private ModelService cachingModelService;

	private static final Logger LOG = Logger.getLogger(CachingModelServiceTest.class);

	private static final String SERVICE_BEAN_DEF = ""// NOPMD
			+ "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"//
			+ "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"//
			+ "       xsi:schemaLocation=\"http://www.springframework.org/schema/beans\n"//
			+ "                            http://www.springframework.org/schema/beans/spring-beans-3.1.xsd\">\n" //

			+ "       <bean\n"//
			+ "             id=\"cachingModelService\"\n"//
			+ "             class=\"de.hybris.platform.servicelayer.internal.model.impl.CachingModelService\"\n"//
			+ "             parent=\"defaultModelService\">\n"//
			+ "       </bean>\n"//
			+ "</beans>";

	@Override
	public void prepareApplicationContextAndSession() throws Exception
	{
		final ApplicationContext parentApplicationContext = Registry.getApplicationContext();
		final GenericApplicationContext applicationContext = new GenericApplicationContext();
		applicationContext.setParent(parentApplicationContext);
		//applicationContext.getBeanFactory().registerScope("tenant", new TenantScope());

		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(applicationContext);
		xmlReader.loadBeanDefinitions(new ByteArrayResource(SERVICE_BEAN_DEF.getBytes()));

		applicationContext.refresh();

		autowireProperties(applicationContext);
	}

	private HttpServletRequest requestBefore = null;
	private String httpSessionIdBefore = null;

	private HttpSession mockHttpSession;

	@Before
	public void setUp()
	{
		product = ProductManager.getInstance().createProduct("test");

		requestBefore = WebSessionFunctions.getCurrentHttpServletRequest();
		httpSessionIdBefore = jaloSession.getHttpSessionId();

		WebSessionFunctions.setCurrentHttpServletRequest(new MockHttpServletRequest());
		mockHttpSession = WebSessionFunctions.getCurrentHttpSession();
		jaloSession.setHttpSessionId(mockHttpSession.getId());
	}

	@After
	public void tearDown() throws ConsistencyCheckException
	{
		try
		{
			product.remove();
		}
		finally
		{
			jaloSession.setHttpSessionId(httpSessionIdBefore);
			WebSessionFunctions.setCurrentHttpServletRequest(requestBefore);
			mockHttpSession = null;
			requestBefore = null;
			httpSessionIdBefore = null;
		}
	}

	@Test
	public void testEnableCachingForTypesProperty()
	{

		// test if no caching is activated by default
		ProductModel model = cachingModelService.get(product);
		cachingModelService.detach(model);
		ProductModel model2 = cachingModelService.get(product);
		assertNotSame(model, model2);

		//test if no caching is activated for different type
		ComposedType type = TypeManager.getInstance().getComposedType("language");
		jaloSession.getSessionContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));
		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertNotSame(model, model2);

		//test if caching is activated for correct type
		type = TypeManager.getInstance().getComposedType("product");
		jaloSession.getSessionContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));

		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertSame(model, model2);

	}


	@Test
	public void testEnableCachingForTypesPropertyInServletContextMode()
	{

		// test if no caching is activated by default
		ProductModel model = cachingModelService.get(product);
		cachingModelService.detach(model);
		ProductModel model2 = cachingModelService.get(product);
		assertNotSame(model, model2);

		//test if no caching is activated for different type
		ComposedType type = TypeManager.getInstance().getComposedType("language");
		WebSessionFunctions.getCurrentHttpSession().getServletContext()
				.setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES, Collections.singleton(type.getPK()));
		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertNotSame(model, model2);

		//test if caching is activated for correct type
		type = TypeManager.getInstance().getComposedType("product");
		WebSessionFunctions.getCurrentHttpSession().getServletContext()
				.setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES, Collections.singleton(type.getPK()));

		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertSame(model, model2);
	}

	@Test
	public void testDisableCachingForTypesProperty()
	{
		jaloSession.getSessionContext().setAttribute(CachingModelService.USE_BLACK_LIST, Boolean.TRUE);
		// test if caching is activated by default
		ProductModel model = cachingModelService.get(product);
		cachingModelService.detach(model);
		ProductModel model2 = cachingModelService.get(product);
		assertSame(model, model2);

		//test if caching is activated for different type
		ComposedType type = TypeManager.getInstance().getComposedType("language");
		jaloSession.getSessionContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));
		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertSame(model, model2);

		//test if no caching is activated for correct type
		type = TypeManager.getInstance().getComposedType("product");
		jaloSession.getSessionContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));

		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertNotSame(model, model2);

	}


	@Test
	public void testDisableCachingForTypesPropertyInServletContextMode()
	{
		mockHttpSession.getServletContext().setAttribute(CachingModelService.USE_BLACK_LIST, Boolean.TRUE);
		// test if caching is activated by default
		ProductModel model = cachingModelService.get(product);
		cachingModelService.detach(model);
		ProductModel model2 = cachingModelService.get(product);
		assertSame(model, model2);

		//test if caching is activated for different type
		ComposedType type = TypeManager.getInstance().getComposedType("language");
		mockHttpSession.getServletContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));
		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertSame(model, model2);

		//test if no caching is activated for correct type
		type = TypeManager.getInstance().getComposedType("product");
		mockHttpSession.getServletContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));

		model = cachingModelService.get(product);
		cachingModelService.detach(model);
		model2 = cachingModelService.get(product);
		assertNotSame(model, model2);
	}

	@Test
	public void testCompareToDefaultModelServiceWithNewSession()
	{
		final ComposedType type = TypeManager.getInstance().getComposedType("Product");
		jaloSession.getSessionContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));
		final long defaultTime = compareToDefaultModelServiceWithNewSession(defaultModelService, 20000);
		final long cachingTime = compareToDefaultModelServiceWithNewSession(cachingModelService, 20000);

		LOG.info("DefaultTime: " + Utilities.formatTime(defaultTime) + ", CachingTime: " + Utilities.formatTime(cachingTime));

		assertTrue("CachingModelService is slower than DefaultModelService", defaultTime > cachingTime);
	}

	private long compareToDefaultModelServiceWithNewSession(final ModelService serviceToUse, final long iterations)
	{
		final long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++)
		{
			JaloSession.deactivate();
			jaloSession.activate();
			assertNotNull(serviceToUse.get(product));
			if (i % 10000 == 0)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Method: " + serviceToUse.getClass().getSimpleName() + ", Count: " + i + ", Time: "
							+ (Utilities.formatTime(System.currentTimeMillis() - start)));
				}
			}
		}
		return System.currentTimeMillis() - start;
	}

	@Test
	@Ignore("HOR-263 PLA-8112 fails sometimes")
	public void testCompareToDefaultModelServiceWithoutNewSession()
	{
		final ComposedType type = TypeManager.getInstance().getComposedType("Product");
		jaloSession.getSessionContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
				Collections.singleton(type.getPK()));
		final long defaultTime = compareToDefaultModelServiceWithoutNewSession(defaultModelService, 1000000);
		final long cachingTime = compareToDefaultModelServiceWithoutNewSession(cachingModelService, 1000000);

		LOG.info("DefaultTime: " + Utilities.formatTime(defaultTime) + ", CachingTime: " + Utilities.formatTime(cachingTime));

		assertTrue("CachingModelService is slower than DefaultModelService", defaultTime > cachingTime);
	}

	private long compareToDefaultModelServiceWithoutNewSession(final ModelService serviceToUse, final long iterations)
	{
		final long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++)
		{
			assertNotNull(serviceToUse.get(product));
			if (i % 10000 == 0)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Method: " + serviceToUse.getClass().getSimpleName() + ", Count: " + i + ", Time: "
							+ (Utilities.formatTime(System.currentTimeMillis() - start)));
				}
			}
		}
		return System.currentTimeMillis() - start;
	}

	/**
	 * PLA-11186
	 */
	@Test
	public void testConcurrentLocMapAccess()
	{
		testConcurrentAccess(60);
	}

	private void testConcurrentAccess(final int DURATION_SEC)
	{
		getOrCreateLanguage("de");
		getOrCreateLanguage("en");

		final ProductModel productModel = defaultModelService.get(product);
		productModel.setName("DE-base", Locale.GERMAN);
		productModel.setName("EN-base", Locale.ENGLISH);
		defaultModelService.save(productModel);

		final PK pk = product.getPK();

		final TestThreadsHolder<Runnable> threads = new TestThreadsHolder<Runnable>(100, true)
		{
			@Override
			public Runnable newRunner(final int threadNumber)
			{
				return threadNumber == 0 ? new ModelUpdateRunner(defaultModelService, pk) : new ModelAccessRunner(
						cachingModelService, pk);
			}
		};
		threads.startAll();
		try
		{
			Thread.sleep(DURATION_SEC * 1000);
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		assertTrue(threads.stopAndDestroy(30));
		assertEquals(Collections.EMPTY_MAP, threads.getErrors());

	}

	static class ModelUpdateRunner implements Runnable
	{
		final ModelService modelService;
		final PK productPK;

		public ModelUpdateRunner(final ModelService modelService, final PK pk)
		{
			this.modelService = modelService;
			this.productPK = pk;
		}

		@Override
		public void run()
		{
			final Thread currentThread = Thread.currentThread();
			while (!currentThread.isInterrupted())
			{
				final ProductModel productModel = modelService.get(productPK);
				productModel.setName("EN-" + System.nanoTime(), Locale.ENGLISH);
				productModel.setName("DE-" + System.nanoTime(), Locale.GERMAN);
				modelService.save(productModel);
				try
				{
					Thread.sleep((long) (Math.random() * 1000));
				}
				catch (final InterruptedException e)
				{
					currentThread.interrupt();
				}
			}
		}
	}

	static class ModelAccessRunner implements Runnable
	{
		final ModelService modelService;
		final PK productPK;

		public ModelAccessRunner(final ModelService modelService, final PK pk)
		{
			this.modelService = modelService;
			this.productPK = pk;
		}

		@Override
		public void run()
		{
			final ComposedType type = TypeManager.getInstance().getComposedType("Product");
			final JaloSession jaloSession = JaloSession.getCurrentSession();
			jaloSession.getSessionContext().setAttribute(CachingModelService.CACHING_MODEL_SERVICE_LIST_OF_TYPES,
					Collections.singleton(type.getPK()));



			final Thread currentThread = Thread.currentThread();
			while (!currentThread.isInterrupted())
			{
				final ProductModel productModel = modelService.get(productPK);

				final String nameDE = productModel.getName(Locale.ENGLISH);
				final String nameEN = productModel.getName(Locale.GERMAN);
				assertNotNull(nameDE);
				assertNotNull(nameEN);

			}
		}
	}
}
