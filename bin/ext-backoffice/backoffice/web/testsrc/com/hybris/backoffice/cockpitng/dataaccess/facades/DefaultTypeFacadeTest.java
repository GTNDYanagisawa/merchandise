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
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.hybris.backoffice.cockpitng.dataaccess.facades.common.PlatformFacadeStrategyHandleCache;
import com.hybris.backoffice.cockpitng.dataaccess.facades.type.DefaultPlatformTypeFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.type.impl.DefaultTypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.impl.TypeFacadeStrategyRegistry;


@IntegrationTest
public class DefaultTypeFacadeTest extends ServicelayerBaseTest
{
	@Resource
	private TypeService typeService;
	@Resource
	private I18NService i18NService;
	@Resource
	private ModelService modelService;

	private DefaultTypeFacade typeFacade;


	@Before
	public void setUp()
	{
		typeFacade = new DefaultTypeFacade();

		final TypeFacadeStrategyRegistry registry = new TypeFacadeStrategyRegistry();
		final DefaultPlatformTypeFacadeStrategy strategy = new DefaultPlatformTypeFacadeStrategy();
		strategy.setTypeService(typeService);
		strategy.setI18nService(i18NService);
		strategy.setModelService(modelService);
		final PlatformFacadeStrategyHandleCache platformFacadeStrategyHandleCache = new PlatformFacadeStrategyHandleCache();
		platformFacadeStrategyHandleCache.setTypeService(typeService);
		strategy.setPlatformFacadeStrategyHandleCache(platformFacadeStrategyHandleCache);
		registry.setDefaultStrategy(strategy);
		typeFacade.setStrategyRegistry(registry);
	}

	@Test
	public void testTypeLoad() throws TypeNotFoundException // NOPMD
	{
		final DataType facadeType = typeFacade.load("Product");
		Assert.notNull(facadeType, "Loaded product is null");

		final ComposedTypeModel platformType = typeService.getComposedTypeForCode("Product");

		compareTypes(facadeType, platformType);
	}


	@Test(expected = TypeNotFoundException.class)
	public void testTypeLoadFail() throws TypeNotFoundException // NOPMD
	{
		typeFacade.load(null);
	}

	/**
	 * @param facadeType
	 * @param platformType
	 */
	private void compareTypes(final DataType facadeType, final ComposedTypeModel platformType)
	{
		// make sure codes are equal
		Assert.isTrue(platformType.getCode().equals(facadeType.getCode()));
		// compareFields(facadeType, platformType);
	}

	@SuppressWarnings("unused")
	private void compareFields(final DataType facadeType, final ComposedTypeModel platformType)
	{
		final Collection<DataAttribute> facadeAttrs = facadeType.getAttributes();
		final Collection<AttributeDescriptorModel> platformAttrs = platformType.getDeclaredattributedescriptors();

		Assert.notNull(facadeAttrs);
		Assert.notNull(platformAttrs);

		// make sure the number of loaded attributes matches
		Assert.isTrue(facadeAttrs.size() == platformAttrs.size());

		for (final AttributeDescriptorModel attrDescr : platformAttrs)
		{
			final DataAttribute facadeAttr = facadeType.getAttribute(attrDescr.getQualifier());
			Assert.isTrue(attrDescr.getQualifier().equals(facadeAttr.getQualifier()));

			// compare attribute types (and multiplicity?)

			// compare modifiers

		}
	}
}
