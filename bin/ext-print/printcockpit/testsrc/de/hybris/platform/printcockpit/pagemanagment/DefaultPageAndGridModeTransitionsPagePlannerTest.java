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

import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;

import java.util.List;
import java.util.Random;

import org.junit.Test;


public class DefaultPageAndGridModeTransitionsPagePlannerTest extends AbstractPagePlannerTest
{

	@Test
	public void testSequenceDynamic2DoubleFixed()
	{

		final PageModel page = preparePage(PageMode.DOUBLE, prepareGrid(4));
		page.setGridMode(GridMode.DYNAMIC);

		fillAllSlotsWithPlacements(page);

		PagePlanner pagePlanner = getPagePlanner(page.getGridMode());
		pagePlanner.addNewPlacements(page, preparePlacements(4), 1, 1);
		pagePlanner.handleChangePageMode(page, PageMode.SEQUENCE);

		pickRandomValuesForPageNumberAndGridElementId(page);

		pagePlanner.handleChangePageMode(page, PageMode.DOUBLE);

		pagePlanner = getPagePlanner(page.getGridMode());
		pagePlanner.handleChangeGridMode(page, GridMode.FIXED);

		final List<PlacementModel> placements = page.getPlacements();
		for (int i = 0; i < 4; i++)
		{
			final PlacementModel placementPage1 = placements.get(i);
			final PlacementModel placementPage2 = placements.get(i + 4);

			assertEquals(0, placementPage1.getPageNumber().intValue());
			assertEquals(1, placementPage2.getPageNumber().intValue());
			assertEquals(i + 1, placementPage1.getGridElementId().intValue());
			assertEquals(i + 1, placementPage2.getGridElementId().intValue());
		}
	}

	private void pickRandomValuesForPageNumberAndGridElementId(final PageModel page)
	{
		final Random rand = new Random();
		boolean random = false;
		for (final PlacementModel placement : page.getPlacements())
		{
			if (random)
			{
				placement.setPageNumber(Integer.valueOf(rand.nextInt(10) - 10));
				placement.setGridElementId(Integer.valueOf(rand.nextInt(10) - 10));
			}
			else
			{
				placement.setPageNumber(Integer.valueOf(-1));
				placement.setGridElementId(Integer.valueOf(-1));
				random = !random;
			}
		}
	}

	@Override
	public GridMode getDefaultGridMode()
	{
		return null;
	}

}
