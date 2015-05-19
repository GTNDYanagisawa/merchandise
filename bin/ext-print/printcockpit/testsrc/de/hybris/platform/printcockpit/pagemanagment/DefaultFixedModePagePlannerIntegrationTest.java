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
package de.hybris.platform.printcockpit.pagemanagment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.model.GridModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


@IntegrationTest
public class DefaultFixedModePagePlannerIntegrationTest extends AbstractPagePlannerIntegrationTest
{

	@Test
	public void testCanAddNewPlacementsSinglePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		assertEquals("Number of empty slots should be equal to capacity of the page", 1, page.getGrid().getElements().size());
		assertTrue("Should be able to add while we have empty slots", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertFalse("Single page has no 2-nd page", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());

		pagePlanner.addNewPlacement(page, preparePlacement(), 0, 1);

		modelService.save(page);

		assertFalse("We already have filled this slot", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertFalse("Single page has no 2-nd page", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());

	}

	@Test
	public void testChangeDoubleToSinglePage()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(1, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		assertTrue("Change to same mode should be allowed", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		assertTrue("No placements so far, change possible", pagePlanner.canChangePageMode(page, PageMode.SINGLE));

		fillAllSlotsWithPlacements(page);

		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.SINGLE));

		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);
		modelService.save(page);

		assertEquals("Page mode should be changed", page.getPageMode(), PageMode.SINGLE);

		assertTrue("Data should be already truncated", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		assertEquals("Placement from 2-nd page should have been removed", 1, page.getPlacements().size());
	}

	@Test
	public void testChangeSingleToDoublePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		assertTrue("Change to same mode should be allowed", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		fillAllSlotsWithPlacements(page);

		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));

		pagePlanner.handleChangePageMode(page, PageMode.DOUBLE);
		modelService.save(page);

		assertEquals("Page mode should be changed", page.getPageMode(), PageMode.DOUBLE);
	}

	@Test
	public void testChangeSequenceToDoublePage()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(3, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		fillAllSlotsWithPlacements(page);
		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		pagePlanner.handleChangePageMode(page, PageMode.DOUBLE);
		modelService.save(page);
		assertEquals("3-rd page's placements should have been removed", 6, page.getPlacements().size());
		assertTrue("Data should have been truncated", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
	}

	@Test
	public void testChangeSequenceToSinglePage()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(3, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		fillAllSlotsWithPlacements(page);
		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);
		modelService.save(page);

		assertEquals("2-nd and 3-rd pages' placements should have been removed", 3, page.getPlacements().size());
		assertTrue("Data should have been truncated", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
	}

	@Test
	public void testChangeSequenceToDoubleToSinglePage()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(3, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		fillAllSlotsWithPlacements(page);

		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		pagePlanner.handleChangePageMode(page, PageMode.DOUBLE);
		assertTrue("Data should have been truncated", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		modelService.save(page);
		assertEquals("Page modes should be the same", PageMode.DOUBLE, page.getPageMode());
		assertEquals("2-nd page's placements should have been removed", 6, page.getPlacements().size());

		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);
		assertTrue("Data should have been truncated", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		modelService.save(page);
		assertEquals("Page modes should be the same", PageMode.SINGLE, page.getPageMode());
		assertEquals("2-nd and 3-rd pages' placements should have been removed", 3, page.getPlacements().size());
	}

	@Test
	public void testChangeSingleOrDoubleToSequencePage()
	{
		PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));
		pagePlanner.handleChangePageMode(page, PageMode.SEQUENCE);
		modelService.save(page);

		assertEquals("Page mode should have been set", PageMode.SEQUENCE, page.getPageMode());
		assertTrue("Change to same always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));

		page = preparePage(PageMode.DOUBLE, prepareGrid(1, 1));
		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));
		pagePlanner.handleChangePageMode(page, PageMode.SEQUENCE);
		modelService.save(page);
		assertEquals("Page mode should have been set", PageMode.SEQUENCE, page.getPageMode());
		assertTrue("Change to same always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));
	}

	@Test
	public void testChangeGridToSmaller()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(2, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());
		final GridModel tooSmallGrid = prepareGrid(1, 2);

		assertTrue("Grid is too small but we have no placements so far", pagePlanner.canChangeGridForPage(page, tooSmallGrid));

		fillAllSlotsWithPlacements(page);
		assertFalse("Grid is too small", pagePlanner.canChangeGridForPage(page, tooSmallGrid));

		pagePlanner.handleChangeGridForPage(page, tooSmallGrid);
		modelService.save(page);

		assertTrue("Placements should be removed to fit new grid's capacity", pagePlanner.canChangeGridForPage(page, tooSmallGrid));
		assertEquals("2-nd placement should be removed in preparation process", 1, page.getPlacements().size());
	}

	@Test
	public void testChangeGridToBigger()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(2, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		fillAllSlotsWithPlacements(page);

		final GridModel bigGrid = prepareGrid(4, 2);
		assertTrue("Grid is big enough for the data", pagePlanner.canChangeGridForPage(page, bigGrid));

		pagePlanner.handleChangeGridForPage(page, bigGrid);
		modelService.save(page);

		assertEquals("New grid is big enough, no data truncation needed", 2, page.getPlacements().size());
	}

	@Test(expected = ModelSavingException.class)
	public void testAddWrongPlacementAndSavePlacement()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(2, 1));
		fillAllSlotsWithPlacements(page);
		modelService.save(page);

		final PlacementModel placement = preparePlacement();

		placement.setPage(page);
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(300));

		modelService.save(placement);
	}

	@Test
	public void testAddWrongPlacementAndSavePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(2, 1));
		fillAllSlotsWithPlacements(page);
		modelService.save(page);

		final List<PlacementModel> placements = new ArrayList<PlacementModel>(page.getPlacements());
		final PlacementModel placement = preparePlacement();

		placement.setPage(page);
		placement.setPageNumber(Integer.valueOf(0));
		placement.setGridElementId(Integer.valueOf(300));

		placements.add(placement);

		page.setPlacements(placements);
		try
		{
			modelService.save(page);
			fail("Last call to save should invoke model saving exception.");
		}
		catch (final ModelSavingException e)
		{
			//fine here - every other exception will fails the test
			//not using @Test(expected=ModelSavingException.class) while other calls to model service could also throw the same exception 
		}
	}

	@Test
	public void testChangePageModeComments()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(2, 1));
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());

		final List<CommentModel> comments = new ArrayList<CommentModel>();
		comments.add(prepareComment("testComment1", "testComment1", 0, 10, 10));
		comments.add(prepareComment("testComment2", "testComment2", 1, 10, 10));
		page.setComments(comments);

		modelService.save(page);

		assertFalse("We have no placements, yet we have a comment", pagePlanner.canChangePageMode(page, PageMode.SINGLE));

		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);

		modelService.save(page);

		for (final CommentModel comment : page.getComments())
		{
			for (final CommentMetadataModel meta : comment.getCommentMetadata())
			{
				assertEquals("If we still have placements they should be on the fist page", Integer.valueOf(0), meta.getPageIndex());
			}
		}
	}

	@Override
	protected GridMode getDefaultGridModeForTest()
	{
		return GridMode.FIXED;
	}

}
