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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.enums.PageAlignment;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.model.GridModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.printcockpit.pagemanagment.PagePlanner.CanAddResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class DefaultDynamicModePagePlannerTest extends AbstractPagePlannerTest
{

	@Test
	public void testCanAddNewPlacementSinglePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertEquals("Number of empty slots should be equal to capacity of the page", 1, page.getGrid().getElements().size());
		assertTrue("Should be able to add while we have empty slots", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertTrue("We can always append since we have free space", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());

		pagePlanner.addNewPlacement(page, preparePlacement(), 0, 1);

		assertFalse("We already have filled this slot", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertFalse("No append possible - all slots are full", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());
	}

	@Test
	public void testCanAddNewPlacementDoublePage()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertEquals("Number of empty slots should be equal to capacity of the page", 1, page.getGrid().getElements().size());
		assertTrue("Should be able to add while we have empty slots", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertTrue("We can always append since we have free space", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());

		final PlacementModel placement1 = preparePlacement();
		final PlacementModel placement2 = preparePlacement();

		pagePlanner.addNewPlacement(page, placement1, 0, 1);
		pagePlanner.addNewPlacement(page, placement2, 0, 1);

		final List<PlacementModel> placements = page.getPlacements();
		assertTrue("", placement2 == placements.get(0));
		assertTrue("", placement1 == placements.get(1));

		assertFalse("We already have filled this slot", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertFalse("No append possible - all slots are full", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());
	}

	@Test
	public void testChangeDoubleToSinglePage()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertTrue("Change to same mode should be allowed", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		assertTrue("No placements so far, change possible", pagePlanner.canChangePageMode(page, PageMode.SINGLE));

		fillAllSlotsWithPlacements(page);

		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.SINGLE));

		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);

		assertEquals("Page mode should be changed", page.getPageMode(), PageMode.SINGLE);

		assertTrue("Data should be already truncated", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		assertEquals("Placement from 2-nd page should have been removed", 1, page.getPlacements().size());
	}

	@Test
	public void testChangeSingleToDoublePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertTrue("Change to same mode should be allowed", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		fillAllSlotsWithPlacements(page);

		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));

		pagePlanner.handleChangePageMode(page, PageMode.DOUBLE);

		assertEquals("Page mode should be changed", page.getPageMode(), PageMode.DOUBLE);
	}

	@Test
	public void testChangeSequenceToDoublePage()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(3));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		fillAllSlotsWithPlacements(page);
		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
		pagePlanner.handleChangePageMode(page, PageMode.DOUBLE);
		assertEquals("3-rd page's placements should have been removed", 6, page.getPlacements().size());
		assertTrue("Data should have been truncated", pagePlanner.canChangePageMode(page, PageMode.DOUBLE));
	}

	@Test
	public void testChangeSequenceToSinglePage()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(3));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		fillAllSlotsWithPlacements(page);
		assertFalse("Too many placements, change impossible", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);
		assertEquals("2-nd and 3-rd pages' placements should have been removed", 3, page.getPlacements().size());
		assertTrue("Data should have been truncated", pagePlanner.canChangePageMode(page, PageMode.SINGLE));
	}

	@Test
	public void testChangeSingleOrDoubleToSequencePage()
	{
		PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));
		pagePlanner.handleChangePageMode(page, PageMode.SEQUENCE);
		assertEquals("Page mode should have been set", PageMode.SEQUENCE, page.getPageMode());
		assertTrue("Change to same always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));

		page = preparePage(PageMode.DOUBLE, prepareGrid(1));
		assertTrue("Change to bigger always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));
		pagePlanner.handleChangePageMode(page, PageMode.SEQUENCE);
		assertEquals("Page mode should have been set", PageMode.SEQUENCE, page.getPageMode());
		assertTrue("Change to same always possible", pagePlanner.canChangePageMode(page, PageMode.SEQUENCE));
	}

	@Test
	public void testChangeGridToSmaller()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(2));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		final GridModel tooSmallGrid = prepareGrid(1);

		assertTrue("Grid is too small but we have no placements so far", pagePlanner.canChangeGridForPage(page, tooSmallGrid));

		fillAllSlotsWithPlacements(page);
		assertFalse("Grid is too small", pagePlanner.canChangeGridForPage(page, tooSmallGrid));

		pagePlanner.handleChangeGridForPage(page, tooSmallGrid);

		assertTrue("Placements should be removed to fit new grid's capacity", pagePlanner.canChangeGridForPage(page, tooSmallGrid));
		assertEquals("2-nd placement should be removed in preparation process", 1, page.getPlacements().size());
	}

	@Test
	public void testChangeGridToBigger()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(2));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		fillAllSlotsWithPlacements(page);

		final GridModel bigGrid = prepareGrid(4);
		assertTrue("Grid is big enough for the data", pagePlanner.canChangeGridForPage(page, bigGrid));

		pagePlanner.handleChangeGridForPage(page, bigGrid);

		assertEquals("New grid is big enough, no data truncation needed", 2, page.getPlacements().size());
	}

	@Test
	public void testNumberOfEmptySlotsSingle()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		CanAddResult canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1);

		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 2);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 5);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		pagePlanner.addNewPlacements(page, Collections.singletonList(preparePlacement()), 0, 1);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 3);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		pagePlanner.addNewPlacements(page, Collections.singletonList(preparePlacement()), 0, 1);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 2);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 3);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());
	}

	@Test
	public void testNumberOfEmptySlotsDouble()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		CanAddResult canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 5);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 5, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 8);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 8, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 9);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 8, canAdd.getCanAddNumber());

		pagePlanner.addNewPlacements(page, Collections.singletonList(preparePlacement()), 0, 1);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 3);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 7);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 7, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 8);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 7, canAdd.getCanAddNumber());

		pagePlanner.addNewPlacements(page, Collections.singletonList(preparePlacement()), 1, 1);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 2);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 3);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 6);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 6, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 123);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 6, canAdd.getCanAddNumber());
	}

	@Test
	public void testChangePageModeComments()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(2));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		final List<CommentModel> comments = new ArrayList<CommentModel>();
		comments.add(prepareComment("testComment1", 0));
		comments.add(prepareComment("testComment2", 1));
		page.setComments(comments);

		assertFalse("We have no placements, yet we have a comment", pagePlanner.canChangePageMode(page, PageMode.SINGLE));

		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);

		for (final CommentModel comment : page.getComments())
		{
			for (final CommentMetadataModel meta : comment.getCommentMetadata())
			{
				assertEquals("If we still have placements they should be on the fist page", Integer.valueOf(0), meta.getPageIndex());
			}
		}
	}

	@Test
	public void testChangeAlternativeGridForPage()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(2));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		fillAllSlotsWithPlacements(page);
		final GridModel altGrid = prepareGrid(1);

		assertFalse("Right grid has 2 placements", pagePlanner.canChangeAlternativeGridForPage(page, altGrid));

		pagePlanner.handleChangeAlternativeGridForPage(page, altGrid);

		assertEquals(altGrid, page.getAlternativeGrid());
		assertEquals(3, page.getPlacements().size());

		for (final PlacementModel placement : page.getPlacements())
		{
			if (placement.getPageNumber().intValue() > 0)
			{
				assertTrue(placement.getGridElementId().intValue() == 1);
			}
		}
	}

	@Test
	public void testAlternativeGridSet()
	{
		PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(2), prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		fillAllSlotsWithPlacements(page);
		assertEquals(3, page.getPlacements().size());
		CanAddResult canAdd = pagePlanner.canAddNewPlacement(page, 1, 2);
		assertFalse("Right grid has just 1 slot", canAdd.isCanAdd());
		assertEquals("All slots have placements", 0, canAdd.getCanAddNumber());

		final GridModel altGrid = prepareGrid(3);
		pagePlanner.handleChangeAlternativeGridForPage(page, altGrid);
		canAdd = pagePlanner.canAddNewPlacements(page, 1, 2, 3);
		assertFalse("Right grid has just 2 free slots", canAdd.isCanAdd());
		assertEquals("All slots have placements", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 2, 2);
		assertTrue(canAdd.isCanAdd());
		assertEquals(2, canAdd.getCanAddNumber());

		pagePlanner.addNewPlacements(page, preparePlacements(2), 1, 2);
		assertEquals(5, page.getPlacements().size());

		page = preparePage(PageMode.DOUBLE, prepareGrid(2), prepareGrid(3));
		prepareAndAddPlacementToPage(page, 0, 1);
		prepareAndAddPlacementToPage(page, 1, 2);
		prepareAndAddPlacementToPage(page, 1, 3);
		canAdd = pagePlanner.canAddNewPlacements(page, 0, 2, 3);
		assertFalse("Left: 1 free; Right: 1 free", canAdd.isCanAdd());
		assertEquals(2, canAdd.getCanAddNumber());
	}

	@Test
	public void testChangePageModeWithAlternativeGrid()
	{
		GridModel lGrid = prepareGrid(0);
		GridModel rGrid = prepareGrid(0);

		PageModel page = preparePage(PageMode.DOUBLE, lGrid, rGrid);
		page.setAlignment(PageAlignment.LEFT);

		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);
		assertEquals(page.getAlternativeGrid(), rGrid);

		pagePlanner.handleChangePageMode(page, PageMode.SEQUENCE);
		assertEquals(page.getAlternativeGrid(), rGrid);
		assertEquals(lGrid, page.getGrid());

		lGrid = prepareGrid(0);
		rGrid = prepareGrid(0);

		page = preparePage(PageMode.SEQUENCE, lGrid);
		page.setAlignment(PageAlignment.RIGHT);

		pagePlanner.handleChangeAlternativeGridForPage(page, rGrid);
		assertEquals(page.getAlternativeGrid(), rGrid);
		assertEquals(lGrid, page.getGrid());
		assertEquals(rGrid, page.getGrid().getLeftGrid());
	}

	@Test
	public void testChangePageAndGridModeWithAlternativeGridWithPlacements()
	{
		final GridModel lGrid = prepareGrid(4);
		final GridModel rGrid = prepareGrid(2);

		final PageModel page = preparePage(PageMode.DOUBLE, lGrid, rGrid);
		page.setAlignment(PageAlignment.LEFT);

		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		prepareAndAddPlacementToPage(page, 0, 1);
		prepareAndAddPlacementToPage(page, 0, 2);
		prepareAndAddPlacementToPage(page, 1, 1);
		prepareAndAddPlacementToPage(page, 1, 2);

		assertFalse(pagePlanner.canChangePageMode(page, PageMode.SINGLE));
		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);

		assertEquals(4, page.getPlacements().size());

	}

	@Test
	public void testComputeAvailableSlotsNumberForInsertModeSinglePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		CanAddResult canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1, true);

		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 2, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 5, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		prepareAndAddPlacementToPage(page, 0, 1);
		prepareAndAddPlacementToPage(page, 0, 2);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 3, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 5, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		prepareAndAddPlacementToPage(page, 0, 1);
		prepareAndAddPlacementToPage(page, 0, 2);
		prepareAndAddPlacementToPage(page, 0, 3);
		prepareAndAddPlacementToPage(page, 0, 4);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 1, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 2, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 3, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());
	}

	@Test
	public void testComputeAvailableSlotsNumberForInsertModeDoublePage()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(4), prepareGrid(3));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());


		prepareAndAddPlacementToPage(page, 0, 1);
		prepareAndAddPlacementToPage(page, 0, 2);

		CanAddResult canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 4, 1, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 2, 1, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		prepareAndAddPlacementToPage(page, 0, 3);
		prepareAndAddPlacementToPage(page, 0, 4);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 4, 1, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 4, 6, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 3, 2, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 3, 5, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		prepareAndAddPlacementToPage(page, 1, 1);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 3, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 6, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 5, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 1, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 2, 2, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 3, 2, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 3, 4, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 3, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 4, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		prepareAndAddPlacementToPage(page, 1, 2);
		prepareAndAddPlacementToPage(page, 1, 3);

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 3, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 2, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());
	}

	@Test
	public void testPageModeAndAssertProperNumerationOfPlacements()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, GridMode.DYNAMIC, PageAlignment.LEFT, prepareGrid(4), null);
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		fillAllSlotsWithPlacements(page);
		List<PlacementModel> placements = page.getPlacements();
		for (final PlacementModel placement : placements)
		{
			placement.setGridElementId(Integer.valueOf(-1));
			placement.setPageNumber(Integer.valueOf(-1));
		}
		pagePlanner.handleChangePageMode(page, PageMode.SINGLE);

		placements = page.getPlacements();
		assertEquals(4, placements.size());
		for (int i = 0; i < placements.size(); i++)
		{
			assertEquals(0, placements.get(i).getPageNumber().intValue());
			assertEquals(i + 1, placements.get(i).getGridElementId().intValue());
		}
	}

	@Test
	public void testChangeGridModeWithShiftToRightPage()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, GridMode.DYNAMIC, PageAlignment.LEFT, prepareGrid(8), prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		pagePlanner.addNewPlacements(page, preparePlacements(8), 0, 1);
		final GridModel tooSmallGrid = prepareGrid(3);
		final GridModel okGrid = prepareGrid(4);
		assertFalse(pagePlanner.canChangeGridForPage(page, tooSmallGrid));
		assertTrue(pagePlanner.canChangeGridForPage(page, okGrid));

		pagePlanner.handleChangeGridForPage(page, okGrid);

		int pageOneCnt = 0;
		int pageTwoCnt = 0;
		for (final PlacementModel placement : page.getPlacements())
		{
			assertTrue(placement.getGridElementId().intValue() <= 4);
			if (placement.getPageNumber().intValue() == 0)
			{
				pageOneCnt++;
			}
			else if (placement.getPageNumber().intValue() == 1)
			{
				pageTwoCnt++;
			}
			else
			{
				fail("Wrong page number.");
			}
		}
		assertEquals(pageOneCnt, 4);
		assertEquals(pageTwoCnt, 4);
	}


	@Test
	public void testCanChangeGridForEmptyPage()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(4));
		page.setPlacements(null);
		Assert.assertTrue(getPagePlanner(getDefaultGridMode()).canChangeGridForPage(page, prepareGrid(3)));
	}

	@Test
	public void testCanChangeAlternativeGridForEmptyPage()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(4));
		page.setPlacements(null);
		Assert.assertTrue(getPagePlanner(getDefaultGridMode()).canChangeAlternativeGridForPage(page, prepareGrid(3)));
	}

	@Test
	public void testCanChangePageMode()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(4));
		page.setPlacements(null);
		Assert.assertTrue(getPagePlanner(getDefaultGridMode()).canChangePageMode(page, PageMode.SINGLE));
	}

	@Test
	public void testCanChangeGridForEmptyPageFixedMode()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(4));
		page.setPlacements(null);
		Assert.assertTrue(getPagePlanner(GridMode.FIXED).canChangeGridForPage(page, prepareGrid(3)));
	}

	@Test
	public void testCanChangeAlternativeGridForEmptyPageFixedMode()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(4));
		page.setPlacements(null);
		Assert.assertTrue(getPagePlanner(GridMode.FIXED).canChangeAlternativeGridForPage(page, prepareGrid(3)));
	}

	@Test
	public void testCanChangePageModeFixedMode()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(4));
		page.setPlacements(null);
		Assert.assertTrue(getPagePlanner(GridMode.FIXED).canChangePageMode(page, PageMode.SINGLE));
	}

	@Override
	public GridMode getDefaultGridMode()
	{
		return GridMode.DYNAMIC;
	}

}
