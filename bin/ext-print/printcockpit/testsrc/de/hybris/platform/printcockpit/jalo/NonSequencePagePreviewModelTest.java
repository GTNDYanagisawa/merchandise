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
package de.hybris.platform.printcockpit.jalo;

import static org.mockito.Mockito.when;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.model.GridElementModel;
import de.hybris.platform.print.model.GridModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.printcockpit.model.layout.LayoutService;
import de.hybris.platform.printcockpit.model.layout.impl.NonSequencePagePreviewModel;
import de.hybris.platform.printcockpit.model.layout.impl.SinglePageModel;
import de.hybris.platform.printcockpit.pagemanagment.PlacementHandler;
import de.hybris.platform.printcockpit.pagemanagment.impl.FixedPlacementHandler;
import de.hybris.platform.printcockpit.view.layouts.grid.PreviewSlot;
import de.hybris.platform.printcockpitnew.services.PrintcockpitService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
public class NonSequencePagePreviewModelTest
{

	@Mock
	private TypedObject typedObject;

	@Mock
	private PrintcockpitService printcockpitService;

	@Mock
	private LayoutService layoutService;
	@Mock
	private PlacementHandler placementHandler;
	@Mock
	private ModelService modelService;

	private NonSequencePagePreviewModel model;

	FixedPlacementHandler fixedPlacementHandler;


	@Before
	public void setUp()
	{

		MockitoAnnotations.initMocks(this);

		final GridModel gridModel = new GridModel();
		final GridMode gridMode = GridMode.FIXED;


		final GridElementModel templateGridElement = new GridElementModel();
		templateGridElement.setXpos(Double.valueOf(100));
		templateGridElement.setYpos(Double.valueOf(100));


		final PageModel pageModel = new PageModel();
		pageModel.setGrid(gridModel);
		pageModel.setGridMode(gridMode);

		when(typedObject.getObject()).thenReturn(pageModel);
		final int[] pagedimensions = new int[]
		{ 100, 100 };
		final List<Integer> gridElementsIds = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3),
				Integer.valueOf(4));
		when(layoutService.getPageDimensions(pageModel)).thenReturn(pagedimensions);
		when(printcockpitService.getGridIds(gridModel)).thenReturn(gridElementsIds);
		when(printcockpitService.getElement(gridModel, 1)).thenReturn(templateGridElement);
		when(printcockpitService.getElement(gridModel, 2)).thenReturn(templateGridElement);
		when(printcockpitService.getElement(gridModel, 3)).thenReturn(templateGridElement);
		when(printcockpitService.getElement(gridModel, 4)).thenReturn(templateGridElement);
		when(placementHandler.initializePlacements(Mockito.eq(pageModel), Mockito.anyList())).thenReturn(Collections.EMPTY_SET);
		when(Boolean.valueOf(modelService.isNew(typedObject.getObject()))).thenReturn(Boolean.FALSE);

		model = new SinglePageModel(typedObject);

		model.setPrintcockpitService(printcockpitService);
		model.setLayoutService(layoutService);
		model.setPlacementsHandlerRegistry(Collections.singletonMap(gridMode, placementHandler));
		model.setModelService(modelService);
		model.initModel();

		fixedPlacementHandler = new FixedPlacementHandler();
	}

	@Test
	public void testComputeSlotDistance()
	{

		final PlacementModel movedPlacement2 = new PlacementModel();
		movedPlacement2.setGridElementId(Integer.valueOf(2));


		final List<PreviewSlot> slots = model.getPreviewPages().get(0).getSlots();
		for (int i = 0; i < slots.size(); i++)
		{
			Assert.assertEquals(4 - i,
					fixedPlacementHandler.getSlotsDistance(model, slots.get(i), Arrays.asList((Object) movedPlacement2)));
		}

	}
}
