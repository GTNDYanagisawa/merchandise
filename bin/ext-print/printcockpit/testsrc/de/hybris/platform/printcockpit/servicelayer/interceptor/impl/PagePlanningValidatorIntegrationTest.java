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
package de.hybris.platform.printcockpit.servicelayer.interceptor.impl;

import static org.junit.Assert.fail;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.model.LayoutTemplateModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.printcockpit.pagemanagment.AbstractPagePlannerIntegrationTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class PagePlanningValidatorIntegrationTest extends AbstractPagePlannerIntegrationTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PagePlanningValidatorIntegrationTest.class);

	@Test(expected = ModelSavingException.class)
	public void testPreparePlacementForAPageWithoutAGrid()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.DYNAMIC);

		saveOrFail(page);

		final PlacementModel placement = preparePlacement();
		placement.setPage(page);
		modelService.save(placement);

	}

	@Test(expected = ModelSavingException.class)
	public void testPutOnNonExistingSlot()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(1, 1));

		final PlacementModel placement = preparePlacement();
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(2));

		page.setPlacements(Collections.singletonList(placement));

		modelService.save(page);
	}

	@Test(expected = ModelSavingException.class)
	public void testPutOnNonExistingPage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(1, 1));

		final PlacementModel placement = preparePlacement();
		placement.setPageNumber(Integer.valueOf(1));
		placement.setGridElementId(Integer.valueOf(1));

		page.setPlacements(Collections.singletonList(placement));

		modelService.save(page);
	}

	@Test(expected = ModelSavingException.class)
	public void testChangeGrid()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(2, 1));

		fillAllSlotsWithPlacements(page);

		saveOrFail(page);

		page.setGrid(prepareGrid(1, 2));

		modelService.save(page);
	}

	@Test(expected = ModelSavingException.class)
	public void testChangePageMode()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(2, 1));

		fillAllSlotsWithPlacements(page);

		saveOrFail(page);

		page.setPageMode(PageMode.SINGLE);

		modelService.save(page);
	}

	@Test(expected = ModelSavingException.class)
	public void testPutPlacementsOnTheSameSlot()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(1, 1));

		final List<PlacementModel> placements = new ArrayList<PlacementModel>();

		PlacementModel placement = preparePlacement();
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(1));
		placement.setPage(page);
		placements.add(placement);

		page.setPlacements(placements);
		saveOrFail(page);

		placement = preparePlacement();
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(1));
		placement.setPage(page);
		placements.add(placement);

		page.setPlacements(placements);

		modelService.save(page);
	}

	@Test(expected = ModelSavingException.class)
	public void testSavePlacementWithWrongPosition()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(1, 1));

		saveOrFail(page);

		final PlacementModel placement = preparePlacement();
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(2));
		placement.setPage(page);

		modelService.save(placement);
	}

	@Test(expected = ModelSavingException.class)
	public void testPutPlacementsOnTheSameSlotAavePlacement()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(1, 1));
		fillAllSlotsWithPlacements(page);
		saveOrFail(page);

		final PlacementModel placement = preparePlacement();
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(1));
		placement.setPage(page);

		modelService.save(placement);
	}

	@Test(expected = ModelSavingException.class)
	public void testMisplacedComments()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(1, 1));
		saveOrFail(page);

		final List<CommentModel> comments = new ArrayList<CommentModel>();

		comments.add(prepareComment("demoComment1", "Subject1", 0, 10, 10));
		comments.add(prepareComment("demoComment2", "Subject2", 1, 10, 10));

		page.setComments(comments);

		modelService.save(page);
	}

	@Test
	public void testModifyPlacementLayoutTemplate()
	{
		final PageModel page = preparePage(PageMode.SINGLE, null, GridMode.FIXED);
		page.setGrid(prepareGrid(4, 1));

		final PlacementModel placement = preparePlacement();
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(1));
		placement.setPage(page);

		page.setPlacements(Collections.singletonList(placement));

		modelService.save(page);

		final LayoutTemplateModel newLayoutTemplate = modelService.create(LayoutTemplateModel.class);
		newLayoutTemplate.setCode("TestTemplate2");
		newLayoutTemplate.setCometConfig(cometConfig);
		newLayoutTemplate.setId(Integer.valueOf(2));
		modelService.save(newLayoutTemplate);

		placement.setLayoutTemplate(newLayoutTemplate);

		boolean success = false;
		try
		{
			modelService.save(placement);
			success = true;
		}
		catch (final Exception e)
		{
			fail("Saving placement's new layout template failed with : " + e.getMessage());
		}

		Assert.assertTrue(success);

	}

	private void saveOrFail(final ItemModel item)
	{
		try
		{
			modelService.save(item);
		}
		catch (final ModelSavingException mse)
		{
			LOG.warn("", mse);
			fail("No exception expected!");
		}
	}

}
