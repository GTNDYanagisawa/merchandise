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

import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.enums.PageAlignment;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.model.GridElementModel;
import de.hybris.platform.print.model.GridModel;
import de.hybris.platform.print.model.ItemPlacementModel;
import de.hybris.platform.print.model.LayoutTemplateModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.printcockpit.pagemanagment.impl.DefaultDynamicModePagePlanner;
import de.hybris.platform.printcockpit.pagemanagment.impl.DefaultFixedModePagePlanner;
import de.hybris.platform.printcockpit.pagemanagment.impl.DefaultPagePlannerRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;


@Ignore
public abstract class AbstractPagePlannerTest
{
	private LayoutTemplateModel defaultTemplate = null;
	protected PagePlannerRegistry pagePlannerRegistry;

	public abstract GridMode getDefaultGridMode();

	@Before
	public void setUp() throws Exception
	{
		final DefaultPagePlannerRegistry pagePlannerRegistryInternal = new DefaultPagePlannerRegistry();
		pagePlannerRegistryInternal.addPagePlanner(GridMode.FIXED, new DefaultFixedModePagePlanner());
		pagePlannerRegistryInternal.addPagePlanner(GridMode.DYNAMIC, new DefaultDynamicModePagePlanner());
		pagePlannerRegistry = pagePlannerRegistryInternal;

		defaultTemplate = new LayoutTemplateModel();
	}

	protected PagePlanner getPagePlanner(final GridMode gridMode)
	{
		return pagePlannerRegistry.getPagePlanner(gridMode);
	}

	protected List<PlacementModel> preparePlacements(final int no)
	{
		final List<PlacementModel> result = new ArrayList<PlacementModel>();
		for (int i = 0; i < no; i++)
		{
			result.add(preparePlacement());
		}
		return result;
	}

	protected PlacementModel preparePlacement()
	{
		final ItemPlacementModel placement = new ItemPlacementModel();
		placement.setItem(prepareProduct());
		return placement;
	}

	protected PageModel preparePage(final PageMode pageMode, final GridModel grid)
	{
		return preparePage(pageMode, grid, null);
	}

	protected PageModel preparePage(final PageMode pageMode, final GridModel grid, final GridModel alternativeGrid)
	{
		return preparePage(pageMode, getDefaultGridMode(), getDefaultPageAlignment(), grid, alternativeGrid);
	}

	protected PageModel preparePage(final PageMode pageMode, final GridMode gridMode, final PageAlignment alignment,
			final GridModel grid, final GridModel alternativeGrid)
	{
		final PageModel page = new PageModel();
		page.setPageMode(pageMode);
		page.setGrid(grid);
		page.setAlternativeGrid(alternativeGrid);
		page.setGridMode(gridMode);
		page.setPlacements(new ArrayList<PlacementModel>());
		page.setAlignment(alignment);

		return page;
	}

	/**
	 * de.hybris.platform.printcockpit.pagemanagment.AbstractPagePlannerTest.getDefaultPageAlignment()
	 * @return PageAlignment.LEFT
	 */
	protected PageAlignment getDefaultPageAlignment()
	{
		return PageAlignment.LEFT;
	}


	protected GridModel prepareGrid(final int gridElementsCount)
	{
		final GridModel grid = new GridModel();
		final List<GridElementModel> gridElements = new ArrayList<GridElementModel>(gridElementsCount);

		for (int i = 0; i < gridElementsCount; i++)
		{
			final GridElementModel gridElement = new GridElementModel();
			gridElement.setDefaultLayoutTemplate(defaultTemplate);
			gridElement.setId(Integer.valueOf(i));
			gridElement.setGrid(grid);
			gridElements.add(gridElement);
		}

		grid.setElements(gridElements);
		return grid;
	}

	private ProductModel prepareProduct()
	{
		final ProductModel res = new ProductModel();
		res.setCode("testProductPlacements_" + Long.toString(System.currentTimeMillis(), 34));
		return res;
	}

	protected void fillAllSlotsWithPlacements(final PageModel page)
	{

		if (page.getAlignment() == PageAlignment.AUTO)
		{
			throw new IllegalArgumentException(PageAlignment.AUTO + " not supported!");
		}

		final GridModel _grid = page.getGrid();
		final GridModel _altGrid = page.getAlternativeGrid() != null ? page.getAlternativeGrid() : _grid;

		final GridModel grid = page.getAlignment() == PageAlignment.RIGHT ? _altGrid : _grid;
		final GridModel altGrid = page.getAlignment() == PageAlignment.RIGHT ? _grid : _altGrid;

		int pages = 1;
		if (page.getPageMode() == PageMode.DOUBLE)
		{
			pages = 2;
		}
		else if (page.getPageMode() == PageMode.SEQUENCE)
		{
			pages = 3;
		}
		page.setPlacements(Collections.<PlacementModel> emptyList());
		for (int pageNo = 0; pageNo < pages; pageNo++)
		{
			final GridModel currentGrid = pageNo % 2 == 0 ? grid : altGrid;

			final int elementsCnt = currentGrid.getElements().size();

			for (int gridId = 1; gridId <= elementsCnt; gridId++)
			{
				prepareAndAddPlacementToPage(page, pageNo, gridId);
			}
		}
	}

	protected void prepareAndAddPlacementToPage(final PageModel page, final int pageNo, final int gridId)
	{
		final PlacementModel placement = preparePlacement();
		placement.setPage(page);
		placement.setPageNumber(Integer.valueOf(pageNo));
		placement.setGridElementId(Integer.valueOf(gridId));
		final ArrayList<PlacementModel> placements = new ArrayList<PlacementModel>(page.getPlacements());
		placements.add(placement);
		page.setPlacements(placements);
	}

	protected void fillSlot(final PageModel page, final int pageNo, final int gridElementId)
	{
		final PagePlanner pagePlanner = pagePlannerRegistry.getPagePlanner(page.getGridMode());
		pagePlanner.addNewPlacement(page, preparePlacement(), pageNo, gridElementId);
	}

	protected CommentModel prepareComment(final String subject, final int pageNo)
	{
		final CommentModel commentModel = new CommentModel();
		commentModel.setSubject(subject);
		final CommentMetadataModel commentMetadata = new CommentMetadataModel();
		commentMetadata.setPageIndex(Integer.valueOf(pageNo));
		commentModel.setCommentMetadata(Collections.singletonList(commentMetadata));
		return commentModel;
	}

}
