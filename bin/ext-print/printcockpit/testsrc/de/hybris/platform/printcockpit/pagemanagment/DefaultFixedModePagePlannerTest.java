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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


@UnitTest
public class DefaultFixedModePagePlannerTest extends AbstractPagePlannerTest
{

	@Test
	public void testCanAddNewPlacementSinglePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertEquals("Number of empty slots should be equal to capacity of the page", 1, page.getGrid().getElements().size());
		assertTrue("Should be able to add while we have empty slots", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertFalse("Single page has no 2-nd page", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());

		pagePlanner.addNewPlacement(page, preparePlacement(), 0, 1);

		assertFalse("We already have filled this slot", pagePlanner.canAddNewPlacement(page, 0, 1).isCanAdd());
		assertFalse("Single page has no 2-nd page", pagePlanner.canAddNewPlacement(page, 1, 1).isCanAdd());
	}

	@Test
	public void testCanAddNewPlacementsSinglePage()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(1));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertEquals("Number of empty slots should be equal to capacity of the page", 1, page.getGrid().getElements().size());
		assertTrue("Should be able to add while we have empty slots", pagePlanner.canAddNewPlacements(page, 0, 1, 1).isCanAdd());
		assertFalse("Not enough empty slots", pagePlanner.canAddNewPlacements(page, 0, 1, 2).isCanAdd());
		assertFalse("Single page has no 2-nd page", pagePlanner.canAddNewPlacements(page, 1, 1, 1).isCanAdd());

		pagePlanner.addNewPlacement(page, preparePlacement(), 0, 1);

		assertFalse("We already have filled this slot", pagePlanner.canAddNewPlacements(page, 0, 1, 1).isCanAdd());
		assertFalse("Single page has no 2-nd page", pagePlanner.canAddNewPlacements(page, 1, 1, 1).isCanAdd());
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
		PageModel page = preparePage(PageMode.SINGLE, prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		CanAddResult canAdd = pagePlanner.canAddNewPlacement(page, 0, 1);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 5);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		fillAllSlotsWithPlacements(page);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 5);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		page = preparePage(PageMode.SINGLE, prepareGrid(4));

		fillSlot(page, 0, 1);
		fillSlot(page, 0, 3);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 1);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 2, 1);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 1);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 4, 1);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 2, 2);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 4, 2);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());
	}

	@Test
	public void testNumberOfEmptySlotsDouble()
	{
		PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		CanAddResult canAdd = pagePlanner.canAddNewPlacement(page, 0, 1);
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

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 4);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 2, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 3, canAdd.getCanAddNumber());

		fillAllSlotsWithPlacements(page);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 5);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 8);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 9);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 2, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		page = preparePage(PageMode.DOUBLE, prepareGrid(4));

		fillSlot(page, 0, 1);
		fillSlot(page, 0, 3);
		fillSlot(page, 1, 2);
		fillSlot(page, 1, 4);

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 2, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 3, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 0, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 4, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 4, 2);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 4);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());
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

	@Override
	public GridMode getDefaultGridMode()
	{
		return GridMode.FIXED;
	}

	@Test
	public void testCanChangeAlternativeGridForPageSingle()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertTrue("There is no second/right page, so grid should be set.",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(2)));
		assertTrue("There is no second/right page, so grid should be set.",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(6)));
	}

	@Test
	public void testCanChangeAlternativeGridForPageSequence()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertTrue("There is no second/right page, so grid should be set.",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(2)));
		assertTrue("There is no second/right page, so grid should be set.",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(6)));
	}

	@Test
	public void testCanChangeAlternativeGridForPageDoubleNoPlacements()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(4), prepareGrid(2));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		assertTrue("There are no placements on the page, so grid should be set.",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(1)));
		assertTrue("There are no placements on the page, so grid should be set.",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(2)));
		assertTrue("There are no placements on the page, so grid should be set.",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(6)));
	}

	@Test
	public void testCanChangeAlternativeGridForPageDoublePlacements()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(2), prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());

		fillSlot(page, 1, 1);
		fillSlot(page, 1, 3);

		assertFalse("Too many placements", pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(1)));
		assertFalse("One placement must be cut", pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(2)));
		assertTrue("The number of slots in new grid is enough to handle all placements",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(6)));


		fillAllSlotsWithPlacements(page);

		assertFalse("Too many placements", pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(1)));
		assertTrue("The number of slots in new grid is enough to handle all placements",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(4)));
		assertTrue("The number of slots in new grid is enough to handle all placements",
				pagePlanner.canChangeAlternativeGridForPage(page, prepareGrid(6)));
	}

	@Test
	public void testHandleChangeAlternativeGridForPageSingle()
	{
		final PageModel page = preparePage(PageMode.SINGLE, prepareGrid(2), prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		final GridModel newGrid = prepareGrid(1);

		pagePlanner.handleChangeAlternativeGridForPage(page, newGrid);

		assertEquals(newGrid, page.getAlternativeGrid());
	}

	@Test
	public void testHandleChangeAlternativeGridForPageSequence()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, prepareGrid(2), prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		final GridModel newGrid = prepareGrid(1);

		pagePlanner.handleChangeAlternativeGridForPage(page, newGrid);

		assertEquals(newGrid, page.getAlternativeGrid());

		page.setAlignment(PageAlignment.LEFT);

		pagePlanner.handleChangeAlternativeGridForPage(page, newGrid);
		assertEquals(newGrid, page.getGrid().getRightGrid());

	}

	@Test
	public void testHandleChangeAlternativeGridForPageDouble()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(2), prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		final GridModel newGrid = prepareGrid(1);

		pagePlanner.handleChangeAlternativeGridForPage(page, newGrid);

		assertEquals(newGrid, page.getAlternativeGrid());

		fillSlot(page, 1, 1);
		fillSlot(page, 1, 3);

		assertEquals(2, page.getPlacements().size());

		pagePlanner.handleChangeAlternativeGridForPage(page, newGrid);
		assertEquals(1, page.getPlacements().size());
	}

	@Test
	public void testPageModeAndAssertProperNumerationOfPlacements()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, GridMode.FIXED, PageAlignment.LEFT, prepareGrid(4), null);
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		fillAllSlotsWithPlacements(page);
		List<PlacementModel> placements = page.getPlacements();
		int gridElId = 0;
		int pageNum = 0;
		for (final PlacementModel placement : placements)
		{
			placement.setGridElementId(Integer.valueOf(gridElId++ % 4));
			placement.setPageNumber(Integer.valueOf(pageNum++ / 4));
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
	public void testComputeAvailableSlotsNumberForOverwriteMode()
	{
		final PageModel page = preparePage(PageMode.DOUBLE, GridMode.FIXED, PageAlignment.LEFT, prepareGrid(4), prepareGrid(4));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		fillSlot(page, 0, 1);
		fillSlot(page, 0, 2);

		CanAddResult canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 5, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 0, 1, 10, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 8, canAdd.getCanAddNumber());

		fillSlot(page, 0, 3);
		fillSlot(page, 0, 4);
		fillSlot(page, 1, 1);
		fillSlot(page, 1, 2);

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 1, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner.canAddNewPlacements(page, 1, 3, 4, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		final PageModel page2 = preparePage(PageMode.SINGLE, GridMode.FIXED, PageAlignment.LEFT, prepareGrid(4), null);
		final PagePlanner pagePlanner2 = getPagePlanner(page2.getGridMode());

		fillSlot(page2, 0, 3);

		canAdd = pagePlanner2.canAddNewPlacements(page2, 0, 1, 4, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 4, canAdd.getCanAddNumber());

		canAdd = pagePlanner2.canAddNewPlacements(page2, 0, 3, 2, true);
		assertTrue("", canAdd.isCanAdd());
		assertEquals("", 2, canAdd.getCanAddNumber());

		canAdd = pagePlanner2.canAddNewPlacements(page2, 0, 4, 2, true);
		assertFalse("", canAdd.isCanAdd());
		assertEquals("", 1, canAdd.getCanAddNumber());
	}

	@Test
	public void testSequenceToDoubleWithTwoGrids()
	{
		final PageModel page = preparePage(PageMode.SEQUENCE, GridMode.FIXED, PageAlignment.LEFT, prepareGrid(16), prepareGrid(8));
		final PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		fillAllSlotsWithPlacements(page);
		for (int i = 1; i <= 16; i++) // simulate messy numbers - may happen for sequence page
		{
			final PlacementModel p = new PlacementModel();
			p.setPage(page);
			p.setPageNumber(Integer.valueOf(i % 2));
			p.setGridElementId(Integer.valueOf(i));
			page.getPlacements().add(p);
		}
		assertEquals(56, page.getPlacements().size());
		pagePlanner.handleChangePageMode(page, PageMode.DOUBLE);
		final Set<Map.Entry<Integer, Integer>> uniques = new HashSet<Map.Entry<Integer, Integer>>();
		for (final PlacementModel placement : page.getPlacements())
		{
			final int gridElId = placement.getGridElementId().intValue();
			final int pageNo = placement.getPageNumber().intValue();
			if (!uniques.add(new AbstractMap.SimpleEntry(placement.getPageNumber(), placement.getGridElementId())))
			{
				fail("Grid element id should be unique on page level! [" + pageNo + ", " + gridElId + "]");
			}
			switch (pageNo)
			{
				case 0:
					assertTrue(gridElId <= 16 && gridElId > 0);
					break;
				case 1:
					assertTrue(gridElId <= 8 && gridElId > 0);
					break;
				default:
					fail("Max page number is 2 for double pages!");
			}
		}
		assertEquals(24, page.getPlacements().size());
	}
}
