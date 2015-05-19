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
package de.hybris.platform.servicelayer.user.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.daos.TitleDao;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultTitleDaoTest extends ServicelayerBaseTest
{
	private static final String DOC = "dr";
	private static final String PROF = "prof";

	@Resource
	private ModelService modelService;

	@Resource
	private TitleDao titleDao;

	@Test
	public void testFindTitlesEmpty()
	{
		final Collection<TitleModel> results = titleDao.findTitles();
		assertNotNull(results);
		assertEquals(0, results.size());
	}

	@Test
	public void testFindTitles()
	{
		createTitle(DOC);
		createTitle(PROF);

		modelService.saveAll();

		final Collection<TitleModel> results = titleDao.findTitles();
		assertNotNull(results);
		assertEquals(2, results.size());
		// TODO compare result content
	}

	@Test
	public void testFindTitleByCodeEmpty()
	{
		final TitleModel title = titleDao.findTitleByCode(DOC);
		assertNull(title);
	}

	@Test
	public void testFindTitleByWrongCode()
	{
		createTitle(DOC);
		createTitle(PROF);

		modelService.saveAll();

		final TitleModel title = titleDao.findTitleByCode("xyz");
		assertNull(title);
	}

	@Test
	public void testFindTitleByCode()
	{
		createTitle(DOC);
		createTitle(PROF);

		modelService.saveAll();

		final TitleModel title = titleDao.findTitleByCode(DOC);
		assertNotNull(title);
		assertEquals(DOC, title.getCode());
	}

	private TitleModel createTitle(final String code)
	{
		return createTitle(code, "(" + code + ")");
	}

	private TitleModel createTitle(final String code, final String name)
	{
		final TitleModel title = modelService.create(TitleModel.class);
		title.setCode(code);
		title.setName(name);

		return title;
	}
}
