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
package de.hybris.platform.deeplink.dao.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.platform.core.Registry;
import de.hybris.platform.deeplink.dao.DeeplinkUrlDao;
import de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel;
import de.hybris.platform.deeplink.model.rules.DeeplinkUrlRuleModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * The Class DeeplinkUrlDaoImplTest.
 */
public class DeeplinkUrlDaoImplTest extends HybrisJUnit4TransactionalTest
{

	private TypeService typeService;
	private ModelService modelService;
	private DeeplinkUrlDao dao;
	private List<DeeplinkUrlRuleModel> createdRules;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		createdRules = createDeeplinkUrlRules();
		dao = (DeeplinkUrlDao) Registry.getApplicationContext().getBean("deeplinUrlDao");
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.deeplink.dao.impl.DeeplinkUrlDaoImpl#findDeeplinkUrlModel(java.lang.String)}.
	 */
	@Test
	public void testFindDeeplinkUrlModel()
	{
		// Create DeeplinkUrl object
		final DeeplinkUrlModel deeplinkUrl = getModelService().create(DeeplinkUrlModel.class);
		deeplinkUrl.setCode("foobar");
		deeplinkUrl.setBaseUrl("www.foobar.com/somestuff");
		deeplinkUrl.setName("FooBar");
		modelService.save(deeplinkUrl);

		final DeeplinkUrlModel foundDeeplinkUrl = dao.findDeeplinkUrlModel("foobar");
		assertThat(foundDeeplinkUrl, is(equalTo(deeplinkUrl)));
	}

	/**
	 * Test method for {@link de.hybris.platform.deeplink.dao.impl.DeeplinkUrlDaoImpl#findDeeplinkUrlRules()}.
	 */
	@Test
	public void testFindDeeplinkUrlRules()
	{
		final List<DeeplinkUrlRuleModel> foundRules = dao.findDeeplinkUrlRules();
		assertThat(Integer.valueOf(foundRules.size()), is(equalTo(Integer.valueOf(3))));
		assertThat(foundRules, is(equalTo(createdRules)));
	}

	/**
	 * Test method for {@link de.hybris.platform.deeplink.dao.impl.DeeplinkUrlDaoImpl#findObject(java.lang.String)}.
	 */
	@Test
	public void testFindObject()
	{
		final DeeplinkUrlRuleModel rule = createdRules.get(0);
		final Object foundObject = dao.findObject(rule.getPk().toString());
		assertThat(foundObject, is(instanceOf(rule.getClass())));
		assertThat((DeeplinkUrlRuleModel) foundObject, is(equalTo(rule)));
	}

	private ModelService getModelService()
	{
		if (modelService == null)
		{
			modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		}
		return modelService;
	}

	private TypeService getTypeService()
	{
		if (typeService == null)
		{
			typeService = (TypeService) Registry.getApplicationContext().getBean("typeService");
		}
		return typeService;
	}

	/**
	 * Creates the deeplink url rules.
	 */
	private List<DeeplinkUrlRuleModel> createDeeplinkUrlRules()
	{
		final List<DeeplinkUrlRuleModel> result = new ArrayList<DeeplinkUrlRuleModel>();

		final DeeplinkUrlRuleModel deeplinkRule1 = getModelService().create(DeeplinkUrlRuleModel.class);
		deeplinkRule1.setBaseUrlPattern(".*www\\.foobar\\.com.*");
		deeplinkRule1.setDestUrlTemplate("www.foobar.com/product/$item.code?deeplink=$deeplinkurl.code");
		deeplinkRule1.setUseForward(Boolean.FALSE);
		deeplinkRule1.setApplicableType(getTypeService().getComposedType("DeeplinkUrl"));
		deeplinkRule1.setPriority(Integer.valueOf(1));
		modelService.save(deeplinkRule1);

		final DeeplinkUrlRuleModel deeplinkRule2 = getModelService().create(DeeplinkUrlRuleModel.class);
		deeplinkRule2.setBaseUrlPattern(".*www\\.foobar\\.com.*");
		deeplinkRule2.setDestUrlTemplate("www.foobar.com/someotherstuff/$item.code?deeplink=$deeplinkurl.code");
		deeplinkRule2.setUseForward(Boolean.FALSE);
		deeplinkRule2.setApplicableType(getTypeService().getComposedType("DeeplinkUrl"));
		deeplinkRule2.setPriority(Integer.valueOf(2));
		modelService.save(deeplinkRule2);

		final DeeplinkUrlRuleModel deeplinkRule3 = getModelService().create(DeeplinkUrlRuleModel.class);
		deeplinkRule3.setBaseUrlPattern(".*www\\.foobar\\.com.*");
		deeplinkRule3.setDestUrlTemplate("www.foobar.com/somethingelse/$item.code?deeplink=$deeplinkurl.code");
		deeplinkRule3.setUseForward(Boolean.FALSE);
		deeplinkRule3.setApplicableType(getTypeService().getComposedType("DeeplinkUrl"));
		deeplinkRule3.setPriority(Integer.valueOf(3));
		modelService.save(deeplinkRule3);

		result.add(deeplinkRule1);
		result.add(deeplinkRule2);
		result.add(deeplinkRule3);

		return result;
	}

}
